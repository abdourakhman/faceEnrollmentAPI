/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.controllers;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.api.daon.models.AuthenticationRequestHelper;
import com.api.daon.models.EvaluationsResult;
import com.api.daon.models.ImageToCompare;
import com.api.daon.models.Response;
import com.api.daon.models.Statut;
import com.api.daon.models.TenantRepoFactoryHelper;
import com.api.daon.models.UserHelper;
import com.api.daon.services.Services;
import com.daon.identityx.rest.model.pojo.Application;
import com.daon.identityx.rest.model.pojo.AuthenticationAttempt;
import com.daon.identityx.rest.model.pojo.AuthenticationAttemptItem;
import com.daon.identityx.rest.model.pojo.AuthenticationRequest;
import com.daon.identityx.rest.model.pojo.DataSample;
import com.daon.identityx.rest.model.pojo.DataSampleFormatEnum;
import com.daon.identityx.rest.model.pojo.DataSampleTypeEnum;
import com.daon.identityx.rest.model.pojo.FaceDataSample;
import com.daon.identityx.rest.model.pojo.Policy;
import com.daon.identityx.rest.model.pojo.Registration;
import com.daon.identityx.rest.model.support.DataHolder;
import com.daon.identityx.rest.model.support.ImageType;
import com.identityx.clientSDK.TenantRepoFactory;
import com.identityx.clientSDK.collections.FaceDataSampleCollection;
import com.identityx.clientSDK.exceptions.IdxRestException;
import com.identityx.clientSDK.repositories.AuthenticationRequestRepository;
import com.identityx.clientSDK.repositories.RegistrationRepository;
import com.identityx.clientSDK.repositories.UserRepository;

/**
 *
 * @author GEMADEC
 */
@RestController
public class ApiController {

	@Autowired
	Services services;

	Logger logger = LoggerFactory.getLogger("ApiController");
	
     
     @Autowired
 	private Environment env;

