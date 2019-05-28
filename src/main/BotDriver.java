/*
 * Christopher DeRoche
 * Date Created: 5/25/2019
 * https://github.com/compact-disc
 * 
 * Driver class for DeRoche_Bot twitter bot
 */

package main;

//imports for twitter4j api
import twitter4j.*;

//imports for java
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Random;
import javax.swing.Timer;

public class BotDriver {

	//Files and directories
	private String logFileLocation = "tweets/logs/logs.txt"; //the log file location
	private String tweetsDirectoryLocation = "tweets"; //root tweets directory
	private String logDirectoryLocation = "tweets/logs"; //logs directory
	
	//private instance variables to use in the program
	private Scanner keyboardInput = new Scanner(System.in); //Scanner used to get any user inputs from the user
	private final int STATUS_CHARACTER_MAX = 280; //Max amount of characters that can be used in a single tweet
	private final int MAX_TWEETS_PER_THREE_HOURS = 300; //max amount of tweets allowed per hour by twitter via the api
	private HashMap<Integer, String> tweets = new HashMap<Integer, String>(); //Hash map to load and store all the tweets
	private File logFile = new File(logFileLocation); //log file for the bot
	private File directory = new File(tweetsDirectoryLocation); //Creates the file directory object for tweets
	
	//private instance variables for logging variables
	private PrintStream loggingFilePrintStream = null; //print stream for the logging file
	private PrintStream consoleStream = System.out; //variable to set the logging back to the console if needed
	private File temporaryLogFile = null; //file for the temporary log file until it is written to the logs.txt file
	
	//default constructor
	public BotDriver() {
		
		System.out.println("Starting Bot Driver...");
		startLoggingSystem();
		checkForAllFilesAndDirectories();
		loadTweets();
		writeLogs();
		System.exit(0);
		
	}
	
	//Method to load all the tweets in the folder to a hashmap
	public void loadTweets() {

		//Variables for the method
		String tweet;
		int mapCounter = 0;

		//Takes the directory makes a list of all of files, then for loop goes over each file object
		for(File tweetFile : this.directory.listFiles()) {
			
			//If the file is actually a directory it skips it and tells you, also skips logs directory
			if(tweetFile.isDirectory()) {
				
				if(!tweetFile.getName().equals("logs")) {
					
					System.out.println("'" + tweetFile.getName() + "'" + " is a directory, skipping...");
					continue; //Continue to the next item in the loop
					
				}else {
					
					continue; //Continue to the next item in the loop
					
				}
				
			}
			
			//Try catch for file scanning
			try {
				
				//Creates stringbuilder objects and scanner to scan the words and create strings
				StringBuilder tweetBuilder = new StringBuilder("");
				Scanner tweetReader = new Scanner(tweetFile);
				
				//Adds deroche bot to the front of each tweet
				tweetBuilder.append("DeRoche Bot: ");
				
				//while there are words, grab them and attach them to the string builder
				while(tweetReader.hasNext()) {
					
					tweetBuilder.append(tweetReader.next() + " ");
					
				}

				//convert the string builder to a string and set the tweet variable
				tweet = tweetBuilder.toString();
				
				//Checks if the final tweet is greater than 0 and less than 280, if it is then add it to the hashmap, if not then skip it, add 1 to the counter
				if(tweet.length() <= STATUS_CHARACTER_MAX && tweet.length() > 0) {
					
					this.tweets.put(mapCounter, tweet);
					mapCounter++;
					
				}else {
					
					System.out.println("'" + tweetFile.getName() + "'" + " is greater than " + STATUS_CHARACTER_MAX + " characters, skipping...");
					
				}
				
				//Catches if it does not work and give error and shutdown
			} catch (FileNotFoundException e) {
				
				System.out.println("Error Reading Files! Shutting Down...");
				e.printStackTrace();
				System.exit(0);
				
			}
			
		}
		
		//If the map is not empty report that it # of tweets loaded successfully
		if(!this.tweets.isEmpty()) {
			
			System.out.println(mapCounter + " Tweets Loaded Successfully!");
			
		}else {
			
			checkForTweets();
			
		}
		
	}
	
