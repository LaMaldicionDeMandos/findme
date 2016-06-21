package org.pasut.android.findme.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_find)
public class FindActivity extends RoboActionBarActivity {
    private User contact;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contact = getIntent().getParcelableExtra(PrepareSearchActivity.CONTACT);
        setupToolbar();
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        setTitle(contact.getName());
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_gift_32);
    }
}
