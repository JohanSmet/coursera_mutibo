package org.coursera.mutibo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.coursera.mutibo.game.GameFactory;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ((TextView) findViewById(R.id.txtWelcome)).setText(String.format(getString(R.string.menu_welcome), GlobalState.getNickName()));
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // start the SyncService before binding to it so it keeps running for the lifetime of the app
        Intent intent = new Intent(this, SyncService.class);
        startService(intent);

        // bind to SyncService
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop()
    {
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

    public void btnLeaderboards_clicked(View p_view)
    {
        Intent f_intent = new Intent(this, LeaderboardActivity.class);
        startActivity(f_intent);
    }

    @Override
    public void onBackPressed()
    {
        Intent f_intent = new Intent(this, LoginActivity.class);
        f_intent.putExtra(LoginActivity.PARAMETER_LOGIN_ACTION, LoginActivity.LoginAction.LOGOUT);

        finish();
        startActivity(f_intent);
    }

    public void btnAbout_clicked(View p_view)
    {
        Intent f_intent = new Intent(this, LoginActivity.class);
        f_intent.putExtra(LoginActivity.PARAMETER_LOGIN_ACTION, LoginActivity.LoginAction.REVOKE_ACCESS_GOOGLE);
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
            mSyncService.downloadDataAsync();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mSyncBound = false;
        }
    };

}
