package org.pasut.android.findme.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.inject.Inject;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;
import org.pasut.android.findme.service.Services;

import jp.wasabeef.glide.transformations.BlurTransformation;
import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_prepare_search)
public class PrepareSearchActivity extends RoboActionBarActivity {
    private final static String TAG = PrepareSearchActivity.class.getSimpleName();
    private final static int SEND_SMS_REQUEST = 5556;

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

    @InjectView(R.id.profile_photo)
    View profilePhoto;

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

    private String phone;

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
        findText.setText(String.format(getString(R.string.searching_person), user.getName()));
        populatePhoto(user);
    }

    private void populatePhoto(final User user) {
        ImageView photo = (ImageView) findViewById(R.id.photo);
        ImageView searchPhoto = (ImageView) findViewById(R.id.search_photo);
        ImageView profilePhoto = (ImageView) findViewById(R.id.profile_photo);
        if (user.getUri() == null) {
            photo.setImageResource(R.drawable.ic_account_circle);
            profilePhoto.setImageResource(R.drawable.ic_account_circle);
            searchPhoto.setImageResource(R.drawable.ic_account_circle);
        } else {
            try {
                photo.setImageURI(user.getUri());
                profilePhoto.setImageURI(user.getUri());
                searchPhoto.setImageURI(user.getUri());
            } catch(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        Glide.with(this).load(user.getUri()).bitmapTransform(new BlurTransformation(this, 20))
                .into(photo);
    }

    public void search(View view) {
        services.callContact(contact, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data changed " + dataSnapshot);
                if (dataSnapshot.exists()) {
                    waitToSearch();
                    Toast.makeText(PrepareSearchActivity.this, "Oops todavia no esta implementado ", Toast.LENGTH_SHORT).show();
                } else {
                    showSendSMSDialog(contact);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "Data canceled " + firebaseError);
                finish();
            }
        });
    }

    private void waitToSearch() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionManager.beginDelayedTransition(main, new Explode());
        }

        findText.setVisibility(View.VISIBLE);
        profilePhoto.setVisibility(View.INVISIBLE);
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
    }

    private void sendSmsOrEmail(final User user) {
        phone = findUserPhoneNumber(user);
        if (phone == null) {
            sendEmail(user.getId(), user);
        } else {
            sendSms(phone, user);
        }
    }

    private void sendEmail(final String email, final User user) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == SEND_SMS_REQUEST) {
            sendSms(phone, contact);
        }
    }

    private void sendSms(final String phone, final User user) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
            if (permission == PackageManager.PERMISSION_DENIED) {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST);
            } else {
                sendSmsWithPermission(phone, user);
            }
        } else {
            sendSmsWithPermission(phone, user);
        }
    }

    private void sendSmsWithPermission(String phone, User user) {
        try {
            SmsManager smsManager = SmsManager.getDefault();

            smsManager.sendTextMessage(phone, null, String.format(getString(R.string.sms), user.getName()),
                    null, null);
            Toast.makeText(this, getString(R.string.sms_sent), Toast.LENGTH_LONG).show();
        }catch (Exception e) {
            Toast.makeText(this, getString(R.string.can_not_send_message), Toast.LENGTH_LONG).show();
        }
    }

    private String findUserPhoneNumber(final User user) {
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = '" + user.getContactId() + "'", null, null);
        String phone = null;
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            phone = cursor.getString(0);
            Log.d(TAG, "Found: Number: " + phone);
        }
        return phone;
    }

    private void showSendSMSDialog(final User user) {
        new AlertDialog.Builder(PrepareSearchActivity.this)
                .setTitle("Recomendar FIND ME?")
                .setMessage(user.getName() + " no tiene FIND ME")
                .setPositiveButton("INVITAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendSmsOrEmail(user);
                    }
                })
                .setNegativeButton("DISCARD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
