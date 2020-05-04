package com.upgrad.FoodOrderingApp.service.entity;

import com.upgrad.FoodOrderingApp.service.common.ItemType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="item")
@NamedQueries({
        @NamedQuery(name = "itemById", query = "select i from ItemEntity i where i.uuid = :itemId"),
        @NamedQuery(name = "itemByUUID", query = "select i from ItemEntity i where i.uuid = :uuid"),
        @NamedQuery(
                name = "getAllItemsInCategoryInRestaurant",
                query =
                        "select i from ItemEntity i  where id in (select ri.itemId from RestaurantItemEntity ri "
                                + "inner join CategoryItemEntity ci on ri.itemId = ci.itemId "
                                + "where ri.restaurantId = (select r.id from RestaurantEntity r where "
                                + "r.uuid=:restaurantUuid) and ci.categoryId = "
                                + "(select c.id from CategoryEntity c where c.uuid=:categoryUuid ) )"
                                + "order by lower(i.itemName) asc")
})

public class ItemEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="item_name")
    @Size(max=30)
    @NotNull
    private String itemName;

    @NotNull
    @Column(name="price")
    private Integer price;

    @ManyToMany
    @JoinTable(name = "restaurant_item", joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "restaurant_id"))
    private List<RestaurantEntity> restaurants = new ArrayList<>();

    @OneToMany(mappedBy = "itemId", cascade= CascadeType.ALL, fetch= FetchType.LAZY)
    private List<RestaurantItemEntity> restaurantItem = new ArrayList<>();

    @Column(name="type")
    @Size(max=10)
    @NotNull
    private ItemType type;

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<RestaurantEntity> getRestaurants() {
        return restaurants;
    }

    public void setRestaurants(List<RestaurantEntity> restaurants) {
        this.restaurants = restaurants;
    }

    public List<RestaurantItemEntity> getRestaurantItem() {
        return restaurantItem;
    }

    public void setRestaurantItem(List<RestaurantItemEntity> restaurantItem) {
        this.restaurantItem = restaurantItem;
    }

    public ItemType getType() {
        return type;
    }

    public ItemEntity() {
    }

}