package com.github.opticyclic.typesafe;

import java.io.File;
import java.nio.file.Paths;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configs {
  private static final Logger log = LoggerFactory.getLogger(Configs.class);

  private Configs() {
    //no-op private constructor to force use of Builder pattern
  }

  public static Configs.Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Config conf = ConfigFactory.empty();

    private Builder() {
      log.info("Loading configs first row is highest priority, second row is fallback and so on");
    }

    /**
     * This config has access to all of the environment variables
     *
     * @see ConfigFactory#systemEnvironment()
     */
    public Builder withSystemEnvironment() {
      conf = conf.withFallback(ConfigFactory.systemEnvironment());
      log.info("Loaded system environment into config");
      return this;
    }

    /**
     * This config has all of the JVM system properties including any custom -D properties.
     * System Properties are cached on first use
     *
     * @see ConfigFactory#systemProperties()
     */
    public Builder withSystemProperties() {
      conf = conf.withFallback(ConfigFactory.systemProperties());
      log.info("Loaded system properties into config");
      return this;
    }

    public Builder withOptionalFile(String path) {
      File secureConfFile = new File(path);
      if(secureConfFile.exists()) {
        log.info("Loaded config file from path ({})", path);
        conf = conf.withFallback(ConfigFactory.parseFile(secureConfFile));
      } else {
        log.info("Attempted to load file from path ({}) but it was not found. Working directory is ({})", path, Paths.get(".").toAbsolutePath().normalize().toString());
      }
      return this;
    }

    public Builder withOptionalHomeDirFile(String path) {
      return withOptionalFile(getUserDirectory() + path);
    }

    private String getUserDirectory() {
      File homeDir = new File(System.getProperty("user.home"));
      return homeDir.getAbsolutePath();
    }

    public Builder withResource(String resource) {
      Config resourceConfig = ConfigFactory.parseResources(resource);
      String empty = resourceConfig.entrySet().size() == 0 ? " contains no values" : "";
      conf = conf.withFallback(resourceConfig);
      log.info("Loaded config file from resource ({}){}", resource, empty);
      return this;
    }

    public Builder withConfig(Config config) {
      conf = conf.withFallback(config);
      return this;
    }

    public Config build() {
      // Resolve substitutions.
      conf = conf.resolve();
      if(log.isDebugEnabled()) {
        log.debug("Logging properties. Make sure sensitive data such as passwords or secrets are not logged!");
        log.debug(conf.root().render());
      }
      return conf;
    }
  }
}
