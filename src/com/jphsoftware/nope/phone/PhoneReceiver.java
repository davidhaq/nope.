package com.jphsoftware.nope.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PhoneReceiver extends BroadcastReceiver {

//	private ITelephony telephonyService;

	@Override
	public void onReceive(Context context, Intent intent) {

//		TelephonyManager telephony = (TelephonyManager) context
//				.getSystemService(Context.TELEPHONY_SERVICE);
//		try {
//			Class c = Class.forName(telephony.getClass().getName());
//			Method m = c.getDeclaredMethod("getITelephony");
//			m.setAccessible(true);
//			telephonyService = (ITelephony) m.invoke(telephony);
//			telephonyService.answerRingingCall();
//			telephonyService.endCall();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
