# Auf JMS

[![Sonatype Central](https://maven-badges.sml.io/sonatype-central/me.ehp246/auf-jms/badge.svg?style=flat-square)](https://central.sonatype.com/artifact/me.ehp246/auf-jms)

## Introduction
Auf JMS is aimed at <a href='https://spring.io/'>Spring</a>-based applications that need to implement a messaging architecture on top of JMS brokers. It offers an annotation-driven and declarative programming model that abstracts away low-level JMS API's by offering a set of annotations and conventions with which application developers declare the intentions via plain Java classes and provided annotations. 

## Quick Start

Assuming you have a Spring Boot application ready, add dependency:

* [Auf JMS](https://mvnrepository.com/artifact/me.ehp246/auf-jms)

### Client Application

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

At this point, you have a JMS client proxy that when invoked will send a message
* to a queue named by Spring property `app.task.inbox`
* with the message type of `RunJob`
* with the message body of `job` serialized in JSON

The proxy won't do anything by itself, so the next step is to...

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

**To send to a topic**

```java
@ByJms(@To(value = "${app.task.status}", type = DestinationType.TOPIC))
public interface TaskStatus {
    void updateJobStatus(@OfProperty String jobId, Status status);
}
```

### Server Application

**Enable by `@EnableForJms`.**

```java
@EnableForJms(value = @Inbound(@From("${app.task.inbox}")))
@SpringBootApplication
class ServerApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }
}
```

**Implement business logic by JMS type**

```java
@ForJmsType
class RunJob {
    public void invoke(Job job) {
        //Do the work
    }
}
```

## Runtime
The latest major version 3 requires the following to run:
* <a href='https://openjdk.org/projects/jdk/21/'>JDK 21</a>
* <a href='https://jakarta.ee/specifications/messaging/3.1/'>JMS 3.1</a>
* <a href='https://mvnrepository.com/artifact/org.springframework'>Spring Framework 6.2</a>
* <a href='https://mvnrepository.com/artifact/com.fasterxml.jackson'>Jackson 2</a>: Core and Databind

In addition to the above, the server-side features provided by `@EnableForJms` requires:
* <a href='https://mvnrepository.com/artifact/org.springframework/spring-jms'>Spring JMS</a>

## Release
The release binaries can be found on [Maven Central](https://mvnrepository.com/artifact/me.ehp246/auf-jms).

### Version 2
The version 2 releases are on JDK 17, JMS 3.1, Spring 6.

### Version 1
The version 1 releases are on <a href='https://javaee.github.io/jms-spec/pages/JMS20FinalRelease'>JMS 2.0</a> and Spring 5.
