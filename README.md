# DeRocheBotTwitter

This project was created to practice using API from another party, in this case, Twitter. DeRoche Bot gave additional practice with timers/timing in java along with file reading and writing using some of the included java libraries. Keeping the code well documented allows for easy future expansion and review of previously written code.

Created using [Twitter4j](http://twitter4j.org/en/), Java, and using Gradle

[Twitter4j JavaDoc](http://twitter4j.org/javadoc/index.html) <br/>
[Twitter Developer Site](https://developer.twitter.com/en.html)

Details:
- Console output is written to log files
- When the program starts you can enter the interval of tweeting
- At any point in running the program if you type "stop" the program will properly stop and write the logs
- Checks tweet files for character limits for twitter (including the "DeRoche Bot: ") tag added to tweets
- Skips tweet files that are too long or zero
- Chooses tweet randomly from the Hash Map of tweets using a random integer (so possible duplicates)

Notes:
- First run creates /tweets directory
- One .txt file per /tweet in the tweets folder
- Deletion of any directories is fine and they will be generated upon next run
- Other directories located inside of /tweets/* will be ignored
- Inside the deroche-bot-twitter root directory there needs to be a plain text file twitter4j.properties file
- To function it is required that you import the libraries downloaded from the Twitter4j site

### twitter4j.properties
debug=true <br/>
oauth.consumerKey=       // your key <br/>
oauth.consumerSecret=    // your secret <br/>
oauth.accessToken=       // your token <br/>
oauth.accessTokenSecret= // your token secret <br/>

### [My Twitter @dr_deroche](https://twitter.com/dr_deroche)
