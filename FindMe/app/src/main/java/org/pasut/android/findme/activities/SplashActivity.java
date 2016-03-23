package org.pasut.android.findme.activities;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import org.pasut.android.findme.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity {
    @InjectView(R.id.splash)
    private ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splash.setBackgroundResource(R.drawable.splash_animation);

        AnimationDrawable anim = (AnimationDrawable) splash.getBackground();
        anim.start();
    }
}
