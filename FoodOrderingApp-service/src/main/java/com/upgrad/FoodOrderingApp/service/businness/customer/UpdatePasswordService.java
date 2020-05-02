package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class UpdatePasswordService {
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    UserAdminBusinessService userAdminBusinessService;
    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;
    @Autowired
    CustomerDao customerDao;


    public Customers updatePassword(String accessToken, String oldPassword, String newPassword) throws UpdateCustomerException, AuthorizationFailedException {

        if (oldPassword.isEmpty() || newPassword.isEmpty())
            throw new UpdateCustomerException("UCR-003", "No field should be empty");


        UserAuthTokenEntity userAuthTokenEntity = userAdminBusinessService.checkAccessToken(accessToken);
        Customers customers = customerDao.getCustomerByUUID(userAuthTokenEntity.getUuid());


        final String encryptedPassword_old = passwordCryptographyProvider.encrypt(oldPassword, customers.getSalt());
        if (!encryptedPassword_old.equals(customers.getPassword()))
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");

            /* if(!customers.getPassword().matches("((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,40})"))
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
            */


        String[] encryptedText = cryptographyProvider.encrypt(newPassword);
        customers.setSalt(encryptedText[0]);
        customers.setPassword(encryptedText[1]);

        customerDao.updateUser(customers);
        return customers;
    }


}
