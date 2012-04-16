package edu.upenn.isc.fastPdfServiceClient.util;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.LogFactory;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.impl.Jdk14Logger;

/**
 * utility methods specific to fastPdfService client
 */
public class FastPdfServiceClientUtils extends FastPdfServiceClientCommonUtils {

  /**
   * configure jdk14 logs once
   */
  private static boolean configuredLogs = false;

  /**
   * @param theClass
   * @return the log
   */
  public static Log retrieveLog(Class<?> theClass) {

    Log theLog = LogFactory.getLog(theClass);
    
    //if this isnt here, dont configure yet
    if (isBlank(FastPdfServiceClientUtils.propertiesValue("encrypt.disableExternalFileLookup", false))
        || theClass.equals(FastPdfServiceClientCommonUtils.class)) {
      return new FastPdfServiceClientLog(theLog);
    }
    
    if (!configuredLogs) {
      String logLevel = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.logging.logLevel", false);
      String logFile = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.logging.logFile", false);
      String fastPdfServiceClientLogLevel = FastPdfServiceClientUtils.propertiesValue(
          "fastPdfServiceClient.logging.fastPdfServiceClientOnly.logLevel", false);
      
      boolean hasLogLevel = !isBlank(logLevel);
      boolean hasLogFile = !isBlank(logFile);
      boolean hasSecureShareClientLogLevel = !isBlank(fastPdfServiceClientLogLevel);
      
      if (hasLogLevel || hasLogFile) {
        if (theLog instanceof Jdk14Logger) {
          Jdk14Logger jdkLogger = (Jdk14Logger) theLog;
          Logger logger = jdkLogger.getLogger();
          long timeToLive = 60;
          while (logger.getParent() != null && timeToLive-- > 0) {
            //this should be root logger
            logger = logger.getParent();
          }
  
          if (length(logger.getHandlers()) == 1) {
  
            //remove console appender if only one
            if (logger.getHandlers()[0].getClass() == ConsoleHandler.class) {
              logger.removeHandler(logger.getHandlers()[0]);
            }
          }
  
          if (length(logger.getHandlers()) == 0) {
            Handler handler = null;
            if (hasLogFile) {
              try {
                handler = new FileHandler(logFile, true);
              } catch (IOException ioe) {
                throw new RuntimeException(ioe);
              }
            } else {
              handler = new ConsoleHandler();
            }
            handler.setFormatter(new SimpleFormatter());
            handler.setLevel(Level.ALL);
            logger.addHandler(handler);

            logger.setUseParentHandlers(false);
          }
          
          if (hasLogLevel) {
            Level level = Level.parse(logLevel);
            
            logger.setLevel(level);

          }
        }
      }
      
      if (hasSecureShareClientLogLevel) {
        Level level = Level.parse(fastPdfServiceClientLogLevel);
        Log fastPdfServiceClientLog = LogFactory.getLog("edu.upenn.isc.fastPdfService");
        if (fastPdfServiceClientLog instanceof Jdk14Logger) {
          Jdk14Logger jdkLogger = (Jdk14Logger) fastPdfServiceClientLog;
          Logger logger = jdkLogger.getLogger();
          logger.setLevel(level);
        }
      }
      
      configuredLogs = true;
    }
    
    return new FastPdfServiceClientLog(theLog);
    
  }
  
  /** override map for properties */
  private static Map<String, String> fastPdfServiceClientOverrideMap = new LinkedHashMap<String, String>();
  
  /**
   * override map for properties for testing
   * @return the override map
   */
  public static Map<String, String> fastPdfServiceClientOverrideMap() {
    return fastPdfServiceClientOverrideMap;
  }
  
  /**
   * fastPdfService client properties
   * @return the properties
   */
  public static Properties fastPdfServiceClientProperties() {
    Properties properties = null;
    try {
      properties = propertiesFromResourceName(
        "fastPdfService.client.properties", true, true, FastPdfServiceClientCommonUtils.class, null);
    } catch (Exception e) {
      throw new RuntimeException("Error accessing file: fastPdfService.client.properties  " +
          "This properties file needs to be in the same directory as fastPdfServiceClient.jar, or on your Java classpath", e);
    }
    return properties;
  }

  /**
   * get a property and validate required from fastPdfService.client.properties
   * @param key 
   * @param required 
   * @return the value
   */
  public static String propertiesValue(String key, boolean required) {
    return FastPdfServiceClientUtils.propertiesValue("fastPdfService.client.properties", 
        fastPdfServiceClientProperties(), 
        FastPdfServiceClientUtils.fastPdfServiceClientOverrideMap(), key, required);
  }


  /**
   * get a boolean and validate from fastPdfService.client.properties
   * @param key
   * @param defaultValue
   * @param required
   * @return the string
   */
  public static boolean propertiesValueBoolean(String key, boolean defaultValue, boolean required ) {
    return FastPdfServiceClientUtils.propertiesValueBoolean(
        "fastPdfService.client.properties", fastPdfServiceClientProperties(), 
        FastPdfServiceClientUtils.fastPdfServiceClientOverrideMap(), 
        key, defaultValue, required);
  }

  /**
   * get a boolean and validate from fastPdfService.client.properties
   * @param key
   * @param defaultValue
   * @param required
   * @return the string
   */
  public static int propertiesValueInt(String key, int defaultValue, boolean required ) {
    return FastPdfServiceClientUtils.propertiesValueInt(
        "fastPdfService.client.properties", fastPdfServiceClientProperties(), 
        FastPdfServiceClientUtils.fastPdfServiceClientOverrideMap(), 
        key, defaultValue, required);
  }

  /**
   * logger
   */
  @SuppressWarnings("unused")
  private static Log LOG = FastPdfServiceClientUtils.retrieveLog(FastPdfServiceClientUtils.class);

}