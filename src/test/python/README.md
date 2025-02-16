## Как запустить
Установить locust  
```
pip install psycopg2
pip install locust
```
Проверка установки 
```
locust -V
```
Запуск
```
locust -f src/test/python/load_testing.py --host=<your host>
```