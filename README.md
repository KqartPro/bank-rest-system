# Запуск проекта

## Docker

1. Клонируйте репозиторий:

    ```
    https://github.com/KqartPro/bank-rest-system.git
    ```
2. Убедитесь, что .jar файл уже собран. Если нет — выполните:
    ```bash
    ./gradlew build
   ```

3. Файл должен появиться по пути:
    ```
    build/libs/BankRest-0.0.1-SNAPSHOT.jar
    ```
   Запустите приложение:
    ```bash
    docker-compose up --build
    ```

4. Приложение будет доступно по адресу:
    ```
    http://localhost:8080
    ```
6. В браузере введите: <br>
   <br>
   Приложение: http://localhost:8080.<br>
   Swagger документация: http://localhost:8080/swagger-ui/index.html#/

## Manual

1. Клонируйте репозиторий:

    ```
    git clone https://github.com/KqartPro/bitlab-final-project.git
    ```
2. Откройте проект и настройте доступ к базе данных в
   файле [application.properties](src/main/resources/application.properties), например для Postgres:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/bitlab_project
    spring.datasource.username=postgres
    spring.datasource.password=postgres
    ```
3. Перейдите в папку проекта и запустите:
    ```bash
    ./gradlew bootRun
    ```
   Для компьютеров с Windows используйте:
     ```bash
    gradlew bootRun
    ```
   Если вы открыли проект в IntelliJ IDEA вы можете использовать встроенную конфигурацию для запуска <br>
   <br>

4. Приложение будет доступно по адресу:
    ```
    http://localhost:8080
    ```
5. В браузере введите: <br>
   <br>
   Приложение: http://localhost:8080.<br>
   Swagger документация: http://localhost:8080/swagger-ui/index.html#/
