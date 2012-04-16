/*
 * @author mchyzer
 * $Id: FastPdfServiceClientWs.java,v 1.2 2012/03/22 21:02:04 mchyzer Exp $
 */
package edu.upenn.isc.fastPdfServiceClient.ws;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.upenn.isc.fastPdfServiceClient.util.FastPdfServiceClientLog;
import edu.upenn.isc.fastPdfServiceClient.util.FastPdfServiceClientUtils;
import edu.upenn.isc.fastPdfServiceExt.edu.internet2.middleware.morphString.Crypto;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.Credentials;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.Header;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.HttpClient;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.HttpStatus;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.UsernamePasswordCredentials;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.auth.AuthScope;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.methods.PostMethod;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.methods.multipart.FilePart;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.methods.multipart.Part;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.methods.multipart.StringPart;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.params.DefaultHttpParams;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.params.HttpMethodParams;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.protocol.Protocol;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log;


/**
 * 
 */
public class FastPdfServiceClientWs {
  
  /**
   * logger
   */
  private static Log LOG = FastPdfServiceClientUtils.retrieveLog(FastPdfServiceClientWs.class);

  /**
   * content type
   */
  private String contentType = null;
  
  /**
   * assign the content type, defaults to xml
   * @param theContentType
   * @return this for chaining
   */
  public FastPdfServiceClientWs assignContentType(String theContentType) {
    this.contentType = theContentType;
    return this;
  }
  
  /**
   * 
   */
  private PostMethod method;
  
  /**
   * 
   */
  public FastPdfServiceClientWs() {
  }
  
  /** */
  private String response;
  
  /**
   * 
   */
  private boolean success = false;

  /** keep a reference to the most recent for testing */
  public static String mostRecentRequest = null;
  
  /** keep a reference to the most recent for testing */
  public static String mostRecentResponse = null;

