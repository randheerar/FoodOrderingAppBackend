package com.upgrad.FoodOrderingApp.service.entity.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "address")

@SqlResultSetMapping(name="AddressResult", columns = { @ColumnResult(name = "id")})

@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "deleteAddressById",
                query   =   "DELETE FROM address WHERE uuid = ?1",resultSetMapping = "Result"
        ),
        @NamedNativeQuery(
                name    =   "getAddress",
                query   =   "select customer.* as customer,address.* from customer_address inner join customer on customer_address.customer_id=customer.id inner join address on customer_address.address_id=address.id where customer.id=?1",resultSetMapping = "Result"
        )
})
@NamedQueries(
        {

                @NamedQuery(name = "addressById", query = "select u from Address u where u.id =:id"),
                @NamedQuery(name = "getaddress", query = "select u from Address u"),
                @NamedQuery(name = "getAddressByUUID", query = "select u from Address u where u.uuid=:uuid"),


        }
)


public class Address implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid", unique = true)
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flat_buil_number;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Size(max = 30)
    @Column(name = "pincode")
    private String pincode;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "state_id")
    private StateEntity state;

    @Column(name = "active")
    private Integer active;

    public Address() {}

    public Address(
            @Size(max = 200) @NotNull String uuid,
            @Size(max = 255) String flatBuilNo,
            @Size(max = 255) String locality,
            @Size(max = 30) String city,
            @Size(max = 30) String pincode,
            StateEntity state) {
        this.uuid = uuid;
        this.flat_buil_number = flatBuilNo;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
    }

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

    public String getFlat_buil_number() {
        return flat_buil_number;
    }

    public void setFlat_buil_number(String flat_buil_number) {
        this.flat_buil_number = flat_buil_number;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity state) {
        this.state = state;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }


}
