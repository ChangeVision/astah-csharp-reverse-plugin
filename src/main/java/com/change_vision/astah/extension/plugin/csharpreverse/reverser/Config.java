package com.change_vision.astah.extension.plugin.csharpreverse.reverser;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * service for Config.properties
 */
public class Config {

  private Properties propertie;
  private InputStream inputFile;

  public Config() throws IOException {
    propertie = new Properties();
    inputFile = Config.class.getResourceAsStream("Config.properties");
    propertie.load(inputFile);
    inputFile.close();
  }

  public Config(InputStream input) throws IOException {
    propertie = new Properties();
    inputFile = input;
    propertie.load(inputFile);
    inputFile.close();
  }

  public String getValue(String key) {

    if (propertie.containsKey(key)) {
      String value = propertie.getProperty(key);
      return value;
    } else
      return "";
  }

  public String getValue(String fileName, String key) throws IOException {
    String value = "";
    inputFile = new FileInputStream(fileName);
    propertie.load(inputFile);
    inputFile.close();
    if (propertie.containsKey(key)) {
      value = propertie.getProperty(key);
      return value;
    } else
      return value;
  }

  public void clear() {
    propertie.clear();
  }

  public void setValue(String key, String value) {
    propertie.setProperty(key, value);
  }

  public static List<String> getClassNameAboutForbidCreateAssociation() throws IOException {
    if (LanguageManager.isCPlus()) {
      return Arrays.asList(new Config().getValue("cplus_types_for_attribute").split(","));
    } else if (LanguageManager.isCSHARP()) {
      return Arrays.asList(new Config().getValue("csharp_types_for_attribute").split(","));
    } else if (LanguageManager.isJAVA()) {
      return Arrays.asList(new Config().getValue("java_types_for_attribute").split(","));
    }
    return null;
  }
}
