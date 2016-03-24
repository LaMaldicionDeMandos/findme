package org.pasut.android.findme.service;

import android.app.Application;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by boot on 3/24/16.
 */
@Singleton
public class DefaultPreferencesService implements PreferencesService {
    private final static String DB_NAME = "Findme-preferences";

    private final SharedPreferences helper;
    private final Gson gson;

    @Inject
    public DefaultPreferencesService(Application app){
        this.helper = app.getSharedPreferences(DB_NAME, 4);
        gson = new Gson();
    }

    public <T> T get(String key, Class<T> clazz){
        String json = helper.getString(key, null);
        return gson.fromJson(json, clazz);
    }

    public <T> void put(String key, T value){
        String json = gson.toJson(value);
        SharedPreferences.Editor editor = helper.edit();
        editor.putString(key, json);
        editor.commit();
    }

    public boolean contain(String key){
        return helper.contains(key);
    }

    @Override
    public void delete(String key) {
        helper.edit().remove(key);
    }
}
