package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.dao.Customer.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerAdressDao;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveAddresService {

    @Autowired
    private AddressDao addressDao;

    @Autowired
    private CustomerAdressDao customerAdressDao;

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


}
