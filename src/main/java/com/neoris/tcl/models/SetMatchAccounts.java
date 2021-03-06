package com.neoris.tcl.models;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the set_match_accounts database table.
 * 
 */
@Entity
@Table(name = "set_match_accounts")
public class SetMatchAccounts implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2750715160416744318L;

	@EmbeddedId
	private SetMatchAccountsPK id;

	private String sign;
	private String userid;

	public SetMatchAccounts() {
	    this.setId(new SetMatchAccountsPK());
	}

	

	public SetMatchAccounts(SetMatchAccountsPK id, String sign, String userid) {
		
		this.id = id;
		this.sign = sign;
		this.userid = userid;
	}



	public SetMatchAccountsPK getId() {
		return this.id;
	}

	public void setId(SetMatchAccountsPK id) {
		this.id = id;
	}

	public String getSign() {
		return this.sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getUserid() {
		return userid;
	}



	public void setUserid(String userid) {
		this.userid = userid;
	}



	@Override
	public String toString() {
		return "SetMatchAccounts [id=" + id + ", sign=" + sign + ", userid=" + userid + "]";
	}



}