package org.coursera.mutibo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.coursera.mutibo.game.GameControl;
import org.coursera.mutibo.game.GameFactory;


public class GameActivity extends Activity
                          implements GamePlayingFragment.OnFragmentInteractionListener,
                                     GameDoneFragment.OnFragmentInteractionListener

{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        txtTotalScore = (TextView) findViewById(R.id.txtTotalScore);
        btnMovies[0]  = (Button)   findViewById(R.id.btnMovie01);
        btnMovies[1]  = (Button)   findViewById(R.id.btnMovie02);
        btnMovies[2]  = (Button)   findViewById(R.id.btnMovie03);
        btnMovies[3]  = (Button)   findViewById(R.id.btnMovie04);
        imgLives[0]   = (ImageView)findViewById(R.id.imgLife1);
        imgLives[1]   = (ImageView)findViewById(R.id.imgLife2);
        imgLives[2]   = (ImageView)findViewById(R.id.imgLife3);

        displayCurrentSet();
    }

    private void displayCurrentSet()
    {
        txtTotalScore.setText(getString(R.string.game_score) + Integer.toString(mGameControl.totalScore()));

        // movies
        for (int f_idx=0; f_idx < 4; ++f_idx)
            btnMovies[f_idx].setText(mGameControl.currentSetMovie(f_idx).getName());

        // remaining lives
        for (int f_idx=0; f_idx < mGameControl.remainingLives(); ++f_idx)
            imgLives[f_idx].setImageResource(R.drawable.heart);

        for (int f_idx=mGameControl.remainingLives(); f_idx < 3; ++f_idx)
            imgLives[f_idx].setImageResource(R.drawable.broken_heart);

        // state dependent information
        if (mGameControl.currentGameState() == GameControl.GAME_STATE_QUESTION)
            displayActiveSet();
        else
            displayFinishedSet();
    }

    private void displayActiveSet()
    {
        // make sure the movie buttons are enabled
        for (int index=0; index < btnMovies.length; ++index)
            btnMovies[index].setEnabled(true);

        // show the game running fragment
        GamePlayingFragment f_fragment = GamePlayingFragment.newInstance(mGameControl.currentSetDifficulty(), mGameControl.currentSetPoints(), 15);

        getFragmentManager().beginTransaction()
                                .replace(R.id.frameFooter, f_fragment)
                            .commit();
    }

    private void displayFinishedSet()
    {
        // make sure the movie buttons are disabled
        for (int index=0; index < btnMovies.length; ++index)
            btnMovies[index].setEnabled(false);

        // show the game over fragment
        GameDoneFragment f_fragment = GameDoneFragment.newInstance(mGameControl.currentSetSuccess(), mGameControl.currentSetReason());

        getFragmentManager().beginTransaction()
                .replace(R.id.frameFooter, f_fragment)
                .commit();
    }

    private void endCurrentSet(int p_movie)
    {
        boolean f_success = mGameControl.answerSet(p_movie);

        if (mGameControl.currentGameState() == GameControl.GAME_STATE_ANSWERED)
            displayCurrentSet();
        else
            startActivity(new Intent(this, GameOverActivity.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    public void btnMovie_clicked(View p_view)
    {
        switch (p_view.getId())
        {
            case R.id.btnMovie01:
                endCurrentSet(0);
                break;

            case R.id.btnMovie02:
                endCurrentSet(1);
                break;


            case R.id.btnMovie03:
                endCurrentSet(2);
                break;

            case R.id.btnMovie04:
                endCurrentSet(3);
                break;

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // interaction with frame GamePlayingFragment
    //

    public void onQuestionTimeout()
    {

    }

    public void onQuestionContinue(int rating)
    {
        mGameControl.continueGame();
        displayCurrentSet();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    //

    private GameControl mGameControl = GameFactory.getInstance().currentGame();
    private TextView    txtTotalScore;
    private Button[]    btnMovies = new Button[4];
    private ImageView[] imgLives  = new ImageView[3];

}
