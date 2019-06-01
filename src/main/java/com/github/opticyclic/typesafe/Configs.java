package com.github.opticyclic.typesafe;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configs {
  private static final Logger log = LoggerFactory.getLogger(Configs.class);

  private Configs() {
    //no-op private constructor to force use of Builder pattern
  }

  public static Configs.Builder newBuilder() {
    return new Builder();
  }

  // This config has access to all of the environment variables
  private static final Config systemEnvironment = ConfigFactory.systemEnvironment();

  // This config has all of the JVM system properties including any custom -D properties
  private static final Config systemProperties = ConfigFactory.systemProperties();

  // Always start with a blank config and add fallbacks
  private static final AtomicReference<Config> propertiesRef = new AtomicReference<>(null);

  public static void initProperties(Config config) {
    boolean success = propertiesRef.compareAndSet(null, config);
    if(!success) {
      throw new RuntimeException("propertiesRef Config has already been initialized. This should only be called once.");
    }
  }

  public static Config properties() {
    return propertiesRef.get();
  }

  public static <T> T getOrDefault(Config config, String path, BiFunction<Config, String, T> extractor, T defaultValue) {
    if(config.hasPath(path)) {
      return extractor.apply(config, path);
    }
    return defaultValue;
  }

  public static <T> T getOrDefault(Config config, String path, BiFunction<Config, String, T> extractor, Supplier<T> defaultSupplier) {
    if(config.hasPath(path)) {
      return extractor.apply(config, path);
    }
    return defaultSupplier.get();
  }

  public static Map<String, Object> asMap(Config config) {
    Map<String, Object> map = new ConcurrentHashMap<>();
    config.entrySet().forEach(e -> map.put(e.getKey(), e.getValue()));
    return map;
  }

  public static class Builder {
    private Config conf = ConfigFactory.empty();

    public Builder() {
      log.info("Loading configs first row is highest priority, second row is fallback and so on");
    }

    public Builder withSystemEnvironment() {
      conf = conf.withFallback(systemEnvironment);
      log.info("Loaded system environment into config");
      return this;
    }

    public Builder withSystemProperties() {
      conf = conf.withFallback(systemProperties);
      log.info("Loaded system properties into config");
      return this;
    }

    public Builder withOptionalFile(String path) {
      File secureConfFile = new File(path);
      if(secureConfFile.exists()) {
        log.info("Loaded config file from path ({})", path);
        conf = conf.withFallback(ConfigFactory.parseFile(secureConfFile));
      } else {
        log.info("Attempted to load file from path ({}) but it was not found", path);
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