	//Returns a random tweet from the hashmap
	public String getRandomStatusUpdate() {
		
		Random randomTweet = new Random();
		int tweetAtPosition = randomTweet.nextInt(tweets.size());
		
		return tweets.get(tweetAtPosition);
		
	}
	
	//Method to tweet a new status to twitter takes a String parameter and that is the message the user entered
	public void statusUpdate() {
		
		//Creates twitter object to connect
		Twitter twitter = TwitterFactory.getSingleton();
		
		//tweet is gathered using the getRandomStatusUpdate method
		String tweet = getRandomStatusUpdate();
		
		//tries to tweet and prints out the tweet if it works, or catches and tells you that it could not tweet
		try {
			
			Status status = twitter.updateStatus(tweet);
			System.out.println("Successfully updated the status to \"" + tweet + "\"");
			
		} catch (TwitterException e) {
			
			System.out.println("Unable to update Status...");
			e.printStackTrace();
			
		}
		
	}
	
	//checks for the logs files/directories
	public void checkForLogs() {
		
		//creates the log directory object
		File logDirectory = new File(logDirectoryLocation);
		
		//if the directory does not exist then make one
		if(!logDirectory.exists()) {
			
			System.out.println("No logs directory exists! Creating directory...");
			logDirectory.mkdir();
			
		}
		
		//If the log file does not exist then create one, catch it does not and report it
		if(!this.logFile.exists()) {
			
			try {
				
				this.logFile.createNewFile();
				System.out.println("'logs.txt' file does not exist. Creating...");
				
			} catch (IOException e) {
				
				System.out.println("Unable to create 'logs.txt' file...");
				e.printStackTrace();
				
			}
			
		}
		
	}
	
	//Method to check if any tweets exists, if they do not then shutdown the bot because it can not tweet anything
	public void checkForTweets() {
		
		//If the map is empty report and exit the program because it can do nothing
		if(this.tweets.isEmpty()) {
			
			System.out.println("There are no tweets loaded...");
			
		}
		
	}
	
	//Check to see if the tweets directory is 
	public void checkTweetsDirectory() {
		
		//checks if it exists in the system and creates one if it does not
		if(!this.directory.exists()) {
			
			System.out.println("No tweets directory exists! Creating directory...");
			this.directory.mkdir();
			
		}
		
	}
	
	public void checkForAllFilesAndDirectories() {
		
		//check if the tweets directory exists
		checkTweetsDirectory();
		
		//Check if the logs directories and files exists
		checkForLogs();
		
	}
	
	public void startLoggingSystem() {
		
		System.out.println("Logging System Loading...");
		
		try {
			
			this.temporaryLogFile = File.createTempFile("runningLogs", ".tmp");
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		try {
			
			this.loggingFilePrintStream = new PrintStream(this.temporaryLogFile);
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		System.out.println("Logs loaded. Switching from console to 'logs.txt' file...");
		setOutputStreamToLogFile();
		System.out.println("Successfully switched to 'logs.txt' file...");
		
	}
	
	public void writeLogs() {
		
		System.out.println("Saving 'logs.txt' file...");
		
		Scanner readLogs = null;
		PrintWriter logWriter = null;
		
		try {
			
			logWriter = new PrintWriter(this.logFileLocation);
			
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
			
		}
		
		try {
			
			readLogs = new Scanner(this.temporaryLogFile);
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			
		}
		
		while(readLogs.hasNext()) {
			
			logWriter.println(readLogs.nextLine());
			
		}
		
		logWriter.close();
		System.out.println("'logs.txt' file saved...");
		
	}
	
	//Set the output stream to the log file in the program
	public void setOutputStreamToLogFile() {
		
		System.setOut(this.loggingFilePrintStream);
		
	}
	
	//Set the output stream to the console in the program
	public void setOutputStreamToConsole() {
		
		System.setOut(this.consoleStream);
		
	}
	
	public void test() {
		
		System.out.println("test method");
		
	}

}
