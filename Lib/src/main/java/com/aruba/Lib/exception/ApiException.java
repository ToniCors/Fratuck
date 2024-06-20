package com.aruba.Lib.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

@Setter
@Getter
@ToString
public class ApiException extends RuntimeException{

	@Serial
	private static final long serialVersionUID = 1L;
	private ResponseError responseError;	
	
	public ApiException(Throwable cause, ResponseError responseError) {
		super(responseError.getMessage(), cause);
		this.setResponseError(responseError);
	}

	public ApiException(ResponseError responseError) {
		super(responseError.getMessage());
		this.setResponseError(responseError);
	}

}
