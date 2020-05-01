package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SignoutBusinessService {

    @Autowired
    private UserAdminBusinessService userAdminBusinessService;

    @Transactional(noRollbackFor={TransactionException.class})
    public UserAuthTokenEntity signout(String  accessToken) throws AuthorizationFailedException {
        return userAdminBusinessService.signoutUser(accessToken);
    }



}
