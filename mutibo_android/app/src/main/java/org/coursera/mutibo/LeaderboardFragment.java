package org.coursera.mutibo;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.coursera.mutibo.R;
import org.coursera.mutibo.data.MutiboUserResult;

import java.util.ArrayList;
import java.util.Collection;

public class LeaderboardFragment extends ListFragment
{
    public static final String LEADERBOARD_ARG_TYPE = "type";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // parameters
        int type = LeaderboardRetrieveTask.RETRIEVE_TYPE_POSITION;

        if (getArguments() != null)
        {
            type = getArguments().getInt(LEADERBOARD_ARG_TYPE);
        }

        // add the footer to the list
        mFooter = getActivity().getLayoutInflater().inflate(R.layout.layout_leaderboard_footer, null, false);
        this.getListView().addFooterView(mFooter);

        // adapter
        setListAdapter(new LeaderboardArrayAdapter(getActivity()));

        // change what happens when the list scroll
        this.getListView().setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i)
            {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                LeaderboardArrayAdapter adapter = (LeaderboardArrayAdapter) getListAdapter();

                // start loading more items when the first item is visible and it isn't the first ranking
                if (firstVisibleItem == 0 && !adapter.isEmpty() && adapter.getItem(0).getRanking() != 1 && !mLoading)
                {
                    int nextRanking = adapter.getItem(0).getRanking() - 15;

                    mLoading = true;
                    new LeaderboardRetrieveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, nextRanking, 15, LeaderboardRetrieveTask.RETRIEVE_TYPE_POSITION);
                }

                // start loading more items when the last item is on the screen
                if (firstVisibleItem + visibleItemCount == totalItemCount && !mLoading && !mTheEnd)
                {
                    int nextRanking = adapter.getItem(adapter.getCount()-1).getRanking() + 1;

                    mLoading = true;
                    new LeaderboardRetrieveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, nextRanking, 15, LeaderboardRetrieveTask.RETRIEVE_TYPE_POSITION);
                }
            }
        });

        // start loading the first entries
        mLoading = true;
        new LeaderboardRetrieveTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, 1, 15, type);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        syncServiceClient = new SyncServiceClient(getActivity());
        syncServiceClient.bind();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        syncServiceClient.unbind();
    }

    // nested classes
    private class LeaderboardRetrieveTask extends AsyncTask<Integer, Integer, Collection<MutiboUserResult>>
    {
        public static final int RETRIEVE_TYPE_POSITION = 1;
        public static final int RETRIEVE_TYPE_PLAYER   = 2;

        protected Collection<MutiboUserResult> doInBackground(Integer... params)
        {
            if (params[2] == RETRIEVE_TYPE_POSITION)
                return syncServiceClient.getSyncService().getLeaderboard(params[0], params[1]);
            else
                return syncServiceClient.getSyncService().getLeaderboardPlayer(params[1]);
        }

        protected void onPostExecute(Collection<MutiboUserResult> result)
        {
            if (!LeaderboardFragment.this.isVisible())
                return;

            LeaderboardArrayAdapter adapter     = (LeaderboardArrayAdapter) getListAdapter();
            boolean                 atBeginning = !adapter.isEmpty() && getListView().getFirstVisiblePosition() == 0;

            // check for empty list
            if (result == null || result.size() <= 0)
            {
                //  change the footer if we're not at the beginning of the list
                if (!atBeginning)
                {
                    ((TextView) mFooter.findViewById(R.id.txtFooter)).setText(R.string.leaderboard_done);
                    mTheEnd = true;
                }

                return;
            }

            if (!atBeginning)
                adapter.addAll(result);
            else
                adapter.addAllToFront(result);

            mLoading = false;
        }
    }

    public class LeaderboardArrayAdapter extends ArrayAdapter<MutiboUserResult>
    {
        public LeaderboardArrayAdapter(Context context)
        {
            super(context, R.layout.layout_leaderboard_item, new ArrayList<MutiboUserResult>());
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            MutiboUserResult userResult = (MutiboUserResult) getListAdapter().getItem(position);

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.layout_leaderboard_item, parent, false);

            ((TextView) rowView.findViewById(R.id.txtRanking)).setText(String.valueOf(userResult.getRanking()) + '.');
            ((TextView) rowView.findViewById(R.id.txtName)).setText(userResult.getNickName());
            ((TextView) rowView.findViewById(R.id.txtScore)).setText(getString(R.string.leaderboard_score) + String.valueOf(userResult.getBestScore()));

            if (userResult.getNickName().equals(GlobalState.getNickName()))
                rowView.setBackgroundColor(0xFFADB3E5);

            return rowView;
        }

        public void addAllToFront(Collection<MutiboUserResult> newItems)
        {
            // store information so we can keep the current items in view
            int index = getListView().getFirstVisiblePosition() + newItems.size();
            View v = getListView().getChildAt(0);
            int top = (v == null) ? 0 : v.getTop();

            // add the items
            int newIndex = 0;

            for (MutiboUserResult item : newItems)
            {
                this.insert(item, newIndex++);
            }

            // scroll
            getListView().setSelectionFromTop(index, top);

        }

        private Context mContext;
    }

    // member variables
    private View mFooter;
    private boolean             mLoading = false;
    private boolean             mTheEnd = false;

    private SyncServiceClient   syncServiceClient;
}

