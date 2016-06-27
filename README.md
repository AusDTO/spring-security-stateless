# spring-security-stateless

[![CircleCI](https://circleci.com/gh/AusDTO/spring-security-stateless.svg?style=shield)](https://circleci.com/gh/AusDTO/spring-security-stateless) [![License](https://img.shields.io/github/license/AusDTO/spring-security-stateless.svg)](https://github.com/AusDTO/spring-security-stateless/blob/master/LICENSE) [![JitPack](https://jitpack.io/v/AusDTO/spring-security-stateless.svg)](https://jitpack.io/#AusDTO/spring-security-stateless)

## tl;dr

Use Spring Security for authentication in your JVM-based application without using HttpSession, eliminating server-side non-persistent user session state.

## Explanation

[HttpSession](http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpSession.html) is standard way to maintain user state in server memory between requests in Java-based web applications. They are easy to use as long as certain precautions are taken, and many frameworks and libraries are based on using HttpSession. If your application takes care of its own authentication (rather than delegating to some access manager reverse proxy), it most likely uses HttpSession to track users' authentication state.

From an operational point of view, using sessions in server memory is problematic since it typically means that each user is must be pegged to the application instance that maintains their session. This is typically implemented using "sticky sessions" or "session persistence" at the load balancer level based on the `JSESSIONID` cookie.

Performing application deployments without disrupting end users becomes trickier even if techniques such as blue-green deployments are used. An instance can't be stopped until it no longer maintains any user sessions. Solutions exist, such as session replication or persisting user sessions in a database, but these have other drawbacks which means that operations are not necessarily simplified.

This library allows you to use [Spring Security](http://projects.spring.io/spring-security/) for authentication without using HttpSession. Authentication state is kept in an encrypted and signed cookie instead. Specificially, this library provides cookie-based implementations of `SecurityContextRepository`, `CsrfTokenRepository`, and `RequestCache`. These can be used all together or independently.

See the sample Spring Boot application in the `src/sampleapp` directory for an example of an application using all three implementations and not using HttpSession at all.

## Requirements

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or later
- [Java Cryptography Extension unlimited strength policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- [Servlet API 3.0](http://download.oracle.com/otndocs/jcp/servlet-3.0-fr-eval-oth-JSpec/) or later
- [Spring Security](http://projects.spring.io/spring-security/) 3.2.0 or later

## Add to your build

Check the [Releases](spring-security-stateless/releases) tab for the latest version. 

### Gradle

Add `https://jitpack.io` as a repository to your Gradle build file (likely `build.gradle`):

    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }

Then add the dependency:

    dependencies {
        ...
        compile 'com.github.AusDTO:spring-security-stateless:1.1.0'
    }

### Maven

Add `https://jitpack.io` as a repository to your Maven POM file (likely `pom.xml`):


    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

Then add the dependency:

    <dependency>
        <groupId>com.github.AusDTO</groupId>
        <artifactId>spring-security-stateless</artifactId>
        <version>1.1.0</version>
    </dependency>

### sbt or Leiningen

See <https://jitpack.io/> for instructions

## How to use

Instructions coming soon. In the meantime, see the example application in the directory `src/sampleapp`.

### Generating a key for encrypting and signing 

To generate a key for encrypting and signing use this command on Unix and macOS:

    openssl rand 32 | base64

Alternatively you can use the following Java code:
 
    java.security.SecureRandom secureRandom = new java.security.SecureRandom();
    byte[] key = new byte[32];
    secureRandom.nextBytes(key);
    String encodedKey = java.util.Base64.getEncoder().encodeToString(key);
    System.out.println(encodedKey);

### API documentation

[Javadoc](https://jitpack.io/com/github/AusDTO/spring-security-stateless/v1.1.0/javadoc/index.html)
