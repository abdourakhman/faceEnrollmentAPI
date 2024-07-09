package com.api.daon.models;
/**
* @author Mehdi GEMADEC
*/
public class Response {
	private String message ;
	private int code;
	private boolean isverifyed;
	private Double score;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public boolean isIsverifyed() {
		return isverifyed;
	}
	public void setIsverifyed(boolean isverifyed) {
		this.isverifyed = isverifyed;
	}
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	public Response(String message, int code, boolean isverifyed, Double score) {
		super();
		this.message = message;
		this.code = code;
		this.isverifyed = isverifyed;
		this.score = score;
	}
	public Response() {
		super();
		
	}
	public void setMessageAndScoreAndCode(String message, Double score, int code) {
		this.message = message;
		this.code = code;
		this.score = score;
		
	}
	public void setMessageAndCode(String message, int code) {
		this.message = message;
		this.code = code;
		
	}
	
	
	
	
	

}
