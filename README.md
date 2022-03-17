# Santander Bank Account Balance Check

## Description
Console application for checking user account balance in Santander Bank. Application is an example implementation 
of ports and adapters architecture (aka. hexagonal architecture).

Application utilises web scrapping on Santander Bank login process and Santander Bank REST API for other calls.

## Usage
### Bash script to start the application
```shell
./run
```
or with run parameters
```shell
./run --login=123456789 --password="password123"
```

### Run parameters
- `--login=<number>`
- `--password=<string>`
- `--sms-code=<number>`
- `--http-debug=<boolean>`
- `--login-page-url=<string>`
- `--accounts-api-url=<string>`

### Run requirements
- jre 17

### Run requirements for development
- jdk 17
- Gradle 7.3.3
- Intellij Idea
- git

### Build project
```java
./gradlew build
```

### Run tests
```java
./gradlew test
```

### Build executable fat jar
```java
./gradlew shadowJar
```

### Run jar
```java
java -jar build/libs/balancecheckapp.jar --option1=value --option2=value
```

### References
- https://jmgarridopaz.github.io/content/articles.html
- https://aveuiller.github.io/about_design_patterns-dependency_injection.html
- https://dzone.com/articles/hexagonal-architecture-it-works
- https://vaadin.com/learn/tutorials/ddd/ddd_and_hexagonal
- https://jmgarridopaz.github.io/content/resources.html
- https://4programmers.net/Forum/Java/344100-kilka_pytan_o_cleanhexagonal_architectureports_adapters
