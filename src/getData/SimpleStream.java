package getData;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class SimpleStream {
    public static void main(String[] args) {
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

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();

        StatusListener listener = new StatusListener() {

            @Override
            public void onException(Exception arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStatus(Status status) {
                User user = status.getUser();
                
                // gets Username
                String username = status.getUser().getScreenName();
                System.out.println(username);
                String profileLocation = user.getLocation();
                System.out.println(profileLocation);
                long tweetId = status.getId(); 
                System.out.println(tweetId);
                String content = status.getText();
                System.out.println(content +"\n");

            }

            @Override
            public void onTrackLimitationNotice(int arg0) {
                // TODO Auto-generated method stub

            }

			@Override
			public void onStallWarning(StallWarning arg0) {
				// TODO Auto-generated method stub
				
			}

        };
        FilterQuery fq = new FilterQuery();
    
        String keywords[] = {"phraseapp"};

        fq.track(keywords);

        twitterStream.addListener(listener);
        twitterStream.filter(fq);  

    }
}