package org.coursera.mutibo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.plus.model.people.Person;

import java.io.IOException;

public class LoginActivity extends Activity
{
    public static enum LoginAction
    {
        LOGIN,
        LOGOUT,
        REVOKE_ACCESS_GOOGLE
    }

    public final static String PARAMETER_LOGIN_ACTION = "action";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // check for parameters
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            loginAction = (LoginAction) bundle.getSerializable(PARAMETER_LOGIN_ACTION);
        }
        else
        {
            loginAction = LoginAction.LOGIN;
        }

        // Google+
        googlePlusClient = new GooglePlusClient();
        googlePlusClient.setupSignInButton((SignInButton) findViewById(R.id.sign_in_button));
    }

    protected void onStart()
    {
        super.onStart();

        syncServiceClient.bind();

        if (googlePlusClient != null)
            googlePlusClient.connect();
    }

    protected void onStop()
    {
        super.onStop();

        syncServiceClient.unbind();

        if (googlePlusClient != null)
            googlePlusClient.disconnect();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    public void btnLogin_clicked(View p_view)
    {
        Intent f_intent = new Intent(this, MenuActivity.class);
        startActivity(f_intent);
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent)
    {
        Boolean handled = false;

        if (googlePlusClient != null)
        {
            handled = googlePlusClient.onActivityResult(requestCode, responseCode, intent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Google SignIn stuff all neatly tucked away in a class
    //

    private class GooglePlusClient implements   View.OnClickListener,
                                                GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
    {
        public GooglePlusClient()
        {
            mGoogleApiClient = new GoogleApiClient.Builder(LoginActivity.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_PROFILE)
                    .build();
        }

        public void setupSignInButton(SignInButton button)
        {
            button.setOnClickListener(this);
            button.setSize(SignInButton.SIZE_WIDE);
        }

        public void connect()
        {
            mGoogleApiClient.connect();
        }

        public void disconnect()
        {
            if (mGoogleApiClient.isConnected())
            {
                mGoogleApiClient.disconnect();
            }
        }

        public String getAccountName()
        {
            return Plus.AccountApi.getAccountName(mGoogleApiClient);
        }

        public String getPersonDisplayName()
        {
            String name = "";
            Person person = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

            if (person != null)
                name = person.getDisplayName();

            return name;
        }

        // View.OnClickListener
        public void onClick(View view)
        {
            if (view.getId() == R.id.sign_in_button && !mGoogleApiClient.isConnecting())
            {
                mSignInClicked = true;
                resolveSignInError();
            }
        }

        // Google+ events
        public void onConnectionFailed(ConnectionResult result)
        {
            if (!mIntentInProgress)
            {
                // Store the ConnectionResult so that we can use it later when the user clicks 'sign-in'.
                mConnectionResult = result;

                if (mSignInClicked)
                {
                    // The user has already clicked 'sign-in' so we attempt to resolve all
                    // errors until the user is signed in, or they cancel.
                    resolveSignInError();
                }
            }
        }

        public void onConnected(Bundle connectionHint)
        {
            switch (loginAction) {
                case LOGIN :
                    new GoogleAuthTask(LoginActivity.this).execute();
                    break;

                case LOGOUT :
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    {
                        mSignInClicked = false;
                        loginAction = LoginAction.LOGIN;

                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient.connect();
                    }
                    break;

                case REVOKE_ACCESS_GOOGLE :
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    {
                        mSignInClicked = false;
                        loginAction = LoginAction.LOGIN;

                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                                .setResultCallback(new ResultCallback<Status>()
                                {
                                    @Override
                                    public void onResult(Status status)
                                    {
                                        // XXX delete user data
                                    }
                                });
                    }
                    break;
            }
        }

        public void onDisconnected()
        {
        }

        public void onConnectionSuspended(int cause)
        {
            mGoogleApiClient.connect();
        }

        public Boolean onActivityResult(int requestCode, int responseCode, Intent intent)
        {
            if (requestCode == RC_SIGN_IN)
            {
                if (responseCode != RESULT_OK)
                {
                    mSignInClicked = false;
                }

                mIntentInProgress = false;

                if (!mGoogleApiClient.isConnecting())
                {
                    mGoogleApiClient.connect();
                }

                return true;
            }

            return false;
        }

        // A helper method to resolve the current ConnectionResult error.
        private void resolveSignInError()
        {
            if (mConnectionResult == null)
            {
                mGoogleApiClient.connect();
                return;
            }

            if (mConnectionResult.hasResolution())
            {
                try {
                    mIntentInProgress = true;
                    startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    // The intent was canceled before it was sent.  Return to the default
                    // state and attempt to connect to get an updated ConnectionResult.
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }

        // request code used to invoke sign in user interactions.
        private static final int RC_SIGN_IN = 0;

        // member variables
        private GoogleApiClient  mGoogleApiClient;  // client to interact with Google APIs.
        private boolean          mIntentInProgress; // to prevent starting further intents when one is already in progress
        private boolean          mSignInClicked;    // did user click the sign-in button already ?
        private ConnectionResult mConnectionResult; // results from onConnectionFailed for later processing
    }

    private class GoogleAuthTask extends AsyncTask<Integer, Integer, SyncService.LoginStatus>
    {
        public GoogleAuthTask(Activity activity)
        {
            this.mActivity = activity;
        }

        protected SyncService.LoginStatus doInBackground(Integer... params)
        {
            // get a Google ID token
            String authToken = "";

            try {
                authToken = GoogleAuthUtil.getToken(
                        mActivity,
                        googlePlusClient.getAccountName(),
                        "audience:server:client_id:***REMOVED***");
            } catch (IOException e) {
                // network error - try again later
            } catch (UserRecoverableAuthException e) {
                // recover
            } catch (GoogleAuthException e) {
                // unrecoverable
            }

            // send a login request
            return syncServiceClient.getSyncService().loginGoogle(authToken, googlePlusClient.getPersonDisplayName());
        }

        protected void onPostExecute(SyncService.LoginStatus result)
        {
            Intent f_intent = new Intent(mActivity, MenuActivity.class);
            startActivity(f_intent);
        }

        private final Activity mActivity;
    }

    private SyncServiceClient   syncServiceClient = new SyncServiceClient(this);
    private GooglePlusClient    googlePlusClient;
    private LoginAction         loginAction;
}
