package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthTokenDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     *
     * @param accessToken access-token obtained during successful login.
     * @return CustomerAuthEntity or null of token not found in database helping to find the customer using the access token.
     *
     * @param accessToken the access token which will be searched in database to find the customer.
     * @return CustomerAuthEntity object if given access token exists in the database.
     */
    public CustomerAuthEntity getCustomerAuthByToken(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthByAccessToken", CustomerAuthEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * method to update customer logout time in the database.
     *
     * @param updatedCustomerAuthEntity CustomerAuthEntity object to update.
     */
    public void updateCustomerAuth(final CustomerAuthEntity updatedCustomerAuthEntity) {
        entityManager.merge(updatedCustomerAuthEntity);
    }
}
