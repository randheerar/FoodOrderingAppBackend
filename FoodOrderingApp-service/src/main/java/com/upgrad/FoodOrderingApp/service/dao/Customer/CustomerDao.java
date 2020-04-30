package com.upgrad.FoodOrderingApp.service.dao.Customer;


import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import org.springframework.stereotype.Repository;

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

}
