package org.coursera.mutibo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MultiplayerScoreFragment extends Fragment
{
    public MultiplayerScoreFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // inflate the view for this fragment
        View view = inflater.inflate(R.layout.fragment_multiplayer_score, container, false);

        mControls[0]             = new Controls();
        mControls[0].txtName     = (TextView)  view.findViewById(R.id.txtName1);
        mControls[0].txtScore    = (TextView)  view.findViewById(R.id.txtScore1);
        mControls[0].pnlLives    = (ViewGroup) view.findViewById(R.id.pnlLives1);
        mControls[0].imgCrown    = (ImageView) view.findViewById(R.id.imgCrown_1);
        mControls[0].imgLives[0] = (ImageView) view.findViewById(R.id.imgLife1_1);
        mControls[0].imgLives[1] = (ImageView) view.findViewById(R.id.imgLife1_2);
        mControls[0].imgLives[2] = (ImageView) view.findViewById(R.id.imgLife1_3);

        mControls[1]             = new Controls();
        mControls[1].txtName     = (TextView)  view.findViewById(R.id.txtName2);
        mControls[1].txtScore    = (TextView)  view.findViewById(R.id.txtScore2);
        mControls[1].pnlLives    = (ViewGroup) view.findViewById(R.id.pnlLives2);
        mControls[1].imgCrown    = (ImageView) view.findViewById(R.id.imgCrown_2);
        mControls[1].imgLives[0] = (ImageView) view.findViewById(R.id.imgLife2_1);
        mControls[1].imgLives[1] = (ImageView) view.findViewById(R.id.imgLife2_2);
        mControls[1].imgLives[2] = (ImageView) view.findViewById(R.id.imgLife2_3);

        return view;
    }

    public void initPlayer(int idx, String playerName)
    {
        mControls[idx].txtName.setText(playerName);
        mControls[idx].txtScore.setText(getString(R.string.game_waiting));
        mControls[idx].pnlLives.setVisibility(View.INVISIBLE);
        mControls[idx].imgCrown.setVisibility(View.GONE);
    }

    public void setScore(String playerName, int score, int lives)
    {
        int player = -1;

        for (int f_idx=0;f_idx<2;++f_idx) {
            if (mControls[f_idx].txtName.getText().equals(playerName))
                player = f_idx;
        }

        if (player >= 0 && player <= 1)
        {
            mControls[player].txtScore.setText(Integer.toString(score));

            for (int f_idx=0; f_idx < lives; ++f_idx)
                mControls[player].imgLives[f_idx].setImageResource(R.drawable.heart);

            for (int f_idx=lives; f_idx < 3; ++f_idx)
                mControls[player].imgLives[f_idx].setImageResource(R.drawable.broken_heart);

            mControls[player].pnlLives.setVisibility(View.VISIBLE);
        }
    }

    public void setWinner(int idx)
    {
        mControls[idx].imgCrown.setVisibility(View.VISIBLE);
    }

    private class Controls
    {
        TextView    txtName;
        TextView    txtScore;
        ViewGroup   pnlLives;
        ImageView   imgCrown;
        ImageView[] imgLives = new ImageView[3];
    }

    // member variables
    private Controls[]  mControls = new Controls[2];

}
