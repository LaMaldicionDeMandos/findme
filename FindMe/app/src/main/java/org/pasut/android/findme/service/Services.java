package org.pasut.android.findme.service;

import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.pasut.android.findme.FindmeApplication;
import org.pasut.android.findme.model.User;
import org.pasut.android.findme.model.UserProfile;
import org.pasut.android.findme.model.UserState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by boot on 10/2/15.
 */
@Singleton
public class Services {
    private final static String TAG = Services.class.getSimpleName();
    private final PreferencesService preferences;
    private final Firebase firebase;

    @Inject
    public Services(final Firebase firebase, final PreferencesService preferences) {
        this.firebase = firebase;
        this.preferences = preferences;
    }

    public void removeListener(final ValueEventListener listener) {
        Log.d(TAG, "Removing Event Listener " + listener);
        firebase.removeEventListener(listener);
    }

    public void signUp(final String accountId) {
        final String userId = getFirebaseUserId(accountId);
        firebase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Data changed " + dataSnapshot);
                if (!dataSnapshot.exists()) {
                    UserProfile userProfile = new UserProfile(UserState.ACTIVE);
                    firebase.child(userId).setValue(userProfile);
                } else {
                    UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                    userProfile.activate();
                    firebase.child(userId).setValue(userProfile);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "Data canceled " + firebaseError);
            }
        });
        //firebase.child(user).setValue("USER");
    }

    private String getFirebaseUserId(final User user) {
        return getFirebaseUserId(user.getId());
    }

    private String getFirebaseUserId(final String userId) {
        return userId.replaceAll("\\.","_");
    }

    public void callContact(final User user, final ValueEventListener listener) {
        String userId = getFirebaseUserId(user);
        firebase.child(userId).addListenerForSingleValueEvent(listener);
    }

    public void callToContact(final User user, final ValueEventListener listener) {
        String userId = getFirebaseUserId(user);
        String me = getFirebaseUserId(preferences.get(FindmeApplication.MASTER_USER_KEY, String.class));
        String template = "%s/requester/%s";
        String path = String.format(template, userId, me);
        firebase.child(path).setValue("off");
        firebase.child(path).addValueEventListener(listener);
    }
}
