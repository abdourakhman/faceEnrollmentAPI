/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.models;

import com.daon.identityx.rest.model.pojo.User;

/**
 *
 * @author Abdourahmane NDIAYE (GEMADEC)
 */
public class RegistrationHelper {
    
    private User user;
    private String registrationId;
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public RegistrationHelper() {
    }

    public RegistrationHelper(User user, String registrationId) {
        this.user = user;
        this.registrationId = registrationId;
    }
    
}
