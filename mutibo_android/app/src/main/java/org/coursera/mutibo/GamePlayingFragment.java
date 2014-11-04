package org.coursera.mutibo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GamePlayingFragment extends Fragment
{
    private static final String ARG_DIFFICULTY  = "difficulty";
    private static final String ARG_POINTS      = "points";
    private static final String ARG_TIMEOUT     = "timeout";


    /**
     * Use this factory method to create a new instance of this fragment using the provided parameters.
     *
     * @param p_difficulty Difficulty rating of the question
     * @param p_points The point value of the question
     * @param p_timeout How long does the user have to answer this question
     * @return A new instance of fragment GamePlayingFragment.
     */

    public static GamePlayingFragment newInstance(int p_difficulty, int p_points, int p_timeout)
    {
        GamePlayingFragment fragment = new GamePlayingFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_DIFFICULTY, p_difficulty);
        args.putInt(ARG_POINTS,     p_points);
        args.putInt(ARG_TIMEOUT,    p_timeout);
        fragment.setArguments(args);

        return fragment;
    }
    public GamePlayingFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mDifficulty = getArguments().getInt(ARG_DIFFICULTY);
            mPoints     = getArguments().getInt(ARG_POINTS);
            mTimeout    = getArguments().getInt(ARG_TIMEOUT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View f_view = inflater.inflate(R.layout.fragment_game_playing, container, false);

        ((TextView) f_view.findViewById(R.id.txtDifficulty)).setText(String.format(getString(R.string.game_difficulty), "bla"));
        ((TextView) f_view.findViewById(R.id.txtPoints)).setText(String.format(getString(R.string.game_points), mPoints));

        return f_view;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        public void onQuestionTimeout();
    }

    // member variables
    private int mDifficulty;
    private int mPoints;
    private int mTimeout;

    private OnFragmentInteractionListener mListener;

}
