package com.aruba.Lib.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseError {
	
	private HttpStatus httpStatus;
	private ErrorCodes errorCodes;
	private String message;
	private String moreInfo;


}
