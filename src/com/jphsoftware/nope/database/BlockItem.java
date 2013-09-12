package com.jphsoftware.nope.database;


public class BlockItem {

	private long id;
	private int lastContact;
	private String number;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getLastContact() {
		return lastContact;
	}

	public void setLastContact(int lastContact) {
		this.lastContact = lastContact;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

}
