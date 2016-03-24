package org.pasut.android.findme.model;

import android.net.Uri;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Created by boot on 3/24/16.
 */
public class User {
    private final String id;
    private final String name;
    private final UserProfile profile;
    private final Uri uri;

    public User(final String id, final String name, final Uri uri, final UserProfile profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

    public UserProfile getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User u = (User)o;
        return Objects.equal(id, u.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(User.class)
                .add("id", id)
                .add("name", name)
                .add("avatar", uri)
                .add("profile", profile)
                .toString();
    }
}
