import os

from locust import HttpUser, task, between
import random
import psycopg2
import uuid

users = []
items = ['cup', 'pen', 'socks']


class UserBehavior(HttpUser):
    wait_time = between(0.4, 0.6)

    def __init__(self, parent):
        super().__init__(parent)
        self.username = None

    def on_start(self):
        self.username = str(uuid.uuid4())
        while self.username in users:
            self.username = str(uuid.uuid4())
        token = self.client.post("/api/auth", json={
            "username": self.username,
            "password": "password"
        }).json().get("token")
        self.client.headers.update({"Authorization": f"Bearer {token}"})
        users.append(self.username)

    def on_stop(self):
        try:
            conn = psycopg2.connect(
                dbname=os.environ.get('MERCH_STORE_DB_NAME'),
                user=os.environ.get('MERCH_STORE_DB_USERNAME'),
                password=os.environ.get('MERCH_STORE_DB_PASSWORD'),
                host="localhost",
                port="5432"
            )
            cursor = conn.cursor()

            cursor.execute("DELETE FROM user_inventory WHERE username = %s", (self.username,))
            cursor.execute("DELETE FROM history WHERE from_user = %s or to_user = %s", (self.username, self.username))
            cursor.execute("DELETE FROM user_balance WHERE username = %s", (self.username,))
            cursor.execute("DELETE FROM users WHERE username = %s", (self.username,))

            conn.commit()
            cursor.close()
            conn.close()

        except Exception as e:
            print(f"Ошибка при удалении данных пользователя {self.username}: {e}")

    @task(8)
    def get_info(self):
        self.client.get("/api/info")

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
