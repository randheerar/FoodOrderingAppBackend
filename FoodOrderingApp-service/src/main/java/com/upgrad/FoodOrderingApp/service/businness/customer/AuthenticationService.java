package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.businness.JwtTokenProvider;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerLoginRseponse;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
@Service
public class AuthenticationService {
    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    CustomerDao customerDao;


    @Transactional(noRollbackFor = {TransactionException.class})
    public CustomerLoginRseponse authenticate(final String phone, final String password) throws AuthenticationFailedException {
        Customers userEntity = customerDao.getUserByPhone(phone);

        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccess_token(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setLogin_at(now);
            userAuthToken.setUser_id(userEntity.getId());
            userAuthToken.setExpires_at(expiresAt);
            userAuthToken.setLogout_at(null);
            userAuthToken.setUuid(userEntity.getUuid());
           //

           try
           {
               UserAuthTokenEntity userAuthTokenEntityInDb=customerDao.getAuthTokenByUUID(userEntity.getUuid());
               if(userAuthTokenEntityInDb==null)
               {
                   customerDao.createAuthToken(userAuthToken);

               }
               else
               {
                   customerDao.deleteAuthTokenById(userAuthTokenEntityInDb.getId());
                   customerDao.createAuthToken(userAuthToken);
               } }
           catch (Exception e)
           {
               e.printStackTrace();
           }



            userEntity.setUuid(userAuthToken.getUuid());
           // userEntity.setAccess_token(userAuthToken.getAccess_token());
            customerDao.updateUser(userEntity);

            CustomerLoginRseponse customerLoginRseponse=new CustomerLoginRseponse();
            customerLoginRseponse.setAccess_token(userAuthToken.getAccess_token());
            customerLoginRseponse.setContactNumber(userEntity.getContact_number());
            customerLoginRseponse.setEmailAddress(userEntity.getEmail());
            customerLoginRseponse.setFirstName(userEntity.getFirstname());
            customerLoginRseponse.setLastName(userEntity.getLastname());
            customerLoginRseponse.setId(userEntity.getId());
            customerLoginRseponse.setUUID(userEntity.getUuid());


            return customerLoginRseponse;
        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }


    }


}
