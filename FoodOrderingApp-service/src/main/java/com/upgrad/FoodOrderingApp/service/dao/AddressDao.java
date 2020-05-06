package com.upgrad.FoodOrderingApp.service.dao;


import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class AddressDao {


    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public AddressEntity createAddress(AddressEntity address) {

        entityManager.persist(address);
          //  entityManager.flush();
          //  System.out.println("ID!!!!!!!! PRIMARY KEY" + address.getId());

        return address;
    }

    @Transactional
    public List<AddressEntity> getAddress(int customer_id)
    {

        List<AddressEntity> addressList=new ArrayList<>();
        addressList= entityManager.createNamedQuery("getaddress", AddressEntity.class).getResultList();

        return addressList;
    }

    @Transactional
    public AddressEntity getAddressByUUID(String uuid)
    {
        try {
            return entityManager.createNamedQuery("getAddressByUUID", AddressEntity.class).setParameter("uuid", uuid).getSingleResult();
        }catch (Exception e)
        {
            return null;
        }

    }



    @Transactional
    public String delete(String id)
    {
        try {
            entityManager.createNamedQuery("deleteAddressById", AddressEntity.class)
                    .setParameter(1, id)
                    .executeUpdate();

             entityManager.flush();

        } catch (Exception e) {
            e.printStackTrace();
         }

        return id;
    }


    public List<StateEntity> getAllState() {

        return entityManager.createNamedQuery("getAllStates", StateEntity.class).getResultList();

    }

    /**
     * This method fetches all the addresses added by the customer.
     *
     * @param customer whose detals to be fetched.
     * @return List of CustomerAddress type object.
     */
    public List<CustomerAddressEntity> customerAddressByCustomer(CustomerEntity customer) {
        List<CustomerAddressEntity> addresses =
                entityManager
                        .createNamedQuery("getCustomerAddressByCustomer", CustomerAddressEntity.class)
                        .setParameter("customer", customer)
                        .getResultList();
        if (addresses == null) {
            return Collections.emptyList();
        }
        return addresses;
    }
}
