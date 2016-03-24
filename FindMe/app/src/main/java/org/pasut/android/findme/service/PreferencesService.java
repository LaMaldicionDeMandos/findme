package org.pasut.android.findme.service;

/**
 * Created by boot on 3/24/16.
 */
public interface PreferencesService {
    <T> T get(String key, Class<T> clazz);
    <T> void put(String key, T value);
    boolean contain(String key);
    void delete(String key);
}
