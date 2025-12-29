package com.gautam.users.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {
    private String statusCode;
    private String message;
    private Object data;

    public ResponseDto(String statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
