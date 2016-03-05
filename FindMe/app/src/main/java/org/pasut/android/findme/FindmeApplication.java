package org.pasut.android.findme;

import android.app.Application;

import roboguice.RoboGuice;

/**
 * Created by boot on 3/4/16.
 */
public class FindmeApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.setUseAnnotationDatabases(false);
        //RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
        //        RoboGuice.newDefaultRoboModule(this), new GiftArModule());
    }
}
