package com.github.opticyclic.typesafe;

import com.typesafe.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigDemo {
  private static final Logger log = LoggerFactory.getLogger(ConfigDemo.class);

  public static void main(String[] args) {
    Config config = Configs.builder()
                      .withSystemEnvironment()
                      .withSystemProperties()
                      .withOptionalFile("application.prod.properties")
                      .withOptionalFile("application.uat.properties")
                      .withOptionalFile("application.dev.properties")
                      .withOptionalFile("application.properties")
                      .withResource("application.properties")
                      .build();

    log.info("The following properties come from 2 different files");
    log.info("Host = " + config.getString("host"));
    log.info("Port = " + config.getString("port"));
  }
}
