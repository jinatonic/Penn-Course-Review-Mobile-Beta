package edu.upenn.cis.cis350.display;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.Sorter;
import edu.upenn.cis.cis350.objects.Course;
import edu.upenn.cis.cis350.objects.CourseAverage;
import edu.upenn.cis.cis350.objects.KeywordMap.Type;
import edu.upenn.cis.cis350.objects.Ratings;

/**
 * Parent activity for all the result pages
 * @author Jinyan Cao, Connie Ho, Cynthia Mai
 */

public abstract class Display extends QueryWrapper {

	public enum Sort { DEFAULT_DESC, DEFAULT_ASC, FIRST_DESC, FIRST_ASC, SECOND_DESC, SECOND_ASC, THIRD_DESC, THIRD_ASC }

	Sort sortingField;
	ArrayList<Course> courseReviews; // for course, instructor
	ArrayList<CourseAverage> courseAvgs; // for department
	HashMap<Integer, Course> row_map; // for retrieving Course from row clicked on
	HashMap<Integer, CourseAverage> row_map_dept; // for retrieving CourseAverage from row clicked on, for use by dept page
	Type displayType; // Type of page displayed (course, instructor, dept)

	// Used for selecting individual courses to show more information
	static final int COURSE_INFO_DIALOG = 8;
	Course c;

	// For selecting which field populates which column
	static final int FIELD_SELECTION_DIALOG = 7;
	TextView selectedCol = null;
	TextView defaultCol = null, firstCol = null, secondCol = null, thirdCol = null;
	int firstColId, secondColId, thirdColId, defaultColId;

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
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();		
		inflater.inflate(R.menu.result_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_search:
			setResult(Constants.RESULT_GO_TO_SEARCH);
			this.finish();
			return true;
		case R.id.menu_recent:
			showDialog(RECENT_DIALOG);
			return true;
		case R.id.menu_favorite:
			showDialog(FAVORITES_DIALOG);
			return true;
		case R.id.menu_settings:
			Intent i = new Intent(this, SettingsPage.class);
			// Start Settings Page activity
			startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
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
		else { 
			// was not favorited, now favoriting
			// Toggle to selected heart icon
			favHeart = (ImageButton) findViewById(R.id.fav_heart);
			favHeart.setImageResource(R.drawable.favorites_selected_100);

			// Add keyword to favorites
			recentSearches.addKeyword(keyword, 1);
		}
		recentSearches.close();
	}

	@Override
	protected void onResume() {
		super.onResume();

		// dismiss any remaining dialog that might be open
		removeDialog(RECENT_DIALOG);
		removeDialog(FAVORITES_DIALOG);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.NORMAL_OPEN_REQUEST) {
			if (resultCode == RESULT_OK) {
				// do nothing
			}
			else if (resultCode == Constants.RESULT_QUIT) {
				setResult(Constants.RESULT_QUIT);
				// Quit application if quit is issued
				this.finish();
			}
			else if (resultCode == Constants.RESULT_GO_TO_SEARCH) {
				setResult(Constants.RESULT_GO_TO_SEARCH);
				this.finish();
			}
			else if (resultCode == Constants.RESULT_GO_TO_START) {
				setResult(Constants.RESULT_GO_TO_START);
				this.finish();
			}
			else if (resultCode == Constants.RESULT_AUTOCOMPLETE_RESETTED) {
				setResult(Constants.RESULT_AUTOCOMPLETE_RESETTED);
				this.finish();
			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		switch (id) {
		case FIELD_SELECTION_DIALOG:
			String[] temp = null;
			switch (this.displayType) {
			case COURSE:
				temp = Constants.COURSE_SELECTION;
				break;
			case INSTRUCTOR:
				temp = Constants.INSTRUCTOR_SELECTION;
				break;
			case DEPARTMENT:
				temp = Constants.DEPT_SELECTION;
				break;
			}
			final String[] selection = temp;

			AlertDialog.Builder bDialog = new AlertDialog.Builder(this);
			ListView recentList = new ListView(this);

			recentList.setAdapter(new ArrayAdapter<String>(Display.this, R.layout.item_list, selection));
			recentList.setCacheColorHint(Color.TRANSPARENT);	// Fix issue with list turning black on scrolling
			bDialog.setView(recentList);
			bDialog.setInverseBackgroundForced(true);

			recentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int pos, long arg3) {
					String selected_field = selection[pos];
					if (selected_field.equals(Constants.amountLearned)) {
						setColumnId(Constants.amountLearnedId);
					}
					else if (selected_field.equals(Constants.commAbility)) {
						setColumnId(Constants.commAbilityId);
					}
					else if (selected_field.equals(Constants.courseQuality)) {
						setColumnId(Constants.courseQualityId);
					}
					else if (selected_field.equals(Constants.difficulty)) {
						setColumnId(Constants.difficultyId);
					}
					else if (selected_field.equals(Constants.instructorAccess)) {
						setColumnId(Constants.instructorAccessId);
					}
					else if (selected_field.equals(Constants.instructorQuality)) {
						setColumnId(Constants.instructorQualityId);
					}
					else if (selected_field.equals(Constants.readingsValue)) {
						setColumnId(Constants.readingsValueId);
					}
					else if (selected_field.equals(Constants.recommendMajor)) {
						setColumnId(Constants.recommendMajorId);
					}
					else if (selected_field.equals(Constants.recommendNonMajor)) {
						setColumnId(Constants.recommendNonMajorId);
					}
					else if (selected_field.equals(Constants.stimulateInterest)) {
						setColumnId(Constants.stimulateInterestId);
					}
					else if (selected_field.equals(Constants.workRequired)) {
						setColumnId(Constants.workRequiredId);
					}
					else if (selected_field.equals(Constants.semester)) {
						setColumnId(Constants.semesterId);
					}
					else {
						Toast toast = Toast.makeText(Display.this, "Error occurred", Toast.LENGTH_SHORT);
						toast.show();
					}

					removeDialog(FIELD_SELECTION_DIALOG);
					printReviews(displayType);
				}

			});

