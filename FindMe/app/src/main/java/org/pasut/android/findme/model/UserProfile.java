package org.pasut.android.findme.model;

import com.google.common.base.MoreObjects;

/**
 * Created by boot on 3/24/16.
 */
public class UserProfile {
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
    public String toString() {
        return MoreObjects.toStringHelper(UserProfile.class)
                .add("state", state).toString();
    }
}
