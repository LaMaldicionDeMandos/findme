package org.pasut.android.findme.activities;

import android.os.Bundle;
import android.widget.TextView;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_prepare_search)
public class PrepareSearchActivity extends RoboActionBarActivity {
    private final static String TAG = PrepareSearchActivity.class.getSimpleName();
    public final static String CONTACT = "contact";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User contact = getIntent().getParcelableExtra(CONTACT);
        TextView text = (TextView) findViewById(R.id.texto);
        text.setText(contact.getName());
    }
}
