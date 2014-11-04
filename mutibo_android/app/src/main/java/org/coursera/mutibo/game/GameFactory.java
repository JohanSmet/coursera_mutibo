package org.coursera.mutibo.game;

public class GameFactory
{
    public final static int GAME_TYPE_SINGLEPLAYER = 1;
    public final static int GAME_TYPE_MULTIPLAYER  = 2;

    public GameControl newGame(int p_type)
    {
        mCurrentGame = null;

        if (p_type == GAME_TYPE_SINGLEPLAYER)
            mCurrentGame = new GameControlSingle();

        if (mCurrentGame != null)
            mCurrentGame.startGame();

        return mCurrentGame;
    }

    public GameControl currentGame()
    {
        return mCurrentGame;
    }

    public static GameFactory getInstance()
    {
        return mInstance;
    }

    // member variables
    private static GameFactory mInstance    = new GameFactory();
    private GameControl mCurrentGame = null;
}
