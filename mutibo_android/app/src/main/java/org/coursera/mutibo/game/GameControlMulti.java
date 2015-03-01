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
import android.os.CountDownTimer;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.coursera.mutibo.GlobalState;
import org.coursera.mutibo.SyncServiceClient;
import org.coursera.mutibo.data.DataStore;
import org.coursera.mutibo.data.MultiplayerMatch;
import org.coursera.mutibo.data.MutiboGameResult;
import org.coursera.mutibo.data.MutiboMovie;
import org.coursera.mutibo.data.MutiboSet;
import org.coursera.mutibo.data.MutiboSetResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class GameControlMulti extends GameControlCommon
{
    private static final String LOG_TAG = "GameControlMulti";

    public GameControlMulti(Context context)
    {
        // parent constructor (2 players)
        super(2);

        this.mScore      = 0;
        this.mNumCorrect = 0;
        this.mSetMovies  = new ArrayList<MutiboMovie>();
        this.mSuccess    = GameControl.SetSuccess.UNKNOWN;
        this.mGameResult = new MutiboGameResult();
        this.mPlayerId   = 1;
        this.mContext    = context;

        this.stateCallback     = null;

        this.syncServiceClient = new SyncServiceClient(mContext);
        this.syncServiceClient.bind();
    }

    @Override
    public boolean  isMultiPlayer()
    {
        return true;
    }

    @Override
    public void startGame()
    {
        this.mScore      = 0;
        this.mNumCorrect = 0;
        this.mLives      = 3;
        changeGameState(GAME_STATE_AWAITING_OPPONENT, null);

        // initialize the GameResult object that will eventually be sent to the server
        mGameResult.setStartTime(new Date());
        mGameResult.setEndTime(null);
        mGameResult.clearSetResults();

        registerForPlayServices();
        startGcmReceiver();
    }

    @Override
    public void endGame()
    {
        // post the final result to the server
        mGameResult.setEndTime(new Date());
        syncServiceClient.getSyncService().postGameResult(mGameResult);

        // cleanup
        stopGcmReceiver();
        syncServiceClient.unbind();
    }

    @Override
    public void cancelGame()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
            syncServiceClient.getSyncService().multiplayerGameCancel(getMatchId());
            syncServiceClient.unbind();
            }
        }).start();

        // cleanup
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

        mGameResult.addSetResult(setResult);

        // wait for other player to finish this set
        changeGameState(GameControl.GAME_STATE_AWAITING_OPPONENT);

        // ask for next set to play from server (in the background)
        new AsyncTask<Long, Long, Long>() {
            protected Long doInBackground(Long... params) {
                return syncServiceClient.getSyncService().multiplayerGameUpdate(mCurrentMatch.getMatchId(), params[0], params[1].intValue());
            }
            protected void onPostExecute(Long result) {
                mNextSet = result;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, setResult.getSetId(), Long.valueOf((long) setResult.getScore()));
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

    public String getOpponentName()
    {
        return mPlayerScore[mOpponentId].mPlayerName;
    }

    public String getMatchId()
    {
        return mCurrentMatch.getMatchId();
    }

    private void updateGameState()
    {
        Bundle extra = new Bundle();
        changeGameState(GAME_STATE_ANSWERED);
    }

    private void setNextSet(Long id)
    {
        mCurrentSet = mDataStore.getSetById(id);

        // put the movies of the set in a random order
        mSetMovies.clear();

        ArrayList<String> f_movies = new ArrayList<String>();
        Collections.addAll(f_movies, mCurrentSet.getGoodMovies());
        Collections.addAll(f_movies, mCurrentSet.getBadMovies());
        Collections.shuffle(f_movies);

        for (String imdbId : f_movies)
        {
            if (mCurrentSet.getBadMovies()[0].equals(imdbId))
                mBadMovieIndex = mSetMovies.size();

            mSetMovies.add(mDataStore.getMovieById(imdbId));
        }
    }

    private void startQuestionCountdown()
    {
        // countdown
        new CountDownTimer(6000, 1000)
        {
            @Override
            public void onTick(long l)
            {
                if (stateCallback != null) {
                    Bundle data = new Bundle();
                    data.putLong("COUNTDOWN", l / 1000);
                    sendEvent(GAME_EVENT_QUESTION_COUNTDOWN, data);
                }
            }

            @Override
            public void onFinish()
            {
                setNextSet(mNextSet);
                changeGameState(GAME_STATE_QUESTION);
            }
        }.start();
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
            // do not start processing message before match initialization is complete
            try {
                mInitLatch.await();
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "mInitLatch.await interrupted", e);
            }

            Bundle extras = intent.getExtras();

            if (!extras.isEmpty()) {
                String messageType = mGcm.getMessageType(intent);

                if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                    handle_message(extras);
                }
            }
        }

        private void handle_message(Bundle extras)
        {
            Log.i(LOG_TAG, extras.toString());

            // TODO: ignore messages that aren't for the current match

            String  msgType = extras.getString("type", "UNKNOWN");

            if (msgType.equals("OPPONENT_READY")) {
                mPlayerScore[0].mPlayerName = extras.getString("player_one");
                mPlayerScore[1].mPlayerName = extras.getString("player_two");
                changeGameState(GAME_STATE_STARTED, extras);
                startQuestionCountdown();
            } else if (msgType.equals("OPPONENT_SCORE")) {
                updatePlayerScore(  extras.getString("player",""),
                                    Integer.parseInt(extras.getString("score", "0")),
                                    Integer.parseInt(extras.getString("lives", "0")),
                                    Integer.parseInt(extras.getString("correct", "0")));
                sendEvent(GAME_EVENT_SCORE_UPDATE, extras);
            } else if (msgType.equals("CONTINUE_GAME")) {
                resetPlayerScores();
                startQuestionCountdown();
            } else if (msgType.equals("END_GAME")) {
                endGame();
                changeGameState(GAME_STATE_FINISHED, extras);
            } else if (msgType.equals("OPPONENT_QUIT")) {
                changeGameState(GAME_STATE_CANCELLED, extras);
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
        mInitLatch  = new CountDownLatch(1);

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
                // if (mGcmRegId.isEmpty())
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
                mCurrentMatch = syncServiceClient.getSyncService().multiplayerChallengeRandom(mGcmRegId);

                // process opponent information
                if (mCurrentMatch.isOpponentReady()) {
                    mPlayerId   = 1;
                    mOpponentId = 0;
                } else {
                    mPlayerId   = 0;
                    mOpponentId = 1;
                }

                // player names
                mPlayerScore[mPlayerId].mPlayerName   = GlobalState.getNickName();
                mPlayerScore[mOpponentId].mPlayerName = mCurrentMatch.getOpponentName();

                // get set to play
                mNextSet = mCurrentMatch.getSetId();

                // match initialization complete
                mInitLatch.countDown();

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

    private int                     mPlayerId;
    private int                     mOpponentId;
    private MultiplayerMatch        mCurrentMatch = null;

    private MutiboSet               mCurrentSet;
    private ArrayList<MutiboMovie>  mSetMovies;
    private GameControl.SetSuccess  mSuccess;
    private int                     mBadMovieIndex;
    private Long                    mNextSet;

    private MutiboGameResult        mGameResult;

    private DataStore mDataStore = DataStore.getInstance();
    private Context   mContext;

    private GoogleCloudMessaging    mGcm;
    private String                  mGcmRegId;
    private CountDownLatch          mInitLatch;

    private SyncServiceClient       syncServiceClient;
}
