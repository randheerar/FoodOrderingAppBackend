package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerLoginRseponse;
import com.upgrad.FoodOrderingApp.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserAuthTokenDao {

    @PersistenceContext private EntityManager entityManager;

    /**
     * This method stores authorization access token in the database
     *
     * @param userAuthTokenEntity the CustomerAuthEntity object from which new authorization will be
     *     created
     */
    public void createCustomerAuthToken(UserAuthTokenEntity userAuthTokenEntity) {
        entityManager.persist(userAuthTokenEntity);
    }

    /**
     *
     * @param accessToken access-token obtained during successful login.
     * @return UserAuthTokenEntity or null of token not found in database. This method helps to find
     *     the customer using the access token.
     * @param accessToken the access token which will be searched in database to find the customer.
     * @return UserAuthTokenEntity object if given access token exists in the database.
     */
    public UserAuthTokenEntity getCustomerAuthByToken(final String accessToken) {
        try {
            return entityManager
                    .createNamedQuery("userAuthByAccessToken", UserAuthTokenEntity.class)
                    .setParameter("accessToken", accessToken)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * method to update customer logout time in the database.
     *
     * @param updatedUserAuthTokenEntity UserAuthTokenEntity object to update.
     */
    public void updateCustomerAuth(final UserAuthTokenEntity updatedUserAuthTokenEntity) {
        entityManager.merge(updatedUserAuthTokenEntity);
    }
}
