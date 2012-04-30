package edu.upenn.cis.cis350.display;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.EasySSLSocketFactory;
import edu.upenn.cis.cis350.database.AuthCache;
import edu.upenn.isc.fastPdfServiceClient.api.FpsPennGroupsHasMember;

/**
 * Initial page if user hasn't authenticated yet
 * @author Connie Ho, Cynthia Mai, Jinyan Cao, Charles Kong
 */

public class AuthenticationPage extends Activity {

	Context context;
	
	AuthCache auth_cache;
	
	private String path = "http://beta.penncoursereview.com/androidapp/auth/?serial=";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.authentication_page);
		
		context = this.getApplicationContext();
		
		// Set font to Times New Roman
		Typeface timesNewRoman = Typeface.createFromAsset(this.getAssets(),"fonts/Times_New_Roman.ttf");
		TextView SerialPrompt1 = (TextView) findViewById(R.id.serial_prompt1);
		SerialPrompt1.setTypeface(timesNewRoman);
		TextView SerialPrompt2 = (TextView) findViewById(R.id.serial_prompt2);
		SerialPrompt2.setTypeface(timesNewRoman);

		// Handle user pushing enter after typing auth
		EditText enteredAuth = (EditText)findViewById(R.id.authenticate_text);
		// Set the on-key listener to listen to textview input
		enteredAuth.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If event is key-down event on "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
						(keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					onAuthSerialButtonClick(v);
					return true;
				}
				return false;
			}
		});

		//button to launch a browser to get serial number
		Button launchBrowser = (Button)findViewById(R.id.launch);
		launchBrowser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Uri uriUrl = Uri.parse("http://www.penncoursereview.com/androidapp");
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
				startActivity(launchBrowser);  
			}
		});
		
		auth_cache = new AuthCache(context);
		auth_cache.open();
		// Check if key exists
		if (auth_cache.checkKey() != null) {
			auth_cache.close();
			goToStartPage();
			return;
		}
		auth_cache.close();
	}

	public void onAuthSerialButtonClick(View v) {
		String serialNumber = ((EditText)findViewById(R.id.authenticate_text)).getText().toString();
		new AuthKey(this).execute(serialNumber);
	}


	public void goToStartPage() {
		Intent i = new Intent(this, StartPage.class);

		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivityForResult(i, Constants.NORMAL_OPEN_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.NORMAL_OPEN_REQUEST) {
			if (resultCode == RESULT_OK) {
				this.finish();
			}
			else if (resultCode == Constants.RESULT_QUIT) {
				this.finish();
			}
			else {
				this.finish();
			}
		}
	}
	
	class AuthKey extends AsyncTask<String, Integer, String> {
		
		Dialog dialog;
		Activity _activity;
		
		boolean auth;
		String serialNumber;
		
		AuthKey(Activity activity) {
			_activity = activity;
			
			dialog = ProgressDialog.show(_activity, "", "Authenticating...", true);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			
			auth = false;
		}
		
		protected void onPostExecute(String result) {
			dialog.dismiss();
			if (auth) {
				auth_cache.open();
				auth_cache.insertKey(serialNumber);
				auth_cache.close();
				goToStartPage();
			}
		}

		@Override
		protected String doInBackground(String... arg0) {
			serialNumber = arg0[0];
			
			int count = 0;
			String pennKey_response = "!ERROR";
			while(count < 3 && (pennKey_response.equals("!ERROR") || pennKey_response.equals("HTTP Error 500")))
			{
				try {
					//Stuff to make it accept all certificates
					SchemeRegistry schemeRegistry = new SchemeRegistry();
					schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
					schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

					HttpParams params = new BasicHttpParams();
					params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
					params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
					params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
					HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);

					ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
					//creates a new Http Client
					HttpClient client = new DefaultHttpClient(cm, params);
					
					Log.w("serialNumber", serialNumber);
					
					//sends a request to the serial website
					HttpGet httpget = new HttpGet(path+serialNumber.trim());

					// Execute the request
					HttpResponse response = client.execute(httpget);
					//gets the response
					pennKey_response = EntityUtils.toString(response.getEntity());
					Log.w("Response",pennKey_response);
					if(pennKey_response.equals("!ERROR") || pennKey_response.equals("HTTP Error 500")){
						Log.w("Error","trying again");
						count++;
						continue;

					}
					else if(pennKey_response.equals("!INVALID")){
						Log.w("Invalid serial","Invalid serial");
						CharSequence text = "Invalid serial number, please enter again";

						displayToast(text);
					}

					else{
						boolean hasMember = new FpsPennGroupsHasMember().assignGroupName("penn:isc:ait:apps:pennCourseReview:groups:pennCourseReviewStudents").assignSubjectSourceId("pennperson").assignSubjectIdentifier(pennKey_response).executeReturnBoolean();
						Log.w("boolean is",hasMember+"");
						if(!hasMember){
							Log.w("Invalid pennkey","invalid pennkey");
							CharSequence text = "Invalid PennKey, unable to authenticate";

							displayToast(text);
						}
						else{
							Log.w("success!","success!");
							CharSequence text = "Authenticated!";
							
							displayToast(text);

							auth = true;
						}

					}

				} catch (IOException e) {
					Log.w("Error","should not be here");
					e.printStackTrace();

				}
				catch (IllegalArgumentException e){
					Log.w("Illegal Character","illegal character");
					CharSequence text = "Invalid Serial, please try again";
					
					displayToast(text);
				}

				count++;
			}
			return "DONE";
		}
		
		private void displayToast(final CharSequence text) {
			AuthenticationPage.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					((EditText)findViewById(R.id.authenticate_text)).setText("");
					toast.show();
				}
			});
		}
		
	}
}
