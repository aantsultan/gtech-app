# GTech Application

Application is build for Transfer Module. 

Requirement :
1. Java 25
2. SpringBoot 4.0.2
3. Intellij IDEA
4. Maven 4.x

This project contain unit test. 

Please run the code in Intellij to run project easier by using Maven Wrapper

Use this command to test by using Jacoco

Compile native using Graal

```
 mvn -Pnative native:compile
```

Create docker from DockerFile

```
 docker build -t gtech-app:latest .
```

Run docker

```
 docker run -p 8080:8080 gtech-app:latest
```