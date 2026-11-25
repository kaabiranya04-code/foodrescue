package com.foodrescue.matching.grpc;

import com.foodrescue.matching.engine.Candidate;
import com.foodrescue.matching.engine.CandidateSource;
import com.foodrescue.matching.engine.MatchCriteria;
import com.foodrescue.matching.engine.MatchRecorder;
import com.foodrescue.matching.engine.MatchResult;
import com.foodrescue.matching.engine.MatchingEngine;
import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Adaptateur gRPC : valide la requête, appelle le moteur et traduit les erreurs en codes gRPC. */
@Component
public class MatchingGrpcService extends MatchingServiceGrpc.MatchingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(MatchingGrpcService.class);

    private final MatchingEngine engine;
    private final CandidateSource source;
    private final MatchRecorder recorder;
    private final long stepDelayMs;

    public MatchingGrpcService(MatchingEngine engine, CandidateSource source, MatchRecorder recorder,
                               @Value("${matching.stream.step-delay-ms:400}") long stepDelayMs) {
        this.engine = engine;
        this.source = source;
        this.recorder = recorder;
        this.stepDelayMs = stepDelayMs;
    }

    // ---------------------------------------------------------------- unary
    @Override
    public void findBestMatch(MatchRequest request, StreamObserver<MatchResponse> responseObserver) {
        MatchCriteria criteria;
        try {
            criteria = toCriteria(request);
        } catch (IllegalArgumentException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
            return;
        }

        try {
            Optional<MatchResult> best = engine.best(criteria, source.loadAll());
            if (best.isEmpty()) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Aucune association compatible pour le don " + criteria.donationId())
                        .asRuntimeException());
                return;
            }
            MatchResult result = best.get();
            recorder.record(criteria.donationId(), result);
            responseObserver.onNext(MatchResponse.newBuilder()
                    .setDonationId(criteria.donationId())
                    .setCharityId(result.candidate().charityId())
                    .setCharityName(result.candidate().name())
                    .setScore(result.score())
                    .setReason(result.reason())
                    .build());
            responseObserver.onCompleted();
        } catch (RuntimeException e) {
            log.error("FindBestMatch a echoue", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Erreur interne du matching").asRuntimeException());
        }
    }

    // ------------------------------------------------------ server streaming
    @Override
    public void streamMatchingUpdates(MatchRequest request, StreamObserver<MatchUpdate> responseObserver) {
        ServerCallStreamObserver<MatchUpdate> out = (ServerCallStreamObserver<MatchUpdate>) responseObserver;
        MatchCriteria criteria;
        try {
            criteria = toCriteria(request);
        } catch (IllegalArgumentException e) {
            out.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
            return;
        }

        try {
            if (!emit(out, criteria, Stage.SEARCHING, 10, "Recherche des associations partenaires...", "")) {
                return;
            }
            if (!pause(out)) {
                return;
            }
            List<Candidate> all = source.loadAll();
            int eligible = engine.eligible(criteria, all).size();
            if (!emit(out, criteria, Stage.CANDIDATES_FOUND, 40, eligible + " association(s) compatible(s)", "")) {
                return;
            }
            if (!pause(out)) {
                return;
            }
            if (!emit(out, criteria, Stage.SCORING, 70, "Calcul du score de chaque candidat...", "")) {
                return;
            }
            if (!pause(out)) {
                return;
            }
            Optional<MatchResult> best = engine.best(criteria, all);
            if (best.isPresent()) {
                MatchResult result = best.get();
                recorder.record(criteria.donationId(), result);
                String msg = "Meilleure association : " + result.candidate().name()
                        + " (score " + result.score() + ")";
                if (!emit(out, criteria, Stage.MATCHED, 100, msg, result.candidate().charityId())) {
                    return;
                }
            } else if (!emit(out, criteria, Stage.NO_MATCH, 100, "Aucune association compatible", "")) {
                return;
            }
            out.onCompleted();
        } catch (RuntimeException e) {
            log.error("StreamMatchingUpdates a echoue", e);
            if (!out.isCancelled()) {
                out.onError(Status.INTERNAL.withDescription("Erreur interne du matching").asRuntimeException());
            }
        }
    }

    // --------------------------------------------------------------- helpers
    /** Envoie une mise à jour, sauf si le client a annulé (deadline dépassé, annulation...). */
    private boolean emit(ServerCallStreamObserver<MatchUpdate> out, MatchCriteria criteria, Stage stage,
                         int progress, String message, String charityId) {
        if (out.isCancelled()) {
            return false;
        }
        out.onNext(MatchUpdate.newBuilder()
                .setDonationId(criteria.donationId())
                .setStage(stage)
                .setMessage(message)
                .setProgressPercent(progress)
                .setTimestampEpochMs(System.currentTimeMillis())
                .setCharityId(charityId)
                .build());
        return true;
    }

    /** Pause entre deux étapes ; s'arrête dès que le client a annulé. */
    private boolean pause(ServerCallStreamObserver<MatchUpdate> out) {
        if (out.isCancelled()) {
            return false;
        }
        try {
            Thread.sleep(stepDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return !out.isCancelled();
    }

    private MatchCriteria toCriteria(MatchRequest r) {
        if (r.getDonationId().isBlank()) {
            throw new IllegalArgumentException("donation_id est obligatoire");
        }
        if (r.getFoodType().isBlank()) {
            throw new IllegalArgumentException("food_type est obligatoire");
        }
        if (r.getRegion().isBlank()) {
            throw new IllegalArgumentException("region est obligatoire");
        }
        if (r.getQuantityKg() <= 0 || r.getQuantityKg() > 10_000) {
            throw new IllegalArgumentException("quantity_kg doit etre dans ]0 ; 10000]");
        }
        if (r.getHoursBeforeExpiry() < 0) {
            throw new IllegalArgumentException("hours_before_expiry ne peut pas etre negatif");
        }
        return new MatchCriteria(r.getDonationId(), r.getFoodType(), r.getQuantityKg(),
                r.getRegion(), r.getHoursBeforeExpiry());
    }
}
