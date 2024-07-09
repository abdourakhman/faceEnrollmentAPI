package com.api.daon.models;
/**
* @author Mehdi GEMADEC
*/
public class EvaluationsResult {
	
	private boolean etat;
	private int code;
	private String message;
	public boolean isEtat() {
		return etat;
	}
	public void setEtat(boolean etat) {
		this.etat = etat;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public EvaluationsResult(boolean etat, int code, String message) {
		super();
		this.etat = etat;
		this.code = code;
		this.message = message;
	}
	public EvaluationsResult() {
		super();
	
	}
	public void setEtatAndCodeAndMessage(boolean etat, int code, String message) {
		this.etat = etat;
		this.code = code;
		this.message = message;
	}
	
	

}
