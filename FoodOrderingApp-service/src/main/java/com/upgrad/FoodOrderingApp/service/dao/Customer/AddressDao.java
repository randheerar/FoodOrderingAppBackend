package com.upgrad.FoodOrderingApp.service.dao.Customer;


import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.entity.customer.Address;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public List<Address> getAddress(int customer_id)
    {

        List<Address> addressList=new ArrayList<>();
        addressList= entityManager.createNamedQuery("getaddress", Address.class).getResultList();

        return addressList;
    }

    @Transactional
    public Address getAddressByUUID(String uuid)
    {
        try {
            return entityManager.createNamedQuery("getAddressByUUID", Address.class).setParameter("uuid", uuid).getSingleResult();
        }catch (Exception e)
        {
            return null;
        }

    }



    @Transactional
    public String delete(String id)
    {
        try {
            entityManager.createNamedQuery("deleteAddressById", Address.class)
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
}
