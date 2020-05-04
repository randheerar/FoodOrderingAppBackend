package com.upgrad.FoodOrderingApp.service.dao.Customer;


import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import com.upgrad.FoodOrderingApp.service.entity.customer.CustomerAddress;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    /**
     * Creates mapping between the customer and the address entity.
     *
     * @param customerAddressEntity Customer and the address to map.
     * @return CustomerAddressEntity object.
     */
    public void createCustomerAddress(final CustomerAddress customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
    }

    /**
     * fetches the address of a customer using givne address.
     *
     * @param address address to fetch.
     * @return CustomerAddressEntity type object.
     */
    public CustomerAddress customerAddressByAddress(final Address address) {
        try {
            return entityManager
                    .createNamedQuery("getCustomerAddressByAddress", CustomerAddress.class)
                    .setParameter("address", address)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }



}
