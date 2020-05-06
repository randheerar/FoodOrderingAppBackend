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

    public CustomerEntity createCustomer(CustomerEntity users) {
        entityManager.persist(users);
        return users;
    }

    public CustomerEntity getUserByPhone(final String phoneNumber) {

        try {
            return entityManager.createNamedQuery("userByPhone", CustomerEntity.class).setParameter("contact_number", phoneNumber).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

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

    public UserAuthTokenEntity getAuthTokenByUUID(String UUID)
    {

        try {
            return entityManager.createNamedQuery("userAuthTokenByUUID", UserAuthTokenEntity.class).setParameter("uuid", UUID).getSingleResult();
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

    @Transactional
    public void updateUser(final CustomerEntity customer) {
        entityManager.merge(customer);
    }

    @Transactional
    public UserAuthTokenEntity signoutUser(final UserAuthTokenEntity userAuthTokenEntity) {
        userAuthTokenEntity.setLogout_at( ZonedDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));
        entityManager.merge(userAuthTokenEntity);
        return  userAuthTokenEntity;
    }

    @Transactional
    public CustomerEntity  editCustomer(CustomerEntity customers)
    {
        try {
/*             entityManager.createNamedQuery("editById", CustomerEntity.class).setParameter("firstname", customers.getFirstname()).setParameter("lastname", customers.getLastname()).setParameter("uuid", customers.getUuid())
              .executeUpdate();*/
            entityManager.createNamedQuery("editById", CustomerEntity.class)
                    .setParameter(1, customers.getFirstName())
                    .setParameter(2, customers.getLastName())
                    .setParameter(3, customers.getUuid())
                    .executeUpdate();

            //Execute the delete query
           // entityManager.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return customers;
    }
}
