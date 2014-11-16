package org.coursera.mutibo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.coursera.mutibo.data.DataStore;
import org.coursera.mutibo.data.MutiboDeck;
import org.coursera.mutibo.data.MutiboGameResult;
import org.coursera.mutibo.data.MutiboMovie;
import org.coursera.mutibo.data.MutiboSync;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.*;
import retrofit.converter.GsonConverter;
import retrofit.http.*;

public class SyncService extends Service
{
    public enum LoginStatus {
        LOGIN_FAILED,
        LOGIN_KNOWN_USER,
        LOGIN_NEW_USER
    }

    private static final String LOG_TAG = "SyncService";

    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String CACHE_CONTROL_SERVER         = "max-age=0";
    public static final String CACHE_CONTROL_PREFER_CACHE   = "max-age=315360000";
    public static final String CACHE_CONTROL_ONLY_CACHE     = "only-if-cached";

    private interface RestClient
    {
        @GET("/deck/list-released")
        public Collection<MutiboDeck> lisReleased(@retrofit.http.Header(HEADER_CACHE_CONTROL) String cacheControlValue);

        @GET("/sync")
        public MutiboSync syncData(@Query("id") Long deckId, @Query("hash") String hash, @retrofit.http.Header(HEADER_CACHE_CONTROL) String cacheControlValue);

        @GET("/movie/poster")
        public Response getMoviePoster(@Query("id") String imdbId, @Query("resolution") String resolution, @retrofit.http.Header(HEADER_CACHE_CONTROL) String cacheControlValue);

        @POST("/login/login-google")
        public Response loginGoogle(@Query("googleToken") String googleToken, @Query("username") String name);

        @POST("/game/results")
        public Response postGameResult(@Body MutiboGameResult gameResult);
    }

    public class SyncBinder extends Binder
    {
        SyncService getService()
        {
            return SyncService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return this.binder;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        if (restClient == null)
        {
            Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            .create()
                        ;

            restClient = new RestAdapter.Builder()
                                .setEndpoint(this.serverBaseUrl)
                                .setConverter(new GsonConverter(gson))
                                .setClient(new OkClient(OkHttpBuilder.getSelfSignedOkHttpClient(this, serverHost)))
                                .setRequestInterceptor(new RequestInterceptor()
                                {
                                    @Override
                                    public void intercept(RequestFacade request)
                                    {
                                        if (GlobalState.getAuthToken() != null)
                                        {
                                            request.addHeader("X-Auth-Token", GlobalState.getAuthToken());
                                        }
                                    }
                                })
                                .setLogLevel(RestAdapter.LogLevel.FULL)
                                .build()
                                    .create(RestClient.class)
                            ;

            gameResultThread = new GameResultPostThread();
            gameResultThread.start();
        }
    }

    @Override
    public void onDestroy()
    {
        Log.i(LOG_TAG, "onDestroy called");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.i(LOG_TAG, "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    void downloadDataAsync()
    {
        Runnable downloadRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                // load all available data from the local cache before going over the network
                Collection<MutiboDeck> decks = listReleasedDecks(CACHE_CONTROL_ONLY_CACHE);

                if (decks != null)
                {
                    downloadAllData(decks, CACHE_CONTROL_ONLY_CACHE);
                }

                // check for updated data - force refresh of deck-list, the rest of the data can
                // come from the cache if still up-to-date (hash matches)
                decks = listReleasedDecks(CACHE_CONTROL_SERVER);

                if (decks != null)
                {
                    downloadAllData(decks, CACHE_CONTROL_PREFER_CACHE);
                }
            }
        };

        Thread thread = new Thread(downloadRunnable);
        thread.start();
    }

    Bitmap downloadPosterBitmap(String imdbId)
    {
        Response response = retrieveMoviePoster(imdbId, "low", CACHE_CONTROL_PREFER_CACHE);

        if (response == null || response.getStatus() != 200)
            return null;

        try {
            return BitmapFactory.decodeStream(response.getBody().in());
        } catch (IOException e) {
            return null;
        }
    }

