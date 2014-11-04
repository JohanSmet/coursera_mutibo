package org.coursera.mutibo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import org.coursera.mutibo.data.DataStore;
import org.coursera.mutibo.data.MutiboDeck;
import org.coursera.mutibo.data.MutiboSync;

import java.util.Collection;

import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Query;

public class SyncService extends Service
{
    public interface RestClient
    {
        @GET("/deck/list-released")
        public Collection<MutiboDeck> lisReleased();

        @GET("/sync")
        public MutiboSync SyncData(@Query("id") Long deckId, @Query("hash") String hash);
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

    void sync_data()
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
                    MutiboSync f_sync = restClient.SyncData(f_deck.getDeckId(), f_deck.getContentHash());

                    f_store.addDeck(f_sync.getMutiboDeck());
                    f_store.addMovies(f_sync.getMutiboMovies());
                    f_store.addSets(f_sync.getMutiboSets());
                }
            }
        };

        Thread thread = new Thread(downloadRunnable);
        thread.start();
    }

    // member variables
    private IBinder     binder = new SyncBinder();
    private RestClient  restClient;

    private String      mutiboServer = "http://10.0.2.2:8080";     // XXX configurable

}
