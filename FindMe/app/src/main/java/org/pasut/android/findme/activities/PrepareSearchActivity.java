package org.pasut.android.findme.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

    @InjectView(R.id.main_container)
    ViewGroup main;

    @InjectView(R.id.search_photo)
    View searchPhoto;

    @InjectView(R.id.call)
    View callButton;

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
        ImageView searchPhoto = (ImageView) findViewById(R.id.search_photo);
        if (user.getUri() == null) {
            photo.setImageResource(R.drawable.ic_account_circle);
            searchPhoto.setImageResource(R.drawable.ic_account_circle);
        } else {
            try {
                photo.setImageURI(user.getUri());
                searchPhoto.setImageURI(user.getUri());
            } catch(NullPointerException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void search(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode transition = new Explode();
            TransitionManager.beginDelayedTransition(main, transition);
        }

        searchPhoto.setVisibility(View.VISIBLE);
        callButton.setVisibility(View.INVISIBLE);


        //Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.clockwise);
        //myView.startAnimation(animation);
/*
        // get the center for the clipping circle
        int cx = (myView.getLeft() + myView.getRight()) / 2;
        int cy = (myView.getTop() + myView.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight());

        // create the animator for this view (the start radius is zero)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

            // make the view visible and start the animation
            anim.start();
        } else {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
            myView.startAnimation(animation);
        }
        */
    }
}
