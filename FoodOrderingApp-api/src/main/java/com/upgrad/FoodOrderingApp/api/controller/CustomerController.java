package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.customer.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.customer.SignoutBusinessService;
import com.upgrad.FoodOrderingApp.service.businness.customer.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerLoginRseponse;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private SignoutBusinessService signoutBusinessService;

    @Autowired
    private SignupBusinessService signupBusinessService;





    @RequestMapping(method = RequestMethod.POST, path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> SignIn(final String authorization) throws AuthenticationFailedException {
        String[] decodedArray;
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            decodedArray = decodedText.split(":");
        }catch (Exception e)
        {
            throw  new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
        CustomerLoginRseponse customerLoginRseponse = authenticationService.authenticate(decodedArray[0],decodedArray[1]);
        LoginResponse authorizedUserResponse =  new LoginResponse();
        authorizedUserResponse.setId(customerLoginRseponse.getUUID());
        authorizedUserResponse.setMessage("LOGGED IN SUCCESSFULLY");
        authorizedUserResponse.setContactNumber(customerLoginRseponse.getContactNumber());
        authorizedUserResponse.setEmailAddress(customerLoginRseponse.getEmailAddress());
        authorizedUserResponse.setFirstName(customerLoginRseponse.getFirstName());
        authorizedUserResponse.setLastName(customerLoginRseponse.getLastName());
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerLoginRseponse.getAccess_token());
        return new ResponseEntity<LoginResponse>(authorizedUserResponse,headers, HttpStatus.OK);


    }
    @RequestMapping(method = RequestMethod.POST, path = "/logout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> signout(final String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity= signoutBusinessService.signout(accessToken);
        if(userAuthTokenEntity==null)
            throw new AuthorizationFailedException("SGR-001","Customer is not Logged in.");




        LogoutResponse userResponse = new LogoutResponse().id(userAuthTokenEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(userResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> SignupCustomerRequest(final SignupCustomerRequest signupUserRequest) throws SignUpRestrictedException {
        final Customers customer = new Customers();
        customer.setUuid(UUID.randomUUID().toString());
        customer.setFirstname(signupUserRequest.getFirstName());
        customer.setLastname(signupUserRequest.getLastName());
        customer.setEmail(signupUserRequest.getEmailAddress());
        customer.setPassword(signupUserRequest.getPassword());
        customer.setContact_number(signupUserRequest.getContactNumber());
        customer.setSalt("1234abc");


        final Customers createdUsers = signupBusinessService.signup(customer);
        SignupCustomerResponse userResponse = new SignupCustomerResponse().id(createdUsers.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(userResponse, HttpStatus.CREATED);
    }






}
