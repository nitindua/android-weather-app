package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.LoggingBehavior;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
//import com.example.weather.LoginUsingCustomFragmentActivity;
//import com.example.weather.LoginUsingActivityActivity.SessionStatusCallback;

public class MainActivity extends Activity implements OnMenuItemClickListener, OnClickListener {

	TextView city, regionCountry, conditionText, temperature;
	ImageView conditionImage;
	ImageButton facebookIcon, googlemapsIcon, googlemapsIcon2;
	Button postCurrent, postForecast;
	TextView day0, weather0, high0, low0;
	TextView day1, weather1, high1, low1;
	TextView day2, weather2, high2, low2;
	TextView day3, weather3, high3, low3;
	TextView day4, weather4, high4, low4;
	TableRow tableRow1;
	TableLayout weatherTable;
	
	private PopupMenu popupMenu;
		//TextView city = (TextView) findViewById(R.id.);
	
	private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	
	private final static int ONE = 1;
    private final static int TWO = 2;
    private String popupChoice;
    private Session session;
    
    String cityJSON = null;
    String regionJSON = null;
	String countryJSON = null;
	String feed = null;
	String img = null;
	String link = null;
	String conditionTextJSON = null;
	String conditionTemp = null;
	String tempUnits = null;
	JSONObject forecast0;
	JSONObject forecast1;
	JSONObject forecast2;
	JSONObject forecast3;
	JSONObject forecast4;
	
	int userChoice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		city = (TextView) findViewById(R.id.city);
		regionCountry = (TextView) findViewById(R.id.regionCountry);
		conditionImage = (ImageView) findViewById(R.id.conditionImage);
		conditionText = (TextView) findViewById(R.id.conditionText);
		temperature = (TextView) findViewById(R.id.temperature);
		tableRow1 = (TableRow) findViewById(R.id.tableRow1);
		day0 = (TextView) findViewById(R.id.columnDay0);
		day1 = (TextView) findViewById(R.id.columnDay1);
		day2 = (TextView) findViewById(R.id.columnDay2);
		day3 = (TextView) findViewById(R.id.columnDay3);
		day4 = (TextView) findViewById(R.id.columnDay4);
		
		weather0 = (TextView) findViewById(R.id.columnWeather0);
		weather1 = (TextView) findViewById(R.id.columnWeather1);
		weather2 = (TextView) findViewById(R.id.columnWeather2);
		weather3 = (TextView) findViewById(R.id.columnWeather3);
		weather4 = (TextView) findViewById(R.id.columnWeather4);

		high0 = (TextView) findViewById(R.id.columnHigh0);
		high1 = (TextView) findViewById(R.id.columnHigh1);
		high2 = (TextView) findViewById(R.id.columnHigh2);
		high3 = (TextView) findViewById(R.id.columnHigh3);
		high4 = (TextView) findViewById(R.id.columnHigh4);
		
		low0 = (TextView) findViewById(R.id.columnLow0);
		low1 = (TextView) findViewById(R.id.columnLow1);
		low2 = (TextView) findViewById(R.id.columnLow2);
		low3 = (TextView) findViewById(R.id.columnLow3);
		low4 = (TextView) findViewById(R.id.columnLow4);
		
		weatherTable = (TableLayout) findViewById(R.id.forecastTable);
		
		
		popupMenu = new PopupMenu(getApplicationContext(), findViewById(R.id.facebookIcon));
        popupMenu.getMenu().add(Menu.NONE, ONE, Menu.NONE, "Post Current Weather");
        popupMenu.getMenu().add(Menu.NONE, TWO, Menu.NONE, "Post Weather Forecast");
        popupMenu.setOnMenuItemClickListener(this);
        findViewById(R.id.facebookIcon).setOnClickListener(this);
		
		
		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
		session = Session.getActiveSession();
        if (session == null) {
        	if (savedInstanceState != null) {
            	//session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
            	session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
            	//session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
        
        
		facebookIcon = (ImageButton) findViewById(R.id.facebookIcon);
		googlemapsIcon = (ImageButton) findViewById(R.id.googlemapsIcon);
		
		postCurrent = (Button) findViewById(R.id.postCurrent);
		postForecast = (Button) findViewById(R.id.postForecast);
	}
	
	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        /*if (session.isOpened()) {
            
            //buttonLoginLogout.setText(R.string.logout);
            facebookIcon.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
        } else {*/
            
            //buttonLoginLogout.setText(R.string.login);
        	facebookIcon.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        //}
    }

