package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.customer.*;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerLoginRseponse;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.POST, path = "/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> signin(final String authorization) throws AuthenticationFailedException {
        String[] decodedArray;
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            decodedArray = decodedText.split(":");
        }catch (Exception e)
        {
            throw  new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }
        CustomerLoginRseponse customerLoginRseponse = customerService.authenticate(decodedArray[0],decodedArray[1]);
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
        UserAuthTokenEntity userAuthTokenEntity= customerService.signout(accessToken);
        if(userAuthTokenEntity==null)
            throw new AuthorizationFailedException("SGR-001","Customer is not Logged in.");




        LogoutResponse userResponse = new LogoutResponse().id(userAuthTokenEntity.getUuid()).message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(userResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signupCustomerRequest(final SignupCustomerRequest signupUserRequest) throws SignUpRestrictedException {
        final Customers customer = new Customers();
        customer.setUuid(UUID.randomUUID().toString());
        customer.setFirstName(signupUserRequest.getFirstName());
        customer.setLastName(signupUserRequest.getLastName());
        customer.setEmailAddress(signupUserRequest.getEmailAddress());
        customer.setPassword(signupUserRequest.getPassword());
        customer.setContactNumber(signupUserRequest.getContactNumber());
        customer.setSalt("1234abc");
        final Customers createdUsers = customerService.signup(customer);
        SignupCustomerResponse userResponse = new SignupCustomerResponse().id(createdUsers.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(userResponse, HttpStatus.CREATED);
    }


    @RequestMapping(method = RequestMethod.PUT, path = "/", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateQuestion(@RequestParam String accesstoken,UpdateCustomerRequest updateCustomerRequest) throws UpdateCustomerException, AuthorizationFailedException {
        final Customers customer = new Customers();
        customer.setFirstName(updateCustomerRequest.getFirstName());
        customer.setLastName(updateCustomerRequest.getLastName());
        Customers customerUpdated= customerService.edit( accesstoken.replace("Bearer ",""),customer);
        UpdateCustomerResponse updateCustomerResponse=new UpdateCustomerResponse();
        updateCustomerResponse.setFirstName(customerUpdated.getFirstName());
        updateCustomerResponse.setLastName(customerUpdated.getLastName());
        updateCustomerResponse.setId(customerUpdated.getUuid()+"");
        updateCustomerResponse.setStatus("200");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/password", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(@RequestParam String accesstoken,UpdatePasswordRequest updatePasswordRequest) throws UpdateCustomerException, AuthorizationFailedException {


        Customers customerUpdated = customerService.updatePassword(accesstoken.replace("Bearer ", ""), updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword());

        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse();
        updatePasswordResponse.setId(customerUpdated.getUuid());
        updatePasswordResponse.setStatus("200");
        return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}
