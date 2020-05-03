package com.upgrad.FoodOrderingApp.service.entity.customer;

import javax.persistence.*;

@Entity
@Table(name = "state")
@NamedQueries(
        {

                @NamedQuery(name = "getAllStates", query = "select u from State u"),


        }
)

public class State {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    String uuid;

    @Column(name = "state_name")
    String state_name;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }
}
