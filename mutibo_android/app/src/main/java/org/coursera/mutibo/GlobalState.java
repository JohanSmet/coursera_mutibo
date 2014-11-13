package org.coursera.mutibo;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GlobalState
{
    public static String getAuthToken()
    {
        mLock.readLock().lock();
        try {
            return mAuthToken;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public static void setAuthToken(String mAuthToken)
    {
        mLock.writeLock().lock();
        try {
            GlobalState.mAuthToken = mAuthToken;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public static String getNickName()
    {
        mLock.readLock().lock();
        try {
            return mNickName;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public static void setNickName(String mNickName)
    {
        mLock.writeLock().lock();
        try {
            GlobalState.mNickName = mNickName;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    // member variables
    private static String           mAuthToken = null;
    private static String           mNickName  = "";
    private static ReadWriteLock    mLock      = new ReentrantReadWriteLock();
}
