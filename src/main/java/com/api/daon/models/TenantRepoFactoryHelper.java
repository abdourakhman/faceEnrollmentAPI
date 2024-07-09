/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.models;

import com.identityx.clientSDK.TenantRepoFactory;

/**
 *
 * @author GEMADEC
 */
public class TenantRepoFactoryHelper {
    
    private String message;
    private TenantRepoFactory tenantRepoFactory;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TenantRepoFactory getTenantRepoFactory() {
        return tenantRepoFactory;
    }

    public void setTenantRepoFactory(TenantRepoFactory tenantRepoFactory) {
        this.tenantRepoFactory = tenantRepoFactory;
    }

    public TenantRepoFactoryHelper(String message, TenantRepoFactory tenantRepoFactory) {
        this.message = message;
        this.tenantRepoFactory = tenantRepoFactory;
    }

    public TenantRepoFactoryHelper() {
    }
    
    
    
    
}
