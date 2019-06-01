package com.github.opticyclic.typesafe;

import com.typesafe.config.Config;
import com.typesafe.config.impl.ConfigImpl;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ConfigsTest {

  @Test
  public void emptyConfigShouldNotFail() {
    Config config = Configs.builder().build();
    Assert.assertNotNull(config);
  }

  @Test
  public void loadConfigFromResources() {
    Config config = Configs.builder()
                      .withResource("test.properties")
                      .build();
    Assert.assertEquals(config.getString("location"), "resources");
  }

  @Test
  public void loadConfigFromFile() {
    Config config = Configs.builder()
                      .withOptionalFile("src/test/data/file.properties")
                      .build();
    Assert.assertEquals(config.getString("location"), "file");
  }

  @Test
  public void loadConfigFromFileWithResourceFallback() {
    Config config = Configs.builder()
                      .withOptionalFile("src/test/data/file.properties")
                      .withResource("test.properties")
                      .build();
    Assert.assertEquals(config.getString("location"), "file");
    Assert.assertEquals(config.getString("fallback"), "test");
  }

  @Test
  public void loadConfigFromSystemProperties() {
    String key = "location";
    String value = "system";

    //First check that it is empty
    Config config = Configs.builder()
                      .withSystemProperties()
                      .build();
    Assert.assertNull(System.getProperty(key));
    Assert.assertFalse(config.hasPath(key));

    //Modify the SystemProperties and reload the cache to check it exists
    System.setProperty(key, value);
    ConfigImpl.reloadSystemPropertiesConfig();
    Config configs = Configs.builder()
                       .withSystemProperties()
                       .build();

    Assert.assertNotNull(System.getProperty(key));
    Assert.assertEquals(configs.getString(key), value);
  }
}