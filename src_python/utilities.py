import json
from pymongo import MongoClient

def save_to_json(custom_object):

    file = "../../output.json"
    try:
        with open(file) as f:
            data = json.load(f)
            data["tweets"].append(custom_object)
            f.close()

        with open(file, 'w') as f:
            json.dump(data, f, indent=2)
            f.close()
    except (Exception):
        print("failed to save a tweet!")

def save_to_txt(custom_object):
    file = "../../output/output.txt"
    with open(file, "a") as f:
        line = ""
        for value in custom_object.items():
            line+= (str(value[1].encode("utf-8"))[2:])[:-1]
            line+= "\t"
        f.write(line+"\n")

def save_to_mongo(collection_name,custom_object):

    # http://api.mongodb.com/python/current/tutorial.html

    client = MongoClient()
    db = client.twitterapitest
    collection = db[collection_name]

    result = collection.insert_one(custom_object).inserted_id
