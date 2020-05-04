package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * queries DB to get all the restaurants from DB
     * @return Restaurant List
     **/
    public List<RestaurantEntity> getAllRestaurantsByRating(){
        List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("getAllRestaurantsByRating", RestaurantEntity.class).getResultList();
        return restaurantEntities;
    }

    /**
     * queries to DB to get the single restaurant from DB
     * @return Restaurant List
     **/
    public RestaurantEntity restaurantByUUID(String uuid){
        try {
            return entityManager.createNamedQuery("restaurantByUUID", RestaurantEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    /**
     * update restaurant details
     * @param restaurantEntity
     * @return restaurantEntity
     */
    public RestaurantEntity updateRestaurantEntity(RestaurantEntity restaurantEntity){
        entityManager.merge(restaurantEntity);
        return  restaurantEntity;
    }
}