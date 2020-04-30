package com.upgrad.FoodOrderingApp.api.controller.customer;


import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.customer.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class SignupController {
    @Autowired
    private SignupBusinessService signupBusinessService;



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
