package org.coursera.mutibo.game;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.coursera.mutibo.SyncServiceClient;
import org.coursera.mutibo.data.DataStore;
import org.coursera.mutibo.data.MutiboGameResult;
import org.coursera.mutibo.data.MutiboMovie;
import org.coursera.mutibo.data.MutiboSet;
import org.coursera.mutibo.data.MutiboSetResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

public class GameControlMulti implements GameControl
{
    private static final String LOG_TAG = "GameControlMulti";

    public GameControlMulti(Context context)
    {
        this.mScore      = 0;
        this.mNumCorrect = 0;
        this.mState      = GAME_STATE_FINISHED;
        this.mSetMovies  = new ArrayList<MutiboMovie>();
        this.mSuccess    = GameControl.SetSuccess.UNKNOWN;
        this.mGameResult = new MutiboGameResult();
        this.mContext    = context;

        this.syncServiceClient = new SyncServiceClient(mContext);
        this.syncServiceClient.bind();
    }

    @Override
    public void startGame()
    {
        this.mScore      = 0;
        this.mNumCorrect = 0;
        this.mLives      = 3;
        this.mState      = GAME_STATE_STARTED;

        registerForPlayServices();
        startGcmReceiver();
    }

    @Override
    public void endGame()
    {
        stopGcmReceiver();
    }

    @Override
    public boolean answerSet(int index)
    {
        // check for success
        boolean correctGuess = false;

        for (String f_bad :  mCurrentSet.getBadMovies())
        {
            correctGuess = correctGuess || (f_bad.equals(mSetMovies.get(index).getImdbId()));
        }

        this.mSuccess = (correctGuess) ? GameControl.SetSuccess.SUCCESS : GameControl.SetSuccess.FAILURE;


        // consequences of a guess
        if (this.mSuccess != GameControl.SetSuccess.SUCCESS)
        {
            --this.mLives;
        }
        else
        {
            ++this.mNumCorrect;
            this.mScore +=  this.mCurrentSet.getPoints();
        }


        // change state of the game
        updateGameState();

        return this.mSuccess == GameControl.SetSuccess.SUCCESS;
    }

    @Override
    public void timeoutSet()
    {
        --this.mLives;
        this.mSuccess = GameControl.SetSuccess.TIMEOUT;

        updateGameState();
    }

    @Override
    public void continueGame(int rating)
    {
        // initialize the SetResult object to report back to the server
        MutiboSetResult setResult = new MutiboSetResult(mCurrentSet.getSetId());
        setResult.setRating(rating);

        if (mSuccess == GameControl.SetSuccess.SUCCESS)
            setResult.setScore(this.mCurrentSet.getPoints());
    }

    @Override
    public int currentGameState()
    {
        return mState;
    }

    @Override
    public int  totalScore()
    {
        return this.mScore;
    }

    @Override
    public int  numCorrectQuestions()
    {
        return this.mNumCorrect;
    }

    @Override
    public int remainingLives()
    {
        return mLives;
    }

    @Override
    public MutiboGameResult gameResult()
    {
        return mGameResult;
    }

    @Override
    public int currentSetDifficulty()
    {
        if (mCurrentSet != null)
            return mCurrentSet.getDifficulty();
        else
            return 0;
    }

    @Override
    public int currentSetPoints()
    {
        if (mCurrentSet != null)
            return mCurrentSet.getPoints();
        else
            return 0;
    }

    @Override
    public int currentSetCorrectAnswer()
    {
        return mBadMovieIndex;
    }

    @Override
    public MutiboMovie currentSetMovie(int index)
    {
        if (index >= 0 && index < mSetMovies.size())
            return mSetMovies.get(index);
        else
            return null;
    }

    @Override
    public String currentSetReason()
    {
        if (mCurrentSet != null)
            return mCurrentSet.getReason();
        else
            return null;
    }

    @Override
    public GameControl.SetSuccess currentSetSuccess()
    {
        return this.mSuccess;
    }

    private void updateGameState()
    {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Google Cloud Messaging
    //

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public  static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String SENDER_ID = "894408186618";

    BroadcastReceiver gcmBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Bundle extras = intent.getExtras();

            if (!extras.isEmpty())
            {
                String messageType = mGcm.getMessageType(intent);

                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    Log.i(LOG_TAG, extras.toString());
                }
            }
        }
    };

    private void startGcmReceiver()
    {
        // register the broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.google.android.c2dm.intent.RECEIVE");
        filter.addCategory("org.coursera.mutibo");

        mContext.registerReceiver(gcmBroadcastReceiver, filter);
    }

    private void stopGcmReceiver()
    {
        // unregister the broadcast receiver
        mContext.unregisterReceiver(gcmBroadcastReceiver);
    }

    private boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);

        if (resultCode != ConnectionResult.SUCCESS)
        {
            return false;
        }

        return true;
    }

    private boolean registerForPlayServices()
    {
        if (!checkPlayServices())
            return false;

        mGcm        = GoogleCloudMessaging.getInstance(mContext);
        mGcmRegId   = getRegistrationId(mContext);
        registerInBackground();

        return true;
    }

    private void registerInBackground()
    {
        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                String msg = "";

                // register for Google Cloud Message if it wasn't done before
                //if (mGcmRegId.isEmpty())
                {
                    try {
                        if (mGcm == null) {
                            mGcm = GoogleCloudMessaging.getInstance(mContext);
                        }

                        mGcmRegId = mGcm.register(SENDER_ID);
                        msg = "Device registered, registration ID=" + mGcmRegId;

                        // Persist the regID - no need to register again.
                        storeRegistrationId(mContext, mGcmRegId);
                    } catch (IOException ex) {
                        msg = "Error :" + ex.getMessage();
                    }
                }

                // start the multiplayer match
                syncServiceClient.getSyncService().multiplayerChallengeRandom(mGcmRegId);

                return msg;
            }

            @Override
            protected void onPostExecute(String msg)
            {
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);
    }

    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGCMPreferences(mContext);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.isEmpty())
        {
            Log.i(LOG_TAG, "Registration not found.");
            return "";
        }

        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.i(LOG_TAG, "App version changed.");
            return "";
        }

        return registrationId;
    }

    private void storeRegistrationId(Context context, String regId)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(LOG_TAG, "Saving regId on app version " + appVersion);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context)
    {
        return mContext.getSharedPreferences(GameControlMulti.class.getSimpleName(), Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context)
    {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    private int                     mScore;
    private int                     mNumCorrect;
    private int                     mLives;
    private int                     mState;

    private MutiboSet               mCurrentSet;
    private ArrayList<MutiboMovie>  mSetMovies;
    private GameControl.SetSuccess  mSuccess;
    private int                     mBadMovieIndex;

    private MutiboGameResult        mGameResult;

    private DataStore mDataStore = DataStore.getInstance();
    private Context   mContext;

    private GoogleCloudMessaging    mGcm;
    private String                  mGcmRegId;

    private SyncServiceClient       syncServiceClient;
}
