package com.upgrad.FoodOrderingApp.service.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="restaurant")
@NamedQueries({
        @NamedQuery(name = "restaurantById", query = "select r from RestaurantEntity r where r.uuid = :restaurantId"),
        @NamedQuery(name = "getAllRestaurantsByRating", query = "select q from RestaurantEntity q order by q.customerRating desc"),
        @NamedQuery(name = "restaurantByUUID", query = "select q from RestaurantEntity q where q.uuid = :uuid"),
})

public class RestaurantEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="restaurant_name")
    @Size(max=30)
    @NotNull
    private String restaurantName;

    @Column(name="photo_url")
    @Size(max=255)
    @NotNull
    private String photoUrl;

    @Column(name="customer_rating")
    @NotNull
    private BigDecimal customerRating;

    @Column(name="average_price_for_two")
    @NotNull
    private Integer averagePriceForTwo;

    @Column(name="number_of_customers_rated")
    @NotNull
    private Integer numberOfCustomersRated;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="address_id")
    private AddressEntity address;

    @ManyToMany
    @JoinTable(name = "restaurant_category", joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<CategoryEntity> categories = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Double getCustomerRating() {
        return customerRating.doubleValue();
    }

    public Integer getAvgPrice() {
        return averagePriceForTwo;
    }

    public Integer getNumberCustomersRated() {
        return numberOfCustomersRated;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public RestaurantEntity() {
    }
}