			dialog = bDialog.create();
			return dialog;

		case COURSE_INFO_DIALOG:
			dialog = new Dialog(Display.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.main_dialog);					

			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			// Set course Alias (title)
			TextView title = (TextView) dialog.findViewById(R.id.dialog_course_number);
			title.setTypeface(timesNewRoman);
			String titleText = c.getAlias();

			title.setText((CharSequence) titleText);
			
			// Set up course info
			TextView courseInfo = (TextView) dialog.findViewById(R.id.dialog_course_info);
			courseInfo.setTypeface(timesNewRoman);
			String courseInfoText = 
							c.getName() + "\n"
							+ c.getFullSemester() + "\n"
							+ c.getInstructor().getName() + "\n"
							+ c.getNumReviewers() + "/" + c.getNumStudents() + " responses";

			courseInfo.setText((CharSequence) courseInfoText);

			Ratings ratings = c.getRatings();
			//Ratings
			TextView ratingsList = (TextView) dialog.findViewById(R.id.RatingsList);
			
			
			String ratingString = "<b>Amount Learned:</b><pre>\t\t\t\t\t\t</pre>" + ratings.getAmountLearned();
			ratingString += "<br><b>Communication Ability:</b><pre>\t\t\t</pre>" + ratings.getCommAbility();
			ratingString += "<br><b>Course Quality:</b><pre>\t\t\t\t\t\t\t</pre>"+ ratings.getCourseQuality();
			ratingString += "<br><b>Difficulty:</b><pre>\t\t\t\t\t\t\t\t\t\t</pre>" + ratings.getDifficulty();
			ratingString += "<br><b>Instructor Access:</b><pre>\t\t\t\t\t</pre>" + ratings.getInstructorAccess();
			ratingString += "<br><b>Instructor Quality:</b><pre>\t\t\t\t\t</pre>" + ratings.getInstructorQuality();
			ratingString += "<br><b>Readings Value:</b><pre>\t\t\t\t\t\t\t</pre>" + ratings.getReadingsValue();
			ratingString += "<br><b>Recommend Major:</b><pre>\t\t\t\t\t</pre>" + ratings.getRecommendMajor();
			ratingString += "<br><b>Recommend Non-major:</b><pre>\t\t</pre>" + ratings.getRecommendNonMajor();
			ratingString += "<br><b>Stimulate Interest:</b><pre>\t\t\t\t\t</pre>" + ratings.getStimulateInterest();
			ratingString += "<br><b>Work Required:</b><pre>\t\t\t\t\t\t\t</pre>" + ratings.getWorkRequired() + "<br>";

			ratingsList.setText(Html.fromHtml(ratingString));

