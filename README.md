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
