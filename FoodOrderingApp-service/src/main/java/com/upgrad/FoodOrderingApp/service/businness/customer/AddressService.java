package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.dao.Customer.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerAdressDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import com.upgrad.FoodOrderingApp.service.entity.customer.State;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {
    @Autowired
    private AddressDao addressDao;
    @Autowired
    private CustomerAdressDao customerAdressDao;
    @Autowired
    CustomerService customerService;


    public List<Address> getAddressList(String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity= customerService.checkAccessToken(accessToken);

        return addressDao.getAddress(userAuthTokenEntity.getUser_id());
    }

    public Address saveAddress(Address address) throws AuthorizationFailedException, AddressNotFoundException {

        Address addressCreated;

        try {
            addressCreated = addressDao.createAddress(address);
        } catch (Exception e) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");

        }
        return addressCreated;
    }

    public CustomerAddress saveCustomerAddressRelation(CustomerAddress customerAddress) throws AuthorizationFailedException, AddressNotFoundException {


        return customerAdressDao.createCustomerAddressRelation(customerAddress);

    }


    public String delete(String uuid, String accesstoken) throws AuthorizationFailedException, AddressNotFoundException {

        if(uuid.isEmpty())
            throw new AddressNotFoundException("ANF-005","Address id can not be empty");

        customerService.checkAccessToken(accesstoken);
        if(addressDao.getAddressByUUID(uuid)==null)
            throw new AddressNotFoundException("ANF-003","No address by this id");

      return   addressDao.delete(uuid);




    }

    public List<State> getStateList() {


        return   addressDao.getAllState();


    }
}
