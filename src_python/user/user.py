import tweepy
import json
import pymongo
from src_python import utilities

# Initialize the API consumer keys and access tokens
consumer_key = "SORXfCgvpS3wdbMRRNtc2qzfB"
consumer_secret = "GBJkn5LCRuqvHqnogOdJJYKS4lNqVKiTWtOL0xlBKzD3p6uYFY"

access_token = "599269165-blChAStgzrWo9TIPcoUHiqhJTf7RqLxz090HnWjF"
access_token_secret = "RjdUeLOkemTPLObB6dLDsdfwSQk3joyqDKer94g27Qi9J"

# Authenticate tweepy using the keys
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

api = tweepy.API(auth, wait_on_rate_limit=True, wait_on_rate_limit_notify=True, compression=True)

def get_user(user_id):

    print("Searching full information for user with id " + str(user_id))
    try:
        user_json = api.get_user(user_id)
    except tweepy.TweepError as tweep_error:
        print("Error with code : " + str(tweep_error.response.text))
        return 0
    return user_json

def get_user_tweets(user_id):

    timeline = []
    progress = 0
    statuses = []
    for status in tweepy.Cursor(api.user_timeline, id=user_id).items():
        timeline.append(status)
        progress+=1
        print("Fetched "+str(progress)+" out of all timeline items")

    return statuses

def get_user_network(user_id):

    print("Searching network for user with id " + str(user_id))

    followers = []
    friends = []
    max_followers = 100000
    max_friends = 100000
    try:
        for page in tweepy.Cursor(api.followers_ids, id=user_id).pages():
            followers.extend(page)
            if len(followers) >= max_followers:
                break
            print("Followers so far : " + str(len(followers)))
        print("finished followers")
        for page in tweepy.Cursor(api.friends_ids, id=user_id).pages():
            friends.extend(page)
            if len(friends) >= max_friends:
                break
            print("Friends so far : " + str(len(friends)))
        print("finished friends")
    except tweepy.TweepError as tweep_error:
        print("Error with code : " + str(tweep_error.response.text))
        return 0
    print("User with ID: " + user_id + " has " + str(len(followers)) + " followers and " + str(len(friends)) + " friends")
    custom_object = {
        "id": user_id,
        "followers": followers,
        "friends": friends
    }
    return custom_object

if __name__ == '__main__':

    # Aristotle University's Twitter user ID
    user_id = "234343780"
    # Tom Hanks's user_id
    #user_id = "50374439"

    ### Get the entire timeline of tweets and retweets of a user ###
    # statuses = get_user_tweets(user_id)
    # for status in statuses:
    #     print (status._json["text"])

    #### Get full information about the user ###
    # user_json = get_user(user_id)

    # Access all the information using .*field*
    # https://developer.twitter.com/en/docs/tweets/data-dictionary/overview/user-object

    # screen_name = str(user_json.screen_name)
    # followers_count = str(user_json.followers_count)
    # friends_count = str(user_json.friends_count)
    #
    # print ("This user has the screen name: "+screen_name)
    # print ("This user has "+followers_count+" followers")
    # print ("This user has "+friends_count+" friends")

    #### Get the network (friends, followers) of the user ###
    network = get_user_network(user_id)
    print(network["friends"])
    print(network["followers"])


