from locust import HttpUser, task, between
import random
import uuid

users = []
items = ['cup', 'pen', 'socks']


class UserBehavior(HttpUser):
    wait_time = between(1, 5)

    def __init__(self, parent):
        super().__init__(parent)
        self.username = None
        self.token = ""

    def on_start(self):
        self.username = str(uuid.uuid4())
        while self.username in users:
            self.username = str(uuid.uuid4())
        self.token = self.client.post("/api/auth", json={
            "username": self.username,
            "password": "password"
        }).json().get("token")
        self.client.headers.update({"Authorization": f"Bearer {self.token}"})
        users.append(self.username)

    @task(10)
    def get_info(self):
        self.client.get("/api/info", headers={"Authorization": f"Bearer {self.token}"})

    @task(1)
    def send_coin(self):
        if len(users) == 1:
            return
        to_user = random.choice(users)
        while to_user == self.username:
            to_user = random.choice(users)
        self.client.post("/api/sendCoin", json={
            "toUser": to_user,
            "amount": 1
        })

    @task(1)
    def buy_item(self):
        self.client.get(f"/api/buy/{random.choice(items)}")