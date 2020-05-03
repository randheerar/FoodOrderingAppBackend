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
}
