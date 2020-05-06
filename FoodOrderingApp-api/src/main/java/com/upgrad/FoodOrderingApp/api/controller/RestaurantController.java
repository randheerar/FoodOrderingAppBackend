package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Autowired
    CustomerService customerService;

    @Autowired
    private ItemService itemService;

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
                    flatBuildingName(restaurantEntity.getAddress().getFlat_buil_number()).
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

        List<RestaurantList> allRestaurantsList = createListOfRestaurantList(matchedRestaurantsByNameList);
        RestaurantListResponse restaurantListResponse =
                new RestaurantListResponse().restaurants(allRestaurantsList);

        if (allRestaurantsList.isEmpty()) {
            return new ResponseEntity<>(restaurantListResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);

    }

    /**
     * returns restaurant list based on the input parameter category Id
     * @return corresponding HTTP status
     **/
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {

        List<RestaurantEntity> restaurantListByCategoryId = restaurantService.restaurantByCategory(categoryId);

        List<RestaurantList> allRestaurantsList = createListOfRestaurantList(restaurantListByCategoryId);
        RestaurantListResponse restaurantListResponse =
                new RestaurantListResponse().restaurants(allRestaurantsList);

        if (allRestaurantsList.isEmpty()) {
            return new ResponseEntity<>(restaurantListResponse, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(restaurantListResponse, HttpStatus.OK);
    }

    /**
     * method to get Restaurant based on restaurant ID
     * @return corresponding HTTP status
     **/
    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantById(@PathVariable("restaurant_id") final String restaurantId)
            throws RestaurantNotFoundException {

        RestaurantEntity restaurantByRestaurantId = restaurantService.restaurantByUUID(restaurantId);

        RestaurantDetailsResponse restaurantDetailsResponse =
                createRestaurantDetailsResponse(restaurantByRestaurantId);
        List<CategoryList> categories = getAllCategoryItemsInRestaurant(restaurantId);
        restaurantDetailsResponse.setCategories(categories);
        return new ResponseEntity<>(restaurantDetailsResponse, HttpStatus.OK);
        }

    /**
     * response in RestaurantUpdatedResponse and returns UUID of Updated restaurant from the db and successful
     * @return corresponding HTTP status
     **/
    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(
            @RequestParam(name = "customer_rating") final Double customerRating,
            @PathVariable("restaurant_id") final String restaurantId,
            @RequestHeader("access_token") final String accessToken)
            throws RestaurantNotFoundException, AuthorizationFailedException, InvalidRatingException {

        customerService.checkAccessToken(accessToken);


        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(updatedRestaurantEntity.getUuid()))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    /* Creates a List of RestaurantList */

    private List<RestaurantList> createListOfRestaurantList(
            final List<RestaurantEntity> allRestaurants) {
        List<RestaurantList> allRestaurantsList = new ArrayList<>();
        for (RestaurantEntity restaurantEntity : allRestaurants) {
            RestaurantList restaurantList = new RestaurantList();
            restaurantList.setId(UUID.fromString(restaurantEntity.getUuid()));
            RestaurantDetailsResponseAddress restaurantDetailsResponseAddress =
                    createRestaurantDetailsResponseAddress(restaurantEntity.getAddress());

            restaurantList.setAddress(restaurantDetailsResponseAddress);
            restaurantList.setAveragePrice(restaurantEntity.getAvgPrice());

            List<CategoryEntity> categoryEntities =
                    categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());
            StringBuilder categoriesString = new StringBuilder();
            for (CategoryEntity category : categoryEntities) {
                categoriesString.append(category.getCategoryName() + ", ");
            }
            restaurantList.setCategories(categoriesString.toString().replaceAll(", $", ""));

            restaurantList.setCustomerRating(BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
            restaurantList.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated());
            restaurantList.setPhotoURL(restaurantEntity.getPhotoUrl());
            restaurantList.setRestaurantName(restaurantEntity.getRestaurantName());
            allRestaurantsList.add(restaurantList);
        }

        return allRestaurantsList;
    }

    /* Creates RestaurantDetailsResponse */

    private RestaurantDetailsResponse createRestaurantDetailsResponse(
            RestaurantEntity restaurantEntity) {

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse();

        restaurantDetailsResponse.setId(UUID.fromString(restaurantEntity.getUuid()));

        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress =
                createRestaurantDetailsResponseAddress(restaurantEntity.getAddress());
        restaurantDetailsResponse.setAddress(restaurantDetailsResponseAddress);

        restaurantDetailsResponse.setAveragePrice(restaurantEntity.getAvgPrice());
        restaurantDetailsResponse.setCustomerRating(
                BigDecimal.valueOf(restaurantEntity.getCustomerRating()));
        restaurantDetailsResponse.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated());
        restaurantDetailsResponse.setPhotoURL(restaurantEntity.getPhotoUrl());
        restaurantDetailsResponse.setRestaurantName(restaurantEntity.getRestaurantName());

        return restaurantDetailsResponse;
    }

    /* Creates RestaurantDetailsResponseAddress */

    private RestaurantDetailsResponseAddress createRestaurantDetailsResponseAddress(
            AddressEntity restaurantAddress) {
        RestaurantDetailsResponseAddress restaurantDetailsResponseAddress =
                new RestaurantDetailsResponseAddress();
        RestaurantDetailsResponseAddressState restaurantDetailsResponseAddressState =
                new RestaurantDetailsResponseAddressState();
        AddressEntity addressEntity = restaurantAddress;
        restaurantDetailsResponseAddress.setId(UUID.fromString(addressEntity.getUuid()));
        restaurantDetailsResponseAddress.setFlatBuildingName(addressEntity.getFlat_buil_number());
        restaurantDetailsResponseAddress.setCity(addressEntity.getCity());
        restaurantDetailsResponseAddress.setLocality(addressEntity.getLocality());
        restaurantDetailsResponseAddress.setPincode(addressEntity.getPincode());

        restaurantDetailsResponseAddressState.setId(
                UUID.fromString(addressEntity.getState().getUuid()));
        restaurantDetailsResponseAddressState.setStateName(addressEntity.getState().getStateName());
        restaurantDetailsResponseAddress.setState(restaurantDetailsResponseAddressState);
        return restaurantDetailsResponseAddress;
    }

    /* Gets List<CategoryList>  in the resturant  */

    private List<CategoryList> getAllCategoryItemsInRestaurant(final String restaurantUuid) {
        List<CategoryList> allCategoryItems = new ArrayList<>();
        List<CategoryEntity> categories = categoryService.getCategoriesByRestaurant(restaurantUuid);

        for (CategoryEntity c : categories) {
            CategoryList categoryList = new CategoryList();
            categoryList.setId(UUID.fromString(c.getUuid()));
            categoryList.setCategoryName(c.getCategoryName());
            List<ItemList> allItemsInCategory =
                    getAllItemsInCategoryInRestaurant(restaurantUuid, c.getUuid());
            categoryList.setItemList(allItemsInCategory);
            allCategoryItems.add(categoryList);
        }

        return allCategoryItems;
    }

    /* Gets List<ItemList>  in given category  in the resturant  */

    private List<ItemList> getAllItemsInCategoryInRestaurant(
            final String restaurantUuid, final String categoryUuid) {
        List<ItemList> itemsInCategoryInRestaurant = new ArrayList<>();
        List<ItemEntity> items =
                itemService.getItemsByCategoryAndRestaurant(restaurantUuid, categoryUuid);
        for (ItemEntity item : items) {
            ItemList itemList = new ItemList();
            itemList.setId(UUID.fromString(item.getUuid()));
            itemList.setItemName(item.getItemName());
            itemList.setPrice(item.getPrice());
            if (item.getType().equals("0")) {
                itemList.setItemType(ItemList.ItemTypeEnum.valueOf("VEG"));
            } else {
                itemList.setItemType(ItemList.ItemTypeEnum.valueOf("NON_VEG"));
            }

            itemsInCategoryInRestaurant.add(itemList);
        }

        return itemsInCategoryInRestaurant;
    }

}
