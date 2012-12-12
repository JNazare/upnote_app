/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.dropbox.android.sample;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI.Entry;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;
import android.widget.PopupWindow;



public class DBRoulette extends Activity {
    private static final String TAG = "DBRoulette";

    ///////////////////////////////////////////////////////////////////////////
    //                      Your app-specific settings.                      //
    ///////////////////////////////////////////////////////////////////////////

    // Replace this with your app key and secret assigned by Dropbox.
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    final static private String APP_KEY = "1c4n6vgplqjcqls";
    final static private String APP_SECRET = "l6k1bk1zrxzrlb3";

    // If you'd like to change the access type to the full Dropbox instead of
    // an app folder, change this value.
    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

    ///////////////////////////////////////////////////////////////////////////
    //                      End app-specific settings.                       //
    ///////////////////////////////////////////////////////////////////////////

    // You don't need to change these, leave them alone.
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";


    DropboxAPI<AndroidAuthSession> mApi;

    private boolean mLoggedIn;

    // Android widgets
    private Button mSubmit;
    private LinearLayout mDisplay;
    
    //private Button addbutton;
    
    private Button mPhoto1;
    private Button mPhoto2;
    private Button mPhoto3;
    private Button mPhoto4;
    private Button mPhoto5;
    
    private Button mRoulette1;
    private Button mRoulette2;
    private Button mRoulette3;
    private Button mRoulette4;
    private Button mRoulette5;
    
    private Button course1button;
    private Button course2button;
    private Button course3button;
    private Button course4button;
    private Button course5button;

    private ImageView mImage;

    private final String PHOTO_DIR = "/classes/";

    final static private int NEW_PICTURE = 1;
    private String mCameraFileName;
    private ArrayList<String> class_names;
    private int button_num;
    private String path;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCameraFileName = savedInstanceState.getString("mCameraFileName");
        }

        // We create a new AuthSession so that we can use the Dropbox API.
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

        // Basic Android widgets
        setContentView(R.layout.main);

        checkAppKeySetup();
    	

        mSubmit = (Button)findViewById(R.id.auth_button);
        mSubmit.setEnabled(false);

        mSubmit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // This logs you out if you're logged in, or vice versa
                if (mLoggedIn) {
                    logOut();
                } else {
                    // Start the remote authentication
                    mApi.getSession().startAuthentication(DBRoulette.this);
                }
            }
        });

        ImageView logopic = new ImageView(this);
        logopic.setImageResource(R.drawable.upnote_logo);
        mDisplay = (LinearLayout)findViewById(R.id.logged_in_display);

        // This is where a photo is displayed
        mImage = (ImageView)findViewById(R.id.image_view);
        
        
        // This is the button to take a photo
        //addbutton = (Button)findViewById(R.id.addbutton);
        mPhoto1 = (Button)findViewById(R.id.class1_photo_button);
        mPhoto2 = (Button)findViewById(R.id.class2_photo_button);
        mPhoto3 = (Button)findViewById(R.id.class3_photo_button);
        mPhoto4 = (Button)findViewById(R.id.class4_photo_button);
        mPhoto5 = (Button)findViewById(R.id.class5_photo_button);
        
        
        
        
        mPhoto1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                button_num = 0;
                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                } catch (ActivityNotFoundException e) {
                    showToast("There doesn't seem to be a camera.");
                }
            }
        
        });
        
        mPhoto2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                button_num = 1;
                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                } catch (ActivityNotFoundException e) {
                    showToast("There doesn't seem to be a camera.");
                }
            }
        
        });
        
        mPhoto3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                button_num = 2;
                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                } catch (ActivityNotFoundException e) {
                    showToast("There doesn't seem to be a camera.");
                }
            }
        
        });
        
        mPhoto4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                button_num = 3;
                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                } catch (ActivityNotFoundException e) {
                    showToast("There doesn't seem to be a camera.");
                }
            }
        
        });
        
        mPhoto5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                button_num = 4;
                // Picture from camera
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                // This is not the right way to do this, but for some reason, having
                // it store it in
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URI isn't working right.

                Date date = new Date();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd-kk-mm-ss");

                String newPicFile = df.format(date) + ".jpg";
                String outPath = "/sdcard/" + newPicFile;
                File outFile = new File(outPath);

                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                Log.i(TAG, "Importing New Picture: " + mCameraFileName);
                try {
                    startActivityForResult(intent, NEW_PICTURE);
                } catch (ActivityNotFoundException e) {
                    showToast("There doesn't seem to be a camera.");
                }
            }
        
        });

        // USEFUL CODE -- DISPLAY's random picture in correct directory
        // This is the button to take a photo
        mRoulette1 = (Button)findViewById(R.id.course1button);
        mRoulette2 = (Button)findViewById(R.id.course2button);
        mRoulette3 = (Button)findViewById(R.id.course3button);
        mRoulette4 = (Button)findViewById(R.id.course4button);
        mRoulette5 = (Button)findViewById(R.id.course5button);

       // Typeface scrawl=Typeface.createFromAsset(getAssets(),"ShadowsIntoLight.ttf");
       // mRoulette1.setTypeface(scrawl);
       // mRoulette2.setTypeface(scrawl);
       // mRoulette3.setTypeface(scrawl);
