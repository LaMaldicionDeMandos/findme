package org.pasut.android.findme;

import com.google.firebase.database.DatabaseReference;
import com.google.inject.Binder;
import com.google.inject.Module;

import org.pasut.android.findme.providers.FirebaseProvider;
import org.pasut.android.findme.service.DefaultPreferencesService;
import org.pasut.android.findme.service.PreferencesService;
import org.pasut.android.findme.service.Services;

/**
 * Created by boot on 3/4/16.
 */
public class FindmeModule implements Module {
    @Override
    public void configure(Binder binder) {
        binder.bind(PreferencesService.class).to(DefaultPreferencesService.class);
        binder.bind(DatabaseReference.class).toProvider(FirebaseProvider.class);
        binder.bind(Services.class);
    }
}
