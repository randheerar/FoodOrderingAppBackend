package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.customer.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.customer.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    CustomerService customerService;

    @Autowired
    AddressService addressService;

    @RequestMapping(
            method = RequestMethod.POST,
            path = "address/",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> SaveAddressRequest(
            @RequestParam String accessToken, final SaveAddressRequest saveAddressRequest)
            throws AddressNotFoundException, AuthorizationFailedException, SaveAddressException {

        UserAuthTokenEntity userAuthTokenEntity = customerService.checkAccessToken(accessToken);

        Customers customerEntity = customerService.getCustomer(userAuthTokenEntity.getUuid());

        final Address addressEntity = new Address();
        if (saveAddressRequest != null) {
            addressEntity.setUuid(UUID.randomUUID().toString());
            addressEntity.setCity(saveAddressRequest.getCity());
            addressEntity.setLocality(saveAddressRequest.getLocality());
            addressEntity.setPincode(saveAddressRequest.getPincode());
            addressEntity.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
            addressEntity.setActive(1);
        }
        addressEntity.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));

        final Address savedAddress = addressService.saveAddress(addressEntity, customerEntity);
        SaveAddressResponse saveAddressResponse =
                new SaveAddressResponse()
                        .id(savedAddress.getUuid())
                        .status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "address/customer",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllAddress(
            @RequestParam String accessToken)
            throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = customerService.checkAccessToken(accessToken);
        Customers customerEntity = customerService.getCustomer(userAuthTokenEntity.getUuid());

        List<Address> addressEntityList = addressService.getAddressList(customerEntity);
        final AddressListResponse addressListResponse = new AddressListResponse();

        if (!addressEntityList.isEmpty()) {
            for (Address addressEntity : addressEntityList) {
                AddressList addressResponseList =
                        new AddressList()
                                .id(UUID.fromString(addressEntity.getUuid()))
                                .flatBuildingName(addressEntity.getFlat_buil_number())
                                .city(addressEntity.getCity())
                                .pincode(addressEntity.getPincode())
                                .locality(addressEntity.getLocality())
                                .state(
                                        new AddressListState()
                                                .id(UUID.fromString(addressEntity.getState().getUuid()))
                                                .stateName(addressEntity.getState().getStateName()));
                addressListResponse.addAddressesItem(addressResponseList);
            }
        } else {
            List<AddressList> addresses = Collections.emptyList();
            addressListResponse.addresses(addresses);
        }

        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);    }


    @RequestMapping(
            method = RequestMethod.DELETE,
            path = "address/{address_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(
            @PathVariable("address_id") String address_id,
            @RequestParam String accesstoken)
            throws AuthorizationFailedException, AddressNotFoundException {


        String uuid =addressService.delete(address_id,accesstoken);
        DeleteAddressResponse deleteAddressResponse=new DeleteAddressResponse();
        deleteAddressResponse.setId(UUID.fromString(uuid));
        deleteAddressResponse.setStatus("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);


    }

    @RequestMapping(
            method = RequestMethod.GET,
            path = "state",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStates()  {
        StateEntity stateEntity = new StateEntity();
        stateEntity.setUuid(UUID.randomUUID().toString());

        List<StateEntity> statesLists = addressService.getStateList();

        StatesListResponse statesListResponse = new StatesListResponse();
        for (StateEntity statesEntity : statesLists) {
            StatesList states =
                    new StatesList()
                            .id(UUID.fromString(statesEntity.getUuid()))
                            .stateName(statesEntity.getStateName());
            statesListResponse.addStatesItem(states);
        }
        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
    }
}
