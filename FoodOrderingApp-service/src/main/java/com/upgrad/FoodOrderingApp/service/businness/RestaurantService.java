package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RestaurantService {
    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     *  returns all the restaurants according to the customer ratings
     *  @return List
     **/
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();

        return restaurantEntities;
    }

    /**
     *  returns the restaurants even if there is partial match in the restaurant
     *  @param=restaurantName
     *  @return matching restaurant details
     **/
    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        List<RestaurantEntity> restaurantListByRating = restaurantDao.getAllRestaurantsByRating();
        List<RestaurantEntity> matchingRestaurantList = new ArrayList<>();

        for (RestaurantEntity restaurantEntity : restaurantListByRating) {
            if (restaurantEntity.getRestaurantName().toLowerCase().contains(restaurantName.toLowerCase())) {
                matchingRestaurantList.add(restaurantEntity);
            }
        }

        return matchingRestaurantList;
    }

    /**
     *  returns all the restaurants based on the input category Id
     *  @param=category Id
     *  @return updated restaurant details
     **/
    public List<RestaurantEntity> restaurantByCategory(final String categoryId) throws CategoryNotFoundException {

        if (categoryId.equals("")) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No Category By this id");
        }

        List<RestaurantEntity> restaurantListByCategoryId = categoryEntity.getRestaurants();
        restaurantListByCategoryId.sort(Comparator.comparing(RestaurantEntity::getRestaurantName));

        return restaurantListByCategoryId;
    }
    /**
     *  returns the restaurant based on input restaurant ID
     *  @param=restaurant Id
     *  @return Resturant
     **/
    public RestaurantEntity restaurantByUUID(String uuid) throws RestaurantNotFoundException {
        if (uuid.equals("")) {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        RestaurantEntity restaurantByRestaurantId = restaurantDao.restaurantByUUID(uuid);

        if (restaurantByRestaurantId == null) {
            throw new RestaurantNotFoundException("RNF-001", "No Restaurant By this Id");
        }

        return restaurantByRestaurantId;
    }

}
