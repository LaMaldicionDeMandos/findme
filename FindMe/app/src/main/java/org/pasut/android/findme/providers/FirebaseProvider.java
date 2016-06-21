package org.pasut.android.findme.providers;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.pasut.android.findme.R;

import roboguice.inject.InjectResource;

/**
 * Created by boot on 3/24/16.
 */
@Singleton
public class FirebaseProvider implements Provider<DatabaseReference>{
    private final static String TAG = FirebaseProvider.class.getSimpleName();
    @InjectResource(R.string.firebase_url)
    private String url;

    private DatabaseReference firebase;

    @Override
    public DatabaseReference get() {
        if (firebase == null) {
            firebase = FirebaseDatabase.getInstance().getReference();
        }
        return firebase;
    }
}
