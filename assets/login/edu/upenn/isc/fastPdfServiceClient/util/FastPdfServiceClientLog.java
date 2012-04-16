/*
 * @author mchyzer
 * $Id: FastPdfServiceClientLog.java,v 1.1 2012/03/22 19:57:57 mchyzer Exp $
 */
package edu.upenn.isc.fastPdfServiceClient.util;

import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log;


/**
 *
 */
public class FastPdfServiceClientLog implements Log {

  /** debug to console */
  private static ThreadLocal<Boolean> debugToConsole = new ThreadLocal<Boolean>();

  /**
   * if we should debug to console
   * @param theDebugToConsole
   */
  public static void assignDebugToConsole(boolean theDebugToConsole) {
    debugToConsole.set(theDebugToConsole);
  }
  
  /**
   * if we should debug to console
   * @return if debug to console
   */
  public static boolean debugToConsole() {
    Boolean debugToConsoleBoolean = debugToConsole.get();
    return FastPdfServiceClientUtils.defaultIfNull(debugToConsoleBoolean, false);
  }
  
  /**
   * debug to console if needed
   * @param prefix 
   * @param message
   * @param t
   */
  private static void debugToConsoleIfNeeded(String prefix, Object message, Throwable t) {
    if (debugToConsole()) {
      System.err.print(prefix);
      System.err.println(message);
      if (t != null) {
        t.printStackTrace();
      }
    }
  }
  
  /** wrap this log */
  private Log enclosedLog;
  
  /**
   * wrap a logger
   * @param theLog
   */
  public FastPdfServiceClientLog(Log theLog) {
    this.enclosedLog = theLog;
  }
  
  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#debug(java.lang.Object)
   */
  public void debug(Object message) {
    debugToConsoleIfNeeded("DEBUG: ", message, null);
    this.enclosedLog.debug(message);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#debug(java.lang.Object, java.lang.Throwable)
   */
  public void debug(Object message, Throwable t) {
    debugToConsoleIfNeeded("DEBUG: ", message, t);
    this.enclosedLog.debug(message, t);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#error(java.lang.Object)
   */
  public void error(Object message) {
    debugToConsoleIfNeeded("ERROR: ", message, null);
    this.enclosedLog.error(message);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#error(java.lang.Object, java.lang.Throwable)
   */
  public void error(Object message, Throwable t) {
    debugToConsoleIfNeeded("ERROR: ", message, t);
    this.enclosedLog.error(message, t);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#fatal(java.lang.Object)
   */
  public void fatal(Object message) {
    debugToConsoleIfNeeded("FATAL: ", message, null);
    this.enclosedLog.fatal(message);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#fatal(java.lang.Object, java.lang.Throwable)
   */
  public void fatal(Object message, Throwable t) {
    debugToConsoleIfNeeded("FATAL: ", message, t);
    this.enclosedLog.fatal(message, t);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#info(java.lang.Object)
   */
  public void info(Object message) {
    debugToConsoleIfNeeded("INFO: ", message, null);
    this.enclosedLog.info(message);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#info(java.lang.Object, java.lang.Throwable)
   */
  public void info(Object message, Throwable t) {
    debugToConsoleIfNeeded("INFO: ", message, t);
    this.enclosedLog.info(message, t);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#isDebugEnabled()
   */
  public boolean isDebugEnabled() {
    return this.enclosedLog.isDebugEnabled();
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#isErrorEnabled()
   */
  public boolean isErrorEnabled() {
    return this.enclosedLog.isErrorEnabled();
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#isFatalEnabled()
   */
  public boolean isFatalEnabled() {
    return this.enclosedLog.isFatalEnabled();
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#isInfoEnabled()
   */
  public boolean isInfoEnabled() {
    return this.enclosedLog.isInfoEnabled();
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#isTraceEnabled()
   */
  public boolean isTraceEnabled() {
    return this.enclosedLog.isTraceEnabled();
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#isWarnEnabled()
   */
  public boolean isWarnEnabled() {
    return this.enclosedLog.isWarnEnabled();
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#trace(java.lang.Object)
   */
  public void trace(Object message) {
    debugToConsoleIfNeeded("TRACE: ", message, null);
    this.enclosedLog.trace(message);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#trace(java.lang.Object, java.lang.Throwable)
   */
  public void trace(Object message, Throwable t) {
    debugToConsoleIfNeeded("TRACE: ", message, t);
    this.enclosedLog.trace(message, t);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#warn(java.lang.Object)
   */
  public void warn(Object message) {
    debugToConsoleIfNeeded("WARN: ", message, null);
    this.enclosedLog.warn(message);
  }

  /**
   * @see edu.upenn.isc.fastPdfServiceExt.org.apache.commons.logging.Log#warn(java.lang.Object, java.lang.Throwable)
   */
  public void warn(Object message, Throwable t) {
    debugToConsoleIfNeeded("WARN: ", message, t);
    this.enclosedLog.warn(message, t);
  }

}