			TextView reviewComments = (TextView) dialog.findViewById(R.id.ReviewComments);
			// Comments
			String commentString = c.getComments();
			if (commentString == null || commentString.equals("null") || commentString.length() < 6) {
				commentString = "There are no available comments for this course.\n";
			}
			reviewComments.setText((CharSequence)commentString);

			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					removeDialog(COURSE_INFO_DIALOG);
				}
			});

			return dialog;
		default:
			return super.onCreateDialog(id);
		}
	}

	public void setColumnId(int newId) {
		if (selectedCol == firstCol) {
			firstCol.setText(Constants.fillString[newId]);
			firstColId = newId;
		}
		else if (selectedCol == secondCol) {
			secondCol.setText(Constants.fillString[newId]);
			secondColId = newId;
		}
		else if (selectedCol == thirdCol) {
			thirdCol.setText(Constants.fillString[newId]);
			thirdColId = newId;
		}
		else 
			return;
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

		ArrayList<String> default_info = new ArrayList<String>();
		ArrayList<String> col1_info = new ArrayList<String>();
		ArrayList<String> col2_info = new ArrayList<String>();
		ArrayList<String> col3_info = new ArrayList<String>();

		if (displayType == Type.DEPARTMENT) {
			for (CourseAverage avg : courseAvgs) {
				default_info.add(avg.getId());
				col1_info.add(avg.getRatings().getRating(firstColId));
				col2_info.add(avg.getRatings().getRating(secondColId));
				col3_info.add(avg.getRatings().getRating(thirdColId));
			}
		}
		else {
			for (Course course : courseReviews) {
				// DEFAULT column
				if (displayType == Type.COURSE) {
					default_info.add(course.getInstructor().getName());
				}
				else {
					default_info.add(course.getAlias());
				}

				// First column
				if (firstColId == Constants.semesterId) {
					col1_info.add(course.getFullSemester());
				}
				else {
					col1_info.add(course.getRatings().getRating(firstColId));
				}

				// Second column
				if (secondColId == Constants.semesterId) {
					col2_info.add(course.getFullSemester());
				}
				else {
					col2_info.add(course.getRatings().getRating(secondColId));
				}

				// Third column
				if (thirdColId == Constants.semesterId) {
					col3_info.add(course.getFullSemester());
				}
				else {
					col3_info.add(course.getRatings().getRating(thirdColId));
				}
			}
		}

		// Error check
		if (default_info.size() != col1_info.size() || col1_info.size() != col2_info.size() && col2_info.size() != col3_info.size()) {
			Log.w("DISPLAY ERROR", "The size of the arrays do not match");
		}

		row_map = new HashMap<Integer, Course>();
		row_map_dept = new HashMap<Integer, CourseAverage>();
		for (int i=0; i < default_info.size(); i++) {
			HashMap<String, String> row = new HashMap<String, String>();
			row.put("col_1", default_info.get(i));
			row.put("col_2", col1_info.get(i));
			row.put("col_3", col2_info.get(i));
			row.put("col_4", col3_info.get(i));

			if (displayType != Type.DEPARTMENT) {
				row_map.put(i, courseReviews.get(i));
			}
			else {
				row_map_dept.put(i, courseAvgs.get(i));
			}

			allRows.add(row);
		}

		if (displayType != Type.DEPARTMENT) {
			SimpleAdapter adapter = new SimpleAdapter(
					this, allRows, R.layout.list_row, columnHeaders, cellIds);
			lv.setAdapter(adapter);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					Log.v("position", ""+position);
					Log.v("id",""+id);
					c = row_map.get(new Integer(position));
					if (c == null) {
						Log.v("Course", "course is null");
					}

					showDialog(COURSE_INFO_DIALOG);
				}
			});
		}

		else {
			SimpleAdapter adapter_dept = new SimpleAdapter(
					this, allRows, R.layout.list_row, columnHeaders, cellIds);
			lv.setAdapter(adapter_dept);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
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
		}

		// Set the longclick listeners for only the last 3 columns, default column is never changed
		firstCol.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				selectedCol = firstCol;
				showDialog(FIELD_SELECTION_DIALOG);
				return true;
			}
		});

		secondCol.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				selectedCol = secondCol;
				showDialog(FIELD_SELECTION_DIALOG);
				return true;
			}
		});

		thirdCol.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				selectedCol = thirdCol;
				showDialog(FIELD_SELECTION_DIALOG);
				return true;
			}
		});
	}

	public void onClickSort(View v) {
		Sorter s = new Sorter();

		ArrayList<Course> temp_courses = new ArrayList<Course>();

		// Map each CourseAverage into a Course for purposes of calling sorting methods on Courses
		Map<Course, CourseAverage> courseToCourseAvg = new HashMap<Course, CourseAverage>();
		if (displayType == Type.DEPARTMENT) {
			for (CourseAverage cAvg : courseAvgs) {
				Course c = new Course(cAvg.getId(), cAvg.getName(), "", "", "", cAvg.getId(), null, 0,
						0, cAvg.getPath(), cAvg.getRatings(), null);
				courseToCourseAvg.put(c, cAvg);
			}

			for (Course c : courseToCourseAvg.keySet()) {
				temp_courses.add(c);
			}
		}
		else {
			temp_courses = courseReviews;
		}

		switch (v.getId()) {
		case R.id.default_tab:
			// Check if we are sorting desc or asc
			// DEFAULT tab can only be instructor name or course id
			if (sortingField == Sort.DEFAULT_ASC) {
				sortingField = Sort.DEFAULT_DESC;
				if (defaultColId == Constants.instructorNameId) {
					temp_courses = s.sortAlphabetically(temp_courses, Type.INSTRUCTOR, 1);
				}
				else {
					temp_courses = s.sortAlphabetically(temp_courses, Type.COURSE, 1);
				}
			}
			else {
				sortingField = Sort.DEFAULT_ASC;
				if (defaultColId == Constants.instructorNameId) {
					temp_courses = s.sortAlphabetically(temp_courses, Type.INSTRUCTOR, 0);
				}
				else {
					temp_courses = s.sortAlphabetically(temp_courses, Type.COURSE, 0);
				}
			}
			break;
		case R.id.first_tab:
			// Check if we are sorting desc or asc
			if (sortingField == Sort.FIRST_ASC) {
				sortingField = Sort.FIRST_DESC;
				if (firstColId == Constants.semesterId) {
					temp_courses = s.sortBySemester(temp_courses, 1);
				}
				else {
					temp_courses = s.sortByRating(temp_courses, firstColId, 1);
				}
			}
			else {
				sortingField = Sort.FIRST_ASC;
				if (firstColId == Constants.semesterId) {
					temp_courses = s.sortBySemester(temp_courses, 0);
				}
				else {
					temp_courses = s.sortByRating(temp_courses, firstColId, 0);
				}
			}
			break;
		case R.id.second_tab:
			// Check if we are sorting desc or asc
			if (sortingField == Sort.SECOND_ASC) {
				sortingField = Sort.SECOND_DESC;
				if (secondColId == Constants.semesterId) {
					temp_courses = s.sortBySemester(temp_courses, 1);
				}
				else {
					temp_courses = s.sortByRating(temp_courses, secondColId, 1);
				}
			}
			else {
				sortingField = Sort.SECOND_ASC;
				if (secondColId == Constants.semesterId) {
					temp_courses = s.sortBySemester(temp_courses, 0);
				}
				else {
					temp_courses = s.sortByRating(temp_courses, secondColId, 0);
				}
			}
			break;
		case R.id.third_tab:
			// Check if we are sorting desc or asc
			if (sortingField == Sort.THIRD_ASC) {
				sortingField = Sort.THIRD_DESC;
				if (thirdColId == Constants.semesterId) {
					temp_courses = s.sortBySemester(temp_courses, 1);
				}
				else {
					temp_courses = s.sortByRating(temp_courses, thirdColId, 1);
				}
			}
			else {
				sortingField = Sort.THIRD_ASC;
				if (thirdColId == Constants.semesterId) {
					temp_courses = s.sortBySemester(temp_courses, 0);
				}
				else {
					temp_courses = s.sortByRating(temp_courses, thirdColId, 0);
				}
			}
			break;
		default:
			break;
		}

		// Now temp_courses is sorted in order, need to re-insert courseAvg if type is department
		if (displayType == Type.DEPARTMENT) {
			courseAvgs = new ArrayList<CourseAverage>();
			for (int i = 0; i < temp_courses.size(); i++) {
				courseAvgs.add(courseToCourseAvg.get(temp_courses.get(i)));
			}
		}
		else {
			courseReviews = temp_courses;
		}

		// Reset background color for all fields
		defaultCol.setBackgroundColor(0);
		firstCol.setBackgroundColor(0);
		secondCol.setBackgroundColor(0);
		thirdCol.setBackgroundColor(0);

		v.setBackgroundColor(getResources().getColor(R.color.highlight_blue));
		printReviews(this.displayType);
	}
}
