/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.api.daon.services;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.api.daon.models.AuthenticationRequestHelper;
import com.api.daon.models.EvaluationsResult;
import com.api.daon.models.FormatData2;
import com.api.daon.models.Statut;
import com.api.daon.models.TenantRepoFactoryHelper;
import com.api.daon.models.UserHelper;
import com.daon.identityx.rest.model.def.PolicyStatusEnum;
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
import com.daon.identityx.rest.model.pojo.RegistrationChallenge;
import com.daon.identityx.rest.model.pojo.User;
import com.daon.identityx.rest.model.support.DataHolder;
import com.daon.identityx.rest.model.support.DataSampleEvaluation;
import com.identityx.clientSDK.TenantRepoFactory;
import com.identityx.clientSDK.collections.ApplicationCollection;
import com.identityx.clientSDK.collections.FaceDataSampleCollection;
import com.identityx.clientSDK.collections.PolicyCollection;
import com.identityx.clientSDK.collections.RegistrationCollection;
import com.identityx.clientSDK.collections.UserCollection;
import com.identityx.clientSDK.credentialsProviders.EncryptedKeyPropFileCredentialsProvider;
import com.identityx.clientSDK.def.ICredentialsProvider;
import com.identityx.clientSDK.exceptions.ClientInitializationException;
import com.identityx.clientSDK.exceptions.IdxRestException;
import com.identityx.clientSDK.queryHolders.ApplicationQueryHolder;
import com.identityx.clientSDK.queryHolders.PolicyQueryHolder;
import com.identityx.clientSDK.queryHolders.RegistrationQueryHolder;
import com.identityx.clientSDK.queryHolders.UserQueryHolder;
import com.identityx.clientSDK.repositories.ApplicationRepository;
import com.identityx.clientSDK.repositories.AuthenticationRequestRepository;
import com.identityx.clientSDK.repositories.PolicyRepository;
import com.identityx.clientSDK.repositories.RegistrationChallengeRepository;
import com.identityx.clientSDK.repositories.RegistrationRepository;
import com.identityx.clientSDK.repositories.UserRepository;

/**
 *
 * @author GEMADEC
 */
@Service
public class Services {

	final ResourceLoader resourceLoader;
	private final Environment env;

	Logger logger = LoggerFactory.getLogger("Services");

    Services(Environment env, ResourceLoader resourceLoader) {
        this.env = env;
        this.resourceLoader = resourceLoader;
    }

	public User CreateUser(String userId) {
		logger.info("Starting CreateUser Id : " + userId);
		User aUser = new User();
		try {
			UserRepository userRepo = this.getTenant().getTenantRepoFactory().getUserRepo();
			aUser.setUserId(userId);
			aUser = userRepo.create(aUser);
			logger.info("User " + userId + " created");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ERROR on creation User :" + userId);
		}
		logger.info("End creation User : " + userId);
		return aUser;
	}

	public UserHelper CreateUserv2(String userId, UserRepository userRepo) {
		logger.info("Starting CreateUser Id : " + userId);
		UserHelper helperUser = this.findUserV2(userId, userRepo);

		if (helperUser.getUser() == null) {
			User aUser = new User();
			try {
				aUser.setUserId(userId);
				aUser = userRepo.create(aUser);
				helperUser.setStatutAndMessageAndUser("User " + userId + " created", aUser, Statut.Success);
				logger.info("User " + userId + " created");
			} catch (Exception e) {
				helperUser.setMessage("ERROR on creation User :" + userId);
				e.printStackTrace();
				logger.error("ERROR on creation User :" + userId);
			}
		}
		logger.info("End creation User : " + userId);
		return helperUser;
	}
	
	
	public UserHelper CreateUserv3(String userId, UserRepository userRepo) {
		logger.info("Starting CreateUser Id : " + userId);
		UserHelper helperUser = this.findUserV3(userId, userRepo);

		if (helperUser.getUser() == null) {
			User aUser = new User();
			try {

				aUser.setUserId(userId);
				aUser = userRepo.create(aUser);
				helperUser.setStatutAndMessageAndUser("User " + userId + " created", aUser, Statut.Success);
				logger.info("User " + userId + " created");
			} catch (Exception e) {
				helperUser.setMessage("ERROR on creation User :" + userId);
				e.printStackTrace();
				logger.error("ERROR on creation User :" + userId);
			}
		}
		logger.info("End creation User : " + userId);
		return helperUser;
	}


