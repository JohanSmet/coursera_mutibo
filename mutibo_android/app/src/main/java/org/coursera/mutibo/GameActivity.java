package org.coursera.mutibo;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        txtTotalScore = (TextView) findViewById(R.id.txtTotalScore);

        btnMovies[0]  = (LinearLayout)  findViewById(R.id.btnMovie01);
        imgMovies[0]  = (ImageView)     findViewById(R.id.imgMovie01);
        txtMovies[0]  = (TextView)      findViewById(R.id.txtMovie01);
        btnMovies[1]  = (LinearLayout)  findViewById(R.id.btnMovie02);
        imgMovies[1]  = (ImageView)     findViewById(R.id.imgMovie02);
        txtMovies[1]  = (TextView)      findViewById(R.id.txtMovie02);
        btnMovies[2]  = (LinearLayout)  findViewById(R.id.btnMovie03);
        imgMovies[2]  = (ImageView)     findViewById(R.id.imgMovie03);
        txtMovies[2]  = (TextView)      findViewById(R.id.txtMovie03);
        btnMovies[3]  = (LinearLayout)  findViewById(R.id.btnMovie04);
        imgMovies[3]  = (ImageView)     findViewById(R.id.imgMovie04);
        txtMovies[3]  = (TextView)      findViewById(R.id.txtMovie04);

        imgLives[0]   = (ImageView)     findViewById(R.id.imgLife1);
        imgLives[1]   = (ImageView)     findViewById(R.id.imgLife2);
        imgLives[2]   = (ImageView)     findViewById(R.id.imgLife3);

        animAnswerCorrect   = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.answer_correct);
        animAnswerCorrect.setEvaluator(new ArgbEvaluator());
        animAnswerIncorrect = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.answer_incorrect);
        animAnswerIncorrect.setEvaluator(new ArgbEvaluator());

        animMovieCorrect    = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.movie_correct);

        defaultBackground = btnMovies[0].getSolidColor();

        displayCurrentSet(savedInstanceState == null);
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

    private void displayCurrentSet(boolean newView)
    {
        txtTotalScore.setText(getString(R.string.game_score) + Integer.toString(mGameControl.totalScore()));

        // movies
        for (int f_idx=0; f_idx < 4; ++f_idx)
            txtMovies[f_idx].setText(mGameControl.currentSetMovie(f_idx).getName());

        addPosterToButtons();

        // remaining lives
        for (int f_idx=0; f_idx < mGameControl.remainingLives(); ++f_idx)
            imgLives[f_idx].setImageResource(R.drawable.heart);

        for (int f_idx=mGameControl.remainingLives(); f_idx < 3; ++f_idx)
            imgLives[f_idx].setImageResource(R.drawable.broken_heart);

        // state dependent information
        if (mGameControl.currentGameState() == GameControl.GAME_STATE_QUESTION)
            displayActiveSet(newView);
        else
            displayFinishedSet(newView);
    }

    private void displayActiveSet(boolean newView)
    {
        // make sure the movie buttons are enabled
        enableMovieButtons(true);

        for (int idx = 0; idx < 4; ++idx)
            btnMovies[idx].setBackgroundColor(defaultBackground);

        // show the game running fragment
        if (newView)
        {
            GamePlayingFragment f_fragment = GamePlayingFragment.newInstance(mGameControl.currentSetDifficulty(), mGameControl.currentSetPoints(), 15);

            getFragmentManager().beginTransaction()
                    .replace(R.id.frameFooter, f_fragment)
                    .commit();
        }
    }

    private void displayFinishedSet(boolean newView)
    {
        // make sure the movie buttons are disabled
        enableMovieButtons(false);


        // animate the correct answer if the player choose the wrong one
        if (mGameControl.currentSetSuccess() != GameControl.SetSuccess.SUCCESS)
        {
            animMovieCorrect.setTarget(btnMovies[mGameControl.currentSetCorrectAnswer()]);
            animMovieCorrect.start();
        }

        // show the game over fragment
        if (newView)
        {
            GameDoneFragment f_fragment = GameDoneFragment.newInstance(mGameControl.currentSetSuccess(), mGameControl.currentSetReason());

            getFragmentManager().beginTransaction()
                    .replace(R.id.frameFooter, f_fragment)
                    .commit();
        }
    }

    private void endCurrentSet(int p_movie)
    {
        if (mGameControl.currentGameState() != GameControl.GAME_STATE_QUESTION)
            return;

        boolean result = mGameControl.answerSet(p_movie);

        // animate the choice of the player
        ObjectAnimator anim = (result) ? animAnswerCorrect : animAnswerIncorrect;
        anim.setTarget(btnMovies[p_movie]);
        anim.start();

        // change the display
        displayCurrentSet(true);
    }

    private void addPosterToButtons()
    {
        for (int f_idx=0; f_idx < 4; ++f_idx)
        {
            new DownloadPosterTask().execute(f_idx);
        }
    }

    private void enableMovieButtons(Boolean enable)
    {
        for (int index=0; index < txtMovies.length; ++index)
        {
            btnMovies[index].setClickable(enable);
            imgMovies[index].setClickable(enable);
            txtMovies[index].setClickable(enable);
            txtMovies[index].setEnabled(enable);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // events
    //

    public void btnMovie01_clicked(View p_view)
    {
        endCurrentSet(0);
    }

    public void btnMovie02_clicked(View p_view)
    {
        endCurrentSet(1);
    }

    public void btnMovie03_clicked(View p_view)
    {
        endCurrentSet(2);
    }

    public void btnMovie04_clicked(View p_view)
    {
        endCurrentSet(3);
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setMessage(R.string.game_cancel)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        GameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton(R.string.dialog_no, null)
                .show();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // interaction with frame GamePlayingFragment
    //

    public void onQuestionTimeout()
    {
        mGameControl.timeoutSet();
        displayCurrentSet(true);
    }

    public void onQuestionContinue(int rating)
    {
        mGameControl.continueGame(rating);

        if (mGameControl.currentGameState() == GameControl.GAME_STATE_FINISHED)
        {
            syncServiceClient.getSyncService().postGameResult(mGameControl.gameResult());
            startActivity(new Intent(this, GameOverActivity.class));
        }
        else
        {
            displayCurrentSet(true);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // nested classes
    //

    private class DownloadPosterTask extends AsyncTask<Integer, Integer, Drawable>
    {
        protected Drawable doInBackground(Integer... params)
        {
            mIndex = params[0];
            String imdbId = mGameControl.currentSetMovie(mIndex).getImdbId();

            Bitmap bitmap = syncServiceClient.getSyncService().downloadPosterBitmap(imdbId);

            return new BitmapDrawable(getResources(), bitmap);
        }

        protected void onPostExecute(Drawable result)
        {
            imgMovies[mIndex].setImageDrawable(result);
        }

        private Integer mIndex;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    //

    private GameControl mGameControl = GameFactory.getInstance().currentGame();
    private TextView    txtTotalScore;

    private LinearLayout[]    btnMovies = new LinearLayout[4];
    private ImageView[]       imgMovies = new ImageView[4];
    private TextView[]        txtMovies = new TextView[4];
    private ImageView[]       imgLives  = new ImageView[3];

    private int               defaultBackground;
    private ObjectAnimator    animAnswerCorrect;
    private ObjectAnimator    animAnswerIncorrect;
    private AnimatorSet       animMovieCorrect;

    private SyncServiceClient   syncServiceClient = new SyncServiceClient(this);
}
