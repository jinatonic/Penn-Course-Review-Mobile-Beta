package edu.upenn.cis.cis350.backend;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;

public class AutoComplete {

	//Example format for instructor: name = "DONAL D FITTS", path = "/instructors/1-DONALD-D-FITTS", course_id = null, type = INSTRUCTOR
	//Example format for course: name = "INTRO GREEK ARCHAELOGY", path = "/coursehistories/2", course_id = "AAMW-401", type = COURSE
	public static ArrayList<KeywordMap> getAutoCompleteForInstructor() {
		ArrayList<KeywordMap> keywordMap = new ArrayList<KeywordMap>();
		JSONObject instructor_object = JSONRequest.retrieveJSONObject(Parser.baseURL + "/instructors" + Parser.token);
		JSONObject instructor_result = null;
		try {
			if(instructor_object.has("result")) {
				instructor_result = instructor_object.getJSONObject("result");
				JSONArray instructor_values = null;
				if (instructor_result.has("values")) {
					instructor_values = instructor_result.getJSONArray("values");
					for(int i = 0; i < instructor_values.length(); i++){
						String name = "";
						String path = "";
						JSONObject o = instructor_values.getJSONObject(i);
						if(o.has("name"))
							name = o.getString("name");
						if(o.has("path"))
							path = o.getString("path");
						KeywordMap k = new KeywordMap(path, name, null, Type.INSTRUCTOR);
						keywordMap.add(k);

					}
				}
			}
		} catch(JSONException e) { 
			e.printStackTrace(); 
			return null;
		}

		return keywordMap;
	}

	public static JSONArray getJSONArrayForDept() {
		JSONObject dept_object = JSONRequest.retrieveJSONObject(Parser.baseURL + "/depts" + Parser.token);
		JSONObject dept_result = null;
		if(dept_object.has("result")){
			try {
				dept_result = dept_object.getJSONObject("result");
				JSONArray dept_values = null;
				if(dept_result.has("values")){
					dept_values = dept_result.getJSONArray("values");
					return dept_values;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
			return null;
		}
		else
			return null;
	}

	public static ArrayList<KeywordMap> getAutoCompleteForCourse(JSONObject dpt) {
		ArrayList<KeywordMap> keywordMap = new ArrayList<KeywordMap>();
		
		String dept_path = "";
		String dept_id = "";
		String dept_name = "";
		
		try {
			if(dpt.has("id"))
				dept_id = dpt.getString("id");
			if(dpt.has("name"))
				dept_name = dpt.getString("name");
			if(dpt.has("path"))
				dept_path = dpt.getString("path");
			KeywordMap deptMap = new KeywordMap(dept_path, dept_name, dept_id, Type.DEPARTMENT);
			keywordMap.add(deptMap);
			JSONObject dpt_object = JSONRequest.retrieveJSONObject(Parser.baseURL + dept_path + Parser.token);
			JSONObject dpt_result = null;
			if(dpt_object.has("result")){
				dpt_result = dpt_object.getJSONObject("result");
				JSONArray coursehistories = null;
				if(dpt_result.has("coursehistories")){
					coursehistories = dpt_result.getJSONArray("coursehistories");
					for(int m = 0 ; m < coursehistories.length(); m++){
						JSONObject course = coursehistories.getJSONObject(m);
						String course_id = "";
						String course_path = "";
						String course_name = "";
						if(course.has("aliases"))
							course_id = course.getJSONArray("aliases").getString(0);
						if(course.has("name"))
							course_name = course.getString("name");
						if(course.has("path"))
							course_path = course.getString("path");
						KeywordMap courseMap = new KeywordMap(course_path, course_name, course_id, Type.COURSE);
						keywordMap.add(courseMap);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
		return keywordMap;
	}
}
