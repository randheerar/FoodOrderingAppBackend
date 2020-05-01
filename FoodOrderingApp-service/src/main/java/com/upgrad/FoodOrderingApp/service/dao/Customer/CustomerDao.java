package com.upgrad.FoodOrderingApp.service.dao.Customer;


import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.entity.customer.UserAuthTokenEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class CustomerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public Customers createCustomer(Customers users) {
        entityManager.persist(users);
        return users;
    }

    public Customers getUserByPhone(final String phoneNumber) {

        try {
            return entityManager.createNamedQuery("userByPhone", Customers.class).setParameter("contact_number", phoneNumber).getSingleResult();
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


    public void updateUser(final Customers customer) {
        entityManager.merge(customer);
    }

}
