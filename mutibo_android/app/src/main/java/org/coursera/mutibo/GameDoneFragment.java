package org.coursera.mutibo;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.coursera.mutibo.game.GameControl;

public class GameDoneFragment extends Fragment implements View.OnClickListener
{
    private static final String ARG_SUCCESS = "success";
    private static final String ARG_REASON  = "reason";

    private static final String STATE_RATING = "rating";

    public static GameDoneFragment newInstance(GameControl.SetSuccess p_success, String p_reason)
    {
        GameDoneFragment fragment = new GameDoneFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_SUCCESS, p_success);
        args.putString(ARG_REASON, p_reason);
        fragment.setArguments(args);

        return fragment;
    }

    public GameDoneFragment()
    {
        // Required empty public constructor
        mRating = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mSuccess = (GameControl.SetSuccess) getArguments().getSerializable(ARG_SUCCESS);
            mReason  = getArguments().getString(ARG_REASON);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View f_view = inflater.inflate(R.layout.fragment_game_done, container, false);

        if (mSuccess == GameControl.SetSuccess.TIMEOUT)
            ((TextView) f_view.findViewById(R.id.txtSuccessReason)).setText(getString(R.string.game_timeout));
        else
            ((TextView) f_view.findViewById(R.id.txtSuccessReason)).setText(getString(mSuccess == GameControl.SetSuccess.SUCCESS ? R.string.game_success : R.string.game_failure) + " " + mReason);

        f_view.findViewById(R.id.btnContinue).setOnClickListener(this);

        imgRating[0] = (ImageView) f_view.findViewById(R.id.imgRating01);
        imgRating[1] = (ImageView) f_view.findViewById(R.id.imgRating02);
        imgRating[2] = (ImageView) f_view.findViewById(R.id.imgRating03);
        imgRating[3] = (ImageView) f_view.findViewById(R.id.imgRating04);
        imgRating[4] = (ImageView) f_view.findViewById(R.id.imgRating05);

        for (ImageView img : imgRating)
            img.setOnClickListener(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_RATING))
            setRating(savedInstanceState.getInt(STATE_RATING));

        return f_view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        super.onSaveInstanceState(savedState);
        savedState.putInt(STATE_RATING, mRating);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btnContinue :
                mListener.onQuestionContinue(mRating);
                break;

            case R.id.imgRating01 :
                setRating(1);
                break;

            case R.id.imgRating02 :
                setRating(2);
                break;

            case R.id.imgRating03 :
                setRating(3);
                break;

            case R.id.imgRating04 :
                setRating(4);
                break;

            case R.id.imgRating05 :
                setRating(5);
                break;
        }
    }

    private void setRating(int rating)
    {
        mRating = rating;

        for (int f_idx=0; f_idx < mRating; ++f_idx)
            imgRating[f_idx].setImageResource(R.drawable.rating_filled);

        for (int f_idx=mRating; f_idx < imgRating.length; ++f_idx)
            imgRating[f_idx].setImageResource(R.drawable.rating_clear);
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
    public void onStop()
    {
        mListener = null;
        super.onStop();
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
        public void onQuestionContinue(int rating);
    }

    // member variables
    private GameControl.SetSuccess  mSuccess;
    private String                  mReason;
    private int                     mRating;
    private ImageView               imgRating[] = new ImageView[5];

    private OnFragmentInteractionListener mListener;

}
