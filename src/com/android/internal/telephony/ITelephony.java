package com.android.internal.telephony;

public abstract interface ITelephony
{
  public abstract void answerRingingCall();

  public abstract void cancelMissedCallsNotification();

  public abstract boolean endCall();

  public abstract void silenceRinger();
}