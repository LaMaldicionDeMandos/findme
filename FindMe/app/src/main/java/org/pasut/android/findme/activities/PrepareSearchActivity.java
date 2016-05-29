package org.pasut.android.findme.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.inject.Inject;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;
import org.pasut.android.findme.model.UserProfile;
import org.pasut.android.findme.model.UserState;
import org.pasut.android.findme.service.Services;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_prepare_search)
public class PrepareSearchActivity extends RoboActionBarActivity {
    private final static String TAG = PrepareSearchActivity.class.getSimpleName();
    public final static String CONTACT = "contact";

    private User contact;

    @InjectView(R.id.main)
    ViewGroup main;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.header)
    ViewGroup header;

    @InjectView(R.id.main_container)
    ViewGroup mainContainer;

    @InjectView(R.id.search_photo)
    View searchPhoto;

    @InjectView(R.id.find_text)
    TextView findText;

    @InjectView(R.id.call)
    View callButton;

    @InjectView(R.id.circle1)
    View circle1;

    @InjectView(R.id.circle2)
    View circle2;

    @Inject
    Services services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contact = getIntent().getParcelableExtra(CONTACT);
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
        findText.setText(String.format(getString(R.string.searching_person), user.getName()));
    }

    public void search(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(main, new Explode());
        }
        findText.setVisibility(View.VISIBLE);
        searchPhoto.setVisibility(View.VISIBLE);
        callButton.setVisibility(View.INVISIBLE);
        circle1.setVisibility(View.VISIBLE);
        circle2.setVisibility(View.VISIBLE);
        //header.setLayoutParams(new RelativeLayout.LayoutParams(header.getWidth(), 0));
        header.setVisibility(View.GONE);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));

        Animation anim1 = AnimationUtils.loadAnimation(this, R.anim.expand1);
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.expand2);
        circle1.startAnimation(anim1);
        circle2.startAnimation(anim2);

        services.callContact(contact, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data changed " + dataSnapshot);
                if (!dataSnapshot.exists()) {
                    Toast.makeText(PrepareSearchActivity.this, "El usuario no existe en firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PrepareSearchActivity.this, "Oops todavia no esta implementado ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "Data canceled " + firebaseError);
            }
        });
    }
}
