package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.Sorter;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.KeywordMap;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;
import edu.upenn.cis.cis350.objects.Ratings;

public abstract class Display extends QueryWrapper {


	public enum Sort {INSTRUCTOR_ASC, INSTRUCTOR_DES, NAME_ASC, NAME_DES, CQ_ASC, 
		CQ_DES, IQ_ASC, IQ_DES, DIFFICULTY_ASC, DIFFICULTY_DES, ID_ASC, ID_DES,
		SEM_ASC, SEM_DES }
	Sort sortingField;
	ArrayList<Course> courseReviews; // for course, instructor
	ArrayList<CourseAverage> courseAvgs; // for department
	HashMap<Integer, Course> row_map; // for retrieving Course from row clicked on
	HashMap<Integer, CourseAverage> row_map_dept; // for retrieving CourseAverage from row clicked on, for use by dept page
	Type displayType; // Type of page displayed (course, instructor, dept)

	public String keyword;

	protected ImageButton favHeart;

	Typeface timesNewRoman;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Set font Times New Roman
		timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		
		favHeart = (ImageButton) findViewById(R.id.fav_heart);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		
		MenuInflater inflater = getMenuInflater();

		recentSearches.open();
		if (recentSearches.ifExists(keyword, 1)) {
			inflater.inflate(R.menu.result_menu2, menu);
		}
		else {
			inflater.inflate(R.menu.result_menu1, menu);
		}
		recentSearches.close();

		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_remove_fav:
			// Remove from db
			recentSearches.open();
			favHeart.setImageResource(R.drawable.favorites_unselected_100);
			recentSearches.removeKeyword(keyword, 1);
			recentSearches.close();
			return true;
		case R.id.menu_add_fav:
			recentSearches.open();
			favHeart.setImageResource(R.drawable.favorites_selected_100);
			recentSearches.addKeyword(keyword, 1);
			recentSearches.close();
			return true;
		case R.id.menu_quit:
			setResult(Constants.RESULT_QUIT, null);
			this.finish();
			return true;
		default: 
			return super.onOptionsItemSelected(item);
		}
	}

	/* Called when user taps on favorites heart icon in upper right corner on a display page */
	public void onFavHeartClick(View v) {
		recentSearches.open();
		if (recentSearches.ifExists(keyword, 1)) { // was favorited, now removing
			// If keyword already exists, toggle to unselected heart icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_unselected_100);

			// Remove keyword from favorites
			recentSearches.removeKeyword(keyword, 1);
		}
		else { // was not favorited, now favoriting
			// Toggle to selected heart icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_selected_100);

			// Add keyword to favorites
			recentSearches.addKeyword(keyword, 1);
		}
		recentSearches.close();
	}
	
	/* Called when user taps on PCR header */
	public void onPCRHeaderClick(View v) {
		// TODO
	}

	/** Formats and prints each row of the table of reviews for course,
	 * instructor, or department pages
	 * @param displayType - the type of page we are printing on (course, instructor, or dept)
	 * */
	public void printReviews(final Type displayType) {
		// Set the current page display type
		this.displayType = displayType;

		ListView lv = (ListView)findViewById(R.id.reviews);
		lv.setClickable(true);
		lv.setTextFilterEnabled(true);

		// Grid item mapping to pass to ListView SimpleAdapter
		String[] columnHeaders = new String[] {"col_1", "col_2", "col_3", "col_4"};
		int[] cellIds = new int[] {R.id.item1, R.id.item2, R.id.item3, R.id.item4};

		// List of all rows (each row being a map of columnHeader to cell text)
		List<HashMap<String, String>> allRows = new ArrayList<HashMap<String, String>>();

		int rowNumber = 0; // used to keep track of which row we are on, for mapping in row_map

		switch (displayType) {
		case COURSE:
		case INSTRUCTOR:

			row_map = new HashMap<Integer, Course>(); // Used for mapping row number to Course in the row

			// Iterate through Courses and create a new row mapping for each
			Iterator<Course> iter = courseReviews.iterator();
			while(iter.hasNext()) {
				Course curCourse = iter.next();
				Ratings curRatings = curCourse.getRatings();

				// Map the current row number to this Course
				row_map.put(new Integer(rowNumber), curCourse);

				// Mapping of columnHeader to text in each cell for this row
				HashMap<String, String> row = new HashMap<String, String>();

				switch (displayType) {
				case COURSE:
					// Instructor
					row.put("col_1", curCourse.getInstructor().getName());
					// Course Quality
					row.put("col_2", ((Double)curRatings.getCourseQuality()).toString());
					break;
				case INSTRUCTOR:
					// Course Id
					row.put("col_1", curCourse.getAlias());
					// Course Semester
					row.put("col_2", curCourse.getFullSemester());
					break;
				default:
					break;

				}
				// Instructor Quality
				row.put("col_3", ((Double)curRatings.getInstructorQuality()).toString());
				// Difficulty
				row.put("col_4", ((Double)curRatings.getDifficulty()).toString());
				// Add this row to allRows
				allRows.add(row);

				rowNumber++;
			} // while loop

			SimpleAdapter adapter = new SimpleAdapter(
					this, allRows, R.layout.list_row, columnHeaders, cellIds);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v, int position, long id) {
					Log.v("position", ""+position);
					Log.v("id",""+id);
					Course c = row_map.get(new Integer(position));
					if (c == null) {
						Log.v("Course", "course is null");
					}
					final Dialog dialog = new Dialog(Display.this);
					dialog.setContentView(R.layout.main_dialog);					

					dialog.setTitle(c.getAlias());

					dialog.setCancelable(true);
					dialog.setCanceledOnTouchOutside(true);

					// Set up title
					TextView title = (TextView) dialog.findViewById(R.id.CourseContent);
					title.setTypeface(timesNewRoman);
					String titleText = 
							c.getName() + "\n"
									+ c.getFullSemester() + "\n"
									+ c.getInstructor().getName() + "\n"
									+ c.getNumReviewers() + "/" + c.getNumStudents() + " responses\n";

					title.setText((CharSequence) titleText);

					//set up comments
					TextView description = (TextView) dialog.findViewById(R.id.Comments);
					String ratingString = "Course Quality: " + c.getRatings().getCourseQuality();
					ratingString += "\nInstructor Quality: " + c.getRatings().getInstructorQuality();
					ratingString += "\nDifficulty: " + c.getRatings().getDifficulty();
					ratingString += "\nMajor Recommendation: " + c.getRatings().getRecommendMajor();
					ratingString += "\nNonmajor Recommendation: " + c.getRatings().getRecommendNonMajor();
					ratingString += "\nAmount Learned: " + c.getRatings().getAmountLearned();
					ratingString += "\nWork Required: " + c.getRatings().getWorkRequired()+ "\n\n";

					String commentString = "";
					if (displayType == KeywordMap.Type.COURSE) {
						commentString = c.getComments();

					} else if (displayType == KeywordMap.Type.INSTRUCTOR) {
						commentString = c.getDescription();
					}
					if (commentString == null || commentString.equals("null") || commentString.length() < 6) {
						commentString = "There are no available comments for this course.\n";
					}
					ratingString += commentString;
					description.setText((CharSequence) ratingString);

					//now that the dialog is set up, it's time to show it    
					dialog.show();
				}

			});

			break; // end of COURSE and INSTRUCTOR cases
		case DEPARTMENT:

			row_map_dept = new HashMap<Integer, CourseAverage>(); // Used for mapping row number to Course in the row

			// Iterate through reviews for course and fill table cells
			Iterator<CourseAverage> iter2 = courseAvgs.iterator();

			while(iter2.hasNext()) {
				CourseAverage curCourseAvg = iter2.next();
				Ratings curRatings = curCourseAvg.getRatings();

				// Map the current row number to this Course
				row_map_dept.put(new Integer(rowNumber), curCourseAvg);

				// Mapping of columnHeader to text in each cell for this row
				HashMap<String, String> row = new HashMap<String, String>();

				// Course Id
				row.put("col_1", curCourseAvg.getId());
				// Course Quality
				row.put("col_2", ((Double)curRatings.getCourseQuality()).toString());
				// Instructor Quality
				row.put("col_3", ((Double)curRatings.getInstructorQuality()).toString());
				// Difficulty
				row.put("col_4", ((Double)curRatings.getDifficulty()).toString());

				// Add this row to allRows
				allRows.add(row);

				rowNumber++;
			} // while loop

			SimpleAdapter adapter_dept = new SimpleAdapter(
					this, allRows, R.layout.list_row, columnHeaders, cellIds);
			lv.setAdapter(adapter_dept);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView parent, View v, int position, long id) {
					Log.v("position", ""+position);
					Log.v("id",""+id);
					CourseAverage c = row_map_dept.get(new Integer(position));
					
					if (c == null) {
						Log.v("Course", "course is null");
						return;
					}
					
					preProcessForNextPage(Constants.COURSE_TAG + c.getId() + " - " + c.getName(), true);
				}
			});

			break; // end of DEPT case
		} // outer switch
	}

	public void onClickSort(View v) {
		Sorter s = new Sorter();

		switch (displayType) {
		case COURSE:
			if(v.getId() == R.id.instructor_tab) {
				if (sortingField == Sort.INSTRUCTOR_ASC) {
					courseReviews = s.sortAlphabetically(courseReviews, Type.INSTRUCTOR, 1);
					sortingField = Sort.INSTRUCTOR_DES;
				} else {
					courseReviews = s.sortAlphabetically(courseReviews, Type.INSTRUCTOR, 0);
					sortingField = Sort.INSTRUCTOR_ASC;
				}
			} else if(v.getId() == R.id.course_quality_tab) {
				if (sortingField == Sort.CQ_ASC) {
					courseReviews = s.sortByRating(courseReviews, "courseQuality", 1);
					sortingField = Sort.CQ_DES;
				} else {
					courseReviews = s.sortByRating(courseReviews, "courseQuality", 0);
					sortingField = Sort.CQ_ASC;
				}
			} else if(v.getId() == R.id.instructor_quality_tab) {
				if (sortingField == Sort.IQ_ASC) {
					courseReviews = s.sortByRating(courseReviews, "instructorQuality", 1);
					sortingField = Sort.IQ_DES;
				} else {
					courseReviews = s.sortByRating(courseReviews, "instructorQuality", 0);
					sortingField = Sort.IQ_ASC;
				}
			} else if(v.getId() == R.id.difficulty_tab) {
				if (sortingField == Sort.DIFFICULTY_ASC) {
					courseReviews = s.sortByRating(courseReviews, "difficulty", 1);
					sortingField = Sort.DIFFICULTY_DES;
				} else {
					courseReviews = s.sortByRating(courseReviews, "difficulty", 0);
					sortingField = Sort.DIFFICULTY_ASC;
				}
			}
			break;
		case INSTRUCTOR:
			if(v.getId() == R.id.course_id_tab) {
				if (sortingField == Sort.ID_ASC) {
					courseReviews = s.sortAlphabetically(courseReviews, Type.COURSE, 1);
					sortingField = Sort.ID_DES;
				} else {
					courseReviews = s.sortAlphabetically(courseReviews, Type.COURSE, 0);
					sortingField = Sort.ID_ASC;
				}
			} else if(v.getId() == R.id.semester_tab) {
				if (sortingField == Sort.SEM_ASC) {
					courseReviews = s.sortBySemester(courseReviews, 1);
					sortingField = Sort.SEM_DES;
				} else {
					courseReviews = s.sortBySemester(courseReviews, 0);
					sortingField = Sort.SEM_ASC;
				}
			} else if(v.getId() == R.id.instructor_quality_tab) {
				if (sortingField == Sort.IQ_ASC) {
					courseReviews = s.sortByRating(courseReviews, "instructorQuality", 1);
					sortingField = Sort.IQ_DES;
				} else {
					courseReviews = s.sortByRating(courseReviews, "instructorQuality", 0);
					sortingField = Sort.IQ_ASC;
				}
			} else if(v.getId() == R.id.difficulty_tab) {
				if (sortingField == Sort.DIFFICULTY_ASC) {
					courseReviews = s.sortByRating(courseReviews, "difficulty", 1);
					sortingField = Sort.DIFFICULTY_DES;
				} else {
					courseReviews = s.sortByRating(courseReviews, "difficulty", 0);
					sortingField = Sort.DIFFICULTY_ASC;
				}
			}
			break;
		case DEPARTMENT:
			// Map each CourseAverage into a Course for purposes of calling sorting methods on Courses
			Map<Course, CourseAverage> courseToCourseAvg = new HashMap<Course, CourseAverage>();
			for (CourseAverage cAvg : courseAvgs) {
				Course c = new Course(cAvg.getId(), cAvg.getName(), "", "", "", cAvg.getId(), null, 0,
						0, cAvg.getPath(), cAvg.getRatings(), null);
				courseToCourseAvg.put(c, cAvg);
			}

			// ArrayList of Courses to sort from and into before mapping back to CourseAverages
			ArrayList<Course> tempCourses = new ArrayList<Course>();
			for (Course tempCourse : courseToCourseAvg.keySet()) {
				tempCourses.add(tempCourse);
			}

			if(v.getId() == R.id.course_id_tab) {
				if (sortingField == Sort.ID_ASC) {
					tempCourses = s.sortAlphabetically(tempCourses, Type.COURSE, 1);
					sortingField = Sort.ID_DES;
				} else {
					tempCourses = s.sortAlphabetically(tempCourses, Type.COURSE, 0);
					sortingField = Sort.ID_ASC;
				}
			} else if(v.getId() == R.id.course_quality_tab) {
				if (sortingField == Sort.CQ_ASC) {
					tempCourses = s.sortByRating(tempCourses, "courseQuality", 1);
					sortingField = Sort.CQ_DES;
				} else {
					tempCourses = s.sortByRating(tempCourses, "courseQuality", 0);
					sortingField = Sort.CQ_ASC;
				}
			} else if(v.getId() == R.id.instructor_quality_tab) {
				if (sortingField == Sort.IQ_ASC) {
					tempCourses = s.sortByRating(tempCourses, "instructorQuality", 1);
					sortingField = Sort.IQ_DES;
				} else {
					tempCourses = s.sortByRating(tempCourses, "instructorQuality", 0);
					sortingField = Sort.IQ_ASC;
				}
			} else if(v.getId() == R.id.difficulty_tab) {
				if (sortingField == Sort.DIFFICULTY_ASC) {
					tempCourses = s.sortByRating(tempCourses, "difficulty", 1);
					sortingField = Sort.DIFFICULTY_DES;
				} else {
					tempCourses = s.sortByRating(tempCourses, "difficulty", 0);
					sortingField = Sort.DIFFICULTY_ASC;
				}
			}

			// Map the sorted Courses back to their sorted Course Averages
			courseAvgs = new ArrayList<CourseAverage>();
			for (int i = 0; i < tempCourses.size(); i++) {
				courseAvgs.add(courseToCourseAvg.get(tempCourses.get(i)));
			}
			break;
		default:
			break;
		}

		switch (displayType) {
		case COURSE:
			findViewById(R.id.instructor_tab).setBackgroundColor(0);
			findViewById(R.id.course_quality_tab).setBackgroundColor(0);
			break;
		case INSTRUCTOR:
			findViewById(R.id.course_id_tab).setBackgroundColor(0);
			findViewById(R.id.semester_tab).setBackgroundColor(0);
			break;
		case DEPARTMENT:
			findViewById(R.id.course_id_tab).setBackgroundColor(0);
			findViewById(R.id.course_quality_tab).setBackgroundColor(0);
			break;
		default:
			break;
		}
		findViewById(R.id.instructor_quality_tab).setBackgroundColor(0);
		findViewById(R.id.difficulty_tab).setBackgroundColor(0);
		v.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		printReviews(this.displayType);
	}
}