//        mRoulette4.setTypeface(scrawl);
//        mRoulette5.setTypeface(scrawl);
//        mPhoto1.setTypeface(scrawl);
//        mPhoto2.setTypeface(scrawl);
//        mPhoto3.setTypeface(scrawl);
//        mPhoto4.setTypeface(scrawl);
//        mPhoto5.setTypeface(scrawl);
//        mSubmit.setTypeface(scrawl);
        
        // Display the proper UI state if logged in or not
        setLoggedIn(mApi.getSession().isLinked());
        List<String> class_paths = new ArrayList<String>();
        class_names = new ArrayList<String>();
        String delims = "/";
        try {
            Entry existingEntry = mApi.metadata("/classes/", 10000, null, true, null);
            List<Entry> files_in_folder = existingEntry.contents;
            int i = 0;
            	while (i < files_in_folder.size()){
            		class_paths.add((files_in_folder.get(i).path));
            		String[] tokens = class_paths.get(i).split(delims);
            		String current_Name = tokens[tokens.length-1];
            		class_names.add(current_Name);
            		i++;
            	}
            	//System.out.println(class_names);

            // do stuff with the Entry
            
        } catch (DropboxException e) {
            System.out.println("Something went wrong: " + e);
        }
        
        mRoulette1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
            	Intent i = new Intent(DBRoulette.this, ViewPhotos.class);
            	path = PHOTO_DIR + class_names.get(0) + "/";
            	i.putExtra("path", path);
            	i.putExtra("name", class_names.get(0));
            	startActivity(i);
            	
            }
        });
        
        mRoulette2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(DBRoulette.this, ViewPhotos.class);
            	path = PHOTO_DIR + class_names.get(1) + "/";
            	i.putExtra("path", path);
            	i.putExtra("name", class_names.get(1));
            	startActivity(i);
            }
        });
        
        mRoulette3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(DBRoulette.this, ViewPhotos.class);
            	path = PHOTO_DIR + class_names.get(2) + "/";
            	i.putExtra("path", path);
            	i.putExtra("name", class_names.get(2));
            	startActivity(i);
            }
        });
        
        mRoulette4.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(DBRoulette.this, ViewPhotos.class);
            	path = PHOTO_DIR + class_names.get(3) + "/";
            	i.putExtra("path", path);
            	i.putExtra("name", class_names.get(3));
            	startActivity(i);
            }
        });
        
        mRoulette5.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent i = new Intent(DBRoulette.this, ViewPhotos.class);
            	path = PHOTO_DIR + class_names.get(4) + "/";
            	i.putExtra("path", path);
            	i.putExtra("name", class_names.get(4));
            	startActivity(i);
            }
        });
        
        //addbutton = (Button)findViewById(R.id.addbutton);
        
        course1button = (Button)findViewById(R.id.course1button);
        course2button = (Button)findViewById(R.id.course2button);
        course3button = (Button)findViewById(R.id.course3button);
        course4button = (Button)findViewById(R.id.course4button);
        course5button = (Button)findViewById(R.id.course5button);
        
        course1button.setText(class_names.get(0));
        course2button.setText(class_names.get(1));
        course3button.setText(class_names.get(2));
        course4button.setText(class_names.get(3));
        course5button.setText(class_names.get(4));
        
        
        
        final Button addbutton = (Button) findViewById(R.id.addbutton);
       // addbutton.setTypeface(scrawl);
        addbutton.setEnabled(false);
        addbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(DBRoulette.this, AddActivity.class);
            	intent.putStringArrayListExtra("CLASSES", class_names);
            	//System.out.println(class_names);
            	startActivity(intent);
                System.out.println("Im adding!\n");
            }
        });
    }
    

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("mCameraFileName", mCameraFileName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }

    // This is what gets called on finishing a media piece to import
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_PICTURE) {
            // return from file upload
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = null;
                if (data != null) {
                    uri = data.getData();
                }
                if (uri == null && mCameraFileName != null) {
                    uri = Uri.fromFile(new File(mCameraFileName));
                }
                
                File file = new File(mCameraFileName);

                if (uri != null) {
                	String UPLOAD_DIR = PHOTO_DIR + class_names.get(button_num) + "/";
                    UploadPicture upload = new UploadPicture(this, mApi, UPLOAD_DIR, file);
                    System.out.println(UPLOAD_DIR);
                    System.out.println(class_names);
                    upload.execute();
                }
            } else {
                Log.w(TAG, "Unknown Activity Result from mediaImport: "
                        + resultCode);
            }
        }
    }

    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }

    /**
     * Convenience function to change UI state based on being logged in
     */
    private void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	if (loggedIn) {
    		mSubmit.setText("Unlink from Dropbox");
            mDisplay.setVisibility(View.VISIBLE);
    	} else {
    		mSubmit.setText("Link with Dropbox");
            mDisplay.setVisibility(View.GONE);
            mImage.setImageDrawable(null);
    	}
    }

    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

     // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
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

    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
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
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.newcourse:
            	Intent intent = new Intent(DBRoulette.this, AddActivity.class);
            	intent.putStringArrayListExtra("CLASSES", class_names);
            	//System.out.println(class_names);
            	startActivity(intent);
                System.out.println("Im adding!\n");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
  
}
