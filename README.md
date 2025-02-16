# Запуск приложения  
1. Установите Java 17 версии и Docker
2. Склонируйте репозиторий и перейдите в папку с проектом  
```
git clone https://github.com/sivalien/merch_store.git
cd <your path to project>
```
2. C помощью команды `export <env>=<value>` установите 
необходимые переменные окружения:
- `MERCH_STORE_DB_USERNAME`
- `MERCH_STORE_DB_PASSWORD`
- `MERCH_STORE_DB_NAME`
- `JWT_KEY` - secret key для Jwt-авторизации 
3. Собрать приложение
```
./gradlew build
```
4. Разверните приложение с помощью docker-compose
```
docker compose up
```

# Тесты
## Юнит и интеграционные тесты
Для запуска интеграционных и юнит тестов введите команду
```
./gradlew test
```

## Проверка тестового покрытия
Для проверки требований тестового покрытия (>40%) 
введите команду:
```
./gradlew jacocoTestCoverageVerification
```
Для того, чтобы получить подробный отчет о тестовом покрытии 
введите команду  
```
./gradlew jacocoTestReport
```
И откройте файл 
`merch_store/build/reports/jacoco/test/html/index.html`
## Нагрузочное тестирование  
###  Результаты  
Приложение удовлетворяет требованиям по 
нагрузке. Подробный отчет о нагрузочном 
тестировании на 250 (отправляют запросы с интервалом 0.2-0.3 секунды) активных пользователей можно в файле 
`src/test/python/report/load_testing_250.html` 
и на 450 активных пользователей
(отправляют запросы с интервалом 0.4-0.6 секунд)
`src/test/python/report/load_testing_450.html`

### Запуск
Установить python, locust и psycopg2
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
locust -f src/test/python/load_testing.py --host=<your host like http://localhost:8080>
```
В консоле появится приглашение перейти по 
`http://0.0.0.0:8089`, там вы сможете задать 
количество пользователей для нагрузочного 
тестирования и увидеть подробный отчет о ходе 
тестирования.