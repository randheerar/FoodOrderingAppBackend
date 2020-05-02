package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UpdatePasswordService {
    @Autowired
    UserAdminBusinessService userAdminBusinessService;


   public Customers updatePassword(String accessToken,String oldPassword,String newPassword) throws UpdateCustomerException,AuthorizationFailedException
   {

       Customers customers=new Customers();
       if(oldPassword.isEmpty() || newPassword.isEmpty())
           throw  new UpdateCustomerException("UCR-003","No field should be empty");


           UserAuthTokenEntity userAuthTokenEntity=userAdminBusinessService.checkAccessToken(accessToken);



           return customers;
   }




}
