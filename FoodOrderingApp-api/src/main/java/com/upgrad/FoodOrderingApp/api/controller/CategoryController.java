package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        List<CategoryEntity> categoryList = categoryService.getAllCategoriesOrderedByName();

        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

        for (CategoryEntity categoryEntity : categoryList) {
            CategoryListResponse categoryListResponse = new CategoryListResponse()
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());

            categoriesListResponse.addCategoriesItem(categoryListResponse);
        }

        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }
}