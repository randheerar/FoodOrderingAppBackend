package com.upgrad.FoodOrderingApp.service.dao.Customer;


import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository

public class CustomerAdressDao {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public CustomerAddress createCustomerAddressRelation(CustomerAddress customerAddress) {

        entityManager.persist(customerAddress);
        entityManager.flush();
        System.out.println("ID!!!!!!!! PRIMARY KEY" + customerAddress.getId());

        return customerAddress;
    }



}
