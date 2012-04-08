package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
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
	Type displayType; // Type of page displayed (course, instructor, dept)

	public void printReviews(Type displayType) {
		// Set the current page display type
		this.displayType = displayType;

		switch (displayType) {
		case COURSE:
		case INSTRUCTOR:
			// Iterate through reviews for course and fill table cells
			Iterator<Course> iter = courseReviews.iterator();
			TableLayout tl = (TableLayout)findViewById(R.id.reviews);
			tl.removeAllViews();

			while(iter.hasNext()) {
				Course curCourse = iter.next();

				/* Create a new row to be added. */
				TableRow tr = new TableRow(this);
				tr.setLayoutParams(new LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));

				switch (displayType) {
				case COURSE:
					/* Create a TextView for Instructor to be the row-content. */
					TextView instructor = createRow(165, Gravity.LEFT, curCourse.getInstructor().getName(), 1);
					tr.addView(instructor);

					/* Create a TextView for Course Quality to be the row-content. */
					TextView courseQuality = createRow(89, Gravity.CENTER_HORIZONTAL,
							((Double)curCourse.getRatings().getCourseQuality()).toString(), 2);
					tr.addView(courseQuality);
					break;
				case INSTRUCTOR:
					/* Create a TextView for Course Id to be the row-content. */
					TextView courseId = createRow(165, Gravity.LEFT, curCourse.getAlias(), 1);
					tr.addView(courseId);

					/* Create a TextView for Course Semester to be the row-content. */
					TextView courseSemester = createRow(89, Gravity.CENTER_HORIZONTAL, curCourse.getSemester(), 2);
					tr.addView(courseSemester);
					break;
				default:
					break;

				}
				/* Create a TextView for Instructor Quality to be the row-content. */
				TextView instructorQuality = createRow(89, Gravity.CENTER_HORIZONTAL,
						((Double)curCourse.getRatings().getInstructorQuality()).toString(), 3);
				tr.addView(instructorQuality);

				/* Create a TextView for Difficulty to be the row-content. */
				TextView difficulty = createRow(89, Gravity.CENTER_HORIZONTAL,
						((Double)curCourse.getRatings().getDifficulty()).toString(), 4);
				tr.addView(difficulty);

				/* Add row to TableLayout. */
				tl.addView(tr,new TableLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT));
			} // while loop
			tl.invalidate();
			break;
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

	public void onClickPick(View v) {
		int x = 0;
		x++;
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
