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

### Product Screenshot
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/b1784b9c-8538-42c6-890a-6fdad472fb4c)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/bad04edf-de4f-4529-b0c0-91ae2b1d949f)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/9d96076e-3ebd-486f-be4c-d6790eef006a)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/715aa1d3-a684-4efa-9fbf-eebef9f0bd6d)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/16c44706-35b4-4a50-ba0c-7cbbcf440687)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/ff2a840b-c467-4a45-aac5-723835dea163)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/55479b56-1dd9-4a92-b567-3ff06abdd268)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/853e42e6-a91b-42df-b69a-52a7de64688a)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/773b946f-a5f9-422b-a5fc-3834fe61ce97)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/4c542392-adf5-49d9-a466-7adff61ba9b0)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/cd4c9dbf-62d7-4fde-9c50-17c6fd386b8e)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/a67b4b0e-b419-4e22-900d-2019a02a7a1f)
![image](https://github.com/Hackfest-BCC-KICK-IT/Stuntle-BE/assets/88434717/c71cbfdf-c552-433b-8088-ce73d502faff)