	public TenantRepoFactoryHelper getTenant() {

		TenantRepoFactoryHelper trfh = new TenantRepoFactoryHelper();
		logger.info("Starting get Tenant");
		try {
			//Resource credent = resourceLoader.getResource("classpath:credential.properties");
			//InputStream credentIn = resourceLoader.getResource("classpath:credential.properties").getInputStream();

			//Resource jks = resourceLoader.getResource("classpath:IdentityXKeyWrapper.jks");
			//InputStream jksIn = resourceLoader.getResource("classpath:IdentityXKeyWrapper.jks").getInputStream();

			ICredentialsProvider tenantCredentialProvider = new EncryptedKeyPropFileCredentialsProvider(
				resourceLoader.getResource("classpath:IdentityXKeyWrapper.jks").getInputStream(),
				env.getProperty("jkspass"),
				resourceLoader.getResource("classpath:credential.properties").getInputStream(), 
				"identityxCert", 
				env.getProperty("jkspass"));

			TenantRepoFactory trf = new TenantRepoFactory(tenantCredentialProvider);
			trfh.setTenantRepoFactory(trf);
			logger.info("Get Tenant success");
			return trfh;

		} catch (Exception e) {
			logger.info("ERROR on get Tenant");
			trfh.setMessage("ERROR on get Tenant");
			e.printStackTrace();

		}
		logger.info("End get Tenant");
		return trfh;

	}

	public Application findApplication(TenantRepoFactory trf, String appId) {

		try {
			logger.info("Starting findApplication");
			ApplicationRepository applicationRepo = trf.getApplicationRepo();
			ApplicationQueryHolder holder = new ApplicationQueryHolder();
			holder.getSearchSpec().setApplicationId(appId);
			ApplicationCollection applicationCollection = applicationRepo.list(holder);

			switch (applicationCollection.getItems().length) {
			case 0:
				logger.error("Could not find an application with the ApplicationId: " + appId);
				return null;
			case 1:
				logger.info("Application found");
				return applicationCollection.getItems()[0];
			default:
				logger.error("More than one application with the same ApplicationId!!!!");
				return null;

			}

		} catch (Exception e) {
			logger.error("ERROR On get Application :" + e);
		}
		return null;
	}

	public User findUser(String userId) throws IOException, ClientInitializationException, IdxRestException {
		TenantRepoFactoryHelper trfh = this.getTenant();
		TenantRepoFactory trf = trfh.getTenantRepoFactory();
		UserRepository userRepo = trf.getUserRepo();
		UserQueryHolder holder = new UserQueryHolder();
		holder.getSearchSpec().setUserId(userId);

		UserCollection userCollection = userRepo.list(holder);
		switch (userCollection.getItems().length) {

		case 0:
			return null;
		case 1:
			logger.info(userId + " found");
			return userCollection.getItems()[0];
		default:
			throw new RuntimeException("More than one user with the same UserId!");

		}

	}

	public UserHelper findUserV2(String userId, UserRepository userRepo) {
		UserQueryHolder holder = new UserQueryHolder();
		UserHelper userHelper = new UserHelper();
		holder.getSearchSpec().setUserId(userId);

		UserCollection userCollection;
		try {
			userCollection = userRepo.list(holder);
			switch (userCollection.getItems().length) {

			case 0:
				userHelper.setStatut(Statut.Success);
				return userHelper;
			case 1:
				userHelper.setStatutAndMessageAndUser("Un utilisateur existe avec le meme Id :" + userId,userCollection.getItems()[0], Statut.Faild);
				return userHelper;
			default:
				userHelper.setStatutAndMessage("More than one user with the same UserId : ", Statut.Error);
				return userHelper;

			}
		} catch (IdxRestException e) {
			e.printStackTrace();
		}

		return userHelper;
	}
	
	public UserHelper findUserV3(String userId, UserRepository userRepo) {
		UserQueryHolder holder = new UserQueryHolder();
		UserHelper userHelper = new UserHelper();
		holder.getSearchSpec().setUserId(userId);

		UserCollection userCollection;
		try {
		
			userCollection = userRepo.list(holder);
			switch (userCollection.getItems().length) {

			case 0:
				userHelper.setStatut(Statut.Success);
				return userHelper;
			case 1:
				userHelper.setStatutAndMessageAndUser("Un utilisateur existe avec le meme Id :" + userId,userCollection.getItems()[0], Statut.Faild);
				return userHelper;
			default:
				userHelper.setStatutAndMessage("More than one user with the same UserId : ", Statut.Error);
				return userHelper;

			}
		} catch (IdxRestException e) {
			e.printStackTrace();
		}

		return userHelper;
	}

