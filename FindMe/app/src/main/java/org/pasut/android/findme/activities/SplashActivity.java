package org.pasut.android.findme.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.google.inject.Inject;

import org.pasut.android.findme.FindmeApplication;
import org.pasut.android.findme.R;
import org.pasut.android.findme.service.PreferencesService;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static org.pasut.android.findme.FindmeApplication.MASTER_USER_KEY;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity {
    public final static int RESPONSE = 1;
    private final static String TAG = SplashActivity.class.getSimpleName();

    @InjectView(R.id.splash)
    private ImageView splash;

    @Inject
    private PreferencesService preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splash.setBackgroundResource(R.drawable.splash_animation);

        AnimationDrawable anim = (AnimationDrawable) splash.getBackground();
        anim.start();

        if(!preferences.contain(MASTER_USER_KEY)) {
            Log.d(TAG, "Todavia no se registro ningun usuario");
            startActivityForResult(new Intent(this, LoginActivity.class), RESPONSE);
        } else {
            Log.d(TAG, "Ya existe un usuario registrado");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
