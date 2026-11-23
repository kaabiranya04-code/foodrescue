package com.foodrescue.charity.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/** Conversions entre java.time et les dates XML (xs:date / xs:dateTime). */
public final class XmlDates {

    private static final DatatypeFactory FACTORY;

    static {
        try {
            FACTORY = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    private XmlDates() {
    }

    public static XMLGregorianCalendar toXml(Instant instant) {
        String lexical = OffsetDateTime
                .ofInstant(instant.truncatedTo(ChronoUnit.SECONDS), ZoneOffset.UTC)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        return FACTORY.newXMLGregorianCalendar(lexical);
    }

    public static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
