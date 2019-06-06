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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class BotDriver {
	
	//timer variable for tweet timer to periodically tweet
	private Timer timer; //timer variable

	//Files and directories
	private String logFileLocation = "tweets/logs/logs.txt"; //the log file location
	private String tweetsDirectoryLocation = "tweets"; //root tweets directory
	private String logDirectoryLocation = "tweets/logs"; //logs directory
	
	//private instance variables to use in the program
	private Scanner keyboardInput = new Scanner(System.in); //Scanner used to get any user inputs from the user
	private final int STATUS_CHARACTER_MAX = 280; //Max amount of characters that can be used in a single tweet
	private HashMap<Integer, Tweet> tweets = new HashMap<Integer, Tweet>(); //Hash map to load and store all the tweets
	private File logFile = new File(logFileLocation); //log file for the bot
	private File directory = new File(tweetsDirectoryLocation); //Creates the file directory object for tweets
	private int intervalInMilliseconds; //rate of timer in milliseconds to use for timer
	
	//private instance variables for logging variables
	private PrintStream loggingFilePrintStream = null; //print stream for the logging file
	private PrintStream consoleStream = System.out; //variable to set the logging back to the console if needed
	private File temporaryLogFile = null; //file for the temporary log file until it is written to the logs.txt file
	
	//default constructor to start the driver when called from run
	public BotDriver() {
		
		System.out.println("Starting Bot Driver...");
		setTimers(); //set the timers for the bot
		startLoggingSystem(); //start the logging system
		checkForAllFilesAndDirectories(); //check for all the files and directories 
		loadTweets(); //load all the tweets into the map
		this.timer = new Timer(); //create new timer object
		timer.schedule(new StatusTaskTimer(), 30000, this.intervalInMilliseconds); //start timer for so many seconds, but wait 30 seconds before starting
		checkForStop(); //run this method until "stop" is typed in, then it will stop the program
		
	}
	
	//If at any time the user types in "stop" into the console, the program exits and saves properly
	public void checkForStop() {
		
		//String to gather the stop message
		String stopMessage = this.keyboardInput.next();
		
		//No matter the case, if stop is entered it will stop the program
		if(stopMessage.equalsIgnoreCase("stop")) {
			
			this.timer.cancel(); //cancel the timer when the program stops
			writeLogs(); //write the logs to the system properly
			System.out.println("DeRoche Bot shutdown...");
			System.exit(0); //exit the program
			
		}
		
	}
	
	//Method to set the intervals for tweets
	public void setTimers() {
		
		int intervalInMinutes; //variable to collect the timer in minutes
		
		//While loop to gather the time in minutes and have minimum of 30 minutes
		do {
			
			//Line and gathering of data for timer
			System.out.print("Enter a time (in minutes) to tweet (minimum 30 minutes): ");
			intervalInMinutes = this.keyboardInput.nextInt();
			
		}while(intervalInMinutes < 30); //keep running the loop if the timer is below 30
		
		//Convert the time entered from second to milliseconds for the timer
		this.intervalInMilliseconds = intervalInMinutes * 60000;
		
		
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
				if(tweet.length() <= this.STATUS_CHARACTER_MAX && tweet.length() > 0) {
					
					this.tweets.put(mapCounter, new Tweet(tweet));
					mapCounter++;
					
				}else {
					
					System.out.println("'" + tweetFile.getName() + "'" + " is greater than " + this.STATUS_CHARACTER_MAX + " characters, skipping...");
					
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
		int tweetAtPosition = randomTweet.nextInt(this.tweets.size());
		
		return this.tweets.get(tweetAtPosition).getMessage();
		
	}
	
	//Method to tweet a new status to twitter takes a String parameter and that is the message the user entered
	public void statusUpdate() {
		
		//Creates twitter object to connect
		Twitter twitter = TwitterFactory.getSingleton();
		
		//tweet is gathered using the getRandomStatusUpdate method
		String tweet = getRandomStatusUpdate();
		
		//tries to tweet and prints out the tweet if it works, or catches and tells you that it could not tweet
		//But it prints to the console and the log file
		try {
			
			Status status = twitter.updateStatus(tweet);
			
			System.out.println("Successfully updated the status to \"" + tweet + "\"");
			
			setOutputStreamToConsole(); //set the stream to the console, print the lines
			System.out.println("Successfully updated the status to \"" + tweet + "\"");
			System.out.println("Type in 'stop' to stop the program at any time...");
			System.out.println();
			
			setOutputStreamToLogFile(); // go back to the log file again
			
		} catch (TwitterException e) {
			
			System.out.println("Unable to update Status...");
			setOutputStreamToConsole();
			System.out.println("Unable to update Status...");
			System.out.println("Type in 'stop' to stop the program at any time...");
			setOutputStreamToLogFile();
			
			e.printStackTrace();
			
		}
		
	}
	
	//checks for the logs files/directories
	public void checkForLogs() {
		
		//creates the log directory object
		File logDirectory = new File(this.logDirectoryLocation);
		
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
	
	//Start the logging system in the program
	public void startLoggingSystem() {
		
		//Print console message
		System.out.println("Logging System Loading...");
		 
		//Try and catch statement for the log file
		try {
			
			this.temporaryLogFile = File.createTempFile("runningLogs", ".tmp"); //Create temporary log file
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		//Try and catch statement
		try {
			
			this.loggingFilePrintStream = new PrintStream(this.temporaryLogFile); //Set the print stream of the program to the temp log file
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
			
		}
		
		System.out.println("Logs loaded. Switching from console to 'logs.txt' file..."); //print message about successful log file
		System.out.println();
		setOutputStreamToLogFile(); //set the print stream to the log file
		System.out.println("Successfully switched to 'logs.txt' file..."); //Print first message to log file
		
	}
	
	//Method to save the logs to the logs.txt file properly
	public void writeLogs() {
		
		//Print a log message
		System.out.println("Saving 'logs.txt' file...");
		
		//Set the scanner and print writer to null to prepare to use
		Scanner readLogs = null;
		FileWriter logWriter = null;
		
		//Try and catch statement for file writer
		try {
			
			logWriter = new FileWriter(this.logFileLocation, true);
			
		} catch (IOException e1) {
			
			System.out.println("Unable to set logWriter...");
			e1.printStackTrace();
			
		}
			
		
		//Try and catch statement
		try {
			
			readLogs = new Scanner(this.temporaryLogFile); //read the entirety of the temp log file into scanner
			
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			
		}
		
		//while the scanner has a next word, read it and print to the file logs.txt
		while(readLogs.hasNext()) {
			
			try {
				
				logWriter.write(readLogs.nextLine() + "\n");//write each line to the logs.txt file
				
			} catch (IOException e) {
				
				System.out.println("Unable to write log files to logs.txt...");
				e.printStackTrace();
				
			}
			
		}
		
		//try catch statement to close the print writer
		try {
			
			logWriter.close(); //close the print writer
			
		} catch (IOException e) {
			
			System.out.println("Unable to close the logWriter...");
			e.printStackTrace(); 
			
		} 
		
		setOutputStreamToConsole(); //go back to the console
		System.out.println("'logs.txt' file saved..."); //print message about successful save to console
		
	}
	
	//Set the output stream to the log file in the program
	public void setOutputStreamToLogFile() {
		
		System.setOut(this.loggingFilePrintStream);
		
	}
	
	//Set the output stream to the console in the program
	public void setOutputStreamToConsole() {
		
		System.setOut(this.consoleStream);
		
	}
	
	//inner class that is periodically called at every interval
	class StatusTaskTimer extends TimerTask {
		
		//method that is called every time the timer goes
		@Override
		public void run() {
			
			statusUpdate(); //call the status update every time the timer task is called
			
		}
		
	}

}
