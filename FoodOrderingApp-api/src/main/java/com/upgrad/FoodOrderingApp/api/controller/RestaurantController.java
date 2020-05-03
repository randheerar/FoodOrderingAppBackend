package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    /**
     * retrieve all restaurants in order of their ratings
     * @return response with the desired http status code
     **/
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByRating();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity restaurantEntity : restaurantEntities) {

            RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
                            stateName(restaurantEntity.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress().
                    id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
                    flatBuildingName(restaurantEntity.getAddress().getFlatBuildNo()).
                    locality(restaurantEntity.getAddress().getLocality()).city(restaurantEntity.getAddress().getCity()).
                    pincode(restaurantEntity.getAddress().getPincode()).state(addressState);

            String restaurantCategories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
                    .stream().map(rc -> String.valueOf(rc.getCategoryName())).collect(Collectors.joining(","));

            RestaurantList restaurantList = new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid())).
                    restaurantName(restaurantEntity.getRestaurantName()).photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(new BigDecimal(restaurantEntity.getCustomerRating()))
                    .averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .address(address).categories(restaurantCategories);

            restaurantListResponse.addRestaurantsItem(restaurantList);
        }

        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * retireve restaurant details by name
     * @return corresponding HTTP status with name
     **/
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName)
            throws RestaurantNotFoundException {

        List<RestaurantEntity> matchedRestaurantsByNameList = restaurantService.restaurantsByName(restaurantName);

        RestaurantListResponse listResponse = new RestaurantListResponse();

        if (matchedRestaurantsByNameList.isEmpty()) {
            return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.NOT_FOUND);
        }

        for (RestaurantEntity restaurantEntity : matchedRestaurantsByNameList) {

            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
                            stateName(restaurantEntity.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress().
                    id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
                    flatBuildingName(restaurantEntity.getAddress().getFlatBuildNo()).
                    locality(restaurantEntity.getAddress().getLocality()).city(restaurantEntity.getAddress().getCity()).
                    pincode(restaurantEntity.getAddress().getPincode()).state(responseAddressState);

            String categories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
                    .stream().map(rc -> String.valueOf(rc.getCategoryName())).collect(Collectors.joining(","));

            RestaurantList restaurantList = new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid())).
                    restaurantName(restaurantEntity.getRestaurantName()).photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(new BigDecimal(restaurantEntity.getCustomerRating())).
                            averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .address(responseAddress).categories(categories);

            listResponse.addRestaurantsItem(restaurantList);
        }
        return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.OK);

    }

}
