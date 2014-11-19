package org.coursera.mutibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.coursera.mutibo.game.GameControl;
import org.coursera.mutibo.game.GameFactory;


public class GameOverActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        ((TextView) findViewById(R.id.txtResults)).setText(String.format(getString(R.string.gamedone_result),
                                                                mGameControl.numCorrectQuestions(),
                                                                mGameControl.totalScore()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    public void btnAnother_clicked(View p_view)
    {
        mGameControl.startGame();

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void btnRanking_clicked(View p_view)
    {
        Intent intent = new Intent(this, LeaderboardActivity.class);
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
