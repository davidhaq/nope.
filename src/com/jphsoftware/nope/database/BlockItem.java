package com.jphsoftware.nope.database;

import java.io.Serializable;

import android.util.Log;

@SuppressWarnings("serial")
public class BlockItem implements Serializable {

	private String number;
	
	private static final String TAG = BlockItem.class.getSimpleName();
	public static final boolean DEBUG = true;

	public BlockItem(String phoneNumber) {
		if(DEBUG)
			Log.d(TAG, phoneNumber);
		
		this.number = phoneNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
