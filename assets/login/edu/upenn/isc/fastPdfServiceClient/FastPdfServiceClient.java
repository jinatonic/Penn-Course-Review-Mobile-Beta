/*
 * @author mchyzer
 * $Id: FastPdfServiceClient.java,v 1.1 2012/03/22 21:02:04 mchyzer Exp $
 */
package edu.upenn.isc.fastPdfServiceClient;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.upenn.isc.fastPdfServiceClient.api.FpsPennGroupsHasMember;
import edu.upenn.isc.fastPdfServiceClient.util.FastPdfServiceClientCommonUtils;
import edu.upenn.isc.fastPdfServiceClient.util.FastPdfServiceClientLog;
import edu.upenn.isc.fastPdfServiceClient.util.FastPdfServiceClientUtils;
import edu.upenn.isc.fastPdfServiceClient.ws.FastPdfServiceClientWs;
import edu.upenn.isc.fastPdfServiceExt.edu.internet2.middleware.morphString.Crypto;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log;


/**
 * main class for secure share client.  note, stdout is for output, stderr is for error messages (or logs)
 */
public class FastPdfServiceClient {

  /** timing gate */
  private static long startTime = System.currentTimeMillis();
  
  /**
   * 
   */
  static Log log = FastPdfServiceClientUtils.retrieveLog(FastPdfServiceClient.class);

  /** custom operations from config file */
  private static Map<String, Class<ClientOperation>> customOperations = null;

  /**
   * get custom operation classes configured in the fastPdfService.client.properties
   * @return the map of operations
   */
  @SuppressWarnings({ "unchecked", "cast" })
  private static Map<String, Class<ClientOperation>> customOperations() {
    
    if (customOperations == null) {
      
      customOperations = new LinkedHashMap<String, Class<ClientOperation>>();
      
      int i=0;
      String operationName = null;
      while (true) {
        operationName = null;
        operationName = FastPdfServiceClientUtils.propertiesValue("customOperation.name." + i, false);
        if (FastPdfServiceClientUtils.isBlank(operationName)) {
          break;
        }
        if (customOperations.containsKey(operationName)) {
          throw new RuntimeException("There is an ldap operation defined twice in fastPdfService.client.properties: '" + operationName + "'");
        }
        try {

          String operationClassName = FastPdfServiceClientUtils.propertiesValue("customOperation.class." + i, true);
          Class<ClientOperation> operationClass = (Class<ClientOperation>)FastPdfServiceClientUtils.forName(operationClassName);
          customOperations.put(operationName, operationClass);

        } catch (RuntimeException re) {
          throw new RuntimeException("Problem with custom operation: " + operationName, re);
        }
        i++;
      }
    }
    
    return customOperations;
    
  }
  
  /** should java exit on error? */
  public static boolean exitOnError = true;
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    
    String operation = null;
    try {
      if (FastPdfServiceClientUtils.length(args) == 0) {
        usage();
        return;
      }
      
      //map of all command line args
      Map<String, String> argMap = FastPdfServiceClientUtils.argMap(args);
      
      Map<String, String> argMapNotUsed = new LinkedHashMap<String, String>(argMap);

      boolean debugMode = FastPdfServiceClientUtils.argMapBoolean(argMap, argMapNotUsed, "debug", false, false);
      
      FastPdfServiceClientLog.assignDebugToConsole(debugMode);
      
      //init if not already
      FastPdfServiceClientUtils.fastPdfServiceClientProperties();
      
      //see where log file came from
      StringBuilder callingLog = new StringBuilder();
      FastPdfServiceClientUtils.propertiesFromResourceName("fastPdfService.client.properties", 
          false, true, FastPdfServiceClientCommonUtils.class, callingLog);
      
      //see if the message about where it came from is
      //log.debug(callingLog.toString());
      
      operation = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "operation", true);
      
