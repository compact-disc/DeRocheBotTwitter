/*
 * Christopher DeRoche
 * Date Created: 6/1/2019
 * https://github.com/compact-disc
 * 
 * The tweet object class to store each tweet in its own object
 */

package twitter;

public class Tweet {
	
	//The private variable to store the tweet message
	private String message;
	
	//Default constructor to initialize the tweet object with message
	public Tweet(String recievedMessage) {
		
		this.message = recievedMessage; // set the tweet message
		
	}
	
	//Getter for the tweet message
	public String getMessage() {
		
		return this.message;
		
	}
	
	//Setter for the tweet message if it needs to be changed
	public void setMessage(String recievedMessage) {
		
		this.message = recievedMessage;
		
	}

}
