package client;

import java.util.Optional;

/**
 * Stored all information about a request to be sent to the server
 * @author sophieguile
 *
 */
public class Request {
	private Optional<String> text1;
	private Optional<String> text2;
	private Optional<String> text3;
	
	/**
	 * The type of request
	 * @author sophieguile
	 *
	 */
	public enum RequestType{
		LOGIN,
		REGISTER,
		LOGOUT,
		JOINGAME,
		WAVE,
		QUIT,
		MOVE,
		UPDATEENEMIES,
		WAVEGAME,
		LEAVEGAME,
		RESTART,
		LOGOOUT,
		WIN
	}
	
	
	private RequestType type;
	
	/**
	 * Gets the type of this request
	 * @return an enum of the request type (Request.RequestType)
	 */
	public RequestType getType(){
		return type;
	}
	
	/**
	 * Constructor for requests with 3 pieces of text
	 * @param text1
	 * @param text2
	 * @param text3
	 * @param type (Request.RequestType)
	 */
	public Request(String text1, String text2, String text3, RequestType type){
		this.text1 = Optional.of(text1);
		this.text2 = Optional.of(text2);
		this.text3 = Optional.of(text3);
		this.type = type;
	}
	
	/**
	 * Constructor for requests with 2 pieces of text
	 * @param text1
	 * @param text2
	 * @param type (Request.RequestType)
	 */
	public Request(String text1, String text2, RequestType type){
		this.text1 = Optional.of(text1);
		this.text2 = Optional.of(text2);
		this.type = type;
	}
	
	/**
	 * Constructor for requests with 1 piece of text
	 * @param text1
	 * @param type (Request.RequestType)
	 */
	public Request(String text1, RequestType type){
		this.text1 = Optional.of(text1);
		this.type = type;
	}
	
	/**
	 * Constructor for request with no text
	 * @param type
	 */
	public Request(RequestType type){
		this.type = type;
	}
	
	/**
	 * 
	 * @return The first piece of text
	 */
	public String getText1(){
		return text1.get();
	}
	
	/**
	 * 
	 * @return The second piece of text
	 */
	public String getText2(){
		return text2.get();
	}
	
	/**
	 * 
	 * @return The third piece of text
	 */
	public String getText3(){
		return text3.get();
	}

	
}
