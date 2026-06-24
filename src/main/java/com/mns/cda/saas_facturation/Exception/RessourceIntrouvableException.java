package com.mns.cda.saas_facturation.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RessourceIntrouvableException extends RuntimeException {
    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
