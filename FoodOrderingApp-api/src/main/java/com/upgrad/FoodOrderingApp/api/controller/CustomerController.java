package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
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

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * API used to login the customer.
     *
     * @param authorization customer contactNumber and password in 'Basic Base64<contactNumber:password>' format.
     * @return ResponseEntity<LoginResponse> type object with HttpStatus as OK.
     * @throws AuthenticationFailedException if customer contactNumber or password is incorrect.
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/login",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(
            @RequestHeader("authorization") final String authorization)
            throws AuthenticationFailedException {
        String[] decodedArray;
        String username, password;
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            decodedArray = decodedText.split(":");
            username = decodedArray[0];
            password = decodedArray[1];
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException exception)
        {
            throw  new AuthenticationFailedException("ATH-003","Incorrect format of decoded customer name and password");
        }

            CustomerAuthEntity userAuthResponse = customerService.authenticate(username, password);
            LoginResponse authorizedUserResponse =  new LoginResponse();
            authorizedUserResponse.setId(userAuthResponse.getCustomer().getUuid());
            authorizedUserResponse.setMessage("LOGGED IN SUCCESSFULLY");
            authorizedUserResponse.setContactNumber(userAuthResponse.getCustomer().getContactNumber());
            authorizedUserResponse.setEmailAddress(userAuthResponse.getCustomer().getEmailAddress());
            authorizedUserResponse.setFirstName(userAuthResponse.getCustomer().getFirstName());
            authorizedUserResponse.setLastName(userAuthResponse.getCustomer().getLastName());

            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", userAuthResponse.getAccessToken());
            List<String> header = new ArrayList<>();
            headers.setAccessControlExposeHeaders(header);

            return new ResponseEntity<LoginResponse>(authorizedUserResponse, headers, HttpStatus.OK);


    }

    /**
     * API used to logout the customer.
     *
     * @param accessToken is the access token of the customer in 'access-token' format.
     * @return ResponseEntity<LogoutResponse> type object along with HttpStatus as OK.
     * @throws AuthorizationFailedException if any of the validation on customer access token fails.
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/logout",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(
            @RequestHeader("authorization") final String accessToken)
            throws AuthorizationFailedException {

        CustomerAuthEntity userAuthTokenEntity= customerService.logout(accessToken.replace("Bearer ",""));

        LogoutResponse userResponse = new LogoutResponse()
                .id(userAuthTokenEntity.getCustomer().getUuid())
                .message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(userResponse, HttpStatus.OK);
    }


    /**
     * API used to signup a new customer
     *
     * @param signupUserRequest this contains all the attributes required for creating a new customer in the database.
     * @return ResponseEntity<SignupCustomerResponse> type object along with HttpStatus CREATED.
     * @throws SignUpRestrictedException if customer enters details not meeting the requirements.
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/signup",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signUp(
            @RequestBody(required = true) final SignupCustomerRequest signupUserRequest)
            throws SignUpRestrictedException {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setFirstName(signupUserRequest.getFirstName());
        customerEntity.setLastName(signupUserRequest.getLastName());
        customerEntity.setEmailAddress(signupUserRequest.getEmailAddress());
        customerEntity.setPassword(signupUserRequest.getPassword());
        customerEntity.setContactNumber(signupUserRequest.getContactNumber());

        CustomerEntity createdUser = customerService.saveCustomer(customerEntity);

        SignupCustomerResponse userResponse =
                new SignupCustomerResponse()
                        .id(createdUser.getUuid())
                        .status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(userResponse, HttpStatus.CREATED);
    }


    /**
     * API used to update customer details.
     *
     * @param updateCustomerRequest this argument contains all the attributes required to update a
     *     customer in the database.
     * @param accessToken customer's access token in 'Bearer <access-token>' format.
     * @return ResponseEntity<UpdateCustomerResponse> type object along with HttpStatus as OK.
     * @throws AuthorizationFailedException if any validation on customer access token fails.
     * @throws UpdateCustomerException if first name is not provided in updateCustomerRequest param.
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomerDetails(
            @RequestHeader("authorization") final String accessToken,
            @RequestBody(required = true) final UpdateCustomerRequest updateCustomerRequest)
            throws UpdateCustomerException, AuthorizationFailedException {

        if (updateCustomerRequest.getFirstName() == null
                || updateCustomerRequest.getFirstName().isEmpty()) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }

        CustomerEntity customerEntity= customerService.getCustomer(accessToken.replace("Bearer ",""));
        customerEntity.setFirstName(updateCustomerRequest.getFirstName());
        customerEntity.setLastName(updateCustomerRequest.getLastName());

        CustomerEntity updatedCustomerEntity = customerService.updateCustomer(customerEntity);

        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setId(updatedCustomerEntity.getUuid());
        updateCustomerResponse.setFirstName(updatedCustomerEntity.getFirstName());
        updateCustomerResponse.setLastName(updatedCustomerEntity.getLastName());
        updateCustomerResponse.status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");

        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }

    /**
     * @param updatePasswordRequest contains all the attributes required to update a customer's password in the database
     *
     * @param accessToken customer access token in 'Bearer <access-token>' format.
     * @return ResponseEntity<UpdatePasswordResponse> type object along with HttpStatus as OK.
     * @throws AuthorizationFailedException if any of the validation on customer access token fails.
     * @throws UpdateCustomerException if old or new password fields are empty.
     */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/password",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
            @RequestHeader("authorization") String accessToken,
            @RequestBody(required = true) UpdatePasswordRequest updatePasswordRequest)
            throws UpdateCustomerException, AuthorizationFailedException {

        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();

        if (oldPassword != null && !oldPassword.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
            CustomerEntity customerEntity = customerService.getCustomer(accessToken.replace("Bearer ", ""));
            CustomerEntity customerUpdated = customerService.updateCustomerPassword(oldPassword, newPassword, customerEntity);

            UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
                    .id(customerUpdated.getUuid())
                    .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
            return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
        } else {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
    }
}
