/*
 * @author mchyzer
 * $Id: FpsPennGroupsHasMember.java,v 1.1 2012/03/22 21:02:04 mchyzer Exp $
 */
package edu.upenn.isc.fastPdfServiceClient.api;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.upenn.isc.fastPdfServiceClient.util.FastPdfServiceClientUtils;
import edu.upenn.isc.fastPdfServiceClient.ws.FastPdfServiceClientWs;


/**
 * class to see if a member is in a group
 */
public class FpsPennGroupsHasMember {

  /**
   * 
   * @param args
   */
  public static void main(String[] args) {

    //kilbride
    System.out.println(new FpsPennGroupsHasMember()
      .assignGroupName("penn:isc:ait:apps:pennCourseReview:groups:pennCourseReviewStudents")
      .assignSubjectSourceId("pennperson").assignSubjectIdentifier("kilbride").executeReturnBoolean());
    
  }
  
  /** group name */
  private String groupName;
  
  /** subjectId */
  private String subjectId;
  
  /** subjectSourceId */
  private String subjectSourceId;
  
  /** subjectIdentifier */
  private String subjectIdentifier;
  

  /**
   * set the group name
   * @param theGroupName
   * @return this for chaining
   */
  public FpsPennGroupsHasMember assignGroupName(String theGroupName) {
    this.groupName = theGroupName;
    return this;
  }
  
  /**
   * set the subject id
   * @param theSubjectId
   * @return this for chaining
   */
  public FpsPennGroupsHasMember assignSubjectId(String theSubjectId) {
    this.subjectId = theSubjectId;
    return this;
  }
  
  /**
   * set the subject source id
   * @param theSubjectSourceId
   * @return this for chaining
   */
  public FpsPennGroupsHasMember assignSubjectSourceId(String theSubjectSourceId) {
    this.subjectSourceId = theSubjectSourceId;
    return this;
  }
  
  /**
   * set the subject source identifier
   * @param theSubjectIdentifier
   * @return this for chaining
   */
  public FpsPennGroupsHasMember assignSubjectIdentifier(String theSubjectIdentifier) {
    this.subjectIdentifier = theSubjectIdentifier;
    return this;
  }
  
  /**
   * validate this call
   */
  private void validate() {
    if (FastPdfServiceClientUtils.isBlank(this.subjectId) == FastPdfServiceClientUtils.isBlank(this.subjectIdentifier)) {
      throw new RuntimeException("Send one of subjectId and subjectIdentifier: " + this);
    }
    if (FastPdfServiceClientUtils.isBlank(this.groupName) ) {
      throw new RuntimeException("groupName is required: " + this);
    }
  }
  
  /**
   * see if member
   * @return true if member, false if not
   */
  public boolean executeReturnBoolean() {
    return FastPdfServiceClientUtils.equals("IS_MEMBER", execute());
  }
  
  /**
   * execute the call and return the results.  If there is a problem calling the service, an
   * exception will be thrown
   * 
   * @return the results
   */
  public String execute() {
    this.validate();

    FastPdfServiceClientWs fastPdfServiceClientWs = new FastPdfServiceClientWs();
    
    Map<String, Object> params = new LinkedHashMap<String, Object>();
    params.put("groupName", this.groupName);
    params.put("subjectId", this.subjectId);
    params.put("subjectSourceId", this.subjectSourceId);
    params.put("subjectIdentifier", this.subjectIdentifier);

    //kick off the web service
    String result = (String)
      fastPdfServiceClientWs.executeService("pennGroupsHasMember", false, params, "X-fastPdfServiceResultCode", false);
      
    return result;
  }
  
}
