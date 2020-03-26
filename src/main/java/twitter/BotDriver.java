/*
 * Christopher DeRoche
 * Date Created: 5/25/2019
 * https://github.com/compact-disc
 * 
 * Driver class for DeRoche_Bot twitter bot
 */

package twitter;

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
	private String tweetsDirectoryLocation = "tweets"; //root tweets directory
	
	//private instance variables to use in the program
	private Scanner keyboardInput = new Scanner(System.in); //Scanner used to get any user inputs from the user
	private final int STATUS_CHARACTER_MAX = 280; //Max amount of characters that can be used in a single tweet
	private HashMap<Integer, Tweet> tweets = new HashMap<Integer, Tweet>(); //Hash map to load and store all the tweets
	private File directory = new File(tweetsDirectoryLocation); //Creates the file directory object for tweets
	private int intervalInMilliseconds; //rate of timer in milliseconds to use for timer
	
	//Logger class
	private TweetLogger log;
	
	//default constructor to start the driver when called from run
	public BotDriver() {
		
		log = new TweetLogger("DeRoche_Bot"); //create the log class
		log.info("Starting Bot Driver");
		
		//Sleep for 100 milliseconds so the logger can be properly created before calling other methods
		try {
			
			Thread.sleep(100);
			
		} catch (InterruptedException e) {
			
			log.severe("Thread sleep error");
			e.printStackTrace();
		}
		
		setTimers(); //set the timers for the bot
		checkForAllFilesAndDirectories(); //check for all the files and directories 
		loadTweets(); //load all the tweets into the map
		this.timer = new Timer(); //create new timer object
		timer.schedule(new StatusTaskTimer(), 30000, this.intervalInMilliseconds); //start timer for so many seconds, but wait 30 seconds before starting
		checkForStop(); //run this method until "stop" is typed in, then it will stop the program
		
	}
	
	//If at any time the user types in "stop" into the console, the program exits and saves properly
	private void checkForStop() {
		
		//string variable to collect the stop message from the user
		String stopMessage;
		
		//if the tweets hash map is empty call the shutdown method
		if(tweets.isEmpty()) {
			
			shutdownIfEmpty(); //call the shutdown method
			
		}
		
		//gather the stop message
		stopMessage = this.keyboardInput.next();
		
		//No matter the case, if stop is entered it will stop the program
		if(stopMessage.equalsIgnoreCase("stop")) {
			
			this.timer.cancel(); //cancel the timer when the program stops
			log.info("DeRoche Bot shutdown");
			System.exit(0); //exit the program
			
		}
		
	}
	
	//method to shutdown the program properly
	private void shutdownIfEmpty() {
		
		this.timer.cancel(); //cancel the timer when the program stops
		log.warning("There are no tweets to load");
		log.info("DeRoche Bot shutdown");
		System.exit(0); //exit the program
		
	}
	
	//Method to set the intervals for tweets
	private void setTimers() {
		
		int intervalInMinutes; //variable to collect the timer in minutes
		
		//While loop to gather the time in minutes and have minimum of 30 minutes
		do {
			
			//Line and gathering of data for timer
			System.out.print("Enter a time interval (in minutes) to tweet: ");
			intervalInMinutes = this.keyboardInput.nextInt();
			
		}while(intervalInMinutes <= 1); //keep running the loop if the timer is below 1
		
		//Convert the time entered from second to milliseconds for the timer
		this.intervalInMilliseconds = intervalInMinutes * 60000;
		
		log.info("Type in 'stop' to stop the program at any time");
		
	}
	
	//Method to load all the tweets in the folder to a hashmap
	@SuppressWarnings("resource")
	private void loadTweets() {

		//Variables for the method
		String tweet;
		int mapCounter = 0;

		//Takes the directory makes a list of all of files, then for loop goes over each file object
		for(File tweetFile : this.directory.listFiles()) {
			
			//If the file is actually a directory it skips it and tells you, also skips logs directory
			if(tweetFile.isDirectory()) {
				
				if(!tweetFile.getName().equals("logs")) {
					
					log.warning("'" + tweetFile.getName() + "'" + " is a directory, skipping");
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
					
					log.warning("'" + tweetFile.getName() + "'" + " is greater than " + this.STATUS_CHARACTER_MAX + " characters, skipping");
					
				}
				
				//Catches if it does not work and give error and shutdown
			} catch (FileNotFoundException e) {
				
				log.severe("Error Reading Files! Shutting Down");
				e.printStackTrace();
				System.exit(0);
				
			}
			
		}
		
		//If the map is not empty report that it # of tweets loaded successfully
		if(!this.tweets.isEmpty()) {
			
			log.info(mapCounter + " Tweets Loaded Successfully!");
			
		}else {
			
			checkForTweets();
			
		}
		
	}
	
	//Returns a random tweet from the hashmap
	private String getRandomStatusUpdate() {
		
		Random randomTweet = new Random();
		int tweetAtPosition = randomTweet.nextInt(this.tweets.size());
		
		return this.tweets.get(tweetAtPosition).getMessage();
		
	}
	
	//Method to tweet a new status to twitter takes a String parameter and that is the message the user entered
	@SuppressWarnings("unused")
	private void statusUpdate() {
		
		//Creates twitter object to connect
		Twitter twitter = TwitterFactory.getSingleton();
		
		//tweet is gathered using the getRandomStatusUpdate method
		String tweet = getRandomStatusUpdate();
		
		//tries to tweet and prints out the tweet if it works, or catches and tells you that it could not tweet
		//But it prints to the console and the log file
		try {
			
			Status status = twitter.updateStatus(tweet);
			
			log.info("Successfully updated the status to \"" + tweet + "\"");
			log.info("Type in 'stop' to stop the program at any time");
			
		} catch (TwitterException e) {
			
			log.severe("Unable to update Status...");
			
			e.printStackTrace();
			
		}
		
	}
	
	//Method to check if any tweets exists, if they do not then shutdown the bot because it can not tweet anything
	private void checkForTweets() {
		
		//If the map is empty report and exit the program because it can do nothing
		if(this.tweets.isEmpty()) {
			
			log.warning("There are no tweets loaded");
			
		}
		
	}
	
	//Check to see if the tweets directory is 
	private void checkTweetsDirectory() {
		
		//checks if it exists in the system and creates one if it does not
		if(!this.directory.exists()) {
			
			log.info("No tweets directory exists! Creating directory");
			this.directory.mkdir();
			
		}
		
	}
	
	private void checkForAllFilesAndDirectories() {
		
		//check if the tweets directory exists
		checkTweetsDirectory();
		
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
