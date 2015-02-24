package org.coursera.mutibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.coursera.mutibo.game.GameControl;
import org.coursera.mutibo.game.GameFactory;
import org.coursera.mutibo.util.GameStarter;


public class GameOverActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // check if the game was a single-player or multi-player game
        Bundle extras = getIntent().getExtras();

        if (extras != null && extras.getString("type", "?").equals("OPPONENT_QUIT")) {
            ((TextView) findViewById(R.id.txtResults)).setText(R.string.gamedone_cancelled);
            findViewById(R.id.scoreFragment).setVisibility(View.GONE);
        } else if (extras == null || !extras.containsKey("player_two")) {
            ((TextView) findViewById(R.id.txtResults)).setText(String.format(getString(R.string.gamedone_result),
                    mGameControl.numCorrectQuestions(),
                    mGameControl.totalScore()));
            findViewById(R.id.scoreFragment).setVisibility(View.GONE);
        } else {
            findViewById(R.id.txtResults).setVisibility(View.GONE);
            MultiplayerScoreFragment fragment = (MultiplayerScoreFragment) getFragmentManager().findFragmentById(R.id.scoreFragment);

            fragment.initPlayer(0, extras.getString("player_one"));
            fragment.initPlayer(1, extras.getString("player_two"));

            int score1 = Integer.parseInt(extras.getString("score_one", "0"));
            int score2 = Integer.parseInt(extras.getString("score_two", "0"));
            int lives1 = Integer.parseInt(extras.getString("lives_one", "0"));
            int lives2 = Integer.parseInt(extras.getString("lives_two", "0"));

            fragment.setScore(  extras.getString("player_one"), score1, lives1);
            fragment.setScore(  extras.getString("player_two"), score2, lives2);

            if (score1 > score2)
                fragment.setWinner(0);
            else if (score1 < score2)
                fragment.setWinner(1);
            else if (score1 == score2 && lives1 > lives2)
                fragment.setWinner(0);
            else if (score1 == score2 && lives1 < lives2)
                fragment.setWinner(1);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    public void btnAnother_clicked(View p_view)
    {
        if (mGameControl.isMultiPlayer())
            GameStarter.launchGame(GameFactory.GAME_TYPE_MULTIPLAYER, this);
        else
            GameStarter.launchGame(GameFactory.GAME_TYPE_SINGLEPLAYER, this);
    }

    public void btnRanking_clicked(View p_view)
    {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra(LeaderboardActivity.PLAYER_ARG, GlobalState.getNickName());
        startActivity(intent);
    }

    public void btnMenu_clicked(View p_view)
    {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    // member variables
    private GameControl mGameControl = GameFactory.getInstance().currentGame();
}