	@PostMapping("/CompareImage")
	public Response CompareImage(@RequestBody ImageToCompare imageToCompare) {
		logger.info("Starting geting Authentication");
		AuthenticationRequestHelper authenticationRequestHelper = new AuthenticationRequestHelper();
		Response response = new Response();
		if (imageToCompare.getId() == null)
			response.setMessage("Parametre UserId introuvable");
		if (imageToCompare.getSelfie() == null)
			response.setMessage("Parametre Selfie introuvable");
		if (imageToCompare.getIdentityPhoto() == null)
			response.setMessage("Parametre IdentityPhoto introuvable");
		if (response.getMessage() == null) {
			logger.info("Starting geting Authentication for user " + imageToCompare.getId());
			// etape 1
			logger.info("etape 1");
			try {
				TenantRepoFactoryHelper trfh = services.getTenant();
				TenantRepoFactory trf = trfh.getTenantRepoFactory();

				UserHelper userHelper = services.CreateUserv2(imageToCompare.getId(), trf.getUserRepo());

				if (userHelper.getUser() != null && userHelper.getStatut().equals(Statut.Success)) {
					Registration reg = new Registration();
					logger.info("etape 2");

					RegistrationRepository regRepo = trf.getRegistrationRepo();
					reg.setUser(userHelper.getUser());
					Application application = services.findApplication(trf, env.getProperty("applicationId"));
					reg.setApplication(application);
					reg.setRegistrationId(imageToCompare.getId());
					regRepo.create(reg);

					logger.info("etape 3");
					byte[] photo = Base64.decodeBase64(imageToCompare.getSelfie().getBytes());

					FaceDataSample faceDataSimple = new FaceDataSample();
					faceDataSimple.setFormat(ImageType.JPG);
					faceDataSimple.setData(photo);
					UserRepository userRepository = trf.getUserRepo();
					FaceDataSampleCollection facedata = userRepository.addData(userHelper.getUser(), faceDataSimple);

					EvaluationsResult resulat = services.evoluation(facedata);

					if (resulat.isEtat()) {

						logger.info("etape 4");
						String authPolicyId = env.getProperty("policy");
						String appId = env.getProperty("applicationId");
						Application app = services.findApplication(trf, appId);

						Policy policy = services.findPolicy(trf, authPolicyId, app);
						AuthenticationRequest request = new AuthenticationRequest();
						request.setPolicy(policy);
						request.setApplication(app);
						request.setDescription("Onboarding Authentication for " + imageToCompare.getId());
						request.setType("RA");
						request.setUser(userHelper.getUser());
						AuthenticationRequestRepository authenticationRequestRepo = trf.getAuthenticationRequestRepo();
						request = authenticationRequestRepo.create(request);
						authenticationRequestHelper.setAuthenticationRequest(request);
						authenticationRequestHelper.setMessage("success");

						logger.info("etape 5");
						byte[] photo2 = Base64.decodeBase64(imageToCompare.getIdentityPhoto().getBytes());

						DataHolder dataHolder = new DataHolder();
						dataHolder.setValue(photo2);

						DataSample dataSample = new DataSample();
						dataSample.setType(DataSampleTypeEnum.Face);
						dataSample.setFormat(DataSampleFormatEnum.JPG);
						dataSample.setData(dataHolder);
						AuthenticationAttemptItem authenticationAttemptItem = new AuthenticationAttemptItem();
						authenticationAttemptItem.setDataSample(dataSample);

						AuthenticationAttempt authenticationAttempt = new AuthenticationAttempt();
						authenticationAttempt.setItems(new AuthenticationAttemptItem[] { authenticationAttemptItem });
						request.setAuthenticationAttempts(new AuthenticationAttempt[] { authenticationAttempt });
						request.setComplete(Boolean.TRUE);
						request = authenticationRequestRepo.update(request);
						AuthenticationAttempt auAttempt = request.getAuthenticationAttempts()[0];

						AuthenticationAttemptItem AuthenticationAttemptItem = auAttempt.getItems()[0];

						if (AuthenticationAttemptItem.getResult().equals("MATCH")) {
							response.setIsverifyed(true);

						} else {
							response.setIsverifyed(false);
						}
						response.setMessageAndScoreAndCode(AuthenticationAttemptItem.getReason(),
								AuthenticationAttemptItem.getScore(), resulat.getCode());

					} else {
						response.setMessageAndCode(resulat.getMessage(), resulat.getCode());
					}

				} else {

					response.setMessageAndCode(userHelper.getMessage(), 999);
				}

			} catch (IdxRestException ex) {
				String error = "An exception occurred while attempting to create an auth request." + "  Exception: "
						+ ex.getMessage();
				logger.error(error, ex);
				response.setMessageAndCode(ex.getMessage(), ex.getCode());
			}

		}
		return response;
	}

