package org.pasut.android.findme.activities;

import android.content.ContentProviderOperation;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.util.Sets;
import com.google.common.collect.Lists;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;
import org.pasut.android.findme.model.UserProfile;
import org.pasut.android.findme.model.UserState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import roboguice.activity.RoboActionBarActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static com.google.common.collect.Lists.newArrayList;

@ContentView(R.layout.activity_contacts)
public class ContactsActivity extends RoboActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    public static final String FINDME_COM = "findme.com";
    private final static String TAG = ContactsActivity.class.getSimpleName();

    @InjectView(R.id.contacts)
    private RecyclerView listView;

    private GoogleApiClient googleClient;

    private final List<User> contacts = newArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupToolbar();

        googleClient = ActivityUtils.getGoogleClient(this, this, this);

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                findContacts();
                if (contacts.isEmpty()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showImportContactsDialog();
                        }
                    });
                }
                return null;
            }
        }.execute();
        Log.d(TAG, contacts.toString());
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> adapter = new ContactsAdapter(contacts);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);
    }

    private void setupToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        //ab.setHomeAsUpIndicator(R.drawable.ic_gift_32);
        //ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Suspended?");
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed: " + connectionResult);
        finish();
    }

    private void showImportContactsDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this)
                .setTitle(R.string.add_contact_title)
                .setMessage(R.string.add_contact_message)
                .setCancelable(false)
                .setNegativeButton(R.string.disagree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Agregando contactos");
                        importContacts();
                    }
                });
        alertBuilder.create().show();
    }

    private void importContacts() {
        Set<User> users = Sets.newHashSet();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(uri,
                new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.Photo.PHOTO_URI},
                ContactsContract.Contacts.IN_VISIBLE_GROUP + " = 1 and " +
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " is not null and " +
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " not like '%@s.whatsapp.net' and " +
                        ContactsContract.CommonDataKinds.Email.ADDRESS + " like '%@%'"
                ,
                null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String id = cursor.getString(0);
            String email = cursor.getString(1);
            String name = cursor.getString(2);
            String photo = cursor.getString(3);
            Log.d(TAG, id + " display name: " + name + " Email: " + email + " Photo: " + photo);
            uri = photo == null ? null : Uri.parse(photo);
            User user = new User(email, name, uri, new UserProfile(UserState.UNKNOW));
            if (!users.contains(user)) {
                addContact(id, user);
            }
            users.add(user);
        }
        contacts.addAll(users);
        listView.getAdapter().notifyDataSetChanged();
        cursor.close();
    }

    private void addContact(final String id, final User user) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ContentProviderOperation op = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, FINDME_COM)
                .withValue(ContactsContract.RawContacts.CONTACT_ID, id)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, user.getId()).build();
        ops.add(op);
        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, user.getName())
                .build();
        ops.add(op);
        try {
            Log.d(TAG, "Inserting user " + user.getName());
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Can't insert raw for user " + user.getName() + " " + e.getMessage());
            Log.e(TAG, "Can't insert raw for user " + user.getName() + " " + e);
        }
    }

    private void findContacts() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        uri = Uri.withAppendedPath(uri, ContactsContract.Contacts.Entity.CONTENT_DIRECTORY);
        Cursor cursor = getContentResolver().query(uri,
                new String[]{ContactsContract.RawContacts.ACCOUNT_NAME, ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, ContactsContract.Contacts.Photo.PHOTO_URI},
                ContactsContract.RawContacts.ACCOUNT_TYPE + " like '" + FINDME_COM + "'",
                null, null);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
            String email = cursor.getString(0);
            String name = cursor.getString(1);
            String photo = cursor.getString(2);
            Log.d(TAG, "Found: display name: " + name + " Email: " + email + " Photo: " + photo);
            uri = photo == null ? null : Uri.parse(photo);
            User user = new User(email, name, uri, new UserProfile(UserState.UNKNOW));
            contacts.add(user);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.getAdapter().notifyDataSetChanged();
            }
        });
        cursor.close();
    }

    public void addContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            Uri contactData = data.getData();
            Cursor userCursor =  getContentResolver().query(contactData, new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI

            }, null, null, null);
            if (userCursor.moveToFirst()) {
                Cursor emailCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        new String[]{
                                ContactsContract.RawContacts.CONTACT_ID,
                                ContactsContract.CommonDataKinds.Email.ADDRESS
                        }, ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                        new String[]{userCursor.getString(0)}, null);
                if (emailCursor.moveToFirst()) {
                    String id = emailCursor.getString(0);
                    String email = emailCursor.getString(1);
                    String name = userCursor.getString(1);
                    String photo = userCursor.getString(2);
                    Uri uri = photo == null ? null : Uri.parse(photo);
                    User user = new User(email, name, uri, new UserProfile(UserState.UNKNOW));
                    addContact(id, user);
                    contacts.add(user);
                    listView.getAdapter().notifyDataSetChanged();
                }
            }
        }
    }

    class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private final List<User> contacts;

        public ContactsAdapter(final List<User> contacts) {
            this.contacts = contacts;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(ContactsActivity.this)
                    .inflate(R.layout.view_contact, viewGroup, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder contactViewHolder, int i) {
            contactViewHolder.name.setText(contacts.get(i).getName());
            if (contacts.get(i).getUri() == null) {
                contactViewHolder.photo.setImageResource(R.drawable.ic_account_circle);
            } else {
                contactViewHolder.photo.setImageURI(contacts.get(i).getUri());
            }
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView name;
            private final ImageView photo;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (TextView)itemView.findViewById(R.id.text);
                photo = (ImageView)itemView.findViewById(R.id.photo);
            }
        }
    }
}
