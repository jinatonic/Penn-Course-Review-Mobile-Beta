/*
 * $Header: /var/cvs/fast/fastUtility/fastPdfServiceClient/ext/edu/upenn/isc/fastPdfServiceExt/org/apache/commons/httpclient/RedirectException.java,v 1.1 2012/03/22 19:57:57 mchyzer Exp $
 * $Revision: 1.1 $
 * $Date: 2012/03/22 19:57:57 $
 *
 * ====================================================================
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient;

/**
 * Signals violation of HTTP specification caused by an invalid redirect
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * @since 3.0
 */
public class RedirectException extends ProtocolException {

    /**
     * Creates a new RedirectException with a <tt>null</tt> detail message. 
     */
    public RedirectException() {
        super();
    }

    /**
     * Creates a new RedirectException with the specified detail message.
     * 
     * @param message The exception detail message
     */
    public RedirectException(String message) {
        super(message);
    }

    /**
     * Creates a new RedirectException with the specified detail message and cause.
     * 
     * @param message the exception detail message
     * @param cause the <tt>Throwable</tt> that caused this exception, or <tt>null</tt>
     * if the cause is unavailable, unknown, or not a <tt>Throwable</tt>
     */
    public RedirectException(String message, Throwable cause) {
        super(message, cause);
    }
}
