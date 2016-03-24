package org.pasut.android.findme;

import com.firebase.client.Firebase;
import com.google.inject.Binder;
import com.google.inject.Module;

import org.pasut.android.findme.providers.FirebaseProvider;
import org.pasut.android.findme.service.DefaultPreferencesService;
import org.pasut.android.findme.service.PreferencesService;

/**
 * Created by boot on 3/4/16.
 */
public class FindmeModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(PreferencesService.class).to(DefaultPreferencesService.class);
        binder.bind(Firebase.class).toProvider(FirebaseProvider.class);
    }
}
