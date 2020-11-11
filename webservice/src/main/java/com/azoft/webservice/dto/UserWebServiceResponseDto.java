package com.azoft.webservice.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class UserWebServiceResponseDto extends ResponseEntity<BodyWrapperDto> {
    public UserWebServiceResponseDto(BodyWrapperDto body, HttpStatus status) {
        super(body, status);
    }
}
