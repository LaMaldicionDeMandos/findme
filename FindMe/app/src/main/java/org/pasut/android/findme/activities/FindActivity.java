package org.pasut.android.findme.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_find)
public class FindActivity extends RoboActionBarActivity {
    private User contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contact = getIntent().getParcelableExtra(PrepareSearchActivity.CONTACT);
    }
}
