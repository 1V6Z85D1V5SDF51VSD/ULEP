package pt.ulisboa.ssobroker.eidas;


public class ApplicationSpecificException extends RuntimeException {
	
	private String msg;
	private String title;
	
	public ApplicationSpecificException(String title, String msg) {
		this.msg = msg;
		this.title = title;
	}
	
	public String getMessage() {
		return msg;
	}
	
	public String getTitle() {
		return title;
	}
}