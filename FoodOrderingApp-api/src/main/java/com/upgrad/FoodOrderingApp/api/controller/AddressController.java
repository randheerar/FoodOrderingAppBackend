package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.customer.SaveAddresService;
import com.upgrad.FoodOrderingApp.service.businness.customer.UserAdminBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    UserAdminBusinessService userAdminBusinessService;

    @Autowired
    SaveAddresService saveAddresService;

    @RequestMapping(method = RequestMethod.POST, path = "/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> SaveAddressRequest(@RequestParam String accesstoken, final SaveAddressRequest saveAddressRequest) throws AddressNotFoundException,AuthorizationFailedException {


        UserAuthTokenEntity userAuthTokenEntity=userAdminBusinessService.checkAccessToken(accesstoken);




        final Address address = new Address();
        address.setUuid(UUID.randomUUID().toString());
        address.setActive(1);
        address.setCity(saveAddressRequest.getCity());
        address.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
        address.setLocality(saveAddressRequest.getLocality());
        address.setPincode(saveAddressRequest.getPincode());
        try {
            address.setStateUuid(Integer.parseInt(saveAddressRequest.getStateUuid()));
        }catch (Exception e)
        {
            throw  new AddressNotFoundException("ANF-002","No state by this id");

        }

        final Address address_created=saveAddresService.saveAddress(address);


        CustomerAddress customerAddress=new CustomerAddress();
        customerAddress.setAddress_id(address_created.getId());
        customerAddress.setCustomer_id(userAuthTokenEntity.getUser_id());
        saveAddresService.saveCustomerAddressRelation(customerAddress);

        SaveAddressResponse userResponse = new SaveAddressResponse().id(address_created.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");


        return new ResponseEntity<SaveAddressResponse>(userResponse, HttpStatus.CREATED);
    }



}
