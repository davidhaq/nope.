package com.jphsoftware.nope;

public class CallBlockItem {

	private String number;
	private String lastCall;

	public CallBlockItem() {
		super();
	}

	public CallBlockItem(String phoneNumber, String lastCall) {
		super();
		this.number = phoneNumber;
		this.lastCall = lastCall;
	}
	public CallBlockItem(String phoneNumber) {
		super();
		this.number = phoneNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getLastCall() {
		return lastCall;
	}

	public void setLastCall(String lastCall) {
		this.lastCall = lastCall;
	}
}
