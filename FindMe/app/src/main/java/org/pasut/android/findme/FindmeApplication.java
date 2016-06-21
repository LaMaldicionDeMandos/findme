package org.pasut.android.findme;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import roboguice.RoboGuice;

/**
 * Created by boot on 3/4/16.
 */
public class FindmeApplication extends Application {
    public final static String MASTER_USER_KEY = "master_user";
    public final static String USER_CONTACTS_KEY = "user_contacts";

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        RoboGuice.setUseAnnotationDatabases(false);
        RoboGuice.getOrCreateBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(this), new FindmeModule());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
