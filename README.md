# spring-security-stateless

[![CircleCI](https://img.shields.io/circleci/project/AusDTO/spring-security-stateless.svg?style=flat-square)](https://circleci.com/gh/AusDTO/spring-security-stateless) [![License](https://img.shields.io/github/license/AusDTO/spring-security-stateless.svg?style=flat-square)](https://github.com/AusDTO/spring-security-stateless/blob/master/LICENSE) [![JitPack](https://jitpack.io/v/AusDTO/spring-security-stateless.svg?style=flat-square)](https://jitpack.io/#AusDTO/spring-security-stateless)

Provides cookie-based implementations of SecurityContextRepository, CsrfTokenRepository, and RequestCache.

## Requirements

- [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or later
- [Java Cryptography Extension unlimited strength policy files](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
- [Servlet API 3.0](http://download.oracle.com/otndocs/jcp/servlet-3.0-fr-eval-oth-JSpec/) or later
- [Spring Security](http://projects.spring.io/spring-security/) 3.2.0 or later

## Add to your build

Check the Releases tab for the latest version. 

### Gradle

Add `https://jitpack.io` as a repository to your Gradle build file (likely `build.gradle`):

    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }

Then add the dependency:

    dependencies {
        ...
        compile 'com.github.AusDTO:spring-security-stateless:1.0.0'
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
        <version>1.0.0</version>
    </dependency>

### sbt or Leiningen

See <https://jitpack.io/> for instructions

## How to use

Instructions coming soon. In the meantime, see the example application in the directory `src/sampleapp`.
