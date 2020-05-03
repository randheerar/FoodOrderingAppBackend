package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Service
public class CustomerUpdateService {


    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;


    public Customers edit(String accessToken, Customers customer) throws AuthorizationFailedException, UpdateCustomerException {

        if (customer.getFirstname() == null)
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");

        if (customer.getFirstname().isEmpty())
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");

        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.checkAccessToken(accessToken);
        customer.setUuid(userAuthTokenEntity.getUuid());

        return customerDao.editCustomer(customer);


    }


}
