package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
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
    public CustomerAddressEntity createCustomerAddressRelation(CustomerAddressEntity customerAddress) {

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
    public void createCustomerAddress(final CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
    }

    /**
     * fetches the address of a customer using givne address.
     *
     * @param address address to fetch.
     * @return CustomerAddressEntity type object.
     */
    public CustomerAddressEntity customerAddressByAddress(final AddressEntity address) {
        try {
            return entityManager
                    .createNamedQuery("getCustomerAddressByAddress", CustomerAddressEntity.class)
                    .setParameter("address", address)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }



}
