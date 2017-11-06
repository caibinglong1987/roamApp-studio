package com.roamtech.telephony.roamapp.util;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class CallTime
  extends Handler
{
  private static final boolean DBG = true;
  private static final String LOG_TAG = "PHONE/CallTime";
  static final boolean PROFILE = false;
  private long startTime;
  private long mInterval;
  private long mLastReportedTime;
  private OnTickListener mListener;
  private PeriodicTimerCallback mTimerCallback;
  private boolean mTimerRunning;
  
  public CallTime(OnTickListener paramOnTickListener)
  {
    this.mListener = paramOnTickListener;
    this.mTimerCallback = new PeriodicTimerCallback();
  }
  
  static long getCallDuration(long startTime)
  {
    long l1 = 0L;
    l1 = System.currentTimeMillis() - startTime;

      log("updateElapsedTime, duration=" + l1);
      return l1;
  }
  
  private static void log(String paramString)
  {
    Log.d(LOG_TAG, "[CallTime] " + paramString);
  }
  
  private void updateElapsedTime(long startTime)
  {
    if (this.mListener != null)
    {
      long l = getCallDuration(startTime);
      this.mListener.onTickForCallTimeElapsed(l / 1000L);
    }
  }
  
  public void cancelTimer()
  {
    log("cancelTimer()...");
    removeCallbacks(this.mTimerCallback);
    this.mTimerRunning = false;
  }
  
  
  public void periodicUpdateTimer()
  {
      if (!mTimerRunning) {
          mTimerRunning = true;

          long now = SystemClock.uptimeMillis();
          long nextReport = mLastReportedTime + mInterval;

          while (now >= nextReport) {
              nextReport += mInterval;
          }

          if (DBG) log("periodicUpdateTimer() @ " + nextReport);
          /// M: @{
          // original code:
          // postAtTime(mTimerCallback, nextReport);
          postAtTime(mTimerCallback, nextReport);
          /// @}
          mLastReportedTime = nextReport;

          if (startTime != 0) {
                  updateElapsedTime(startTime);
          }

      } else {
          if (DBG) log("periodicUpdateTimer: timer already running, bail");
      }
  }
  
  public void reset()
  {
    log("reset()...");
    this.mLastReportedTime = (SystemClock.uptimeMillis() - this.mInterval);
  }
  
  public void setActiveCallMode(long startTime)
  {
    log("setActiveCallMode(" + startTime + ")...");
    this.startTime = startTime;
    this.mInterval = 1000L;
  }
  
  public static abstract interface OnTickListener
  {
    public abstract void onTickForCallTimeElapsed(long paramLong);
  }
  
  private class PeriodicTimerCallback
    implements Runnable
  {
    PeriodicTimerCallback() {}
    
    public void run()
    {
      mTimerRunning = false;
      CallTime.this.periodicUpdateTimer();
    }
  }
}

