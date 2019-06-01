# Typesafe Config Demo

This demonstrates how to use TypeSafe Config in a useful way for Docker applications.

It provides a flexible way to read config from multiple places with fallbacks.

With Docker you probably want:

* Environment variables
* Command line parameters
* File properties
* Classpath properties

The code is a modified version of this [Stubborn Java post](https://www.stubbornjava.com/posts/environment-aware-configuration-with-typesafe-config) and the associated [source code](https://github.com/StubbornJava/StubbornJava/blob/master/stubbornjava-common/src/main/java/com/stubbornjava/common/Configs.java).

**NOTE:** If you are using Spring you should [read how it is done there](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) .

## Running The Demo

There are 2 properties files checked in: 

* `application.prod.properties` in the project root
* `src/main/resources/application.properties` which is on the classpath at runtime

The `application.properties` file has a `host` and `port` property but the `application.prod.properties` file only has the `host` property.

When you run the demo you will see that the `application.prod.properties` file takes precedence for the `host` property and the `port` property falls back to the value in the  `application.properties` file.

