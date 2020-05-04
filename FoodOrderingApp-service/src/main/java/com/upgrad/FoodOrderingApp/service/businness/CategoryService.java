package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    @Autowired
    private RestaurantDao restaurantDao;

    /**
     * returns categories of restaurant in alphabetical order.
     * @return takes restaurant uuid as input param and returns sorted categories alphabetically
     **/
    public List<CategoryEntity> getCategoriesByRestaurant(String RestaurantUuid){
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(RestaurantUuid);
        return restaurantEntity.getCategories().stream().sorted(Comparator.comparing(CategoryEntity::getCategoryName)).collect(Collectors.toList());
    }
}