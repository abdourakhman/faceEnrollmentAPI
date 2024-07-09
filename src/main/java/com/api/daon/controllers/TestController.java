/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.controllers;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.daon.models.AuthenticationRequestHelper;
import com.api.daon.models.FormatData;
import com.api.daon.models.FormatData2;
import com.api.daon.models.RegistrationHelper;
import com.api.daon.models.TenantRepoFactoryHelper;
import com.api.daon.models.UserHelper;
import com.api.daon.models.UserId;
import com.api.daon.services.Services;
import com.daon.identityx.rest.model.pojo.Application;
import com.daon.identityx.rest.model.pojo.AuthenticationRequest;
import com.daon.identityx.rest.model.pojo.FaceDataSample;
import com.daon.identityx.rest.model.pojo.Registration;
import com.daon.identityx.rest.model.pojo.RegistrationChallenge;
import com.daon.identityx.rest.model.pojo.User;
import com.daon.identityx.rest.model.support.ImageType;
import com.identityx.clientSDK.TenantRepoFactory;
import com.identityx.clientSDK.collections.FaceDataSampleCollection;
import com.identityx.clientSDK.exceptions.ClientInitializationException;
import com.identityx.clientSDK.exceptions.IdxRestException;
import com.identityx.clientSDK.repositories.RegistrationRepository;
import com.identityx.clientSDK.repositories.UserRepository;

/**
 *
 * @author GEMADEC
 */
@RestController
public class TestController {

    @Autowired
    Services services;
    
    @Autowired
 	private Environment env;

     Logger logger = LoggerFactory.getLogger("TestController");
    @PostMapping("/CreateUser")
    public UserHelper CreateUser(@RequestBody UserId userId) {
        UserHelper userH = new UserHelper();

        if (userId.getUserId() != null) {
            userH.setUser(services.CreateUser(userId.getUserId()));
            return userH;
        } else {
            userH.setMessage("Paramètre introuvable");
            return userH;

        }
    }

    @PostMapping("/CreateRegistration")
    public Registration CreateRegistration(@RequestBody UserId userId) throws IOException, ClientInitializationException, IdxRestException {

        //fin User by id
        User user = services.findUser(userId.getUserId());
        if (null != user) {

            Registration reg = new Registration();
            try {
                TenantRepoFactoryHelper trfh = services.getTenant();
                TenantRepoFactory trf = trfh.getTenantRepoFactory();
                RegistrationRepository regRepo = trf.getRegistrationRepo();
                reg.setUser(user);
                Application application = services.findApplication(trf, env.getProperty("applicationId"));
                reg.setApplication(application);
                reg.setRegistrationId("reg"+System.currentTimeMillis()) ;
                reg = regRepo.create(reg);
                return reg;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return null;
    }

    @PostMapping("/CreateRegistrationChallenge")
    public RegistrationChallenge CreateRegistrationChallenge(@RequestBody RegistrationHelper rHelper) throws IOException, ClientInitializationException, IdxRestException {
        
        Registration registration = services.findRegistration(rHelper.getUser(), rHelper.getRegistrationId());
        if (registration != null) {
            RegistrationChallenge regChallenge =  services.addRegistrationChallenge(registration);
            return regChallenge;
        }
        return null;
    }


    @PostMapping("/AddSelfieImage")
    public FaceDataSampleCollection AddSelfieImage(@RequestBody FormatData formatData) {
        FaceDataSampleCollection faceDataSampleCollection = new FaceDataSampleCollection();
        try {
            User user = services.findUser(formatData.getIdUser());
            byte[] photo = Base64.decodeBase64(formatData.getData().getBytes());

            FaceDataSample faceDataSimple = new FaceDataSample();
            faceDataSimple.setFormat(ImageType.JPG);
            faceDataSimple.setData(photo);
            TenantRepoFactoryHelper trfh = services.getTenant();
            TenantRepoFactory trf = trfh.getTenantRepoFactory();
            UserRepository userRepository = trf.getUserRepo();
            faceDataSampleCollection = userRepository.addData(user, faceDataSimple);

            System.out.println("update user ");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return faceDataSampleCollection;
    }
    
    
    
    @GetMapping("/createAuthRequest")
    public AuthenticationRequest createAuthRequest() throws IdxRestException, IOException, ClientInitializationException {
        logger.info("Start createAuthRequest");
        return services.createAuthRequest();
    }
    
    @PostMapping("/createAuthRequestForUser")
    public AuthenticationRequestHelper createAuthRequestforUser(@RequestBody UserId userId) throws IdxRestException, IOException, ClientInitializationException {
        logger.info("Start createAuthRequest for User");
        return services.createAuthRequestForUser(userId.getUserId());
    }
    
    
    @PostMapping("/UpdateTheAuthenticationRequest")
    public AuthenticationRequestHelper UpdateTheAuthenticationRequest(@RequestBody FormatData2 formatData) throws IdxRestException, IOException, ClientInitializationException {
        logger.info("Start UpdateTheAuthenticationRequest");
        return services.UpdateTheAuthenticationRequest(formatData);
    }
    
    
    
    
    
    
/*
    @PostMapping("/AddSelfieImage3")
    public DataSampleCollection AddSelfieImage3(@RequestBody FormatData formatData) {
        DataSampleCollection dataSampleCollection = new DataSampleCollection();
        try {
            User user = services.findUser(formatData.getIdUser());
            byte[] photo = Base64.decodeBase64(formatData.getData().getBytes());

            FaceData faceData = new FaceData();
            faceData.setImageType(ImageType.JPG);

            DataHolder dataHolder = new DataHolder();
            dataHolder.setValue(photo);
            faceData.setSensitiveData(dataHolder);
            DataSample dataSample = new DataSample();
            dataSample.setUpdated(new Date());
            dataSample.setData(dataHolder);
            dataSample.setType(DataSampleTypeEnum.Face);
            TenantRepoFactoryHelper trfh = services.getTenant();
            TenantRepoFactory trf = trfh.getTenantRepoFactory();
            UserRepository userRepository = trf.getUserRepo();
            dataSampleCollection = userRepository.addData(user, dataSample);
            System.out.println("update user 3");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSampleCollection;
    }

    @PostMapping("/AddSelfieImage2")
    public void AddSelfieImage2(@RequestBody FormatData formatData) {

        try {
            User user = services.findUser(formatData.getIdUser());

            FaceData facedata = new FaceData();
            facedata.setImageType(ImageType.JPG);
            DataHolder dataHolder = new DataHolder();
            byte[] photo = Base64.decodeBase64(formatData.getData().getBytes());
            dataHolder.setValue(photo);
            facedata.setSensitiveData(dataHolder);

            user.setFace(facedata);

            TenantRepoFactoryHelper trfh = services.getTenant();
            TenantRepoFactory trf = trfh.getTenantRepoFactory();
            UserRepository userRepo = trf.getUserRepo();
            FaceDataSample faceDataSimple = new FaceDataSample();
            faceDataSimple.setFormat(ImageType.JPG);
            faceDataSimple.setData(photo);

            // userRepo.addData(user,faceDataSimple);
            userRepo.update(user);
            System.out.println("update user 2");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

}
