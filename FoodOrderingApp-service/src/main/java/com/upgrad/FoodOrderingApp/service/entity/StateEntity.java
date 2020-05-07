package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name="state")
@NamedQueries({
        @NamedQuery(name = "getStateByUuid", query = "SELECT s from StateEntity s WHERE  s.uuid = :uuid"),
        @NamedQuery(name = "getAllStates", query = "SELECT s FROM StateEntity s")
})

public class StateEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Size(max = 200)
    @NotNull
    @Column(name = "uuid", unique = true)
    private String uuid;

    @Size(max = 30)
    @Column(name = "state_name")
    private String stateName;

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

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public StateEntity() {}

    public StateEntity(
            @NotNull @Size(max = 200) String uuid, @NotNull @Size(max = 30) String stateName) {
        this.uuid = uuid;
        this.stateName = stateName;
    }
}