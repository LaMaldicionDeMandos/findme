package org.pasut.android.findme.activities;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.util.Sets;

import org.pasut.android.findme.R;
import org.pasut.android.findme.model.User;
import org.pasut.android.findme.model.UserProfile;
import org.pasut.android.findme.model.UserState;

import java.util.ArrayList;
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

    private ContactsAdapter adapter;
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    @InjectView(R.id.contacts)
    private RecyclerView listView;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

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
        adapter = new ContactsAdapter(this, contacts,
                new ContactsAdapter.ViewHolder.ClickListener() {
                    @Override
                    public void onItemClicked(int position) {
                        if (actionMode != null) {
                            toggleSelection(position);
                        }
                    }

                    @Override
                    public boolean onItemLongClicked(int position) {
                        if (actionMode == null) {
                            actionMode = startSupportActionMode(actionModeCallback);
                        }

                        toggleSelection(position);
                        return true;
                    }
                });
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(adapter);

        listView.setItemAnimator(new DefaultItemAnimator());
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            toolbar.setTitle("");
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private void setupToolbar(){
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
                } else {
                    String userName = userCursor.getString(1);
                    showNotEmailUser(userName);
                }
            }
        }
    }

    private void showNotEmailUser(final String userName) {
        String message = getString(R.string.not_email_message);
        Toast.makeText(this, String.format(message, userName), Toast.LENGTH_LONG).show();
    }

    static class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private final List<User> contacts;
        private final SparseBooleanArray selectedItems;
        private final Context context;

        private ViewHolder.ClickListener clickListener;

        public ContactsAdapter(final Context context, final List<User> contacts,
                               final ViewHolder.ClickListener listener) {
            this.context = context;
            this.contacts = contacts;
            this.selectedItems = new SparseBooleanArray();
            this.clickListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(context)
                    .inflate(R.layout.view_contact, viewGroup, false);
            return new ViewHolder(v, clickListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder contactViewHolder, int i) {
            contactViewHolder.name.setText(contacts.get(i).getName());
            contactViewHolder.email.setText(contacts.get(i).getId());
            if (contacts.get(i).getUri() == null) {
                contactViewHolder.photo.setImageResource(R.drawable.ic_account_circle);
            } else {
                contactViewHolder.photo.setImageURI(contacts.get(i).getUri());
            }
            contactViewHolder.layout.setBackgroundResource(isSelected(i)
                    ? R.color.selected_item
                    : R.color.background);
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,
        View.OnClickListener {
            private final TextView name;
            private final TextView email;
            private final ImageView photo;
            private final View layout;

            private ClickListener listener;

            public ViewHolder(View itemView, ClickListener listener) {
                super(itemView);
                this.listener = listener;
                name = (TextView)itemView.findViewById(R.id.username);
                email = (TextView)itemView.findViewById(R.id.email);
                photo = (ImageView)itemView.findViewById(R.id.photo);
                layout = itemView.findViewById(R.id.layout);
                itemView.setOnLongClickListener(this);
                itemView.setOnClickListener(this);
            }

            @Override
            public boolean onLongClick(View v) {
                if (listener != null) {
                    return listener.onItemLongClicked(getLayoutPosition());
                }
                return false;
            }

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClicked(getLayoutPosition());
                }
            }

            interface ClickListener {
                void onItemClicked(int position);
                boolean onItemLongClicked(int position);
            }
        }

        public boolean isSelected(final int position) {
            return getSelectedItems().contains(position);
        }

        public void toggleSelection(int position) {
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
            } else {
                selectedItems.put(position, true);
            }
            notifyItemChanged(position);
        }

        public void clearSelection() {
            List<Integer> selection = getSelectedItems();
            selectedItems.clear();
            for (Integer i : selection) {
                notifyItemChanged(i);
            }
        }

        public int getSelectedItemCount() {
            return selectedItems.size();
        }

        public List<Integer> getSelectedItems() {
            List<Integer> items = new ArrayList<>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); ++i) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_contacts, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // TODO: actually remove items
                    Log.d(TAG, "menu_remove");
                    Toast.makeText(getBaseContext(),"Deleting item", Toast.LENGTH_LONG).show();
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.clearSelection();
            toolbar.setTitle(R.string.contacts);
            actionMode = null;
        }
    }
}
