package org.coursera.mutibo.util;

import android.os.Bundle;
import android.util.Log;

import org.coursera.mutibo.BuildConfig;

public class DebugLog
{
    public static int d(String tag, String msg)
    {
        if (BuildConfig.DEBUG)
            return Log.d(tag, msg);
        else
            return 0;
    }

    public static int d(String tag, String msg, Throwable tr)
    {
        if (BuildConfig.DEBUG)
            return Log.d(tag, msg, tr);
        else
            return 0;
    }

    public static int d (String tag, String msg, Bundle bundle)
    {
        if (!BuildConfig.DEBUG)
            return 0;

        Log.d(tag, msg  + " begin bundle");

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            Log.d(tag, String.format(" - %s = %s (%s)", key, value.toString(), value.getClass()));
        }

        return Log.d(tag, msg  + " end bundle");
    }
}
