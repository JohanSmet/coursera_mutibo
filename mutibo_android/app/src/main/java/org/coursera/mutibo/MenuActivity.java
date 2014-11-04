package org.coursera.mutibo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.coursera.mutibo.game.GameFactory;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Bind to SyncService
        Intent intent = new Intent(this, SyncService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unbind from the service
        if (mSyncBound)
        {
            unbindService(mConnection);
            mSyncBound = false;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // button events
    //

    public void btnSinglePlayer_clicked(View p_view)
    {
        // create a new single player game
        GameFactory.getInstance().newGame(GameFactory.GAME_TYPE_SINGLEPLAYER);

        Intent f_intent = new Intent(this, GameActivity.class);
        startActivity(f_intent);
    }

    public void btnLogout_clicked(View p_view)
    {
        Intent f_intent = new Intent(this, LoginActivity.class);
        startActivity(f_intent);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    //

    SyncService mSyncService;
    boolean     mSyncBound = false;

    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // we've bound to the sync service - store its information
            SyncService.SyncBinder binder = (SyncService.SyncBinder) service;
            mSyncService = binder.getService();
            mSyncBound = true;

            // download server data
            mSyncService.sync_data();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mSyncBound = false;
        }
    };

}