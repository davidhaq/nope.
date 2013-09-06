package com.jphsoftware.nope.database;

public class BlockItem {

	private String number;
	private String lastContact;

	public BlockItem() {
		super();
	}

	public BlockItem(String phoneNumber, String lastContact) {
		super();
		this.number = phoneNumber;
		this.lastContact = lastContact;
	}

	public BlockItem(String phoneNumber) {
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
		return lastContact;
	}

	public void setLastCall(String lastCall) {
		this.lastContact = lastCall;
	}
}