	@PostMapping("/CompareImageV2")
	public Response CompareImageV2(@RequestBody ImageToCompare imageToCompare) {
		logger.info("Starting geting Authentication");
		//AuthenticationRequestHelper authenticationRequestHelper = new AuthenticationRequestHelper();
		Response response = new Response();
		if (imageToCompare.getId() == null)
			response.setMessage("Parametre UserId introuvable");
		if (imageToCompare.getSelfie() == null)
			response.setMessage("Parametre Selfie introuvable");
		if (imageToCompare.getIdentityPhoto() == null)
			response.setMessage("Parametre IdentityPhoto introuvable");
		if (response.getMessage() == null) {
			logger.info("Starting geting Authentication for user " + imageToCompare.getId());
			// etape 1
			logger.info("etape 1");
			try {
				TenantRepoFactoryHelper trfh = services.getTenant();
				TenantRepoFactory trf = trfh.getTenantRepoFactory();

				UserHelper userHelper = services.CreateUserv3(imageToCompare.getId(), trf.getUserRepo());
				//User userdd = userHelper.getUser();
				if (userHelper.getUser() != null /* && userHelper.getStatut().equals(Statut.Success) */) {

					// User user
					logger.info("etape 2");
					RegistrationRepository regRepo = trf.getRegistrationRepo();
					// Registration ExistingReg = regRepo.getById(imageToCompare.getId());
					//Registration ExistingReg = regRepo.get(userHelper.getUser().getRegistrations().getHref());
					Registration reg = new Registration();
					if (userHelper.getStatut() == Statut.Success) {

						reg.setUser(userHelper.getUser());
						Application application = services.findApplication(trf,
								env.getProperty("applicationId"));
						reg.setApplication(application);
						reg.setRegistrationId(imageToCompare.getId());
						regRepo.create(reg);

					}/* else {
						reg = ExistingReg;
					}*/

					logger.info("etape 3");
					byte[] photo = Base64.decodeBase64(imageToCompare.getSelfie().getBytes());
					logger.info("etape 3.a");
					FaceDataSample faceDataSimple = new FaceDataSample();
					logger.info("etape 3.b");
					faceDataSimple.setFormat(ImageType.JPG);
					faceDataSimple.setData(photo);
					logger.info("etape 3.c");
					UserRepository userRepository = trf.getUserRepo();
					logger.info("etape 3.d");
					FaceDataSampleCollection facedata = userRepository.addData(userHelper.getUser(), faceDataSimple);
					logger.info("etape 3.e");
					EvaluationsResult resulat = services.evoluation(facedata);

					if (resulat.isEtat()) {

						logger.info("etape 4");
						String authPolicyId = env.getProperty("policy");
						String appId = env.getProperty("applicationId");
						Application app = services.findApplication(trf, appId);

						Policy policy = services.findPolicy(trf, authPolicyId, app);
						AuthenticationRequest request = new AuthenticationRequest();
						request.setPolicy(policy);
						request.setApplication(app);
						request.setDescription("Onboarding Authentication for " + imageToCompare.getId());
						request.setType("RA");
						request.setUser(userHelper.getUser());
						AuthenticationRequestRepository authenticationRequestRepo = trf.getAuthenticationRequestRepo();
						request = authenticationRequestRepo.create(request);
						//authenticationRequestHelper.setAuthenticationRequest(request);
						//authenticationRequestHelper.setMessage("success");

						logger.info("etape 5");
						byte[] photo2 = Base64.decodeBase64(imageToCompare.getIdentityPhoto().getBytes());

						DataHolder dataHolder = new DataHolder();
						dataHolder.setValue(photo2);

						DataSample dataSample = new DataSample();
						dataSample.setType(DataSampleTypeEnum.Face);
						dataSample.setFormat(DataSampleFormatEnum.JPG);
						dataSample.setData(dataHolder);
						AuthenticationAttemptItem authenticationAttemptItem = new AuthenticationAttemptItem();
						authenticationAttemptItem.setDataSample(dataSample);

						AuthenticationAttempt authenticationAttempt = new AuthenticationAttempt();
						authenticationAttempt.setItems(new AuthenticationAttemptItem[] { authenticationAttemptItem });
						request.setAuthenticationAttempts(new AuthenticationAttempt[] { authenticationAttempt });
						request.setComplete(Boolean.TRUE);
						request = authenticationRequestRepo.update(request);
						AuthenticationAttempt auAttempt = request.getAuthenticationAttempts()[0];

						AuthenticationAttemptItem AuthenticationAttemptItem = auAttempt.getItems()[0];

						Boolean daon_decision = Boolean.parseBoolean(env.getProperty("daon_decision"));
						Double threshold_score = Double.parseDouble(env.getProperty("threshold_score"));

						if (daon_decision.equals(true)) {
							if (AuthenticationAttemptItem.getResult().equals("MATCH")) {
								response.setIsverifyed(true);

							} else {
								response.setIsverifyed(false);
							}

						} else {

							if (AuthenticationAttemptItem.getScore() <= threshold_score) {
								response.setIsverifyed(true);
							} else {
								response.setIsverifyed(false);
							}

						}
						response.setMessageAndScoreAndCode(AuthenticationAttemptItem.getReason(),
								AuthenticationAttemptItem.getScore(), resulat.getCode());

					} else {
						response.setMessageAndCode(resulat.getMessage(), resulat.getCode());
					}

				} else {

					response.setMessageAndCode(userHelper.getMessage(), 999);
				}

			} catch (IdxRestException ex) {
				String error = "An exception occurred while attempting to create an auth request." + "  Exception: "
						+ ex.getMessage();
				logger.error(error, ex);
				response.setMessageAndCode(ex.getMessage(), ex.getCode());
			}

		}
		return response;
	}
}
