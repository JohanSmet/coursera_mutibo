package org.coursera.mutibo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

import org.coursera.mutibo.data.DataStore;
import org.coursera.mutibo.data.MutiboDeck;
import org.coursera.mutibo.data.MutiboMovie;
import org.coursera.mutibo.data.MutiboSync;

import java.io.IOException;
import java.util.Collection;

import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public class SyncService extends Service
{
    public interface RestClient
    {
        @GET("/deck/list-released")
        public Collection<MutiboDeck> lisReleased();

        @GET("/sync")
        public MutiboSync syncData(@Query("id") Long deckId, @Query("hash") String hash);

        @GET("/movie/poster")
        public Response getMoviePoster(@Query("id") String imdbId, @Query("resolution") String resolution);
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
            restClient = new RestAdapter.Builder()
                                .setEndpoint(this.mutiboServer)
                                .build()
                                    .create(RestClient.class)
                            ;
        }
    }

    void downloadDataAsync()
    {
        Runnable downloadRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                DataStore f_store = DataStore.getInstance();

                // retrieve a list of released decks
                Collection<MutiboDeck> f_decks = restClient.lisReleased();

                // retrieve data for each deck
                for (MutiboDeck f_deck : f_decks)
                {
                    MutiboSync f_sync = restClient.syncData(f_deck.getDeckId(), f_deck.getContentHash());

                    f_store.addDeck(f_sync.getMutiboDeck());
                    f_store.addMovies(f_sync.getMutiboMovies());
                    f_store.addSets(f_sync.getMutiboSets());

                    // retrieve posters for all movies
                    for (MutiboMovie movie : f_sync.getMutiboMovies())
                    {
                        restClient.getMoviePoster(movie.getImdbId(), "low");
                    }
                }
            }
        };

        Thread thread = new Thread(downloadRunnable);
        thread.start();
    }

    Bitmap downloadPosterBitmap(String imdbId)
    {
        Response response = restClient.getMoviePoster(imdbId, "low");

        if (response.getStatus() != 200)
            return null;

        try {
            return BitmapFactory.decodeStream(response.getBody().in());
        } catch (IOException e) {
            return null;
        }
    }

    // member variables
    private IBinder     binder = new SyncBinder();
    private RestClient  restClient;

    private String      mutiboServer = "http://10.0.2.2:8080";     // XXX configurable

}
