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


import edu.upenn.cis.cis350.backend.Constants;
import edu.upenn.cis.cis350.backend.EasySSLSocketFactory;
import edu.upenn.isc.fastPdfServiceClient.api.FpsPennGroupsHasMember;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationPage extends Activity {


	private String path = "http://beta.penncoursereview.com/androidapp/auth/?serial=";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.authentication_page);

		Button auth = (Button)findViewById(R.id.auth);
		//what happens when user wants to authenticate their serial number
		auth.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

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
						String serialNumber = ((EditText)findViewById(R.id.authenticate_text)).getText().toString();
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
							Context context = getApplicationContext();
							CharSequence text = "Invalid Serial, please enter again";

							int duration = Toast.LENGTH_LONG;

							Toast toast = Toast.makeText(context, text, duration);
							((EditText)findViewById(R.id.authenticate_text)).setText("");
							toast.show();
						}

						else{
							boolean hasMember = new FpsPennGroupsHasMember().assignGroupName("penn:isc:ait:apps:pennCourseReview:groups:pennCourseReviewStudents").assignSubjectSourceId("pennperson").assignSubjectIdentifier(pennKey_response).executeReturnBoolean();
							Log.w("boolean is",hasMember+"");
							if(!hasMember){
								Log.w("Invalid pennkey","invalid pennkey");
								Context context = getApplicationContext();
								CharSequence text = "Invalid PennKey, unable to authenticate";

								int duration = Toast.LENGTH_LONG;

								Toast toast = Toast.makeText(context, text, duration);
								((EditText)findViewById(R.id.authenticate_text)).setText("");
								toast.show();
							}
							else{
								Log.w("success!","success!");
								Context context = getApplicationContext();
								CharSequence text = "Authenticated!";

								int duration = Toast.LENGTH_LONG;

								Toast toast = Toast.makeText(context, text, duration);
								((EditText)findViewById(R.id.authenticate_text)).setText("");
								toast.show();
								goToStartPage();
							}

						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.w("Error","should not be here");
						e.printStackTrace();

					}
					catch (IllegalArgumentException e){
						Log.w("Illegal Character","illegal character");
						Context context = getApplicationContext();
						CharSequence text = "Invalid Serial, please enter again";

						int duration = Toast.LENGTH_LONG;

						Toast toast = Toast.makeText(context, text, duration);
						((EditText)findViewById(R.id.authenticate_text)).setText("");
					}

					count++;
				}

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


	}
	public void goToStartPage() {
		Intent i = new Intent(this, StartPage.class);

		// Pass the Intent to the proper Activity (check for course search vs. dept search)
		startActivity(i);
	}
}
