package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.UserAuthTokenEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method saves the details of the new customer in database.
     *
     * @param users for creating new customer.
     * @return CustomerEntity object.
     */
    public CustomerEntity saveCustomer(final CustomerEntity users) {
        entityManager.persist(users);
        return users;
    }

    /**
     * This method helps finds the customer by using contact number.
     *
     * @param phoneNumber to find the customer is already registered with this number
     * @return CustomerEntity if the contact number exists in the database
     */
    public CustomerEntity getUserByPhone(final String phoneNumber) {
        try {
            return entityManager
                    .createNamedQuery("userByPhone", CustomerEntity.class)
                    .setParameter("contact_number", phoneNumber)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method stores authorization access token in the database
     *
     * @param userAuthTokenEntity the CustomerAuthEntity object from which new authorization will be
     *     created
     */
    public UserAuthTokenEntity createAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    @Modifying
    @Transactional
    public UserAuthTokenEntity updateAuthToken(final UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.merge(userAuthTokenEntity);
        return userAuthTokenEntity;
    }

    public UserAuthTokenEntity getAuthTokenByUUID(int id)
    {

        try {
            return entityManager.createNamedQuery("userAuthTokenByUUID", UserAuthTokenEntity.class).setParameter("customer_id", id).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CustomerEntity getCustomerByUUID(String UUID)
    {

        try {
            return entityManager.createNamedQuery("userByUuid", CustomerEntity.class).setParameter("uuid", UUID).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }



    public void deleteAuthTokenById(int id)
    {
        try {
             entityManager.createNamedQuery("deleteAuthTokenById", UserAuthTokenEntity.class) .setParameter(1, id)
                     .executeUpdate();
             entityManager.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public UserAuthTokenEntity checkAuthToken(final String accessToken) {

        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }


    public CustomerEntity updateUser(final CustomerEntity customer) {
        entityManager.merge(customer);
        return customer;
    }

    @Transactional
    public UserAuthTokenEntity signoutUser(final UserAuthTokenEntity userAuthTokenEntity) {
        userAuthTokenEntity.setLogoutAt( ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
        entityManager.merge(userAuthTokenEntity);
        return  userAuthTokenEntity;
    }
}
