/*
 * $Header: /var/cvs/fast/fastUtility/fastPdfServiceClient/ext/edu/upenn/isc/fastPdfServiceExt/org/apache/commons/httpclient/cookie/CookieVersionSupport.java,v 1.1 2012/03/22 19:57:57 mchyzer Exp $
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

package edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.cookie;

import edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient.Header;

/**
 * Defines cookie specification specific capabilities
 * 
 * @author <a href="mailto:oleg at ural.ru">Oleg Kalnichevski</a>
 *
 * @since 3.1
 */
public interface CookieVersionSupport {    

    int getVersion();
    
    Header getVersionHeader();

}