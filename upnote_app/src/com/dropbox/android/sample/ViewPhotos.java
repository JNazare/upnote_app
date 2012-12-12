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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class ViewPhotos extends Activity {
	DropboxAPI<AndroidAuthSession> mApi;
	
	final static private String APP_KEY = "1c4n6vgplqjcqls";
    final static private String APP_SECRET = "l6k1bk1zrxzrlb3";
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	
    private ImageView myImage;
    private String courseName;
    private String path;
    private TextView myText;
    private int count;
    
    private final String PHOTO_DIR = "/classes/";
    private String selected;
    //public final static String COURSE_NAME = "com.example.DBRoulette.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);
        count = 0;
        Intent i = getIntent();
        path = i.getStringExtra("path");
        String name = i.getStringExtra("name");
        System.out.println(path);
		setContentView(R.layout.activity_view_photos);
		myImage = (ImageView)findViewById(R.id.image_view);
		myText = (TextView)findViewById(R.id.text_view);
		myText.setText("Course: " + name);
		DownloadRandomPicture download = new DownloadRandomPicture(ViewPhotos.this, mApi, path, myImage, count);
        download.execute();
        
        final Button backpicbutton = (Button)findViewById(R.id.backpicbutton);
        final Button fwdpicbutton = (Button)findViewById(R.id.fwdpicbutton);
        
        //Typeface scrawl=Typeface.createFromAsset(getAssets(),"ShadowsIntoLight.ttf");
        //backpicbutton.setTypeface(scrawl);
        //fwdpicbutton.setTypeface(scrawl);
        //myText.setTypeface(scrawl);
        
        backpicbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	count = count - 1;
            	System.out.println(path);
                DownloadRandomPicture download = new DownloadRandomPicture(ViewPhotos.this, mApi, path, myImage, count);
                download.execute();
            }
        });
        
        fwdpicbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	count = count + 1;
            	System.out.println(path);
                DownloadRandomPicture download = new DownloadRandomPicture(ViewPhotos.this, mApi, path, myImage, count);
                download.execute();
            }
        });
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
