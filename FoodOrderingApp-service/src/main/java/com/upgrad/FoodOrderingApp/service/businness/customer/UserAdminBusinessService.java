package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

        if(customers.getContact_number().isEmpty() || customers.getEmail().isEmpty() || customers.getFirstname().isEmpty() || customers.getPassword().isEmpty()){
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled.");
        }

        if(!isValid(customers.getEmail()))
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



}

