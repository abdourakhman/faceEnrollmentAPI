/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.models;

import com.daon.identityx.rest.model.pojo.AuthenticationRequest;

/**
 *
 * @author GEMADEC
 */
public class AuthenticationRequestHelper {
    private Statut etat= Statut.Initial;
    private String message;
    private AuthenticationRequest AuthenticationRequest;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setMessageAndEtat(String message,Statut etat) {
        this.message = message;
        this.etat = etat;
    }

    public AuthenticationRequest getAuthenticationRequest() {
        return AuthenticationRequest;
    }

    public void setAuthenticationRequest(AuthenticationRequest AuthenticationRequest) {
        this.AuthenticationRequest = AuthenticationRequest;
    }

    public AuthenticationRequestHelper(String message, AuthenticationRequest AuthenticationRequest) {
        this.message = message;
        this.AuthenticationRequest = AuthenticationRequest;
    }
    
    

    public Statut getEtat() {
		return etat;
	}

	public void setEtat(Statut etat) {
		this.etat = etat;
	}

	public AuthenticationRequestHelper(Statut etat, String message,
			com.daon.identityx.rest.model.pojo.AuthenticationRequest authenticationRequest) {
		super();
		this.etat = etat;
		this.message = message;
		AuthenticationRequest = authenticationRequest;
	}

	public AuthenticationRequestHelper() {
    }

	public void setMessageAndStatut(String message, Statut etat) {
		this.etat = etat;
		this.message = message;
	}
    
    
    
}
