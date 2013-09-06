package com.jphsoftware.nope.database;

import java.io.Serializable;
import java.util.ArrayList;

public class BlockItem implements Serializable {

	private String number;
	private ArrayList<String> contactLog;

	public BlockItem() {
		super();
	}

	public BlockItem(String phoneNumber, ArrayList<String> contactLog) {
		super();
		this.number = phoneNumber;
		this.contactLog = contactLog;
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

	public ArrayList<String> getContactLog() {
		return contactLog;
	}

	public void setContactLog(ArrayList<String> contactLog) {
		this.contactLog = contactLog;
	}

	public String getLastContact() {

		return getContactLog().get(0);
	}
}
