package com.heliopause.nope.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.heliopause.nope.Constants;

import java.util.HashMap;

public class Configuration {

    // Debug constants
    private static final boolean DEBUG = true;
    private static final String TAG = Configuration.class.getSimpleName();

    public static Configuration instance = null;
    private Context context;
    public boolean mAdvanced = false;
    boolean mBlockUnknownNumber;
    public boolean mEnabled = false;
    public boolean mHelp = false;
    boolean mIgnoreCase = true;
    private HashMap<String, MyMessage> mNumberToLastMessage = new HashMap<String, MyMessage>();
    private long mPeriod = 5000L;

    public static Configuration getInstance(Context paramContext) {
        if (instance == null) {
            if (DEBUG)
                Log.d(TAG, "Creating new configuration instance");
            instance = new Configuration();
            instance.context = paramContext;
            instance.loadPreference();
        }
        if (DEBUG)
            Log.d(TAG, "Using configuration instance");
        return instance;
    }

    public boolean checkAndUpdateSMS(Context paramContext, String paramString1,
                                     String paramString2) {
        long l = System.currentTimeMillis();
        MyMessage localMyMessage2;
        boolean bool = false;
        if (this.mNumberToLastMessage.containsKey(paramString1)) {
            localMyMessage2 = this.mNumberToLastMessage.get(paramString1);
            if ((l - localMyMessage2.mLastTime < this.mPeriod)
                    || (localMyMessage2.mBody.equalsIgnoreCase(paramString2))) {
                localMyMessage2.mCount = (1 + localMyMessage2.mCount);
                localMyMessage2.mLastTime = l;
                bool = true;
            }
        } else {
            if (!TextUtils.isEmpty(paramString1)) {
                if (TextUtils.isEmpty(paramString2))
                    paramString2 = "";
                MyMessage localMyMessage1 = new MyMessage();
                localMyMessage1.mSender = paramString1;
                localMyMessage1.mSenderName = getSenderName(paramContext,
                        paramString1);
                if (TextUtils.isEmpty(localMyMessage1.mSenderName))
                    localMyMessage1.mSenderName = localMyMessage1.mSender;
                localMyMessage1.mBody = paramString2;
                localMyMessage1.mCount = 1;
                localMyMessage1.mFirstTime = l;
                localMyMessage1.mLastTime = l;
                this.mNumberToLastMessage.put(localMyMessage1.mSender,
                        localMyMessage1);
                bool = false;
            }

        }
        return bool;
    }

    private static String getSenderName(Context paramContext, String paramString) {
        Uri localUri = Uri.withAppendedPath(
                ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(paramString));
        String[] arrayOfString = {"_id", "number", "display_name"};
        String str1 = "", str2;
        Cursor localCursor = paramContext.getContentResolver().query(localUri,
                arrayOfString, null, null, null);
        if (localCursor != null) {
            try {
                if (localCursor.moveToFirst()) {
                    str2 = localCursor.getString(2);
                    str1 = str2;
                    return str1;
                }
            } finally {
                if (localCursor != null)
                    localCursor.close();
            }
        } else {
            str1 = "";
            return str1;
        }

        return str1;

    }

    void loadPreference() {

        if (DEBUG)
            Log.d(TAG, "calling loadPreference");
        SharedPreferences localSharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        this.mEnabled = localSharedPreferences.getBoolean(
                Constants.SPAM_BLOCK_SERVICE_STATUS, false);
        if (DEBUG)
            Log.d(TAG, "SpamBlock status: " + mEnabled);

    }

    static class MyMessage {
        String mBody;
        int mCount = 0;
        long mFirstTime;
        boolean mIsDifferent;
        long mLastTime;
        String mSender;
        String mSenderName;
    }
}
