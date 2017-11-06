import tweepy
import json
import pymongo
from src_python import utilities

# Initialize the API consumer keys and access tokens
consumer_key = ""
consumer_secret = ""

access_token = ""
access_token_secret = ""

# Authenticate tweepy using the keys
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

class TwitterListener(tweepy.StreamListener):

    print("Starting tweet collection...")
    def on_data(self, data):

        tweet_json = json.loads(data)

        # get any value using tweet_json[*name_of_field*]
        # see: https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/tweet-object for field names

        custom_object = {
            "id": tweet_json["id_str"],
            "created_at": tweet_json["created_at"],
            "text": tweet_json["text"],
            "by_user": tweet_json["user"]["id_str"]
        }

        save_method = "txt"

        if save_method == "json":
            # save to json
            utilities.save_to_json(custom_object)
        elif save_method == "txt":
            # save to txt
            utilities.save_to_txt(custom_object)
        elif save_method == "mongodb":
            # store in MongoDB
            utilities.save_to_mongo("tweets",custom_object)

        #else dont save at all
        print(data)

    def on_error(self, status):
        print(status)

if __name__ == '__main__':

    # Initialize the listener
    listener = TwitterListener()
    stream = tweepy.Stream(auth, listener)

    # Set the parameters for the stream listener
    topics = ["music,guitar,concerts"]
    langs = ["en", "el"]
    async = True
    encoding = "utf-8"
    # Create bounding boxes using http://boundingbox.klokantech.com/
    # Copy and paste CSV Raw inside an array as below
    geo_greece = [19.4897460938,34.7596661247,28.5205078125,41.7385284694]
    locations = geo_greece

    # Start Streaming
    # ATTENTION: using both location and topics doesn't work

    # by topic
    stream.filter(languages=langs, track=topics, async=async, encoding=encoding)
    # by location
    #stream.filter(languages=langs, locations=locations, async=async, encoding=encoding)