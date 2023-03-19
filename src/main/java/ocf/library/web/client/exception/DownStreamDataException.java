package ocf.library.web.client.exception;

import org.springframework.http.HttpStatus;

public class DownStreamDataException extends RuntimeException {
	private HttpStatus errorCode;
	private String errorMessage;
	private String url;
	public DownStreamDataException(HttpStatus errorCode, String errorMessage, String url) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.url = url;
	}
	public HttpStatus getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(HttpStatus errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	

}
