package org.pasut.android.findme.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import org.pasut.android.findme.R;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
