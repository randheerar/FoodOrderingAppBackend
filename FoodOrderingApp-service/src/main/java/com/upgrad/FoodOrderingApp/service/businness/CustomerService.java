package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.UserAuthTokenDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.apache.commons.validator.routines.EmailValidator;
import com.upgrad.FoodOrderingApp.service.entity.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Propagation;
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

//    boolean isValidEmail(String email) {
//        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
//        return email.matches(regex);
//    }


    /**
     * logic for 'signup' endpoint.
     *
     * @param customerEntity for creating new customer.
     * @return CustomerEntity object.
     * @throws SignUpRestrictedException if any of the validation fails.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity)
            throws SignUpRestrictedException {

        if (!customerEntity.getContactNumber().isEmpty()
                && !customerEntity.getEmailAddress().isEmpty()
                && !customerEntity.getFirstName().isEmpty()
                && !customerEntity.getPassword().isEmpty()) {

            if (isContactNumberInUse(customerEntity.getContactNumber())) {
                throw new SignUpRestrictedException(
                        "SGR-001", "This contact number is already registered! Try other contact number.");
            }

            if (!isValidEmail(customerEntity.getEmailAddress()))
                throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");

//            if (!customerEntity.getContactNumber().matches("[0-9]+") || customerEntity.getContactNumber().length() != 10)
//                throw new SignUpRestrictedException("SGR-003", "Invalid contact number!)");
            if (!isValidContactNumber(customerEntity.getContactNumber())) {
                throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
            }

//            if (!customerEntity.getPassword().matches("^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$"))
//                throw new SignUpRestrictedException("SGR-004", "Weak password!");

            if (!isValidPassword(customerEntity.getPassword())) {
                throw new SignUpRestrictedException("SGR-004", "Weak password!");
            }

            customerEntity.setUuid(UUID.randomUUID().toString());

            String[] encryptedText = cryptographyProvider.encrypt(customerEntity.getPassword());
            customerEntity.setSalt(encryptedText[0]);
            customerEntity.setPassword(encryptedText[1]);
            return customerDao.saveCustomer(customerEntity);
        } else {
            throw new SignUpRestrictedException(
                    "SGR-005", "Except last name all fields should be filled");
        }
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
        ZonedDateTime getLogoutAt = userAuthTokenEntity.getLogoutAt();
        ZonedDateTime dateCurrent = ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
        if (getLogoutAt != null)
            if (getLogoutAt.isBefore(dateCurrent))
                throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");


        /**Your session is expired. Log in again to access this endpoint*/
        ZonedDateTime expireTime = userAuthTokenEntity.getExpiresAt();

        if (expireTime != null)
            if (expireTime.isBefore(dateCurrent))
                throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");



            return userAuthTokenEntity;

    }
    @Transactional(noRollbackFor = {TransactionException.class})
    public UserAuthTokenEntity authenticate(final String phone, final String password)
            throws AuthenticationFailedException {

        CustomerEntity userEntity = customerDao.getUserByPhone(phone);

        if(userEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, userEntity.getSalt());

        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            String accessToken = jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt);
            userAuthToken.setAccessToken(accessToken);
            userAuthToken.setLoginAt(now);
            userAuthToken.setCustomer(userEntity);
            userAuthToken.setExpiresAt(expiresAt);
            userAuthToken.setLogoutAt(null);
            userAuthToken.setUuid(UUID.randomUUID().toString());
            customerDao.createAuthToken(userAuthToken);
            return userAuthToken;
        }
        else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }


    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customer) {
        return customerDao.updateUser(customer);
    }

    @Transactional(noRollbackFor={TransactionException.class})
    public UserAuthTokenEntity signout(String  accessToken) throws AuthorizationFailedException {
        return signoutUser(accessToken);
    }

   /* @Transactional
    public CustomerEntity signupCustomer(CustomerEntity users) throws SignUpRestrictedException {
        return signup(users);

    }*/
   @Transactional(noRollbackFor = {TransactionException.class})

    public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword, final CustomerEntity customerEntity) throws UpdateCustomerException {

       if(newPassword.matches("^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$")) {

           // CustomerEntity customers = customerDao.getCustomerByUUID(userAuthTokenEntity.getUuid());

           String encryptedPassword_old = passwordCryptographyProvider.encrypt(oldPassword, customerEntity.getSalt());
           if (!encryptedPassword_old.equals(customerEntity.getPassword())) {
               throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
           }
           String[] encryptedText = cryptographyProvider.encrypt(newPassword);
           customerEntity.setSalt(encryptedText[0]);
           customerEntity.setPassword(encryptedText[1]);

           return customerDao.updateUser(customerEntity);
       } else {
           throw new UpdateCustomerException("UCR-001", "Weak password!");
       }
    }

    /**
     * This method retrieves the Customer info
     *
     * @param accessToken Takes access-token as input which is obtained during successful login.
     * @return CustomerEntity - Customer who obtained this access-token during his login.
     * @throws AuthorizationFailedException Based on token validity.
     */
    public CustomerEntity getCustomer(String accessToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuthTokenEntity = userAuthTokenDao.getCustomerAuthByToken(accessToken);
        System.out.println("test2 "+userAuthTokenEntity.getCustomer());
        if (userAuthTokenEntity != null) {

            if (userAuthTokenEntity.getLogoutAt() != null) {
                throw new AuthorizationFailedException(
                        "ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
            }

            if (ZonedDateTime.now().isAfter(userAuthTokenEntity.getExpiresAt())) {
                throw new AuthorizationFailedException(
                        "ATHR-003", "Your session is expired. Log in again to access this endpoint.");
            }
            return userAuthTokenEntity.getCustomer();
        } else {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
    }

    /**
     * This method implements the logic for 'logout' endpoint.
     *
     * @param accessToken Customers access token in 'Bearer <access-token>' format.
     * @return Updated CustomerAuthEntity object.
     * @throws AuthorizationFailedException if any of the validation fails on customer authorization.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity logout(final String accessToken) throws AuthorizationFailedException {
        System.out.println("test1 "+accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userAuthTokenDao.getCustomerAuthByToken(accessToken);
        CustomerEntity customerEntity = getCustomer(accessToken);
        userAuthTokenEntity.setCustomer(customerEntity);
        userAuthTokenEntity.setLogoutAt(ZonedDateTime.now());
        userAuthTokenDao.updateCustomerAuth(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    private boolean isContactNumberInUse(final String contactNumber) {
        return customerDao.getUserByPhone(contactNumber) != null;
    }

    // method checks for format of the email is correct or not using EmailValidator
    private boolean isValidEmail(final String emailAddress) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(emailAddress);
    }

    // method checks for given contact number is valid or not
    private boolean isValidContactNumber(final String contactNumber) {
        if (contactNumber.length() != 10) {
            return false;
        }
        for (int i = 0; i < contactNumber.length(); i++) {
            if (!Character.isDigit(contactNumber.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPassword(final String password) {
        return password.matches("^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$");
    }

}

