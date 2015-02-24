package org.coursera.mutibo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.coursera.mutibo.game.GameControl;
import org.coursera.mutibo.game.GameControlMulti;
import org.coursera.mutibo.game.GameFactory;


public class GameAwaitingOpponentActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_awaiting_opponent);

        lblOpponent  = (TextView) findViewById(R.id.lblOpponent);
        pnlCountdown = (LinearLayout) findViewById(R.id.pnlCountdown);
        lblCountdown = (TextView) findViewById(R.id.lblCountdown);
        waitProgress = (ProgressBar) findViewById(R.id.waitProgress);

        pnlCountdown.setVisibility(View.INVISIBLE);

        // register for state changes of the game
        mGameControl.registerStateCallback(new ResultReceiver(null) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData)
            {
                if (resultCode == GameControl.GAME_STATE_STARTED && mGameControl instanceof GameControlMulti) {
                    String opponentName = ((GameControlMulti) mGameControl).getOpponentName();
                    lblOpponent.setText(String.format(getString(R.string.game_await_opponent), opponentName));

                    startGameCountdown();
                }
                else if (resultCode == GameControl.GAME_EVENT_QUESTION_COUNTDOWN && resultData != null) {
                   lblCountdown.setText(Long.toString(resultData.getLong("COUNTDOWN", 0)));
                }
                else if (resultCode == GameControl.GAME_STATE_QUESTION) {
                    Intent f_intent = new Intent(GameAwaitingOpponentActivity.this, GameActivity.class);
                    startActivity(f_intent);
                }
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // Bind to SyncService
        syncServiceClient.bind();

    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // Unbind from the service
        syncServiceClient.unbind();
    }

    private void startGameCountdown()
    {
        // UI
        waitProgress.setVisibility(View.INVISIBLE);
        pnlCountdown.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.game_await_cancel)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
/*                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                syncServiceClient.getSyncService().multiplayerGameCancel(((GameControlMulti) mGameControl).getMatchId());
                            }
                        }).start();*/
                        mGameControl.cancelGame();

                        GameAwaitingOpponentActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    //

    private GameControl     mGameControl    = GameFactory.getInstance().currentGame();

    private TextView        lblOpponent;
    private LinearLayout    pnlCountdown;
    private TextView        lblCountdown;
    private ProgressBar     waitProgress;

    private SyncServiceClient   syncServiceClient = new SyncServiceClient(this);
}
