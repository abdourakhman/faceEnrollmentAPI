/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.models;

import com.daon.identityx.rest.model.pojo.User;

/**
 *
 * @author GEMADEC
 */
public class UserHelper {
	private Statut statut=Statut.Initial;
    private String message;
    private User user;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserHelper(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public UserHelper() {
    }

	public UserHelper(String string) {
		 this.message = string;
	}

	public Statut getStatut() {
		return statut;
	}

	public void setStatut(Statut statut) {
		this.statut = statut;
	}
	public void setStatutAndMessageAndUser(String message, User user,Statut statut) {
		this.message = message;
        this.user = user;
        this.statut = statut;
	}

	public void setStatutAndMessage(String message, Statut statut) {
		this.message = message;
        this.statut = statut;
		
	}
	
	
    
    
}
