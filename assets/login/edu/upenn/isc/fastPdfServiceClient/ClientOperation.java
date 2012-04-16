/*
 * @author mchyzer
 * $Id: ClientOperation.java,v 1.1 2012/03/22 19:57:57 mchyzer Exp $
 */
package edu.upenn.isc.fastPdfServiceClient;


/**
 * an operation of the fastPdfService client
 */
public interface ClientOperation {

  /**
   * execute an operation
   * @param operationParams
   * @return the string output to go to screen or file
   */
  public String operate(OperationParams operationParams);
  
}
