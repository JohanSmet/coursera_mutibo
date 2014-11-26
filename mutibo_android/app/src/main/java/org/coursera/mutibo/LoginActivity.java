package org.coursera.mutibo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
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
import java.util.concurrent.ThreadPoolExecutor;

public class LoginActivity extends Activity
{
    private Bundle savedState;

    public static enum LoginAction
    {
        LOGIN,
        LOGOUT,
        REVOKE
    }

    private static enum Authenticator
    {
        NONE,
        GOOGLE_PLUS,
        FACEBOOK;

        public static Authenticator fromString (String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                return NONE;
            }
        }
    }

    public final static String PARAMETER_LOGIN_ACTION = "action";

    private final static String STATE_AUTO_LOGIN = "autoLogin";
    private final static String STATE_LAST_AUTHENTICATOR = "lastAuthenticator";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // restore state
        if (savedInstanceState != null)
        {
            mAutoLogin          = savedInstanceState.getBoolean(STATE_AUTO_LOGIN);
            mLastAuthenticator  = (Authenticator) savedInstanceState.getSerializable(STATE_LAST_AUTHENTICATOR);
        }
        else
        {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            mAutoLogin          = sharedPref.getBoolean(STATE_AUTO_LOGIN, false);
            mLastAuthenticator  = Authenticator.fromString(sharedPref.getString(STATE_LAST_AUTHENTICATOR, Authenticator.NONE.toString()));
        }

        // check for parameters
        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
        {
            mLoginAction = (LoginAction) bundle.getSerializable(PARAMETER_LOGIN_ACTION);
        }
        else
        {
            mLoginAction = LoginAction.LOGIN;
        }

        // UI
        CheckBox autoLogin = (CheckBox) findViewById(R.id.cbAutoLogin);
        autoLogin.setChecked(mAutoLogin);
        autoLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mAutoLogin = ((CheckBox) view).isChecked();
            }
        });

        // facebook
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        // Google+
        googlePlusClient = new GooglePlusClient();
        googlePlusClient.setupSignInButton((SignInButton) findViewById(R.id.sign_in_button));
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        syncServiceClient.bind();

        if (mAutoLogin && mLastAuthenticator == Authenticator.GOOGLE_PLUS && googlePlusClient != null)
            googlePlusClient.connect();

        if (mLastAuthenticator == Authenticator.FACEBOOK && mLoginAction == LoginAction.LOGOUT)
        {
            Session.getActiveSession().close();
            mLoginAction = LoginAction.LOGIN;
        }

        if (mLastAuthenticator == Authenticator.FACEBOOK && mLoginAction == LoginAction.REVOKE)
        {
            new FacebookRevokeTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        syncServiceClient.unbind();

        if (googlePlusClient != null)
            googlePlusClient.disconnect();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        // our state
        savedState.putBoolean(STATE_AUTO_LOGIN, mAutoLogin);
        savedState.putSerializable(STATE_LAST_AUTHENTICATOR, mLastAuthenticator);
        storeSettings();

        // facebook
        uiHelper.onSaveInstanceState(savedState);

        // save the view hierarchy state
        super.onSaveInstanceState(savedState);
    }

    private void loginSucceeded(SyncService.LoginStatus status)
    {
        findViewById(R.id.loginProgress).setVisibility(View.INVISIBLE);

        switch (status) {
            case LOGIN_NEW_USER:
                startActivity(new Intent(LoginActivity.this, NewPlayerActivity.class));
                break;

            case LOGIN_KNOWN_USER:
                finish();
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                break;

            default:
                ((TextView) findViewById(R.id.lblLoginFailed)).setText(R.string.login_failed);
                break;
        }
    }

    private void storeSettings()
    {
        // persistence
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEdit   = sharedPref.edit();

        prefEdit.putBoolean(STATE_AUTO_LOGIN, mAutoLogin);
        prefEdit.putString(STATE_LAST_AUTHENTICATOR, mLastAuthenticator.toString());
        prefEdit.commit();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    protected void onActivityResult(int requestCode, int responseCode, Intent intent)
    {
        Boolean handled = false;

        if (!handled && googlePlusClient != null)
        {
            handled = googlePlusClient.onActivityResult(requestCode, responseCode, intent);
        }

        if (!handled && uiHelper != null)
        {
            uiHelper.onActivityResult(requestCode, responseCode, intent);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // facebook login
    //

    private Session.StatusCallback statusCallback = new Session.StatusCallback()
    {
        @Override
        public void call(final Session session, SessionState state, Exception exception)
        {
            if (state.isOpened())
            {
                Log.d("LoginActivity", "Facebook session opened");
                findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
                new FacebookAuthTask(session).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else if (state.isClosed())
            {
                Log.d("LoginActivity", "Facebook session closed");
            }
            else if (state == SessionState.OPENING)
            {
                findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);
            }
        }
    };

    private class FacebookAuthTask extends AsyncTask<Integer, Integer, SyncService.LoginStatus>
    {
        public FacebookAuthTask(Session session)
        {
            mSession = session;
        }

        protected SyncService.LoginStatus doInBackground(Integer... params)
        {
            String accessToken = mSession.getAccessToken();

            Request request = Request.newMeRequest(mSession, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response)
                {
                    if (mSession == Session.getActiveSession() && user != null)
                    {
                        mUserId   = user.getId();
                        mUserName = user.getFirstName();
                    }
                }
            });

            Request.executeAndWait(request);

            // send a login request
            return syncServiceClient.getSyncService().loginFacebook(accessToken, mUserId, mUserName);
        }

        protected void onPostExecute(SyncService.LoginStatus result)
        {
            mLastAuthenticator = Authenticator.FACEBOOK;
            loginSucceeded(result);
        }

        Session mSession;
        String  mUserId      = "";
        String  mUserName    = "";
    }

    private class FacebookRevokeTask extends AsyncTask<Integer, Integer, LoginAction>
    {
        protected LoginAction doInBackground(Integer... params)
        {
            new Request(Session.getActiveSession(),
                    "/me/permissions",
                    null,
                    HttpMethod.DELETE,
                    new Request.Callback()
                    {
                        public void onCompleted(Response response)
                        {
                            Session.getActiveSession().close();
                            syncServiceClient.getSyncService().deleteCurrentUser();
                        }
                    }
            ).executeAndWait();

            return LoginAction.LOGIN;
        }

        protected void onPostExecute(LoginAction result)
        {
            mLoginAction = result;
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
                findViewById(R.id.loginProgress).setVisibility(View.VISIBLE);

                mLoginAction   = LoginAction.LOGIN;
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
            switch (mLoginAction) {
                case LOGIN :
                    new GoogleAuthTask(LoginActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;

                case LOGOUT :
                    if (mLastAuthenticator == Authenticator.GOOGLE_PLUS &&  mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    {
                        mSignInClicked = false;
                        mLoginAction = LoginAction.LOGIN;

                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                        mGoogleApiClient.connect();
                    }
                    break;

                case REVOKE :
                    if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                    {
                        mSignInClicked = false;
                        mLoginAction = LoginAction.LOGIN;

                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                                .setResultCallback(new ResultCallback<Status>()
                                {
                                    @Override
                                    public void onResult(Status status)
                                    {
                                        syncServiceClient.getSyncService().deleteCurrentUser();
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
            mLastAuthenticator = Authenticator.GOOGLE_PLUS;

            loginSucceeded(result);
        }

        private final Activity mActivity;
    }

    private SyncServiceClient   syncServiceClient = new SyncServiceClient(this);

    private LoginAction         mLoginAction;
    private Authenticator       mLastAuthenticator;
    private boolean             mAutoLogin;

    // google
    private GooglePlusClient    googlePlusClient;

    // facebook
    private UiLifecycleHelper uiHelper;

}
