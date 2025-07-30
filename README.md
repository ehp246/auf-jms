# Auf JMS

[![Java CI with Maven](https://github.com/ehp246/auf-jms/actions/workflows/build.yml/badge.svg)](https://github.com/ehp246/auf-jms/actions/workflows/build.yml)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=bugs)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=coverage)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=ehp246_auf-jms&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=ehp246_auf-jms)


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
