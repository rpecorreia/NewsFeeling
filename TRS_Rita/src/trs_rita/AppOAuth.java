
package trs_rita;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author Rita Correia
 */
public class AppOAuth {
    Twitter twitter;

    public AppOAuth() throws TwitterException {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        
        builder.setOAuthConsumerKey(Keys.consumerKey);
        builder.setOAuthConsumerSecret(Keys.consumerSecret);
        Configuration configuration = builder.build();
        
        TwitterFactory factory = new TwitterFactory(configuration);
        twitter = factory.getInstance();

        AccessToken atok= new AccessToken(
                Keys.accessToken, 
                Keys.accessTokenSecret
        );
        twitter.setOAuthAccessToken(atok);
    }
}
