package com.upgrad.FoodOrderingApp.api.controller.customer;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.service.businness.customer.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerLoginRseponse;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.omg.CORBA.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
@RequestMapping("/customer")
public class SignInController {
    @Autowired
    private AuthenticationService authenticationService;

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
}
