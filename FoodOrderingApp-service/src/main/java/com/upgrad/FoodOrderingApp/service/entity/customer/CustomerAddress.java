package com.upgrad.FoodOrderingApp.service.entity.customer;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_address")
public class CustomerAddress implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;


    @Column(name = "customer_id")
    private int customer_id;
    @Column(name = "address_id")
    private int address_id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }
}
