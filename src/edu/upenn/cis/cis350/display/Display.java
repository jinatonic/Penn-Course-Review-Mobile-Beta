package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import edu.upenn.cis.cis350.backend.Sorter;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;
import edu.upenn.cis.cis350.objects.Ratings;

public abstract class Display extends Activity {

	public enum Sort {INSTRUCTOR_ASC, INSTRUCTOR_DES, NAME_ASC, NAME_DES, CQ_ASC, 
		CQ_DES, IQ_ASC, IQ_DES, DIFFICULTY_ASC, DIFFICULTY_DES, ID_ASC, ID_DES,
		SEM_ASC, SEM_DES }
	Sort sortingField;
	ArrayList<Course> courseReviews; // for course, instructor
	ArrayList<CourseAverage> courseAvgs; // for department
	HashMap<Integer, Course> row_map; // for retrieving Course from row clicked on
	Type displayType; // Type of page displayed (course, instructor, dept)

	/** Formats and prints each row of the table of reviews for course,
	 * instructor, or department pages
	 * @param displayType - the type of page we are printing on (course, instructor, or dept)
	 * */
	public void printReviews(Type displayType) {
		// Set the current page display type
		this.displayType = displayType;

		switch (displayType) {
		case COURSE:
		case INSTRUCTOR:
			ListView lv = (ListView)findViewById(R.id.reviews);
			row_map = new HashMap<Integer, Course>(); // Used for mapping row number to Course in the row
			// Grid item mapping to pass to ListView SimpleAdapter
			String[] columnHeaders = new String[] {"col_1", "col_2", "col_3", "col_4"};
			int[] cellIds = new int[] {R.id.item1, R.id.item2, R.id.item3, R.id.item4};
			
			// List of all rows (each row being a map of columnHeader to cell text)
			List<HashMap<String, String>> allRows = new ArrayList<HashMap<String, String>>();
			
			// Iterate through Courses and create a new row mapping for each
			Iterator<Course> iter = courseReviews.iterator();
			int rowNumber = 0; // used to keep track of which row we are on, for mapping in list_map
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
					row.put("col_2", curCourse.getSemester());
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
				}
			});
			
			break; // end of COURSE and INSTRUCTOR cases
		case DEPARTMENT:
			// Iterate through reviews for course and fill table cells
			Iterator<CourseAverage> iter2 = courseAvgs.iterator();
			TableLayout tl2 = (TableLayout)findViewById(R.id.reviews);
			tl2.removeAllViews();

			while(iter2.hasNext()) {
				CourseAverage curCourseAvg = iter2.next();
				Ratings curRatings = curCourseAvg.getRatings();

				/* Create a new row to be added. */
				TableRow tr2 = new TableRow(this);
				tr2.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));

				/* Create a TextView for Course Id to be the row-content. */
				TextView courseId = createRow(165, Gravity.LEFT, curCourseAvg.getId(), 1);
				tr2.addView(courseId);

				/* Create a TextView for Course Quality to be the row-content. */
				TextView courseQuality = createRow(89, Gravity.CENTER_HORIZONTAL,
						((Double)curRatings.getCourseQuality()).toString(), 2);
				tr2.addView(courseQuality);

				/* Create a TextView for Instructor Quality to be the row-content. */
				TextView instructorQuality = createRow(89, Gravity.CENTER_HORIZONTAL,
						((Double)curRatings.getInstructorQuality()).toString(), 3);
				tr2.addView(instructorQuality);

				/* Create a TextView for Difficulty to be the row-content. */
				TextView difficulty = createRow(89, Gravity.CENTER_HORIZONTAL,
						((Double)curRatings.getDifficulty()).toString(), 4);
				tr2.addView(difficulty);

				/* Add row to TableLayout. */
				tl2.addView(tr2,new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
			} // while loop
			tl2.invalidate();
			break;
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

	TextView createRow(int width, int gravity, String text, int colNum) {
		TextView row = new TextView(this);
		row.setHeight(70);
		row.setWidth(width);
		row.setTextSize((float)11);
		row.setTextColor(getResources().getColor(R.color.text_gray));
		row.setGravity(gravity);
		row.setText(text);
		row.setBackgroundResource(R.layout.cell_gridline);
		row.setClickable(true);
		LayoutParams diffParams = new LayoutParams(
				LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		diffParams.column = colNum;
		row.setLayoutParams(diffParams);
		return row;
	}
}
