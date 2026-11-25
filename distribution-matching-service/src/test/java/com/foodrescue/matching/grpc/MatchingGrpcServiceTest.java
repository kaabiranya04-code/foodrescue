package com.foodrescue.matching.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.foodrescue.matching.engine.Candidate;
import com.foodrescue.matching.engine.MatchingEngine;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Tests gRPC de bout en bout : vrai serveur sur un port libre, vrai client. */
class MatchingGrpcServiceTest {

    private static final List<Candidate> CANDIDATES = List.of(
            new Candidate("CH-0001", "Association Amal", "Tunis", 500,
                    Set.of("khobz", "legumes", "plats-prepares")),
            new Candidate("CH-0002", "Croissant-Rouge Sfax", "Sfax", 300,
                    Set.of("khobz", "fruits", "legumes")),
            new Candidate("CH-0003", "Association Sousse", "Sousse", 1000, Set.of("*")));

    private final List<String> recorded = new CopyOnWriteArrayList<>();
    private Server server;
    private ManagedChannel channel;

    private MatchingServiceGrpc.MatchingServiceBlockingStub startServer(long stepDelayMs) throws IOException {
        MatchingGrpcService service = new MatchingGrpcService(
                new MatchingEngine(), () -> CANDIDATES, (donationId, result) -> recorded.add(donationId),
                stepDelayMs);
        server = ServerBuilder.forPort(0).addService(service).build().start();
        channel = ManagedChannelBuilder.forAddress("localhost", server.getPort()).usePlaintext().build();
        return MatchingServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        if (channel != null) {
            channel.shutdownNow();
            channel.awaitTermination(2, TimeUnit.SECONDS);
        }
        if (server != null) {
            server.shutdownNow();
            server.awaitTermination(2, TimeUnit.SECONDS);
        }
    }

    private static MatchRequest request(String donationId, String foodType, double qty, String region, int hours) {
        return MatchRequest.newBuilder()
                .setDonationId(donationId)
                .setFoodType(foodType)
                .setQuantityKg(qty)
                .setRegion(region)
                .setHoursBeforeExpiry(hours)
                .build();
    }

    @Test
    void findBestMatch_returnsBestCandidateAndRecordsIt() throws IOException {
        MatchingServiceGrpc.MatchingServiceBlockingStub stub = startServer(10);

        MatchResponse response = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .findBestMatch(request("D-1", "khobz", 100, "Sfax", 4));

        assertThat(response.getCharityId()).isEqualTo("CH-0002");
        assertThat(response.getScore()).isGreaterThan(0);
        assertThat(response.getReason()).isNotBlank();
        assertThat(recorded).containsExactly("D-1");
    }

    @Test
    void findBestMatch_invalidQuantity_returnsInvalidArgument() throws IOException {
        MatchingServiceGrpc.MatchingServiceBlockingStub stub = startServer(10);

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
                () -> stub.findBestMatch(request("D-2", "khobz", 0, "Tunis", 4)));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT);
    }

    @Test
    void findBestMatch_noCompatibleCandidate_returnsNotFound() throws IOException {
        MatchingServiceGrpc.MatchingServiceBlockingStub stub = startServer(10);

        // 5000 kg dépasse la capacité de toutes les associations
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class,
                () -> stub.findBestMatch(request("D-3", "khobz", 5000, "Tunis", 4)));

        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND);
        assertThat(recorded).isEmpty();
    }

    @Test
    void streamMatchingUpdates_emitsStagesInOrderAndEndsWithMatched() throws IOException {
        MatchingServiceGrpc.MatchingServiceBlockingStub stub = startServer(20);

        Iterator<MatchUpdate> it = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .streamMatchingUpdates(request("D-4", "khobz", 100, "Sfax", 4));
        List<MatchUpdate> updates = new ArrayList<>();
        it.forEachRemaining(updates::add);

        assertThat(updates).extracting(MatchUpdate::getStage)
                .containsExactly(Stage.SEARCHING, Stage.CANDIDATES_FOUND, Stage.SCORING, Stage.MATCHED);
        assertThat(updates).extracting(MatchUpdate::getProgressPercent)
                .containsExactly(10, 40, 70, 100);
        assertThat(updates.get(3).getCharityId()).isEqualTo("CH-0002");
    }

    @Test
    void streamMatchingUpdates_noCandidate_endsWithNoMatchWithoutError() throws IOException {
        MatchingServiceGrpc.MatchingServiceBlockingStub stub = startServer(10);

        Iterator<MatchUpdate> it = stub.withDeadlineAfter(5, TimeUnit.SECONDS)
                .streamMatchingUpdates(request("D-5", "khobz", 5000, "Tunis", 4));
        List<MatchUpdate> updates = new ArrayList<>();
        it.forEachRemaining(updates::add);

        assertThat(updates.get(updates.size() - 1).getStage()).isEqualTo(Stage.NO_MATCH);
    }

    @Test
    void streamMatchingUpdates_clientDeadlineTooShort_returnsDeadlineExceeded() throws IOException {
        // chaque étape dure 500 ms, le client n'attend que 150 ms
        MatchingServiceGrpc.MatchingServiceBlockingStub stub = startServer(500);

        Iterator<MatchUpdate> it = stub.withDeadlineAfter(150, TimeUnit.MILLISECONDS)
                .streamMatchingUpdates(request("D-6", "khobz", 100, "Sfax", 4));

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () -> {
            while (it.hasNext()) {
                it.next();
            }
        });
        assertThat(ex.getStatus().getCode()).isEqualTo(Status.Code.DEADLINE_EXCEEDED);
    }
}