	public AuthenticationRequest createAuthRequest()
			throws IdxRestException, IOException, ClientInitializationException {
		logger.info("Start createAuthRequest ");
		//String authPolicyId = ;
		TenantRepoFactoryHelper trfh = this.getTenant();
		TenantRepoFactory trf = trfh.getTenantRepoFactory();

		//String appId = this.getParamFromProp("applicationId");
		Application app = this.findApplication(trf, env.getProperty("applicationId"));
		
		if(app!=null) {
			
			try {

				Policy policy = this.findPolicy(trf, env.getProperty("policy"), app);
				AuthenticationRequest request = new AuthenticationRequest();
				request.setPolicy(policy);
				request.setApplication(app);
				request.setDescription("Test transaction");
				request.setType("FI");
				AuthenticationRequestRepository authenticationRequestRepo = trf.getAuthenticationRequestRepo();
				request = authenticationRequestRepo.create(request);
				return request;
			} catch (IdxRestException ex) {
				String error = "An exception occurred while attempting to create an auth request." + "  Exception: "
						+ ex.getMessage();
				logger.error(error, ex);
				throw new RuntimeException(error, ex);
			}
			
		}
		return null;

	
	}

	public Registration findRegistration(User user, String registrationId) throws IdxRestException {

		RegistrationRepository regRepo = this.getTenant().getTenantRepoFactory().getRegistrationRepo();
		RegistrationQueryHolder holder = new RegistrationQueryHolder();
		holder.getSearchSpec().setRegistrationId(registrationId);
		RegistrationCollection registrationCollection = regRepo.list(user.getRegistrations().getHref(), holder);
		switch (registrationCollection.getItems().length) {
			case 0:
				return null;
			case 1:
				logger.info("Registration " + registrationId + " found");
				return registrationCollection.getItems()[0];
			default:
				throw new RuntimeException("More than one registration with the same RegistrationId!");
		}
	}

	public Policy findPolicy(TenantRepoFactory trf, String aPolicyId, Application app) {

		try {
			logger.info("Starting findPolicy");
			PolicyQueryHolder holder = new PolicyQueryHolder();
			holder.getSearchSpec().setPolicyId(aPolicyId);
			holder.getSearchSpec().setStatus(PolicyStatusEnum.ACTIVE);
			PolicyRepository policyRepo = trf.getPolicyRepo();
			PolicyCollection policyCollection = policyRepo.list(app.getPolicies().getHref(), holder);
			switch (policyCollection.getItems().length) {
			case 0:
				logger.info("Could not find an active policy with the PolicyId: " + aPolicyId);
				throw new RuntimeException("Could not find an active policy with the PolicyId: " + aPolicyId);
			case 1:
				logger.info("Policy found");
				return policyCollection.getItems()[0];
			default:
				logger.info("There is more than one active policy with the name: " + aPolicyId);
				throw new RuntimeException("There is more than one active policy with the name: " + aPolicyId);
			}

		} catch (Exception e) {
			logger.error("ERROR On findPolicy :\n" + e);
		}
		return null;
	}

	public AuthenticationRequestHelper createAuthRequestForUser(String userId)
			throws IdxRestException, IOException, ClientInitializationException {
		logger.info("Start createAuthRequest for user");
		//String authPolicyId = this.getParamFromProp("policy");
		TenantRepoFactoryHelper trfh = this.getTenant();
		TenantRepoFactory trf = trfh.getTenantRepoFactory();
		AuthenticationRequestHelper authenticationRequestHelper = new AuthenticationRequestHelper();
		//String appId = this.getParamFromProp("applicationId");
		Application app = this.findApplication(trf, env.getProperty("applicationId"));

		User user = this.findUser(userId);
		if (user != null) {
			try {

				Policy policy = this.findPolicy(trf, env.getProperty("policy"), app);
				AuthenticationRequest request = new AuthenticationRequest();
				request.setPolicy(policy);
				request.setApplication(app);
				request.setDescription("OnboardingAuthentication for " + userId);
				request.setType("RA");
				request.setUser(user);
				AuthenticationRequestRepository authenticationRequestRepo = trf.getAuthenticationRequestRepo();
				request = authenticationRequestRepo.create(request);
				authenticationRequestHelper.setAuthenticationRequest(request);
				authenticationRequestHelper.setMessage("success");
				return authenticationRequestHelper;
			} catch (IdxRestException ex) {
				String error = "An exception occurred while attempting to create an auth request." + "  Exception: "
						+ ex.getMessage();
				logger.error(error, ex);
				throw new RuntimeException(error, ex);
			}

		} else {
			authenticationRequestHelper.setMessage("Faild - Utilisateur introuvable");
			return authenticationRequestHelper;
		}
	}

