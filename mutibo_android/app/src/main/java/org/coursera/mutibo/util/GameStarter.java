package org.coursera.mutibo.util;

import android.content.Context;
import android.content.Intent;

import org.coursera.mutibo.GameActivity;
import org.coursera.mutibo.GameAwaitingOpponentActivity;
import org.coursera.mutibo.game.GameControl;
import org.coursera.mutibo.game.GameFactory;

public class GameStarter
{
    public static void launchGame(int type, Context context)
    {
        // create a new multiplayer player game
        GameControl game = GameFactory.getInstance().newGame(type, context);

        // launch correct activity
        Intent intent = new Intent(context, (game.currentGameState() == GameControl.GAME_STATE_AWAITING_OPPONENT) ? GameAwaitingOpponentActivity.class : GameActivity.class);
        context.startActivity(intent);
    }
}
