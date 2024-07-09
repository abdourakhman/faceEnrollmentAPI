/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.models;

/**
 *
 * @author Mehdi GEMADEC
 */
public class ImageToCompare {
    
    private String id;
    private String selfie;
    private String identityPhoto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelfie() {
        return selfie;
    }

    public void setSelfie(String selfie) {
        this.selfie = selfie;
    }

    public String getIdentityPhoto() {
        return identityPhoto;
    }

    public void setIdentityPhoto(String identityPhoto) {
        this.identityPhoto = identityPhoto;
    }

    public ImageToCompare() {
    }

    public ImageToCompare(String id, String selfie, String identityPhoto) {
        this.id = id;
        this.selfie = selfie;
        this.identityPhoto = identityPhoto;
    }

    
}
