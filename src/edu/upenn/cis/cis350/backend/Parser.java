package edu.upenn.cis.cis350.backend;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.Instructor;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.Ratings;
import edu.upenn.cis.cis350.objects.Section;

public class Parser {

	Sorter s = new Sorter();
	public static final String baseURL = "http://api.penncoursereview.com/v1";
	public static final String token = "?token=cis350a_3uZg7s5d62hHBtZGeTDl"; // private token (github repo is private)


	public JSONArray getReviewsForDept(KeywordMap dept_map) {
		String dept_path = dept_map.getPath();
		/*if(dept == null) return null;
		dept = dept.trim().toUpperCase();

		//change this stuff once autocomplete works to get dept name, id
		try{
			JSONObject all_dept = JSONRequest.retrieveJSONObject(baseURL + "/depts" + token);

		JSONArray dept_array = all_dept.getJSONObject("result").getJSONArray("values");
		String dept_name = "";
		String dept_id = "";
		for(int h = 0; h < dept_array.length(); h ++){
			if(dept_array.getJSONObject(h).getString("id").equalsIgnoreCase(dept)){
				dept_name = dept_array.getJSONObject(h).getString("name");
				dept_id = dept_array.getJSONObject(h).getString("id");
				break;
			}

		}

		System.out.println(dept);
		String path = "/depts/"+dept;*/
		String url = baseURL + dept_path + token;
		try{
			JSONObject json = JSONRequest.retrieveJSONObject(url);
			if (json == null) {
				return null;
			}
			System.out.println(url);

			JSONObject result = null;
			if (json.has("result")) {
				result = json.getJSONObject("result");
				JSONArray coursehistories = null;
				if (result.has("coursehistories")) {
					coursehistories = result.getJSONArray("coursehistories");
					return coursehistories;
				}
			}
			return null;
		} catch (JSONException e) { 
			e.printStackTrace(); 
			return null;
		}
	}

	public CourseAverage getCourseAvgForDept(JSONObject o) {
		String course_path = null;
		String course_name = "";
		String course_id = "";
		try {
			if(o.has("aliases"))
				course_id = o.getJSONArray("aliases").getString(0);
			if(o.has("name"))
				course_name = o.getString("name");
			if (o.has("path")) {
				course_path = o.getString("path");
				CourseAverage a = new CourseAverage(course_name, course_id, course_path, storeReviews(course_path));
				return a;
			}
			return null;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

	}

	//path is of form: "/instructors/1-DONALD-D-FITTS
	public JSONArray getReviewsForInstructor(KeywordMap instructor_map){
		String path = instructor_map.getPath();
		if (path == null) return null;
		//String reviewpath = path + "/reviews";
		//String instructor_name = "GET NAME FROM DATABASE"; //TBD
		try {
			//Log.w("PATH", reviewpath);

			JSONObject js = JSONRequest.retrieveJSONObject(baseURL + path + token);
			JSONArray values = js.getJSONObject("result").getJSONObject("reviews").getJSONArray("values");
			
			return values;
			//reviews = createCourseReview(reviewpath, null, instructor_name, null, null);
		} catch (JSONException e) { 
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Course> getCourseForInstructor(JSONObject section) {
		try {
			String section_path = section.getJSONObject("section").getString("path");
			Log.w("section_path", section_path);
			JSONObject section_result = JSONRequest.retrieveJSONObject(baseURL + section_path + token).getJSONObject("result");
			//section path = courses/11706/sections/001
			JSONObject course_info = section_result.getJSONObject("courses");
			JSONArray alias = course_info.getJSONArray("aliases");
			String [] aliases = new String[alias.length()];
			for(int j = 0; j < alias.length(); j++)
				aliases[j] = alias.getString(j);

			String semester = course_info.getString("semester");
			String name = course_info.getString("name");
			String course_path = section_result.getJSONObject("reviews").getString("path");
			return createCourseReview(course_path, aliases, name, null, semester);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Course> getReviewsForCourse(KeywordMap course_map) {
		if (course_map == null) return null;
		String course_path = course_map.getPath();

		/*String dept = course.substring(0, course.indexOf('-'));

		String url = baseURL + "/depts/"+ dept + token;

		JSONObject json = JSONRequest.retrieveJSONObject(url);
		if (json == null) {
			return null;
		}
		JSONObject result = null;
		JSONArray coursehistories = null;
		if (json.has("result")) {
			try {
				result = (JSONObject)json.get("result");
				if (result.has("coursehistories")) {
					coursehistories = (JSONArray)result.getJSONArray("coursehistories");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}	
		else {
			return null;
		}


		String path ="";
		for (int i = 0; i < coursehistories.length(); ++i) {
			try {
				JSONObject j = coursehistories.getJSONObject(i);
				JSONArray aliases = null;
				if (j.has("aliases")) {
					aliases = j.getJSONArray("aliases");

					for (int k = 0; k < aliases.length(); ++k) {
						if (aliases.getString(k).toLowerCase().equals(course)) {
							System.out.println(j.toString());
							path = (String)j.get("path");
							break;
						}
					}
				}
				else {
					return null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		 */
		System.out.println(course_path);
		ArrayList<Course> reviews = new ArrayList<Course>();
		reviews = storeReviews(course_path);

		System.out.println(reviews.size());

		return displayCourseReviews(reviews);
	}

	public ArrayList<Course> displayCourseReviews(ArrayList<Course> reviews) {
		// Right now this method just sorts by difficulty, add additional sorting functionality here or in other methods?
		reviews = s.sortByRating(reviews, "difficulty", 0);
		return reviews;
	}

	//path is format: "/coursehistories/2"
	public ArrayList<Course> storeReviews(String path) {
		ArrayList<Course> courseReviews = new ArrayList<Course>();
		JSONObject js = JSONRequest.retrieveJSONObject(baseURL + path + token);

		String[] course_aliases = null;

		if (js.has("result")) {
			JSONObject jresult;
			try {
				jresult = js.getJSONObject("result");
				if (jresult.has("aliases")) {
					JSONArray als = jresult.getJSONArray("aliases");
					course_aliases = new String[als.length()];
					for (int k = 0; k < als.length(); ++k) {
						course_aliases[k] = als.getString(k);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

			if (jresult.has("courses")) {
				try {
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
							JSONObject course_object = JSONRequest.retrieveJSONObject(baseURL + course_path + token);
							JSONObject course_result = null;
							if (course_object.has("result")) {
								course_result = course_object.getJSONObject("result");
								if (course_result.has("description")) {
									description = course_result.getString("description");
								}
								String review_path = course_path + "/reviews";
								ArrayList<Course> c =  createCourseReview(review_path,course_aliases,name,description,semester);
								courseReviews.addAll(c);
							}
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return courseReviews;
	}

	public ArrayList<Course> createCourseReview(String path, String[] course_aliases, String name, String description, String semester) throws JSONException {
		ArrayList<Course> course_list = new ArrayList<Course>();
		JSONObject json = JSONRequest.retrieveJSONObject(baseURL + path + token);
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
				course_list.add(c);
			}
		}
		return course_list;
	}
}