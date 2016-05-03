package org.pasut.android.findme.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.Date;

/**
 * Created by boot on 3/24/16.
 */
public class User implements Parcelable {
    private final String id;
    private final String name;
    private final UserProfile profile;
    private final Uri uri;
    private final long lastAccess;

    public User(final String id, final String name, final Uri uri, final UserProfile profile) {
        this(id, name, uri, profile, new Date().getTime());
    }

    public User(final String id, final String name, final Uri uri, final UserProfile profile,
                final long lastAccess) {
        this.id = id;
        this.name = name;
        this.profile = profile;
        this.uri = uri;
        this.lastAccess = lastAccess;
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

    public long getLastAccess() {
        return lastAccess;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User u = (User)o;
        return Objects.equal(id, u.id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.profile, flags);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.lastAccess);
    }

    protected User(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.profile = in.readParcelable(UserProfile.class.getClassLoader());
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.lastAccess = in.readLong();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
                .add("lastAccess", lastAccess)
                .toString();
    }
}
