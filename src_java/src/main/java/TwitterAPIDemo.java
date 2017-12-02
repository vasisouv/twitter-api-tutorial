import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import twitter4j.*;
import twitter4j.TwitterStream;
import twitter4j.conf.ConfigurationBuilder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

final class TwitterAPIDemo {

    public static void main(String[] args) throws TwitterException, JSONException {

        // Creating a ConfigurationBuilder object to authenticate our application
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("")
                .setOAuthConsumerSecret("")
                .setOAuthAccessToken("")
                .setOAuthAccessTokenSecret("");

        // Utilizing the official Twitter4j examples
        // more @ https://github.com/yusuke/twitter4j/tree/master/twitter4j-examples/src/main/java/twitter4j/examples

        String username = "Aristoteleio";

        // Get the entire timeline of a specific user
        /*List<String> userTweets = getTweetsOfUser(username,cb);
        for (String tweet : userTweets){
            System.out.println(tweet);
        }
        // Get Tweets using the Twitter Streaming API
        String[] keywords = {"music,guitar,concert"};
        getTweetsFromStream(keywords,cb);

        // Get User by screen name
        User user = getUserByScreenName(username,cb);
        if (user != null){
            System.out.println("This user's id is "+String.valueOf(user.getId()));
        }*/

        // Get a user's network
        /*JSONObject userNetwork = getUserNetwork(username,cb);

        JSONArray friends = userNetwork.getJSONArray("friendsIDs");
        JSONArray followers = userNetwork.getJSONArray("followersIDs");

        System.out.println("Showing this person's friends");
        for (int i=0 ; i<friends.length() ; i++){
            System.out.println(friends.get(i).toString());
        }
        System.out.println("Showing this person's followers");
        for (int i=0 ; i<followers.length() ; i++){
            System.out.println(followers.get(i).toString());
        }
        System.out.println("This user has a total of "+String.valueOf(friends.length())+" friends" +
                " and "+String.valueOf(followers.length())+" followers on Twitter");*/

    }
    private static JSONObject getUserNetwork(String username,ConfigurationBuilder cb) throws JSONException {

        JSONObject obj = new JSONObject();

        obj.put("username", username);

        Twitter api = new TwitterFactory(cb.build()).getInstance();

        List<String> friendsIDs = new ArrayList<String>();
        List<String> followersIDs = new ArrayList<String>();

        // Getting the friends IDs
        try {
            long cursor = -1;
            IDs ids;
            do {
                RateLimitStatus status = api.getRateLimitStatus().get("/friends/ids");
                checkLimit(status);

                ids = api.getFriendsIDs(username,cursor);
                for (long id : ids.getIDs()) {
                    //System.out.println(id);
                    friendsIDs.add(String.valueOf(id));
                }
                System.out.println("Friends so far : "+String.valueOf(friendsIDs.size()));
            } while ((cursor = ids.getNextCursor()) != 0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get friends' ids: " + te.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Getting the followers IDs
        try {
            long cursor = -1;
            IDs ids;
            do {
                RateLimitStatus status = api.getRateLimitStatus().get("/followers/ids");
                checkLimit(status);

                ids = api.getFollowersIDs(username,cursor);
                for (long id : ids.getIDs()) {
                    followersIDs.add(String.valueOf(id));
                }
                System.out.println("Followers so far : "+String.valueOf(followersIDs.size()));
            } while ((cursor = ids.getNextCursor()) != 0);
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get followers' ids: " + te.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        obj.put("friendsIDs", friendsIDs);
        obj.put("followersIDs",followersIDs);
        return obj;
    }
    private static User getUserByScreenName(String username,ConfigurationBuilder cb){

        Twitter api = new TwitterFactory(cb.build()).getInstance();
        try {

            User user = api.showUser(username);
            if (user.getStatus() == null) {
                // the user is protected or non-existent
                return null;
            }
            return user;
        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get user: " + te.getMessage());
            return null;
        }
    }
    private static List<String> getTweetsOfUser(String username, ConfigurationBuilder cb){
        // gets Twitter instance with default credentials
        Twitter api = new TwitterFactory(cb.build()).getInstance();
        List<String> tweets = new ArrayList<String>();

        try {
            List<Status> statuses;

            int pageNum = 1;
            Paging paging = new Paging(pageNum,1000);
            do{
                statuses = api.getUserTimeline(username,paging);
                for (Status status : statuses) {
                    String tweetText = status.getText();
                    tweets.add(tweetText);
                }
                pageNum+=1;
                paging.setPage(pageNum);

                System.out.println("Fetched "+String.valueOf(tweets.size())+"tweets so far");
            }while (statuses.size() != 0);

        } catch (TwitterException te) {
            te.printStackTrace();
            System.out.println("Failed to get timeline: " + te.getMessage());
        }
        return tweets;
    }
    private static void getTweetsFromStream(String[] keywords,ConfigurationBuilder cb){

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status)
            {
                System.out.println(status.getText());

//                BasicDBObject obj = new BasicDBObject();
//                obj.put("id",status.getId());
//                obj.put("created_at",status.getCreatedAt());
//                obj.put("text",status.getText());
//                obj.put("by_user",status.getUser().getId());
//
//                try {
//                    saveToMongo("tweets",obj);
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                }
            }
            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }
            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }
            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }
            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        FilterQuery fq = new FilterQuery();
        fq.language("en");

        // keywords
        //fq.track(keywords);

        // location
        // use this tool to create bounding boxes: http://boundingbox.klokantech.com/
        double[] geoGreeceW = {20.26,34.87};
        double[] geoGreeceE = {27.16,41.29};
        double[][] location = {geoGreeceW,geoGreeceE};
        fq.locations(location);

        // ATTENTION: using both location and keywords filtering does not work.
        twitterStream.addListener(listener);
        twitterStream.filter(fq);
    }
    private static void checkLimit(RateLimitStatus status) throws InterruptedException {
        if (status.getRemaining() == 0){
            // the api is rate limiting the application
            int timeRemaining = status.getSecondsUntilReset();
            System.out.println("Rate limit reached! Should wake up in "+String.valueOf(timeRemaining/60)+" minutes!");

            Thread.sleep(1000 * timeRemaining);
            System.out.println("Waking up and resuming!");
        }
    }
    private static void saveToMongo(String collectionName,BasicDBObject obj) throws UnknownHostException {

        // http://www.mkyong.com/mongodb/java-mongodb-hello-world-example/
        MongoClient mongo = new MongoClient( "localhost" , 27017 );
        DB db = mongo.getDB("twitterapitest");

        DBCollection table = db.getCollection(collectionName);
        table.insert(obj);
    }
}