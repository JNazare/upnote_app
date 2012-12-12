package com.dropbox.android.sample;

import java.util.ArrayList;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class AddActivity extends Activity {
	DropboxAPI<AndroidAuthSession> mApi;
	
	final static private String APP_KEY = "1c4n6vgplqjcqls";
    final static private String APP_SECRET = "l6k1bk1zrxzrlb3";
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
    private final String PHOTO_DIR = "/classes/";
    private String selected;
    //public final static String COURSE_NAME = "com.example.DBRoulette.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        
        Intent i = getIntent();
        ArrayList<String> class_list = i.getStringArrayListExtra("CLASSES");
        class_list.remove(class_list.size()-1);
        System.out.println(class_list);
        
        
		setContentView(R.layout.activity_add);
		
		Spinner s = (Spinner) findViewById(R.id.currentcoursespinner);
		s.setOnItemSelectedListener(new SelectedListener());
	    ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, class_list);
	    s.setAdapter(adapter);
	    Typeface scrawl=Typeface.createFromAsset(getAssets(),"ShadowsIntoLight.ttf");
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_add, menu);
		return true;
	}
	
	public class SelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        selected = parent.getItemAtPosition(pos).toString();
	        System.out.println(selected);
	    }

	    public void onNothingSelected(AdapterView parent) {
	        // Do nothing.
	    }
	}

	
	public void addCourse(View view) {
	    // Do something in response to button -- DOES NOT CHECK IF FOLDER CURRENTLY EXISTS
		System.out.println("I'll add a course here\n");
		EditText addText = (EditText) findViewById(R.id.addcoursetext);
		Typeface scrawl=Typeface.createFromAsset(getAssets(),"ShadowsIntoLight.ttf");
        //addText.setTypeface(scrawl);
		String message = addText.getText().toString();
		try {
			mApi.delete(PHOTO_DIR + selected);
			mApi.createFolder(PHOTO_DIR + message);
			Context context = getApplicationContext();
			CharSequence text = "New course added";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} catch (DropboxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		
	}
	
	
	private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }
	 
	private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

}
