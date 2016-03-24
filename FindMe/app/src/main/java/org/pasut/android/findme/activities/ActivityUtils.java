package org.pasut.android.findme.activities;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * Created by boot on 9/30/15.
 */
public class ActivityUtils {
    public static GoogleApiClient getGoogleClient(final Context context,
                                                  final GoogleApiClient.ConnectionCallbacks connectionCallback,
                                                  final GoogleApiClient.OnConnectionFailedListener failedCallback) {

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .addScope(Plus.SCOPE_PLUS_LOGIN);
        if (connectionCallback != null) {
            builder.addConnectionCallbacks(connectionCallback);
        }
        if (failedCallback != null) {
            builder.addOnConnectionFailedListener(failedCallback);
        }
        return builder.build();
    }

    public static GoogleApiClient getCoogleClient(final Context context,
                                                   final GoogleApiClient.ConnectionCallbacks connectionCallback) {
        return getGoogleClient(context, connectionCallback, null);
    }

    public static GoogleApiClient getCoogleClient(final Context context) {
        return getGoogleClient(context, null, null);
    }
}
