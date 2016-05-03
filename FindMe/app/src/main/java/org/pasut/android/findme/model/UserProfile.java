package org.pasut.android.findme.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * Created by boot on 3/24/16.
 */
public class UserProfile implements Parcelable {
    private UserState state;

    public UserProfile(){
        this(UserState.PENDING);
    }

    public UserProfile(final UserState state) {
        this.state = state;
    }

    public UserState getState() {
        return state;
    }

    public void activate() {
        this.state = UserState.ACTIVE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected UserProfile(Parcel in) {
        this.state = UserState.valueOf(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.state.toString());
    }

    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel source) {
            return new UserProfile(source);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(UserProfile.class)
                .add("state", state).toString();
    }
}
