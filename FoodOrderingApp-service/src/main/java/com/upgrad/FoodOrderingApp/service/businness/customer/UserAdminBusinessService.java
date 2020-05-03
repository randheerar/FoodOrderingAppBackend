package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

@Service
public class UserAdminBusinessService {
    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    private CustomerDao customerDao;

    boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }


    public Customers signup(Customers customers) throws SignUpRestrictedException {
        String password = customers.getPassword();

        if (customers.getContact_number().isEmpty() || customers.getEmail().isEmpty() || customers.getFirstname().isEmpty() || customers.getPassword().isEmpty()) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled.");
        }

        if (!isValid(customers.getEmail()))
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");

        if (!customers.getContact_number().matches("[0-9]+") || customers.getContact_number().length() != 10)
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!)");

       /* if(!customers.getPassword().matches("((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,40})"))
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
*/


        String[] encryptedText = cryptographyProvider.encrypt(customers.getPassword());
        customers.setSalt(encryptedText[0]);
        customers.setPassword(encryptedText[1]);

        if (customerDao.getUserByPhone(customers.getContact_number()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }


        return customerDao.createCustomer(customers);
    }


    @Transactional(noRollbackFor = {TransactionException.class})
    public UserAuthTokenEntity signoutUser(String accessToken) throws AuthorizationFailedException {

        UserAuthTokenEntity userAuthTokenEntity = checkAccessToken(accessToken);
         return customerDao.signoutUser(userAuthTokenEntity);

    }

    @Transactional(noRollbackFor = {TransactionException.class})
    public UserAuthTokenEntity checkAccessToken(String accessToken) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthTokenEntity = customerDao.checkAuthToken(accessToken.replace("Bearer ",""));
        if (userAuthTokenEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        }
        ZonedDateTime getLogoutAt = userAuthTokenEntity.getLogout_at();
        ZonedDateTime dateCurrent = ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        if (getLogoutAt != null)
            if (getLogoutAt.isBefore(dateCurrent))
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");


        /**Your session is expired. Log in again to access this endpoint*/
        ZonedDateTime expireTime = userAuthTokenEntity.getExpires_at();

        if (expireTime != null)
            if (expireTime.isBefore(dateCurrent))
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");



            return userAuthTokenEntity;

    }


}

