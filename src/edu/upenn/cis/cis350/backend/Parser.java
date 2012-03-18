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

import android.content.Context;
import android.util.Log;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.Instructor;
import edu.upenn.cis.cis350.objects.Ratings;
import edu.upenn.cis.cis350.objects.Section;

public class Parser {

	Sorter s = new Sorter();
	public final String baseURL = "http://api.penncoursereview.com/v1";
	public final String token = "?token=cis350a_3uZg7s5d62hHBtZGeTDl"; // private token (github repo is private)

	public SearchCache cache;
	
	public Parser(SearchCache _cache) {
		cache = _cache;
	}
	
	public JSONObject retrieveJSONObject(String path){
		try{
			URL url = new URL(path);
			Log.w("Parser: retrieveJSONObject", "url=" + url);
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
			Log.w("Parser: retrieveJSONObject", "IOException: Bad Url");
			e.printStackTrace();
			return null;
		} catch (JSONException e) {
			Log.w("Parser: retrieveJSONObject", "JSONException: mis-formatted JSON");
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Course> getReviewsForDept(String dept) throws IOException, ParseException, JSONException {
		if(dept == null) return null;
		dept = dept.trim().toUpperCase();
		System.out.println(dept);
		String path = "/depts/"+dept;
		String url = baseURL + path + token;

		JSONObject json = retrieveJSONObject(url);
		if (json == null) {
			return null;
		}
		System.out.println(url);
		ArrayList<Course> reviews = new ArrayList<Course>();
		JSONObject result = null;
		if (json.has("result")) {
			result = json.getJSONObject("result");
			JSONArray coursehistories = null;
			if (result.has("coursehistories")) {
				coursehistories = result.getJSONArray("coursehistories");
				for (int i = 0; i < coursehistories.length(); i++) {
					JSONObject o = coursehistories.getJSONObject(i);
					String course_path = null;
					if (o.has("path")) {
						course_path = o.getString("path");
						reviews.addAll(storeReviews(course_path));
					}
				}
			}
		}	
		System.out.println(reviews.size());

		return displayCourseReviews(reviews);
	}
	
	public ArrayList<Course> getReviewsForCourse(String course) throws IOException, ParseException, JSONException {
		if(course == null) return null;
		course = course.trim();
		if(course.length()>7) return null;
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
		if(dept == "" || num == "") return null;
		String alias = dept + "-" + num;
		System.out.println(alias);
		
		/* Try to get the data from cache if exists */
		ArrayList<Course> reviews = cache.getCourse(alias);
		if (reviews.size() > 0)
			return reviews;

		String url = baseURL + "/depts/"+ dept + token;

		JSONObject json = retrieveJSONObject(url);
		if (json == null) {
			return null;
		}
		JSONObject result = null;
		JSONArray coursehistories = null;
		if (json.has("result")) {
			result = (JSONObject)json.get("result");
			if (result.has("coursehistories")) {
				coursehistories = (JSONArray)result.getJSONArray("coursehistories");
			}
		}	
		else {
			return null;
		}


		String path ="";
		for (int i = 0; i < coursehistories.length(); ++i) {
			JSONObject j = coursehistories.getJSONObject(i);
			JSONArray aliases = null;
			if (j.has("aliases")) {
				aliases = j.getJSONArray("aliases");

				for (int k = 0; k < aliases.length(); ++k) {
					if (aliases.get(k).equals(alias)) {
						System.out.println(j.toString());
						path = (String)j.get("path");
						break;
					}
				}
			}
			else {
				return null;
			}
		}

		System.out.println(path);
		reviews = new ArrayList<Course>();
		reviews = storeReviews(path);

		System.out.println(reviews.size());

		return displayCourseReviews(reviews);
	}

	public ArrayList<Course> displayCourseReviews(ArrayList<Course> reviews) {
		// Right now this method just sorts by difficulty, add additional sorting functionality here or in other methods?
		reviews = s.sortByRating(reviews, "difficulty", 0);
		/*
		ArrayList<Course> courses = new ArrayList<Course>();
		for (Course c : reviews) {
			//s += c.getID() + "\n"+ c.getRatings().getDifficulty() + "\n\n";
			courses.add(c);
		}
		 */
		return reviews;
	}

	public ArrayList<Course> storeReviews(String path) throws IOException, ParseException, JSONException {
		ArrayList<Course> courseReviews = new ArrayList<Course>();
		JSONObject js = retrieveJSONObject(baseURL + path + token);

		String[] course_aliases = null;
		
		if (js.has("result")) {
			JSONObject jresult = js.getJSONObject("result");
			if (jresult.has("aliases")) {
				JSONArray als = jresult.getJSONArray("aliases");
				course_aliases = new String[als.length()];
				for (int k = 0; k < als.length(); ++k) {
					course_aliases[k] = als.getString(k);
				}
			}

			if (jresult.has("courses")) {
				JSONArray courses_array = jresult.getJSONArray("courses");
				for (int m = 0; m < courses_array.length(); m++) {
					String description = null;
					String name = null;
					String semester = null;
					String course_path = null;
					JSONObject course2 = courses_array.getJSONObject(m);
					if (course2.has("name")) {
						name = course2.getString("name");
					}
					if (course2.has("semester")) {
						semester = course2.getString("semester");
					}
					if (course2.has("path")) {
						course_path = course2.getString("path");
						JSONObject course_object = retrieveJSONObject(baseURL + course_path + token);
						JSONObject course_result = null;
						if (course_object.has("result")) {
							course_result = course_object.getJSONObject("result");
							if (course_result.has("description")) {
								description = course_result.getString("description");
							}
							String review_path = course_path + "/reviews";
							Course c = createCourseReview(review_path,course_aliases,name,description,semester);
							
							/* Add this course review to database */
							cache.addCourse(c);
							
							courseReviews.add(c);
							Log.w("TESTTT","ADDING NEW COURSE");
						}
					}
				}
			}
		}
		return courseReviews;
	}

	public Course createCourseReview(String path,String[] course_aliases,String name, String description, String semester) throws NumberFormatException, JSONException {
		JSONObject json = retrieveJSONObject(baseURL + path + token);
		JSONArray courses = null;
		if (json.has("result") && json.getJSONObject("result").has("values")) {
			courses = json.getJSONObject("result").getJSONArray("values");

			for (int j = 0; j < courses.length(); j++) {
				JSONObject course = courses.getJSONObject(j);
				JSONObject instructor = null;
				Instructor i = null;
				if(course.has("instructor")) {
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
				if (course.has("ratings")) {
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

					if (ratings.has("rAmountLearned")) {
						rAmountLearned = ratings.getString("rAmountLearned");
					}
					if (ratings.has("rCommAbility")) {
						rCommAbility = ratings.getString("rCommAbility");
					}
					if (ratings.has("rCourseQuality")) {
						rCourseQuality = ratings.getString("rCourseQuality");
					}
					if (ratings.has("rDifficulty")) {
						rDifficulty = ratings.getString("rDifficulty");
					}
					if (ratings.has("rInstructorAccess")) {
						rInstructorAccess = ratings.getString("rInstructorAccess");
					}
					if (ratings.has("rInstructorQuality")) {
						rInstructorQuality = ratings.getString("rInstructorQuality");
					}
					if (ratings.has("rReadingsValue")) {
						rReadingsValue = ratings.getString("rReadingsValue");
					}
					if (ratings.has("rRecommendMajor")) {
						rRecommendMajor =ratings.getString("rRecommendMajor");
					}
					if (ratings.has("rRecommendNonMajor")) {
						rRecommendNonMajor = ratings.getString("rRecommendNonMajor");
					}
					if (ratings.has("rStimulateInterest")) {
						rStimulateInterest = ratings.getString("rStimulateInterest");
					}
					if (ratings.has("rWorkRequired")) {
						rWorkRequired = ratings.getString("rWorkRequired");
					}

					r = new Ratings(
										rAmountLearned != null ? Double.parseDouble(rAmountLearned) : null,
										rCommAbility != null ? Double.parseDouble(rCommAbility) : null,
										rCourseQuality != null ? Double.parseDouble(rCourseQuality) : null,
										rDifficulty != null ? Double.parseDouble(rDifficulty) : null,
										rInstructorAccess != null ? Double.parseDouble(rInstructorAccess) : null,
										rInstructorQuality != null ? Double.parseDouble(rInstructorQuality) : null,
										rReadingsValue != null ? Double.parseDouble(rReadingsValue) : null,
										rRecommendMajor != null ? Double.parseDouble(rRecommendMajor) : null,
										rRecommendNonMajor != null ? Double.parseDouble(rRecommendNonMajor) : null,
										rStimulateInterest != null ? Double.parseDouble(rStimulateInterest) : null,
										rWorkRequired != null ? Double.parseDouble(rWorkRequired) : null
									);
				}
				JSONObject section = null;
				Section s = null;
				if (course.has("section") && course.getJSONObject("section").has("aliases")) {
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
					if (section.has("id"))
						s_id = section.getString("id");
					if (section.has("path"))
						s_path = section.getString("path");
					if (section.has("name"))
						s_name = section.getString("name");
					if (section.has("sectionnum"))
						s_sectionnum = section.getString("sectionnum");
					s = new Section((aliases == null) ? "" : aliases[0], s_id,s_path,s_name,s_sectionnum);
				}
				String comments = null;
				String id = null;
				int num_reviewers = 0;
				int num_students = 0;
				String c_path = null;
				if (course.has("comments"))
					comments = course.getString("comments");
				if (course.has("id"))
					id = course.getString("id");
				if (course.has("num_reviewers"))
					num_reviewers = course.getInt("num_reviewers");
				if (course.has("path"))
					c_path = course.getString("path");
				if (course.has("num_students"));
				num_students = course.getInt("num_students");
				Course c = new Course((course_aliases == null) ? "" : course_aliases[0], name, description, semester, comments,id,i,num_reviewers,num_students,c_path,r,s);
				return c;
			}
		}
		return null;
	}
}