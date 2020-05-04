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
    public List<RestaurantEntity> getAllRestaurantsByRating() {
        return entityManager.createNamedQuery("getAllRestaurantsByRating", RestaurantEntity.class).getResultList();
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

    /**
     * This method gets restaurants by Category
     *
     * @param categoryUuid
     * @return List of restaurantEntity
     */
    public List<RestaurantEntity> restaurantByCategory(final String categoryUuid) {

        return entityManager
                .createNamedQuery("restaurantByCategory", RestaurantEntity.class)
                .setParameter("categoryUuid", categoryUuid)
                .getResultList();
    }

    /**
     * This method gets lists of all restaurants by Search string
     *
     * @param searchString
     * @return List of RestaurantEntity
     */
    public List<RestaurantEntity> restaurantsByName(final String searchString) {
        return entityManager
                .createNamedQuery("getRestaurantByName", RestaurantEntity.class)
                .setParameter("searchString", "%" + searchString + "%")
                .getResultList();
    }
}