package org.coursera.mutibo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.CountDownLatch;

public class SyncServiceClient
{
    public SyncServiceClient(Context context)
    {
        mContext = context;
    }

    public void bind()
    {
        Intent intent = new Intent(mContext, SyncService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind()
    {
        if (mSyncBound)
        {
            mContext.unbindService(mConnection);
            mSyncBound = false;
        }
    }

    private void wait_for_service()
    {
        while (!mSyncBound)
        {
            try {
                mSyncCountDown.await();
            } catch (InterruptedException e) {
            }
        }
    }

    public SyncService getSyncService()
    {
        wait_for_service();
        return mSyncService;
    }

    // member variables
    private final Context mContext;

    SyncService     mSyncService    = null;
    boolean         mSyncBound      = false;
    CountDownLatch  mSyncCountDown  = new CountDownLatch(1);

    private ServiceConnection mConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service)
        {
            // we've bound to the sync service - store its information
            SyncService.SyncBinder binder = (SyncService.SyncBinder) service;
            mSyncService = binder.getService();
            mSyncBound   = true;
            mSyncCountDown.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mSyncBound = false;
        }
    };
}
