package edu.upenn.cis.cis350;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class Parser {
	private static final String BASE_URL = "http://api.penncoursereview.com/v1";
	private static final String TOKEN = "?token=cis350a_3uZg7s5d62hHBtZGeTDl"; // private token (github repo is private)

	public JSONObject retrieveJSONObject(String path) throws IOException, ParseException, JSONException {
		URL url = new URL(path);
		URLConnection connection = url.openConnection();
		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		while ((line = reader.readLine()) != null) {
			builder.append(line);
		}
		Log.v("Length",builder.toString());

		return new JSONObject(builder.toString());
	}
	
	public String getReviewsForCourse(String course) throws IOException, ParseException, JSONException {
		System.out.println(course);
		String dept = "";
		String num = "";
		for (int i = 0; i <course.length(); ++i) {
			if (course.charAt(i) >= 48 && course.charAt(i) <= 57) {
				dept = course.substring(0,i).toUpperCase();
				num = course.substring(i);
				break;
			}
		}
		String alias = dept + "-" + num;
		System.out.println(alias);

		String url = BASE_URL + "/depts/"+ dept + TOKEN;

		JSONObject json = retrieveJSONObject(url);
		JSONObject result = (JSONObject)json.get("result");
		JSONArray coursehistories = (JSONArray)result.getJSONArray("coursehistories");
		String path ="";
		for (int i = 0; i < coursehistories.length(); ++i) {
			JSONObject j = coursehistories.getJSONObject(i);
			JSONArray aliases = j.getJSONArray("aliases");
			for (int k = 0; k < aliases.length(); ++k) {
				if (aliases.get(k).equals(alias)) {
					System.out.println(j.toString());
					path = (String)j.get("path") + "/reviews";
					break;
				}
			}
		}

		System.out.println(path);
		ArrayList<Course> reviews = new ArrayList<Course>();
		reviews = storeReviews(path);

		System.out.println(reviews.size());

		return displayCourseReviews(reviews);
	}

	public String displayCourseReviews(ArrayList<Course> reviews) {
		String s = "";
		for (Course c: reviews) {
			s+=c.getID() + "\n"+ c.getRatings() + "\n\n";
		}
		return s;
	}
	
	public ArrayList<Course> storeReviews(String path) throws IOException, ParseException, JSONException {
		JSONObject json = retrieveJSONObject(BASE_URL+path + TOKEN);
		JSONArray courses = json.getJSONObject("result").getJSONArray("values");
		ArrayList<Course> courseReviews = new ArrayList<Course>();
		for (int j = 0; j < courses.length(); ++j) {
			JSONObject course = courses.getJSONObject(j);
			JSONObject instructor = course.getJSONObject("instructor");
			Instructor i = new Instructor(instructor.getString("id"),instructor.getString("name"),instructor.getString("path"));
			JSONObject ratings = course.getJSONObject("ratings");
			Ratings r = new Ratings(Double.parseDouble(ratings.getString("rAmountLearned")),Double.parseDouble(ratings.getString("rCommAbility")),
					Double.parseDouble(ratings.getString("rCourseQuality")),Double.parseDouble(ratings.getString("rDifficulty")),
					Double.parseDouble(ratings.getString("rInstructorAccess")),Double.parseDouble(ratings.getString("rInstructorQuality")),
					Double.parseDouble(ratings.getString("rReadingsValue")),Double.parseDouble(ratings.getString("rRecommendMajor")),
					Double.parseDouble(ratings.getString("rRecommendNonMajor")),Double.parseDouble(ratings.getString("rStimulateInterest")),
					Double.parseDouble(ratings.getString("rWorkRequired")));
			JSONObject section = course.getJSONObject("section");
			JSONArray a = section.getJSONArray("aliases");
			String[] aliases = new String[a.length()];
			for (int p = 0; p < a.length(); ++p) {
				aliases[p] = a.getString(p);
			}
			Section s = new Section(aliases, section.getString("id"),section.getString("path"),section.getString("name"),
					section.getString("sectionnum"));
			Course c = new Course(course.getString("comments"),course.getString("id"),i,course.getInt("num_reviewers"),
					course.getInt("num_students"),course.getString("path"),r,s);
			courseReviews.add(c);
		}
		return courseReviews;
	}
}