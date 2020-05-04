package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public ItemEntity getItemByUUID(String uuid) {
        try {
            return entityManager.createNamedQuery("itemByUUID", ItemEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /**
     * This method gets Items for a given category in a restaurant
     *
     * @param restaurantUuid Restaurant whose items are to be queried, categoryUuid Category to be
     *     queried. * @return List of ItemEntity
     * @return List of ItemEntity
     */
    public List<ItemEntity> getAllItemsInCategoryInRestaurant(
            final String restaurantUuid, final String categoryUuid) {
        List<ItemEntity> items =
                entityManager
                        .createNamedQuery("getAllItemsInCategoryInRestaurant", ItemEntity.class)
                        .setParameter("restaurantUuid", restaurantUuid)
                        .setParameter("categoryUuid", categoryUuid)
                        .getResultList();
        return items;
    }
}