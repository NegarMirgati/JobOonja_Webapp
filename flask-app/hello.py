from flask import Flask, jsonify, make_response
from num2fawords import words, ordinal_words
import random
import time, threading
import json
import datetime
import requests
from wonderwords import RandomSentence
from prompt_gpt import generate_project_description

app = Flask(__name__)


@app.route("/")
def hello():
    return make_response("Hello World!!!", 200)


@app.route("/project")
def project():
    projects = app.config["p_handler"].get_projects()
    return make_response(projects, 200)


@app.route("/skill")
def skill():
    skills = app.config["p_handler"].get_skills()
    return make_response(skills, 200)


class Project:
    def __init__(
        self,
        id,
        title,
        description,
        imageURL,
        budget,
        creation_date,
        deadline,
        skills,
        scores,
    ):
        self.title = title
        self.id = id
        self.description = description
        self.creation_date = creation_date
        self.deadline = deadline
        self.image_url = imageURL
        self.budget = budget
        self.skills = []
        for skill, score in zip(skills, scores):
            self.skills.append({"name": skill, "point": score})


class ProjectHandler:
    """A simple example class"""

    def __init__(self):
        self.i = 0
        self.projects = []
        self.skills = [
            "Linux",
            "SEO",
            "Java",
            "Python",
            "C++",
            "C#",
            "Unity",
            "Javascript",
            "HTML",
            "C",
            "Photoshop",
        ]

    def create_project(self):
        self.i += 1
        project_title = f"پروژه {words(self.i)}"
        # s = RandomSentence()
        # project_description = s.sentence()
        num_skills = random.randint(1, len(self.skills) - 5)
        project_skills = random.sample(self.skills, num_skills)
        project_description = generate_project_description(",".join(project_skills))
        # Example usage
        placehorder_url = self.get_random_image_url()
        response = requests.get(placehorder_url)
        img_url = response.__dict__["url"]
        skills_scores = random.choices(range(1, 10), k=num_skills)
        budget = random.randint(100000, 100000000)
        creation_date = datetime.datetime.now()
        dt_obj = creation_date + datetime.timedelta(hours=random.randint(1, 4))
        creation_date = int(creation_date.timestamp() * 1000)
        deadline = int(dt_obj.timestamp() * 1000)
        new_project = Project(
            self.i,
            project_title,
            project_description,
            img_url,
            budget,
            creation_date,
            deadline,
            project_skills,
            skills_scores,
        )
        self.projects.append(new_project)
        print(new_project)

    def start_project_cycle(self):
        print("time: ", time.ctime())
        self.create_project()
        threading.Timer(60, self.start_project_cycle).start()

    def get_projects(self):
        # return json.dumps()
        mapping = {"image_url": "imageUrl", "creation_date": "creationDate"}
        return json.dumps(
            self.projects,
            default=lambda x: {mapping.get(k, k): v for k, v in x.__dict__.items()},
        )

    def get_skills(self):
        return json.dumps(
            self.skills,
        )

    def get_random_image_url(self):
        # Request a random image of size 400x300
        url = "https://picsum.photos/240/240"
        return url


with app.app_context():
    p_handler = ProjectHandler()
    app.config["p_handler"] = p_handler
    p_handler.start_project_cycle()


def runFlaskApp():
    app.run(host="127.0.0.1", port=5002, debug=False, threaded=True)


if __name__ == "__main__":
    # Executing the Threads seperatly.
    t = threading.Thread(target=runFlaskApp)
    t.start()