    private void onClickLogin() {
    	Log.d("Session(`gin)", session.getState().toString());
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
        	Log.d("Session(1)", "1");
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
            Log.d("Session(2)", "2");
        }
        Log.d("Session(after login)", session.getState().toString());
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }
    
    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.d("Session(inside callback)", session.getState().toString());
            //updateView();
        	if(session.isOpened())
        	{
        		//publishCurrentWeather();
        		Log.d("Session(inside callback!!!)", session.getState().toString());
        		Log.d("User choice", String.valueOf(userChoice));
        		publishCurrentWeather();
        	}
        }
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the `; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_googlemaps:
	        	google2();
	            return true;
	        /*case R.id.action_compose:
	            composeMessage();
	            return true; */
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void postCurrent(View view){
		userChoice = 1;
		Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
        	onClickLogin();
        }
		//onClickLogin();
        else
        	publishCurrentWeather();
	}
	
	public void postForecast(View view){
		userChoice = 2;
		Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
        	onClickLogin();
        }
		//onClickLogin();
        else
        	publishCurrentWeather();
		//onClickLogin();
		//publishCurrentWeather();
	}
	
	public void google(View view){
		Log.d("google2", "google2");
		String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", ((EditText) findViewById(R.id.location)).getText().toString() );
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		//intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
	}
	
	public void google2(){
		Log.d("google2", "google2");
		String uri = String.format(Locale.ENGLISH, "geo:0,0?q=%s", ((EditText) findViewById(R.id.location)).getText().toString() );
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		//intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
		startActivity(intent);
	}
	
	public void searchLocation(View view){
		EditText location = (EditText) findViewById(R.id.location);
		RadioGroup unitsGroup = (RadioGroup) findViewById(R.id.radioGroup1);
		char units;
		if(unitsGroup.getCheckedRadioButtonId() == R.id.radio0)
			units = 'f';
		else
			units = 'c';
		String locationValue = location.getText().toString();
 
		String type = validateInput(locationValue);
		//Toast.makeText(getApplicationContext(), type, Toast.LENGTH_LONG).show();
		if(!type.equalsIgnoreCase("false")){
			locationValue = locationValue.replaceAll("\\s+","+");
			try{
				String urlString = "http://cs-server.usc.edu:12471/WeatherForecast/WeatherForecastServlet?locationValue=" + URLEncoder.encode(locationValue, "UTF-8") + "&locationType=" + type +"&tempUnits=" + units;
				//Toast.makeText(getApplicationContext(), urlString, Toast.LENGTH_LONG).show();
				new GetResponse().execute(urlString);
		    }catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public String validateInput(String locationValue){
		if(locationValue == null || locationValue == ""){
			Toast.makeText(getApplicationContext(), "Please enter a location", Toast.LENGTH_LONG).show();
			return "false";
		}
		String type = null;
		String pattern = "^[0-9]+$";
		Pattern reg = Pattern.compile(pattern);
		Matcher matchResult = reg.matcher(locationValue);
		if(matchResult.matches() == true)
	    {    
	        type = "zip";
	        pattern = "^[0-9]{5}$";
	        reg = Pattern.compile(pattern);
			matchResult = reg.matcher(locationValue);
	        if(matchResult.matches() == false)
	        {
	        	Toast.makeText(getApplicationContext(), "Invalid zipcode: must be 5 digits.\n Example: 90089 ", Toast.LENGTH_LONG).show();
	        	return "false";
	        }
	        else
	        {
	        	return type;
	        }
	    } 
		else
		{
			String pattern1 = "^[a-zA-Z0-9 \'-.]+\\s*,\\s*[a-zA-Z0-9 \'-.]+$";
	        String pattern2 = "^[a-zA-Z0-9 \'-.]+\\s*,\\s*[a-zA-Z0-9 \'-.]+\\s*,\\s*[a-zA-Z0-9 \'-.]+$";
	        Pattern reg1 = Pattern.compile(pattern1);
	        Pattern reg2 = Pattern.compile(pattern2);
	        Matcher matchResult1 = reg1.matcher(locationValue);
	        Matcher matchResult2 = reg2.matcher(locationValue);
	        if(matchResult1.matches() == false && matchResult2.matches() == false)
	        {
	        	Toast.makeText(getApplicationContext(), "Invalid location: Must include city and either one or both state and country with all values seperated by a comma.\nExample: Los Angeles, CA, USA or Los Angeles, CA or Los Angeles, USA", Toast.LENGTH_LONG).show();
	        	return "false";
	        }
	        type = "city";
	        return type;
	    }
	}
	
	
	
	private class GetResponse extends AsyncTask<String, Void, String>{
		
		protected String doInBackground(String... urls) {
			android.os.Debug.waitForDebugger();
			Log.d("doInBackground", "here");
			URL url;
			try {
				url = new URL(urls[0]);
				final URLConnection urlConnection = url.openConnection();
				urlConnection.connect();
				StringBuilder builder = new StringBuilder();
				String line;
				InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());
		        BufferedReader in = new BufferedReader(isr);
		        while((line = in.readLine()) != null) {
		        	 builder.append(line);
		        }
		        return builder.toString();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		
		protected void onPostExecute(String jsonString) {
			if(jsonString != null){
				Log.d("postExecute", jsonString);
				try{
					JSONObject jsonObject = new JSONObject(jsonString);
					JSONObject weather = jsonObject.getJSONObject("weather");
					img = weather.getString("img");
					feed = weather.getString("feed");
					link = weather.getString("link");
					JSONObject location = weather.getJSONObject("location");
					cityJSON = location.getString("city");
					regionJSON = location.getString("region");
					countryJSON = location.getString("country");
					JSONObject condition = weather.getJSONObject("condition");
					conditionTextJSON = condition.getString("text");
					conditionTemp = condition.getString("temp");
					JSONObject units = weather.getJSONObject("units");
					tempUnits = units.getString("temperature");
					JSONArray forecast = weather.getJSONArray("forecast"); 
					String degree = null;
					
					MainActivity.this.city.setText(cityJSON);
					regionCountry.setText(regionJSON + ", " + countryJSON);
					
					new DisplayImage().execute(img);
					
					MainActivity.this.conditionText.setText(conditionTextJSON);
					if(tempUnits.equalsIgnoreCase("f"))
						degree = "\u2109";
					else
						degree = "\u2103";
					temperature.setText(conditionTemp + degree);
					
					forecast0 = forecast.getJSONObject(0);
					day0.setText(forecast0.getString("day"));
					weather0.setText(forecast0.getString("text"));
					high0.setText(forecast0.getString("high") + degree);
					low0.setText(forecast0.getString("low") + degree);
					
					forecast1 = forecast.getJSONObject(1);
					day1.setText(forecast1.getString("day"));
					weather1.setText(forecast1.getString("text"));
					high1.setText(forecast1.getString("high") + degree);
					low1.setText(forecast1.getString("low") + degree);
					
					forecast2 = forecast.getJSONObject(2);
					day2.setText(forecast2.getString("day"));
					weather2.setText(forecast2.getString("text"));
					high2.setText(forecast2.getString("high") + degree);
					low2.setText(forecast2.getString("low") + degree);
					
					forecast3 = forecast.getJSONObject(3);
					day3.setText(forecast3.getString("day"));
					weather3.setText(forecast3.getString("text"));
					high3.setText(forecast3.getString("high") + degree);
					low3.setText(forecast3.getString("low") + degree);
					
					forecast4 = forecast.getJSONObject(4);
					day4.setText(forecast4.getString("day"));
					weather4.setText(forecast4.getString("text"));
					high4.setText(forecast4.getString("high") + degree);
					low4.setText(forecast4.getString("low") + degree);
					
					weatherTable.setBackgroundResource(R.layout.shape);
					tableRow1.setVisibility(View.VISIBLE);
					postCurrent.setVisibility(View.VISIBLE);
					postForecast.setVisibility(View.VISIBLE);
					//facebookIcon.setVisibility(View.VISIBLE);
					//googlemapsIcon.setVisibility(View.VISIBLE);
					//googlemapsIcon2.setVisibility(View.VISIBLE);
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
	    }
		
	}
	
	private class DisplayImage extends AsyncTask<String, Void, Bitmap>{

		@Override
		protected Bitmap doInBackground(String... imgs) {
			try{
				URL url = new URL(imgs[0]);
				Log.d("displayImage", imgs[0]);
				Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
				return bmp;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		
		protected void onPostExecute(Bitmap bmp){
			Log.d("displayImage", "in");
			conditionImage.setImageBitmap(bmp);
			conditionImage.setBackgroundColor(Color.TRANSPARENT);
			conditionImage.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		popupMenu.show();
		Log.d("OnClick", "HERE");
		
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if(item.getItemId() == ONE){
			onClickLogin();
			userChoice = 1;
			//publishCurrentWeather();
		}
		else{
			onClickLogin();
			//publishWeatherForecast();
			userChoice = 2;
		}
		return true;
	}
	
	public void publishCurrentWeather(){
		
		Log.d("1", "1");
		String degree = null;
		if(tempUnits.equalsIgnoreCase("F"))
			degree = "\u2109";
		else
			degree = "\u2103";
		Bundle params = new Bundle();
		if(userChoice == 1){
		    params.putString("name", cityJSON + ", " + regionJSON + ", " + countryJSON);
		    params.putString("caption", "The current condition for " + cityJSON + " is " + conditionTextJSON + '.');
		    params.putString("description", "Temperature is " + conditionTemp + degree + ".");
		    params.putString("link", feed);
		    params.putString("picture", img);
		    
		    JSONObject property = new JSONObject(); 
		    try{
			    property.put("text", "here"); 
			    property.put("href", link); 
			    JSONObject properties = new JSONObject(); 
			    properties.put("Look at details", property); 
			    params.putString("properties", properties.toString());
		    }
		    catch(Exception e){
		    	e.printStackTrace();
		    }
		}
		else{
			try{
				params.putString("name", cityJSON + ", " + regionJSON + ", " + countryJSON);
			    params.putString("caption", "Weather Forecast for " + cityJSON);
			    params.putString("description", forecast0.getString("day") + ": " + forecast0.getString("text") + ", " + forecast0.getString("high") + "/" + forecast0.getString("low") + degree + "; "
			    							+	forecast1.getString("day") + ": " + forecast1.getString("text") + ", " + forecast1.getString("high") + "/" + forecast1.getString("low") + degree + "; "
			    							+	forecast2.getString("day") + ": " + forecast2.getString("text") + ", " + forecast2.getString("high") + "/" + forecast2.getString("low") + degree + "; "
			    							+ 	forecast3.getString("day") + ": " + forecast3.getString("text") + ", " + forecast3.getString("high") + "/" + forecast3.getString("low") + degree + "; "
			    							+ 	forecast4.getString("day") + ": " + forecast4.getString("text") + ", " + forecast4.getString("high") + "/" + forecast4.getString("low") + degree + ". " );
			    		
			    		
			    		
			    params.putString("link", feed);
			    params.putString("picture", "http://www-scf.usc.edu/~csci571/2013Fall/hw8/weather.jpg");
			    JSONObject property = new JSONObject(); 
		    
			    property.put("text", "here"); 
			    property.put("href", link); 
			    JSONObject properties = new JSONObject(); 
			    properties.put("Look at details", property); 
			    params.putString("properties", properties.toString());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
			
	    
        Log.d("Session", session.getState().toString());
	    if(session.isOpened()){
	    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(this, session, params)).setOnCompleteListener(new OnCompleteListener() {
	    	
	            public void onComplete(Bundle values, FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getApplicationContext(), "Posted story, id: "+postId, Toast.LENGTH_SHORT).show();
	                    } 
	                    else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                    }
	                } 
	                else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                } 
	                else {
	                    // Generic, ex: network error
	                    Toast.makeText(getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
	}
	    Log.d("3", "3");
	}
	
	public void publishWeatherForecast(){
		
		try{
			String degree = null;
			if(tempUnits.equalsIgnoreCase("F"))
				degree = "\u2109";
			else
				degree = "\u2103";
		Bundle params = new Bundle();
		params.putString("name", cityJSON + ", " + regionJSON + ", " + countryJSON);
	    params.putString("caption", "Weather Forecast for " + cityJSON);
	    params.putString("description", forecast0.getString("day") + ": " + forecast0.getString("text") + ", " + forecast0.getString("high") + "/" + forecast0.getString("low") + degree + tempUnits + "; "
	    							+	forecast1.getString("day") + ": " + forecast1.getString("text") + ", " + forecast1.getString("high") + "/" + forecast1.getString("low") + degree + tempUnits + "; "
	    							+	forecast2.getString("day") + ": " + forecast2.getString("text") + ", " + forecast2.getString("high") + "/" + forecast2.getString("low") + degree + tempUnits + "; "
	    							+ 	forecast3.getString("day") + ": " + forecast3.getString("text") + ", " + forecast3.getString("high") + "/" + forecast3.getString("low") + degree + tempUnits + "; "
	    							+ 	forecast4.getString("day") + ": " + forecast4.getString("text") + ", " + forecast4.getString("high") + "/" + forecast4.getString("low") + degree + tempUnits + ". " );
	    		
	    		
	    		
	    params.putString("link", feed);
	    params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
	    JSONObject property = new JSONObject(); 
    
	    property.put("text", "here"); 
	    property.put("href", link); 
	    JSONObject properties = new JSONObject(); 
	    properties.put("Look at details", property); 
	    params.putString("properties", properties.toString());
    
	    
	    
	    
	    WebDialog feedDialog = (new WebDialog.FeedDialogBuilder(getApplicationContext(), Session.getActiveSession(), params)).setOnCompleteListener(new OnCompleteListener() {

	            public void onComplete(Bundle values, FacebookException error) {
	                if (error == null) {
	                    // When the story is posted, echo the success
	                    // and the post Id.
	                    final String postId = values.getString("post_id");
	                    if (postId != null) {
	                        Toast.makeText(getApplicationContext(), "Posted story, id: "+postId, Toast.LENGTH_SHORT).show();
	                    } 
	                    else {
	                        // User clicked the Cancel button
	                        Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                    }
	                } 
	                else if (error instanceof FacebookOperationCanceledException) {
	                    // User clicked the "x" button
	                    Toast.makeText(getApplicationContext(), "Publish cancelled", Toast.LENGTH_SHORT).show();
	                } 
	                else {
	                    // Generic, ex: network error
	                    Toast.makeText(getApplicationContext(), "Error posting story", Toast.LENGTH_SHORT).show();
	                }
	            }

	        })
	        .build();
	    feedDialog.show();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}

	
