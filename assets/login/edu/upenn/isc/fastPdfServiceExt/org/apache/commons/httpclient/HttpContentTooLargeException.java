package edu.upenn.isc.fastPdfServiceExt.org.apache.commons.httpclient;

/**
 * Signals that the response content was larger than anticipated. 
 * 
 * @author Ortwin Gl�ck
 */
public class HttpContentTooLargeException extends HttpException {
    private int maxlen;

    public HttpContentTooLargeException(String message, int maxlen) {
        super(message);
        this.maxlen = maxlen;
    }
    
    /**
     * @return the maximum anticipated content length in bytes.
     */
    public int getMaxLength() {
        return maxlen;
    }
}
