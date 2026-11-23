package com.foodrescue.charity.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/** Convertie automatiquement en SOAP Fault (faultcode Client) par Spring-WS. */
@SoapFault(faultCode = FaultCode.CLIENT)
public class CharityNotApprovedException extends RuntimeException {

    public CharityNotApprovedException(String id) {
        super("[CHARITY_NOT_APPROVED] " + String.format("L'association %s n'est pas agréée", id));
    }
}
