package org.pasut.android.findme.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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
        populate(contact);
    }

    private void populate(final User user) {
        TextView text = (TextView) findViewById(R.id.name);
        text.setText(user.getName());
        ImageView photo = (ImageView) findViewById(R.id.photo);
        if (user.getUri() == null) {
            photo.setImageResource(R.drawable.ic_account_circle);
        } else {
            try {
                photo.setImageURI(user.getUri());
            } catch(NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }
}
