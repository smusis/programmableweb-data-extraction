package getData;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GetTweets {
	public static void main(String[] args) throws Exception {
		//getTwitterData();
		computeTwitterScore();
	}
	public static void getTwitterData() throws ClassNotFoundException, SQLException{
		String TWITTER_CONSUMER_KEY = "FcHQddhNaoqWi4aSx0PEkUSy9";
		String TWITTER_SECRET_KEY = "lXZxRCUcy93GmeRTYdWImPdWVN7DpilWO33Q4vJRYVRnZc8N2n";
		String TWITTER_ACCESS_TOKEN = "2486806796-t5ciOdXPqxqzibzdfMuX0CHhsVKVkukMORMmoyT";
		String TWITTER_ACCESS_TOKEN_SECRET = "cZurUOpsfX87013hnt9qs16Gy6Rj8Vx4EQSbVOAtMyHIe";

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		    .setOAuthConsumerKey(TWITTER_CONSUMER_KEY)
		    .setOAuthConsumerSecret(TWITTER_SECRET_KEY)
		    .setOAuthAccessToken(TWITTER_ACCESS_TOKEN)
		    .setOAuthAccessTokenSecret(TWITTER_ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
		    Query query = new Query("phraseapp");
		    query.setCount(1000);
		    //query.setSince("2012-01-01");
		    QueryResult result;
		    do {
		        result = twitter.search(query);
		        List<Status> tweets = result.getTweets();
		        System.out.println(tweets.size());
		        for (Status tweet : tweets) {
		            System.out.println("@" + tweet.getUser().getScreenName() + " - " + tweet.getText());
		            //System.exit(1);
		        }
		    } while ((query = result.nextQuery()) != null);
		    System.exit(0);
		} catch (TwitterException te) {
		    te.printStackTrace();
		    System.out.println("Failed to search tweets: " + te.getMessage());
		    System.exit(-1);
		}
	}
	
	//Compute Twitter score for 100 selected APIs
	public static void computeTwitterScore() throws IOException{
		final BufferedReader br = new BufferedReader(new FileReader("E:/Research Projects/ICSM 2014 project/Data/TwitterSentiments.txt")); 
		String line; 
		while ((line = br.readLine()) != null) {
			int score=0;
			float tweets=0;
			String[] split=line.split("	");
			for(int i=0;i<split.length;i++){
				if(split[i].equals("NEG")){
					score=score+1;
					tweets=tweets+1;
				}
				if(split[i].equals("N")){
					score=score+2;
					tweets=tweets+1;
				}
				if(split[i].equals("P")){
					score=score+3;
					tweets=tweets+1;
				}
			}
			if(tweets!=0){
			System.out.println(score/tweets);}
			else{
				System.out.println("2.0");
			}
				
		}
	}
}
