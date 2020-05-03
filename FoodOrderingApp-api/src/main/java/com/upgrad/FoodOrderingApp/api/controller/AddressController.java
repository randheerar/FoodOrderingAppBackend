package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.customer.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.customer.UserAdminBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import com.upgrad.FoodOrderingApp.service.entity.customer.State;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AddressController {
    @Autowired
    UserAdminBusinessService userAdminBusinessService;

    @Autowired
    AddressService addressService;

    @RequestMapping(method = RequestMethod.POST, path = "address/", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> SaveAddressRequest(@RequestParam String accesstoken, final SaveAddressRequest saveAddressRequest) throws AddressNotFoundException, AuthorizationFailedException {


        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.checkAccessToken(accesstoken);
        final Address address = new Address();
        address.setUuid(UUID.randomUUID().toString());
        address.setActive(1);
        address.setCity(saveAddressRequest.getCity());
        address.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
        address.setLocality(saveAddressRequest.getLocality());
        address.setPincode(saveAddressRequest.getPincode());
        try {
            address.setStateUuid(Integer.parseInt(saveAddressRequest.getStateUuid()));
        } catch (Exception e) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");

        }

        final Address address_created = addressService.saveAddress(address);
        CustomerAddress customerAddress = new CustomerAddress();
        customerAddress.setAddress_id(address_created.getId());
        customerAddress.setCustomer_id(userAuthTokenEntity.getUser_id());
        addressService.saveCustomerAddressRelation(customerAddress);
        SaveAddressResponse userResponse = new SaveAddressResponse().id(address_created.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(userResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Address>> getAllAddress(@RequestParam String accessToken) throws AuthorizationFailedException {
        List<Address> addressLists = addressService.getAddressList(accessToken);
        return new ResponseEntity<List<Address>>(addressLists, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "address/{address_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable("address_id") String address_id, @RequestParam String accesstoken) throws AuthorizationFailedException, AddressNotFoundException {


        String uuid =addressService.delete(address_id,accesstoken);
        DeleteAddressResponse deleteAddressResponse=new DeleteAddressResponse();
        deleteAddressResponse.setId(UUID.fromString(uuid));
        deleteAddressResponse.setStatus("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);


    }

    @RequestMapping(method = RequestMethod.GET, path = "state", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<StatesList>> getStates()  {
        List<State> addressLists = addressService.getStateList();
        List<StatesList> statesList=new ArrayList<>();

        for(State stateitem:addressLists)
        {
            StatesList state=new StatesList();

            state.setId(UUID.fromString(stateitem.getUuid()));
            state.setStateName(stateitem.getState_name());
            statesList.add(state);
        }


        return new ResponseEntity<List<StatesList>>(statesList, HttpStatus.OK);
    }
}