    LoginStatus loginGoogle(String googleToken, String name)
    {
        try {
            Response response = restClient.loginGoogle(googleToken, name);

            if (response.getStatus() != 200)
                return LoginStatus.LOGIN_FAILED;

            // find the X-Auth-Token header
            for (retrofit.client.Header header : response.getHeaders())
            {
                if (header.getName() != null && header.getName().equalsIgnoreCase("X-Auth-Token"))
                {
                    GlobalState.setAuthToken(header.getValue());
                    break;
                }
            }

            // check the body of the request
            if (response.getBody().toString().equals("NEW"))
                return LoginStatus.LOGIN_NEW_USER;
            else
                return LoginStatus.LOGIN_KNOWN_USER;

        } catch (RetrofitError e) {
            Log.d(LOG_TAG, "loginGoogle", e);
        }

        return LoginStatus.LOGIN_FAILED;
    }

    void postGameResult(MutiboGameResult gameResult)
    {
        // store the game result in a queue to be sent to the server when we're online and logged in
        gameResultQueue.add(gameResult);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // helper functions
    //

    private void downloadAllData(Collection<MutiboDeck> decks, String cacheControl)
    {
        DataStore f_store = DataStore.getInstance();

        // retrieve data for each deck
        for (MutiboDeck deck : decks)
        {
            MutiboSync f_sync = syncData(deck, cacheControl);

            if (f_sync != null)
            {
                f_store.addDeck(f_sync.getMutiboDeck());
                f_store.addMovies(f_sync.getMutiboMovies());
                f_store.addSets(f_sync.getMutiboSets());

                // retrieve posters for all movies
                for (MutiboMovie movie : f_sync.getMutiboMovies())
                {
                    retrieveMoviePoster(movie.getImdbId(), "low", cacheControl);
                }
            }
        }
    }

    private Collection<MutiboDeck> listReleasedDecks(String cacheControl)
    {
        try {
            return restClient.lisReleased(cacheControl);
        } catch (RetrofitError e) {
            Log.d(LOG_TAG, "listReleasedDecks", e);
            return null;
        }
    }

    private MutiboSync syncData(MutiboDeck deck, String cacheControl)
    {
        try {
            return restClient.syncData(deck.getDeckId(), deck.getContentHash(), cacheControl);
        } catch (RetrofitError e) {
            Log.d(LOG_TAG, "syncData", e);
            return null;
        }
    }

    private Response retrieveMoviePoster(String imdbId, String resolution, String cacheControl)
    {
        try {
            return restClient.getMoviePoster(imdbId, resolution, cacheControl);
        } catch (RetrofitError e) {
            Log.d(LOG_TAG, "syncData", e);
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // nested class
    //

    private class GameResultPostThread extends Thread
    {
        @Override
        public void run()
        {
            try {

                while (!isInterrupted())
                {
                    // wait for a new result to come in
                    MutiboGameResult gameResult = gameResultQueue.take();

                    // a null gameResult is a request to stop the thread's execution
                    if (gameResult == null) {
                        return;
                    }

                    // check if we're able to reach the internet
                    Boolean online = NetworkStatus.internetAvailable(SyncService.this);

                    if (GlobalState.getAuthToken() == null)
                        online = false;

                    // post the result to the server
                    try {
                        if (online)
                        {
                            restClient.postGameResult(gameResult);
                        }
                    } catch (RetrofitError e) {
                        Log.d(LOG_TAG, "GameResultPostThread", e);
                        online = false;
                    }

                    // if not online, sleep for a while and try again later
                    if (!online)
                    {
                        sleep(5 * 60 * 1000);
                    }
                }
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    //

    private IBinder     binder = new SyncBinder();
    private RestClient  restClient;

    private String      serverHost    = "ivy"; // "10.0.2.2";
    private String      serverBaseUrl = "https://" + serverHost + ":8443";

    private LinkedBlockingQueue<MutiboGameResult>   gameResultQueue = new LinkedBlockingQueue<MutiboGameResult>();
    private GameResultPostThread                    gameResultThread;
}
