package org.coursera.mutibo;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
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

        for (int idx=0; idx < 4; ++idx)
        {
            final int finalIdx = idx;

            View.OnClickListener listener = new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    endCurrentSet(finalIdx);
                }
            };

            btnMovies[idx].setOnClickListener(listener);
            imgMovies[idx].setOnClickListener(listener);
            txtMovies[idx].setOnClickListener(listener);
        }

        animAnswerCorrect   = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.answer_correct);
        animAnswerCorrect.setEvaluator(new ArgbEvaluator());
        animAnswerIncorrect = (ObjectAnimator) AnimatorInflater.loadAnimator(this, R.animator.answer_incorrect);
        animAnswerIncorrect.setEvaluator(new ArgbEvaluator());

        animMovieCorrect    = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.movie_correct);

        defaultBackground = btnMovies[0].getSolidColor();

        if (mGameControl.isMultiPlayer() && savedInstanceState != null) {
            scoreDialog = (ScoreDialog) getFragmentManager().findFragmentByTag("scoreDialog");
        }

        if (mGameControl.isMultiPlayer() && scoreDialog == null) {
            scoreDialog = new ScoreDialog();
        }

        displayCurrentSet(savedInstanceState == null);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        // bind to SyncService
        syncServiceClient.bind();

        // listen to events from the game controller
        mGameControl.registerStateCallback(new GameStateChangedCallback(new Handler()));
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // unbind from the service
        syncServiceClient.unbind();

        // stop listening to events from the game controller
        mGameControl.registerStateCallback(null);
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

        // animate the correct answer if the player choose the wrong one (except when trigger by a configuration change)
        if (newView && mGameControl.currentSetSuccess() != GameControl.SetSuccess.SUCCESS)
        {
            animMovieCorrect.setTarget(btnMovies[mGameControl.currentSetCorrectAnswer()]);
            animMovieCorrect.start();
        }

        // set the correct backgrounds when triggered by a configuration change
        if (!newView)
        {
            int playerAnswer = mGameControl.playerAnswer();
            int color        = (mGameControl.currentSetSuccess() == GameControl.SetSuccess.SUCCESS) ? 0xff00c000 : 0xffc00000;

            btnMovies[playerAnswer].setBackgroundColor(color);
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
                        mGameControl.cancelGame();
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

        if (mGameControl.isMultiPlayer()) {
            scoreDialog.show(getFragmentManager(), "scoreDialog");
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // nested classes
    //

    private class GameStateChangedCallback extends ResultReceiver
    {
        public GameStateChangedCallback(Handler handler)
        {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData)
        {
            if (resultCode == GameControl.GAME_STATE_QUESTION) {
                if (scoreDialog != null)
                    scoreDialog.dismiss();
                displayCurrentSet(true);
            } else if (resultCode == GameControl.GAME_STATE_FINISHED || resultCode == GameControl.GAME_STATE_CANCELLED) {
                // go to the end of game activity
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                if (resultData != null)
                    intent.putExtras(resultData);

                startActivity(intent);
            } else if (resultCode == GameControl.GAME_EVENT_SCORE_UPDATE && resultData != null && scoreDialog != null) {
                scoreDialog.updateScores();
            } else if (resultCode == GameControl.GAME_EVENT_QUESTION_COUNTDOWN && resultData != null && scoreDialog != null) {
                scoreDialog.setCountdown(resultData.getLong("COUNTDOWN", 0));
            }
        }
    }

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

    public static class ScoreDialog extends DialogFragment
    {
        public ScoreDialog()
        {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState)
        {
            // create the dialog only once
            if (mDialog == null) {
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_multiplayer_score, null);

                mDialog = new AlertDialog.Builder(getActivity())
                        .setView(view)
                        .create();
                mDialog.setCanceledOnTouchOutside(false);

                scoreFragment = (MultiplayerScoreFragment) getFragmentManager().findFragmentById(R.id.scoreFragment);
                lblStatus = (TextView) view.findViewById(R.id.txtStatus);
            }

            // initialize the view
            lblStatus.setText(R.string.multi_status);
            updateScores();

            return mDialog;
        }

        @Override
        public void onCancel(DialogInterface dialog)
        {
            getActivity().onBackPressed();
            super.onCancel(dialog);
        }

        public void updateScores()
        {
            if (scoreFragment == null)
                return;

            GameControl gameControl = GameFactory.getInstance().currentGame();

            for (int idx = 0; idx < gameControl.playerCount(); ++idx)
            {
                GameControl.PlayerScore playerScore = gameControl.playerScore(idx);

                scoreFragment.initPlayer(idx, playerScore.mPlayerName);

                if (playerScore.mUpToDate)
                    scoreFragment.setScore(playerScore.mPlayerName, playerScore.mScore, playerScore.mLives);
            }
        }

        public void setCountdown(long countdown)
        {
            if (lblStatus != null)
                lblStatus.setText(String.format(getString(R.string.multi_starting), countdown));
        }

        // member variables
        private Dialog                      mDialog = null;
        private MultiplayerScoreFragment    scoreFragment;
        private TextView                    lblStatus;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // member variables
    //

    private GameControl       mGameControl = GameFactory.getInstance().currentGame();

    private TextView          txtTotalScore;
    private LinearLayout[]    btnMovies = new LinearLayout[4];
    private ImageView[]       imgMovies = new ImageView[4];
    private TextView[]        txtMovies = new TextView[4];
    private ImageView[]       imgLives  = new ImageView[3];

    private int               defaultBackground;
    private ObjectAnimator    animAnswerCorrect;
    private ObjectAnimator    animAnswerIncorrect;
    private AnimatorSet       animMovieCorrect;

    private ScoreDialog       scoreDialog = null;

    private SyncServiceClient syncServiceClient = new SyncServiceClient(this);
}