      //where results should go if file
      String saveResultsToFile = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "saveResultsToFile", false);
      boolean shouldSaveResultsToFile = !FastPdfServiceClientUtils.isBlank(saveResultsToFile);
      
      if (shouldSaveResultsToFile) {
        log.debug("Will save results to file: " + FastPdfServiceClientUtils.fileCanonicalPath(new File(saveResultsToFile)));
      }
      
      String result = null;
      
      if (customOperations().containsKey(operation)) {
        
        Class<ClientOperation> operationClass = customOperations().get(operation);
        ClientOperation clientOperation = FastPdfServiceClientUtils.newInstance(operationClass);
        
        OperationParams operationParams = new OperationParams();
        operationParams.setArgMap(argMap);
        operationParams.setArgMapNotUsed(argMapNotUsed);
        operationParams.setShouldSaveResultsToFile(shouldSaveResultsToFile);
        
        result = clientOperation.operate(operationParams);
        
      } else if (FastPdfServiceClientUtils.equals(operation, "encryptPassword")) {
        
        result = encryptText(argMap, argMapNotUsed, shouldSaveResultsToFile);
        
      } else if (FastPdfServiceClientUtils.equals(operation, "pennGroupsHasMember")) {
        result = pennGroupsHasMember(argMap, argMapNotUsed);

      } else if (FastPdfServiceClientUtils.equals(operation, "sendFile")) {
        result = sendFile(argMap, argMapNotUsed);

      } else {
        System.err.println("Error: invalid operation: '" + operation + "', for usage help, run: java -jar fastPdfServiceClient.jar" );
        if (exitOnError) {
          System.exit(1);
        }
        throw new RuntimeException("Invalid usage");
      }
      
      //this already has a newline on it
      if (shouldSaveResultsToFile) {
        FastPdfServiceClientUtils.saveStringIntoFile(new File(saveResultsToFile), result);
      } else {
        System.out.print(result);
      }

      failOnArgsNotUsed(argMapNotUsed);
      
    } catch (Exception e) {
      System.err.println("Error with fastPdfService client, check the logs: " + e.getMessage());
      log.fatal(e.getMessage(), e);
      if (exitOnError) {
        System.exit(1);
      }
      if (e instanceof RuntimeException) {
        throw (RuntimeException)e;
      }
      throw new RuntimeException(e.getMessage(), e);
    } finally {
      try {
        log.debug("Elapsed time: " + (System.currentTimeMillis() - startTime) + "ms");
      } catch (Exception e) {}
      FastPdfServiceClientLog.assignDebugToConsole(false);
    }
    
  }

  /**
   * @param argMapNotUsed
   */
  private static void failOnArgsNotUsed(Map<String, String> argMapNotUsed) {
    if (argMapNotUsed.size() > 0) {
      boolean failOnExtraParams = FastPdfServiceClientUtils.propertiesValueBoolean(
          "fastPdfServiceClient.failOnExtraCommandLineArgs", true, true);
      String error = "Invalid command line arguments: " + argMapNotUsed.keySet();
      if (failOnExtraParams) {
        throw new RuntimeException(error);
      }
      log.error(error);
    }
  }

  /**
   * @param argMap
   * @param argMapNotUsed
   * @param shouldSaveResultsToFile
   * @return result
   */
  private static String encryptText(Map<String, String> argMap,
      Map<String, String> argMapNotUsed,
      boolean shouldSaveResultsToFile) {
    boolean dontMask = FastPdfServiceClientUtils.argMapBoolean(argMap, argMapNotUsed, "dontMask", false, false);
    
    String encryptKey = FastPdfServiceClientUtils.propertiesValue("encrypt.key", true);
    
    boolean disableExternalFileLookup = FastPdfServiceClientUtils.propertiesValueBoolean(
        "encrypt.disableExternalFileLookup", false, true);
    
    //lets lookup if file
    encryptKey = FastPdfServiceClientUtils.readFromFileIfFile(encryptKey, disableExternalFileLookup);
    
    //lets get the password from stdin
    String password = FastPdfServiceClientUtils.retrievePasswordFromStdin(dontMask, 
        "Type the string to encrypt (note: pasting might echo it back): ");
    
    String encrypted = new Crypto(encryptKey).encrypt(password);
    
    if (shouldSaveResultsToFile) {
      return encrypted;
    }
    return "Encrypted password: " + encrypted;
  }

  /**
   * @param argMap
   * @param argMapNotUsed
   * @return result
   */
  private static String pennGroupsHasMember(Map<String, String> argMap,
      Map<String, String> argMapNotUsed) {

    // --groupName=a:b:c [--subjectSourceId=pennperson] [--subjectIdentifier=apennkey] [--subjectId=12345678] [--debug=true]
                                                                                                            
    String groupName = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "groupName", true);
    String subjectSourceId = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "subjectSourceId", false);
    String subjectId = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "subjectId", false);
    String subjectIdentifier = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "subjectIdentifier", false);
    
    FpsPennGroupsHasMember pennGroupsHasMember = new FpsPennGroupsHasMember();        

    pennGroupsHasMember.assignGroupName(groupName);
    pennGroupsHasMember.assignSubjectId(subjectId);
    pennGroupsHasMember.assignSubjectIdentifier(subjectIdentifier);
    pennGroupsHasMember.assignSubjectSourceId(subjectSourceId);
    pennGroupsHasMember.assignGroupName(groupName);
    
    
    failOnArgsNotUsed(argMapNotUsed);

    String result = pennGroupsHasMember.execute();
    
    return result.toString();
  }

  /**
   * @param argMap
   * @param argMapNotUsed
   * @return result
   */
  private static String sendFile(Map<String, String> argMap,
      Map<String, String> argMapNotUsed) {
    
    String fileContents = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "fileContents", false);
    
    String theFileName = "[contents on command line]";
    if (FastPdfServiceClientUtils.isBlank(fileContents)) {
      String fileName = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "fileName", true);

      fileContents = FastPdfServiceClientUtils.readFileIntoString(new File(fileName));
      
      theFileName = FastPdfServiceClientUtils.fileCanonicalPath(new File(fileName));
    }
    
    if (fileContents.startsWith("POST") || fileContents.startsWith("GET")
        || fileContents.startsWith("PUT") || fileContents.startsWith("DELETE")
        || fileContents.startsWith("Connection:")) {
      throw new RuntimeException("The file is detected as containing HTTP headers, it should only contain the payload (e.g. the XML): " + theFileName);
    }
    
    String fastWebService = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "fastWebService", true);
    boolean isFastWebService = FastPdfServiceClientUtils.argMapBoolean(argMap, argMapNotUsed, "isFastWebService", false, true);

    //this is part of the log file if logging output
    String labelForLog = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "labelForLog", false);
    
    labelForLog = FastPdfServiceClientUtils.defaultIfBlank(labelForLog, "sendFile");
    
    boolean indentOutput = FastPdfServiceClientUtils.argMapBoolean(argMap, argMapNotUsed, "indentOutput", false, true);
    
    String contentType = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "contentType", false);
    
    failOnArgsNotUsed(argMapNotUsed);
    
    FastPdfServiceClientWs fastPdfServiceClientWs = new FastPdfServiceClientWs();
    
    if (FastPdfServiceClientUtils.isNotBlank(contentType)) {
      fastPdfServiceClientWs.assignContentType(contentType);
    }
    
    try {
      //TODO add in params here
      String results = (String)fastPdfServiceClientWs.executeService(fastWebService, isFastWebService, null, null, true);

      return results;
      
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * retrieve params from args
   * @param argMap
   * @param argMapNotUsed
   * @return the list of params or empty list if none
   */
  private static List<File> retrieveFilesFromArgs(
      Map<String, String> argMap, Map<String, String> argMapNotUsed) {

    List<File> files = new ArrayList<File>();
    int index = 0;
    while (true) {

      String argName = FastPdfServiceClientUtils.argMapString(argMap, argMapNotUsed, "file" + index, false);
      if (FastPdfServiceClientUtils.isBlank(argName)) {
        break;
      }
      File file = new File(argName);
      if (!file.exists() || !file.isFile()) {
        throw new RuntimeException("File doesnt exist or is not a file: " 
            + FastPdfServiceClientUtils.fileCanonicalPath(file));
      }
      files.add(file);
      index++;
    }
    
    if (files.size() == 0) {
      throw new RuntimeException("No files found.  pass in with --file0=fileName --file1=fileName1 etc");
    }
    
    return files;
  }
  
  /**
   * print usage and exit
   */
  public static void usage() {
    //read in the usage file
    String usage = FastPdfServiceClientUtils.readResourceIntoString("fastPdfService.client.usage.txt", FastPdfServiceClientCommonUtils.class);
    System.err.println(usage);
  }

}
