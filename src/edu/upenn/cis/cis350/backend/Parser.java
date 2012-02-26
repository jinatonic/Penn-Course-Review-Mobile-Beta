package edu.upenn.cis.cis350.backend;

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

import edu.upenn.cis.cis350.objects.*;

public class Parser {
	public final String baseURL = "http://api.penncoursereview.com/v1";
	public final String token = "?token=cis350a_3uZg7s5d62hHBtZGeTDl"; // private token (github repo is private)

	public JSONObject retrieveJSONObject(String path){
		try{
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
		catch(IOException e) {
			System.out.println("Bad url");
			return null;
		} catch (JSONException e) {
			return null;
		}
	}

	public String getReviewsForCourse(String course) throws IOException, ParseException, JSONException {
		course = course.trim();
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

		String url = baseURL + "/depts/"+ dept + token;

		JSONObject json = retrieveJSONObject(url);
		if (json == null)
			return null;
		JSONObject result = null;
		JSONArray coursehistories = null;
		if (json.has("result")) {
			result = (JSONObject)json.get("result");
			if (result.has("coursehistories"))
				coursehistories = (JSONArray)result.getJSONArray("coursehistories");
		}	
		else {
			return null;
		}


		String path ="";
		for (int i = 0; i < coursehistories.length(); ++i) {
			JSONObject j = coursehistories.getJSONObject(i);
			JSONArray aliases = null;
			if(j.has("aliases")){
				aliases = j.getJSONArray("aliases");

				for (int k = 0; k < aliases.length(); ++k) {
					if (aliases.get(k).equals(alias)) {
						System.out.println(j.toString());
						path = (String)j.get("path") + "/reviews";
						break;
					}
				}
			}
			else
				return null;
		}

		System.out.println(path);
		ArrayList<Course> reviews = new ArrayList<Course>();
		reviews = storeReviews(path);

		System.out.println(reviews.size());

		return displayCourseReviews(reviews);
	}

	public String displayCourseReviews(ArrayList<Course> reviews) {
		String s = "";
		for (Course c : reviews) {
			s += c.getID() + "\n"+ c.getRatings() + "\n\n";
		}
		return s;
	}

	public ArrayList<Course> storeReviews(String path) throws IOException, ParseException, JSONException {
		JSONObject json = retrieveJSONObject(baseURL + path + token);
		JSONArray courses = null;
		if (json.has("result") && json.getJSONObject("result").has("values")){
			courses = json.getJSONObject("result").getJSONArray("values");
			ArrayList<Course> courseReviews = new ArrayList<Course>();
			for (int j = 0; j < courses.length(); ++j) {
				JSONObject course = courses.getJSONObject(j);
				JSONObject instructor = null;
				Instructor i = null;
				if(course.has("instructor")){
					instructor = course.getJSONObject("instructor");
					String i_id = null;
					String i_name = null;
					String i_path = null;
					if(instructor.has("id"))
						i_id = instructor.getString("id");
					if(instructor.has("name"))
						i_name = instructor.getString("name");
					if(instructor.has("path"))
						i_path = instructor.getString("path");
					i = new Instructor(i_id, i_name, i_path);
				}
				JSONObject ratings = null;
				Ratings r = null;
				if(course.has("ratings")){
					ratings = course.getJSONObject("ratings");
					String rAmountLearned = null;
					String rCommAbility = null;
					String rCourseQuality = null;
					String rDifficulty = null;
					String rInstructorAccess = null;
					String rInstructorQuality = null;
					String rReadingsValue = null;
					String rRecommendMajor = null;
					String rRecommendNonMajor = null;
					String rStimulateInterest = null;
					String rWorkRequired = null;

					if(ratings.has("rAmountLearned"))
						rAmountLearned = ratings.getString("rAmountLearned");
					if(ratings.has("rCommAbility"))
						rCommAbility = ratings.getString("rCommAbility");
					if(ratings.has("rCourseQuality"))
						rCourseQuality = ratings.getString("rCourseQuality");
					if(ratings.has("rDifficulty"))
						rDifficulty = ratings.getString("rDifficulty");
					if(ratings.has("rInstructorAccess"))
						rInstructorAccess = ratings.getString("rInstructorAccess");
					if(ratings.has("rInstructorQuality"))
						rInstructorQuality = ratings.getString("rInstructorQuality");
					if(ratings.has("rReadingsValue"))
						rReadingsValue = ratings.getString("rReadingsValue");
					if(ratings.has("rRecommendMajor"))
						rRecommendMajor =ratings.getString("rRecommendMajor");
					if(ratings.has("rRecommendNonMajor"))
						rRecommendNonMajor = ratings.getString("rRecommendNonMajor");
					if(ratings.has("rStimulateInterest"))
						rStimulateInterest = ratings.getString("rStimulateInterest");
					if(ratings.has("rWorkRequired"))
						rWorkRequired = ratings.getString("rWorkRequired");

					r = new Ratings(rAmountLearned!=null?Double.parseDouble(rAmountLearned):null,
							rCommAbility!=null?Double.parseDouble(rCommAbility):null,
							rCourseQuality!=null?Double.parseDouble(rCourseQuality):null,
							rDifficulty!=null?Double.parseDouble(rDifficulty):null,
							rInstructorAccess!=null?Double.parseDouble(rInstructorAccess):null,
							rInstructorQuality!=null?Double.parseDouble(rInstructorQuality):null,
							rReadingsValue!=null?Double.parseDouble(rReadingsValue):null,
							rRecommendMajor!=null?Double.parseDouble(rRecommendMajor):null,
							rRecommendNonMajor!=null?Double.parseDouble(rRecommendNonMajor):null,
							rStimulateInterest!=null?Double.parseDouble(rStimulateInterest):null,
							rWorkRequired!=null?Double.parseDouble(rWorkRequired):null);
				}
				JSONObject section = null;
				Section s = null;
				if(course.has("section") && course.getJSONObject("section").has("aliases")){
					section = course.getJSONObject("section");
					JSONArray a = section.getJSONArray("aliases");
					String[] aliases = new String[a.length()];
					for (int p = 0; p < a.length(); ++p) {
						aliases[p] = a.getString(p);
					}
					String s_id = null;
					String s_path = null;
					String s_name = null;
					String s_sectionnum= null;
					if(section.has("id"))
						s_id = section.getString("id");
					if(section.has("path"))
						s_path = section.getString("path");
					if(section.has("name"))
						s_name = section.getString("name");
					if(section.has("sectionnum"))
						s_sectionnum = section.getString("sectionnum");
					s = new Section(aliases, s_id,s_path,s_name,s_sectionnum);
				}
				String comments = null;
				String id = null;
				int num_reviewers = 0;
				int num_students = 0;
				String c_path = null;
				if(course.has("comments"))
					comments = course.getString("comments");
				if(course.has("id"))
					id = course.getString("id");
				if(course.has("num_reviewers"))
					num_reviewers = course.getInt("num_reviewers");
				if(course.has("path"))
					c_path = course.getString("path");
				if(course.has("num_students"));
				num_students = course.getInt("num_students");
				Course c = new Course(comments,id,i,num_reviewers,num_students,c_path,r,s);
				courseReviews.add(c);
			}
			return courseReviews;
		}
		return null;
	}
}