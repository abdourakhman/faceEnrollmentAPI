/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.models;

/**
 *
 * @author GEMADEC
 */
public class FormatData {
    
    
    private String format;
    private String data;
    private String idUser;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }
    
    

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FormatData(String format, String data, String idUser) {
        this.format = format;
        this.data = data;
        this.idUser = idUser;
    }

    

    public FormatData() {
    }
    
}
