package org.pasut.android.findme.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_prepare_search)
public class PrepareSearchActivity extends RoboActionBarActivity {
    private final static String TAG = PrepareSearchActivity.class.getSimpleName();
    public final static String CONTACT = "contact";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User contact = getIntent().getParcelableExtra(CONTACT);
        setupToolbar();
        populate(contact);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(){
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_gift_32);
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
