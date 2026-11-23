package com.foodrescue.charity.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

/** Convertie automatiquement en SOAP Fault (faultcode Client) par Spring-WS. */
@SoapFault(faultCode = FaultCode.CLIENT)
public class RegistrationNotFoundException extends RuntimeException {

    public RegistrationNotFoundException(String id) {
        super("[REGISTRATION_NOT_FOUND] " + String.format("L'enregistrement %s est introuvable", id));
    }
}
