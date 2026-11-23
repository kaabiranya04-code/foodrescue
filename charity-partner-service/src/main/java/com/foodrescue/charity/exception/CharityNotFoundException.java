package com.foodrescue.charity.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/** Convertie automatiquement en SOAP Fault (faultcode Client) par Spring-WS. */
@SoapFault(faultCode = FaultCode.CLIENT)
public class CharityNotFoundException extends RuntimeException {

    public CharityNotFoundException(String id) {
        super("[CHARITY_NOT_FOUND] " + String.format("L'association %s est introuvable", id));
    }
}
