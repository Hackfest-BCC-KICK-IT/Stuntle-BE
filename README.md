# Stuntle BE

### Team Description
```
BCC KICK IT
1. Aditya Nugraha Tarihoran - Hustler
2. Abdullah Fathan - Hipster
3. Vinncent Alexander Wong - Hacker
```

### Application Description

```
Stuntle is an application for stunting prevention 
in semi-urban areas to help health workers in 
semi-urban areas who are limited in 
monitoring the condition of mothers and 
children in their area to prevent stunting 
as early as possible by implementing 
technology to accelerate the process of 
discovering stunting symptoms that are 
difficult to see with the naked eye.
```

### How to run this application?

> Before proceed to step for running application, we would like you to know that this application is written with Java
> and Spring Boot. You need to install some environment too like Docker since we use Docker here for containerize our
> application.
1. Clone this repository
2. Start the dependencies that is needed by this application on `docker-compose.yaml` by typing command `docker-compose up -d`.
3. Go to `/src/main/resources` and you will see `application.yaml.example` there.
4. Copy entire content of `application.yaml.example` and make new file in that directory too with name `*.properties`(*however `application.properties` is most used one*)
5. Paste the content that you have copy to `*.properties` file and fill all the needed value in there(we provide some default value too but you can change it)
6. Go back to your project directory and you now can run the Spring Boot application by typing `./mvnw spring-boot:run`
7. If you see these logs, it means your application is successfully running(by the way, we use `Netty` as the web server here)
```
2024-01-18T17:42:49.369+07:00  INFO 164623 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 1 endpoint(s) beneath base path '/actuator'
2024-01-18T17:42:50.917+07:00  INFO 164623 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
2024-01-18T17:42:50.947+07:00  INFO 164623 --- [           main] bcc.stuntle.StuntleApplication           : Started StuntleApplication in 7.987 seconds (process running for 8.627)
```
8. Go rock with our code and happy code! :space_invader: :robot: :boom:

```
You can access the documentation of API with our Swagger UI.
Go to your web browser and type following URL:

http://localhost:8080/swagger/index.html.

Our application also need basic authentication so please 
make sure to provide the username and password that is needed. 

However to ease the usage of our application, 
you can type the default credential that we provide.
username: stuntle, password: stuntle.
```