	public AuthenticationRequestHelper UpdateTheAuthenticationRequest(FormatData2 formatData)
			throws IdxRestException, IOException, ClientInitializationException {
		logger.info("Start createAuthRequest for user");

		TenantRepoFactoryHelper trfh = this.getTenant();
		TenantRepoFactory trf = trfh.getTenantRepoFactory();
		AuthenticationRequestHelper authenticationRequestHelper = new AuthenticationRequestHelper();

		User user = this.findUser(formatData.getIdUser());
		if (user != null) {
			try {

				byte[] photo = Base64.decodeBase64(formatData.getData().getBytes());

				DataHolder dataHolder = new DataHolder();
				dataHolder.setValue(photo);

				AuthenticationRequest request = new AuthenticationRequest();

				DataSample dataSample = new DataSample();
				dataSample.setType(DataSampleTypeEnum.Face);
				dataSample.setFormat(DataSampleFormatEnum.JPG);
				dataSample.setData(dataHolder);
				AuthenticationAttemptItem authenticationAttemptItem = new AuthenticationAttemptItem();
				// authenticationAttemptItem.setFaceData(new FaceData[]{faceData});
				authenticationAttemptItem.setDataSample(dataSample);

				AuthenticationAttempt authenticationAttempt = new AuthenticationAttempt();
				authenticationAttempt.setItems(new AuthenticationAttemptItem[] { authenticationAttemptItem });

				AuthenticationRequestRepository authenticationRequestRepo = trf.getAuthenticationRequestRepo();
				request = authenticationRequestRepo.getById(formatData.getIdRegistration());
				System.out.println(formatData.getIdRegistration());

				request.setAuthenticationAttempts(new AuthenticationAttempt[] { authenticationAttempt });
				request.setComplete(Boolean.TRUE);
				request = authenticationRequestRepo.update(request);

				authenticationRequestHelper.setAuthenticationRequest(request);

				authenticationRequestHelper.setMessage("success");
				return authenticationRequestHelper;
			} catch (IdxRestException ex) {
				String error = "An exception occurred while attempting to create an auth request." + "  Exception: "
						+ ex.getMessage();
				logger.error(error, ex);
				throw new RuntimeException(error, ex);
			}

		} else {
			authenticationRequestHelper.setMessage("Faild - Utilisateur introuvable");
			return authenticationRequestHelper;
		}
	}

	public EvaluationsResult evoluation(FaceDataSampleCollection resultat) {
		logger.info("Start Evoluation");
		EvaluationsResult response = new EvaluationsResult();

		if (resultat.getItems().length > 0) {
			response.setEtatAndCodeAndMessage(true, 0, "OK");
			FaceDataSample res = resultat.getItems()[0];
			DataSampleEvaluation eva = res.getEvaluations()[0];
			if (eva.getResultCode() != 0) {
				response.setEtatAndCodeAndMessage(false, eva.getResultCode(), eva.getResultMessage());
			}
		}
		logger.info("End Evoluation with code error " + response.getCode());
		return response;
	}

	public RegistrationChallenge addRegistrationChallenge(Registration reg) throws IdxRestException {
		TenantRepoFactory trf = this.getTenant().getTenantRepoFactory();
		RegistrationChallengeRepository regChallengeRepository = trf.getRegistrationChallengeRepo();
		RegistrationChallenge regChallenge = new RegistrationChallenge();
		logger.info("creating registration challenge");
		Application app = this.findApplication(trf, env.getProperty("applicationId"));
		Policy policy = this.findPolicy(trf, env.getProperty("fido.reg_policy_id"),app);
		regChallenge.setRegistration(reg);
		regChallenge.setPolicy(policy);
		regChallenge = regChallengeRepository.create(regChallenge);
		return regChallenge;
}
}
