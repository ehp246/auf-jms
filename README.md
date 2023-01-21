# Auf JMS

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-jms/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/me.ehp246/auf-jms)

## Introduction
Auf JMS is aimed at <a href='https://spring.io/'>Spring</a>-based applications that need to implement a messaging archiecture on top of a JMS broker. It offers an annotation-driven and declarative programming model similar to  <a href='https://docs.spring.io/spring-data/commons/docs/current/reference/html/#repositories'>Spring Data Repositories</a>. Auf JMS abstracts away low-level JMS API's by offering a set of annotations and conventions with which application developers declare the intentions via plain Java interfaces and provided annotations. 

## Quick Start

Assuming you have a Spring Boot application ready, add dependency:

* [Auf REST](https://mvnrepository.com/artifact/me.ehp246/auf-jms)

### On client applications

**Enable by `@EnableByJms`.**

```
@EnableByJms
@SpringBootApplication
class ClientApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
```

**Declare by `@ByJms`.**

```
@ByJms(@To("${app.task.inbox}"))
public interface TaskInbox {
    void runJob(Job job);
}
```

At this point, you have a JMS client that when invoked will send a message
* to a queue named by Spring property `app.task.inbox`
* with the message type of `RunJob`
* with the message body of `job` serialized in JSON

The client won't do anything by itself, so the next step is to...

**Inject and enjoy.**

```
@Service
public class AppService {
    // Do something with it
    @Autowired
    private TaskInbox taskInbox;
    ...
}
```

<br>
The following are a few more examples.

**To send to a topic**

```java
@ByJms(@To(value = "${app.task.status}", type = DestinationType.TOPIC))
public interface TaskStatus {
    void updateJobStatus(@OfProperty String jobId, Status status);
}
```

## Runtime
The latest version requires the following to run:
* <a href='https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core'>Log4j 2</a>
* <a href='https://mvnrepository.com/artifact/com.fasterxml.jackson'>Jackson 2</a>: Core and Databind
* <a href='https://mvnrepository.com/artifact/org.springframework'>Spring 6</a>: Bean, Context
* <a href='https://openjdk.org/projects/jdk/17/'>JDK 17</a>
### Servers
To use the server-side features provided by `@EnableForJms`:
* <a href='https://mvnrepository.com/artifact/org.springframework'>Spring 6</a>: JMS

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-jms).
