package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@NamedQueries({
        @NamedQuery(name = "stateByUUID", query = "SELECT s from StateEntity s WHERE  s.uuid = :uuid"),
        @NamedQuery(name = "getAllStates", query = "SELECT s FROM StateEntity s")
})

@Table(name="state")
public class StateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    @Column(name="id")
    private int id;

    @Column(name="uuid")
    @Size(max=200)
    @NotNull
    private String uuid;

    @Column(name="state_name")
    @Size(max=30)
    @NotNull
    private String state_name;

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
}