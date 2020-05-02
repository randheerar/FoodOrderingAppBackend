package com.upgrad.FoodOrderingApp.service.dao.Customer;


import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.Customers;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AddressDao {


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Address createAddress(Address address) {

            entityManager.persist(address);
            entityManager.flush();
            System.out.println("ID!!!!!!!! PRIMARY KEY" + address.getId());

        return address;
    }



}
