package org.pasut.android.findme.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;
import com.google.inject.Inject;

import org.pasut.android.findme.R;
import org.pasut.android.findme.service.PreferencesService;
import org.pasut.android.findme.service.Services;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static org.pasut.android.findme.FindmeApplication.MASTER_USER_KEY;
import static org.pasut.android.findme.activities.ActivityUtils.getGoogleClient;

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RoboActivity implements GoogleApiClient.ConnectionCallbacks,
GoogleApiClient.OnConnectionFailedListener, ResultCallback {
    public final static int RESPONSE = 1;
    private final static String TAG = SplashActivity.class.getSimpleName();

    @InjectView(R.id.splash)
    private ImageView splash;

    @Inject
    private PreferencesService preferences;

    public static final String LOGIN = "login";
    public static final String SIGN_UP = "sign up";

    private GoogleApiClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splash.setBackgroundResource(R.drawable.splash_animation);
        googleClient = getGoogleClient(this, this, this);
        AnimationDrawable anim = (AnimationDrawable) splash.getBackground();
        anim.start();

        if(!preferences.contain(MASTER_USER_KEY)) {
            Log.d(TAG, "Todavia no se registro ningun usuario");
            startActivityForResult(new Intent(this, LoginActivity.class), RESPONSE);
        } else {
            Log.d(TAG, "Ya existe un usuario registrado");
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "Registro ok");
            googleClient.connect();
        } else {
            Log.d(TAG, "Registro fail");
            Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed: " + connectionResult);
        startActivityForResult(new Intent(this, LoginActivity.class), RESPONSE);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected");
        //TODO
        //saveUserAccount();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Suspended?");
    }

    private void saveUserAccount() {
        if (!preferences.contain(MASTER_USER_KEY)) {
            String account = Plus.AccountApi.getAccountName(googleClient);
            Log.d(TAG, "Save google account: " + account);
            persistUserOnPreferences(account);
            //TODO
            //services.signUp(account);
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Nope", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void persistUserOnPreferences(final String user) {
        preferences.put(MASTER_USER_KEY, user);
    }

    @Override
    public void onResult(Result result) {
        Log.d(TAG, result.toString());
    }
}
