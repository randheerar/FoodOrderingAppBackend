package com.upgrad.FoodOrderingApp.service.businness.customer;


import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignupBusinessService {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Transactional
    public Customers signup(Customers users) throws SignUpRestrictedException {
        return userAdminBusinessService.signup(users);

    }

}
