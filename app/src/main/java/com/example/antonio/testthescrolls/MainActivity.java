package com.example.antonio.testthescrolls;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.Image;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private ViewGroup mRoot;
    private TextView mWelcomeMessage;
    private TextView mIdTextView;
    private ImageView mProfilePic;
    private CallbackManager mCallbackManager;
    private AccessTokenTracker accessTokentracker;
    private ProfileTracker profileTracker;
    private boolean isSmall = true;
    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();

            Profile profile = Profile.getCurrentProfile();

            setProfileData(profile);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mp = MediaPlayer.create(this,R.raw.triggered);

        mRoot = (ViewGroup) findViewById(R.id.container_a);
        mProfilePic = (ImageView) findViewById(R.id.imageView2);
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(mRoot);
                ViewGroup.LayoutParams params = view.getLayoutParams();
                mp.start();

                if (isSmall){
                    params.height= 900;
                    params.width= 900;
                    isSmall = false;
                }
                else {
                    params.height = 300;
                    params.width = 300;
                    isSmall = true;
                }

                view.setLayoutParams(params);
            }
        });
        mWelcomeMessage = (TextView) findViewById(R.id.textView);
        mIdTextView = (TextView) findViewById(R.id.textView3);
        LoginButton button = (LoginButton) findViewById(R.id.login_button);
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add("public_profile");
        button.setReadPermissions(permissions);

        mCallbackManager = CallbackManager.Factory.create();

        accessTokentracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    mIdTextView.setVisibility(View.GONE);
                    mProfilePic.setVisibility(View.GONE);
                    mWelcomeMessage.setText("Please log in");
                }
            }
        };
        accessTokentracker.startTracking();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if (currentProfile == null) {
                    mWelcomeMessage.setText("Please log in");
                }
            }
        };
        profileTracker.startTracking();

        button.registerCallback(mCallbackManager, mCallback);
 
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        setProfileData(profile);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();

        profileTracker.stopTracking();
        accessTokentracker.stopTracking();
    }

    private void setProfileData(Profile profile) {

        if (profile != null) {
            mWelcomeMessage.setText("Welcome " + profile.getName());
            mIdTextView.setText("Your ID is : " + profile.getId());
            Picasso.with(this).load(profile.getProfilePictureUri(300,300)).into(mProfilePic);
            mIdTextView.setVisibility(View.VISIBLE);
            mProfilePic.setVisibility(View.VISIBLE);

        } else {
            mWelcomeMessage.setText("Please log in");
            mIdTextView.setVisibility(View.GONE);
            mProfilePic.setVisibility(View.GONE);
        }
    }
}
