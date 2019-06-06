# deroche-bot-twitter
Created using [Twitter4j](http://twitter4j.org/en/), Java, and using Maven

[Twitter4j JavaDoc](http://twitter4j.org/javadoc/index.html) <br/>
[Twitter Developer Site](https://developer.twitter.com/en.html)

Details:
- First run creates /tweets and /logs directories
- One .txt file per /tweet in the tweets folder
- Log file inside /tweets/logs/logs.txt is created and logs are written there
- Deletion of any log files or directories is fine and they will be generated upon next run
- Other directories located inside of /tweets/* or /tweets/logs/* will be ignored
- Other files inside /tweets/logs/* will be ignored

Notes:
- Inside the deroche-bot-twitter root directory there needs to be a plain text file twitter4j.properties file
- To function it is required that you import the libraries downloaded from the Twitter4j site

### twitter4j.properties
debug = true
oauth.consumerKey =       // your key <br/>
oauth.consumerSecret =    // your secret <br/>
oauth.accessToken =       // your token <br/>
oauth.accessTokenSecret = // your token secret <br/>

### [My Twitter @dr_deroche](https://twitter.com/dr_deroche)
I created this so I could tweet random things automatically, but also practice writing code and using API.
