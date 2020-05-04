package com.upgrad.FoodOrderingApp.service.businness.customer;

import com.upgrad.FoodOrderingApp.service.businness.JwtTokenProvider;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.dao.Customer.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.Customer.UserAuthTokenDao;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerLoginRseponse;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class CustomerService {

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private UserAuthTokenDao userAuthTokenDao;

    boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    @Transactional(noRollbackFor = {TransactionException.class})
    public Customers signup(Customers customers) throws SignUpRestrictedException {
        String password = customers.getPassword();

        if (customers.getContactNumber().isEmpty() || customers.getEmailAddress().isEmpty() || customers.getFirstName().isEmpty() || customers.getPassword().isEmpty()) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled.");
        }

        if (!isValid(customers.getEmailAddress()))
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");

        if (!customers.getContactNumber().matches("[0-9]+") || customers.getContactNumber().length() != 10)
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!)");

       /* if(!customers.getPassword().matches("((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[#@$%&*!^]).{8,40})"))
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
*/


        String[] encryptedText = cryptographyProvider.encrypt(customers.getPassword());
        customers.setSalt(encryptedText[0]);
        customers.setPassword(encryptedText[1]);

        if (customerDao.getUserByPhone(customers.getContactNumber()) != null) {
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
            userAuthToken.setCustomer(userEntity);
            userAuthToken.setExpires_at(expiresAt);
            userAuthToken.setLogout_at(null);
            userAuthToken.setUuid(UUID.randomUUID().toString());

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
            customerLoginRseponse.setContactNumber(userEntity.getContactNumber());
            customerLoginRseponse.setEmailAddress(userEntity.getEmailAddress());
            customerLoginRseponse.setFirstName(userEntity.getFirstName());
            customerLoginRseponse.setLastName(userEntity.getLastName());
            customerLoginRseponse.setId(userEntity.getId());
            customerLoginRseponse.setUUID(userEntity.getUuid());


            return customerLoginRseponse;
        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }


    }
    @Transactional(noRollbackFor = {TransactionException.class})

    public Customers edit(String accessToken, Customers customer) throws AuthorizationFailedException, UpdateCustomerException {

        if (customer.getFirstName() == null)
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");

        if (customer.getLastName().isEmpty())
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");

        UserAuthTokenEntity userAuthTokenEntity = checkAccessToken(accessToken);
        customer.setUuid(userAuthTokenEntity.getUuid());

        return customerDao.editCustomer(customer);


    }
    @Transactional(noRollbackFor={TransactionException.class})
    public UserAuthTokenEntity signout(String  accessToken) throws AuthorizationFailedException {
        return signoutUser(accessToken);
    }

   /* @Transactional
    public Customers signupCustomer(Customers users) throws SignUpRestrictedException {
        return signup(users);

    }*/
   @Transactional(noRollbackFor = {TransactionException.class})

    public Customers updatePassword(String accessToken, String oldPassword, String newPassword) throws UpdateCustomerException, AuthorizationFailedException {

        if (oldPassword.isEmpty() || newPassword.isEmpty())
            throw new UpdateCustomerException("UCR-003", "No field should be empty");


        UserAuthTokenEntity userAuthTokenEntity = checkAccessToken(accessToken);
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

    /**
     * This method retrieves the Customer info
     *
     * @param uuid Takes access-token as input which is obtained during successful login.
     * @return CustomerEntity - Customer who obtained this access-token during his login.
     * @throws AuthorizationFailedException Based on token validity.
     */
    public Customers getCustomer(String uuid) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthTokenDao.getCustomerAuthByToken(uuid);
        return userAuthTokenEntity.getCustomer();
    }

}