  /**
   * 
   * @param fastWebService
   * @param params
   * @param httpParamToReturn if we want to return a value of an http param
   * @param multiPart if true, then this is a multi part mime form submit
   * @return the value of an http param
   */
  public Object executeService(String fastWebService, boolean isFastWebService, Map<String, Object> params, String httpParamToReturn, boolean multiPart) {
    
    String logDir = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.logging.webService.documentDir", false);
    File requestFile = null;
    File responseFile = null;
    
    if (!FastPdfServiceClientUtils.isBlank(logDir)) {
      
      logDir = FastPdfServiceClientUtils.stripEnd(logDir, "/");
      logDir = FastPdfServiceClientUtils.stripEnd(logDir, "\\");
      Date date = new Date();
      String logName = logDir  + File.separator + "wsLog_" 
        + new SimpleDateFormat("yyyy_MM").format(date)
        + File.separator + "day_" 
        + new SimpleDateFormat("dd" + File.separator + "HH_mm_ss_SSS").format(date)
        + "_" + ((int)(1000 * Math.random())) + "_" + fastWebService;
      
      requestFile = new File(logName + "_request.log");
      
      responseFile = new File(logName + "_response.log");

      //make parents
      FastPdfServiceClientUtils.mkdirs(requestFile.getParentFile());
      
    }
    int[] responseCode = new int[1];
    
    //make sure right content type is in request (e.g. application/xhtml+xml
    this.method = postMethod(fastWebService, isFastWebService, params, requestFile, responseCode, multiPart);

    //make sure a request came back
    Header successHeader = this.method.getResponseHeader("X-fastPdfServiceSuccess");
    String successString = successHeader == null ? null : successHeader.getValue();

    this.success = "T".equals(successString);
    
    this.response = FastPdfServiceClientUtils.defaultString(FastPdfServiceClientUtils.responseBodyAsString(this.method));

    if (!this.success) {
      throw new RuntimeException("Web service problem! " + webServiceUrl() + ", " + this.response);
    }
    
    mostRecentResponse = this.response;

    if (responseFile != null || FastPdfServiceClientLog.debugToConsole()) {
      if (responseFile != null) {
        LOG.debug("WebService: logging response to: " + FastPdfServiceClientUtils.fileCanonicalPath(responseFile));
      }
      
      String theResponse = this.response;
      Exception indentException = null;

      StringBuilder headers = new StringBuilder();

      headers.append("HTTP/1.1 ").append(responseCode[0]).append(" ").append(HttpStatus.getStatusText(responseCode[0])).append("\n");
      
      for (Header header : this.method.getResponseHeaders()) {
        String name = header.getName();
        String value = header.getValue();
        
        //dont allow cookies to go to logs
        if (FastPdfServiceClientUtils.equals(name, "Set-Cookie")) {
          value = value.replaceAll("JSESSIONID=(.*)?;", "JSESSIONID=xxxxxxxxxxxx;");
        }
        headers.append(name).append(": ").append(value).append("\n");
      }
      headers.append("\n");
      String theResponseTotal = headers + theResponse;
      if (responseFile != null) {
        FastPdfServiceClientUtils.saveStringIntoFile(responseFile, theResponseTotal);
      }
      if (FastPdfServiceClientLog.debugToConsole()) {
        System.err.println("\n################ RESPONSE START " + "###############\n");
        System.err.println(theResponseTotal);
        System.err.println("\n################ RESPONSE END ###############\n\n");
      }
      if (indentException != null) {
        throw new RuntimeException("Problems indenting xml (is it valid?), turn off the indenting in the " +
            "fastPdfService.client.properties: fastPdfServiceClient.logging.webService.indent", indentException);
      }
    }

    if (FastPdfServiceClientUtils.isBlank(httpParamToReturn)) {
      return this.response;
    }
    
    Header header = this.method.getResponseHeader(httpParamToReturn);
    String headerString = header == null ? null : header.getValue();
    return headerString;
    
  }
  
  
  /**
   * http client
   * @return the http client
   */
  @SuppressWarnings({ "deprecation", "unchecked" })
  private static HttpClient httpClient() {
    
    //see if invalid SSL
    String httpsSocketFactoryName = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.https.customSocketFactory", false);
    
    //is there overhead here?  should only do this once?
    //perhaps give a custom factory
    if (!FastPdfServiceClientUtils.isBlank(httpsSocketFactoryName)) {
      Class<? extends SecureProtocolSocketFactory> httpsSocketFactoryClass = FastPdfServiceClientUtils.forName(httpsSocketFactoryName);
      SecureProtocolSocketFactory httpsSocketFactoryInstance = FastPdfServiceClientUtils.newInstance(httpsSocketFactoryClass);
      Protocol easyhttps = new Protocol("https", httpsSocketFactoryInstance, 443);
      Protocol.registerProtocol("https", easyhttps);
    }
    
    HttpClient httpClient = new HttpClient();

    DefaultHttpParams.getDefaultParams().setParameter(
        HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

    httpClient.getParams().setAuthenticationPreemptive(true);
    
    int soTimeoutMillis = FastPdfServiceClientUtils.propertiesValueInt(
        "fastPdfServiceClient.webService.httpSocketTimeoutMillis", 90000, true);
    
    httpClient.getParams().setSoTimeout(soTimeoutMillis);
    httpClient.getParams().setParameter(HttpMethodParams.HEAD_BODY_CHECK_TIMEOUT, soTimeoutMillis);
    
    int connectionManagerMillis = FastPdfServiceClientUtils.propertiesValueInt(
        "fastPdfServiceClient.webService.httpConnectionManagerTimeoutMillis", 90000, true);
    
    httpClient.getParams().setConnectionManagerTimeout(connectionManagerMillis);

    String user = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.webService.login", true);
    
    LOG.debug("WebService: connecting as user: '" + user + "'");
    
    boolean disableExternalFileLookup = FastPdfServiceClientUtils.propertiesValueBoolean(
        "encrypt.disableExternalFileLookup", false, true);
    
    //lets lookup if file
    String wsPass = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.webService.password", true);
    String wsPassFromFile = FastPdfServiceClientUtils.readFromFileIfFile(wsPass, disableExternalFileLookup);

    String passPrefix = null;

    if (!FastPdfServiceClientUtils.equals(wsPass, wsPassFromFile)) {

      passPrefix = "WebService pass: reading encrypted value from file: " + wsPass;

      String encryptKey = FastPdfServiceClientUtils.propertiesValue("encrypt.key", true);
      
      wsPass = new Crypto(encryptKey).decrypt(wsPassFromFile);
      
    } else {
      passPrefix = "WebService pass: reading scalar value from fastPdfService.client.properties";
    }
    
    if (FastPdfServiceClientUtils.propertiesValueBoolean("fastPdfServiceClient.logging.logMaskedPassword", false, false)) {
      LOG.debug(passPrefix + ": " + FastPdfServiceClientUtils.repeat("*", wsPass.length()));
    }

    Credentials defaultcreds = new UsernamePasswordCredentials(user, wsPass);

    //set auth scope to null and negative so it applies to all hosts and ports
    httpClient.getState().setCredentials(new AuthScope(null, -1), defaultcreds);

    return httpClient;
  }

  /**
   * 
   * @return the url
   */
  private String webServiceUrl() {
    String url = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.webService.url", true);
    
    url = FastPdfServiceClientUtils.stripEnd(url, "/");
    return url;
  }
    
  /**
   * @param fastWebService e.g. shareFiles
   * @param isFastPdfService true if fastWebService
   * @param params 
   * @param multiPart 
   * @return the method
   */
  private PostMethod postMethod(String fastWebService, boolean isFastPdfService, Map<String, Object> params, boolean multiPart) {
    try {
      String url = webServiceUrl();
      params.put(isFastPdfService ? "fastWebService" : "webService", fastWebService);
      String webServiceVersion = FastPdfServiceClientUtils.propertiesValue("fastPdfServiceClient.webService.client.version", true);
      params.put("version", webServiceVersion);

      LOG.debug("WebService: connecting to URL: '" + url + "'");
      
      //strip out nulls
      Map<String, Object> nonNullParams = new LinkedHashMap<String, Object>();
      for (String key : params.keySet()) {
        if (params.get(key) != null) {
          nonNullParams.put(key, params.get(key));
        }
      }
      
      params = nonNullParams;
      
      //URL e.g. http://localhost:8093/fastPdfService-ws/servicesRest/v1_3_000/...
      //NOTE: aStem:aGroup urlencoded substitutes %3A for a colon
      PostMethod postMethod = new PostMethod(url);
      
      if (multiPart) {
        Part[] parts = new Part[params.size()];
        
        int index = 0;
        for (String key : params.keySet()) {
          
          Object value = params.get(key);
          
          if (value instanceof String) {
            parts[index] = new StringPart(key, (String)value);
          } else if (value instanceof File) {
            parts[index] = new FilePart(key, (File)value);
          } else {
            if (value != null) {
              throw new RuntimeException("Not expecting type of param: " + key + ", " + value);
            }
          }
          index++;
        }
        
        MultipartRequestEntity multipartRequestEntity = new MultipartRequestEntity(parts, postMethod.getParams());
        postMethod.setRequestEntity(multipartRequestEntity);
        
        
      } else {
        for (String key : params.keySet()) {
          
          Object value = params.get(key);
          
          if (value instanceof String) {
            postMethod.addParameter(key, (String)value);
          } else {
            if (value != null) {
              throw new RuntimeException("Not expecting type of param: " + key + ", " + value);
            }
          }
        }
      }
      //no keep alive so response if easier to indent for tests
      postMethod.setRequestHeader("Connection", "close");
      
      return postMethod;
    } catch (Exception e) {
      throw new RuntimeException("Problem with client", e);
    }
  }

  /**
   * 
   * @param fastWebService
   * @param isFastWebService 
   * @param params
   * @param logFile if not null, log the contents of the request there
   * @param responseCode array of size one to get the response code back
   * @param multiPart 
   * @return the post method
   */
  private PostMethod postMethod(
      String fastWebService, boolean isFastWebService, Map<String, Object> params, File logFile, int[] responseCode, boolean multiPart)  {
    
    try {
      HttpClient httpClient = httpClient();
  
      PostMethod postMethod = postMethod(fastWebService, isFastWebService, params, multiPart);
  
      if (logFile != null || FastPdfServiceClientLog.debugToConsole()) {
        if (logFile != null) {
          LOG.debug("WebService: logging request to: " + FastPdfServiceClientUtils.fileCanonicalPath(logFile));
        }
        StringBuilder headers = new StringBuilder();
  //      POST /fastPdfServiceWs/servicesRest/v1_4_000/subjects HTTP/1.1
  //      Connection: close
  //      Authorization: Basic bWNoeXplcjpEaxxxxxxxxxx==
  //      User-Agent: Jakarta Commons-HttpClient/3.1
  //      Host: localhost:8090
  //      Content-Length: 226
  //      Content-Type: text/xml; charset=UTF-8
        headers.append("POST ").append(postMethod.getURI().getPathQuery()).append(" HTTP/1.1\n");
        headers.append("Connection: close\n");
        headers.append("Authorization: Basic xxxxxxxxxxxxxxxx\n");
        headers.append("User-Agent: Jakarta Commons-HttpClient/3.1\n");
        headers.append("Host: ").append(postMethod.getURI().getHost()).append(":")
          .append(postMethod.getURI().getPort()).append("\n");
        headers.append("Content-Length: ").append(
            postMethod.getRequestEntity().getContentLength()).append("\n");
        headers.append("Content-Type: ").append(
            postMethod.getRequestEntity().getContentType()).append("\n");
        headers.append("\n");
        
        String theRequest = headers.toString();
        if (logFile != null) {
          FastPdfServiceClientUtils.saveStringIntoFile(logFile, theRequest);
        }
        if (FastPdfServiceClientLog.debugToConsole()) {
          System.err.println("\n################ REQUEST START ###############\n");
          System.err.println(theRequest);
          System.err.println("\n################ REQUEST END ###############\n\n");
        }
      }
    
      int responseCodeInt = httpClient.executeMethod(postMethod);
  
      if (responseCode != null && responseCode.length > 0) {
        responseCode[0] = responseCodeInt;
      }
      
      return postMethod;
    } catch (Exception e) {
      throw new RuntimeException("Problem making post: ", e);
    }


  }
  
}