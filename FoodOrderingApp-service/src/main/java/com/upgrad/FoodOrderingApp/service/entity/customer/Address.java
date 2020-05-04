package com.upgrad.FoodOrderingApp.service.entity.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
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
   /* @OneToMany(targetEntity=CustomerAddress.class ,mappedBy = "address", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<CustomerAddress> customerAddresses=new HashSet<>();

    public Set<CustomerAddress> getCustomerAddresses() {
        return customerAddresses;
    }

    public void setCustomerAddresses(Set<CustomerAddress> customerAddresses) {
        this.customerAddresses = customerAddresses;
    }*/


   /* @JoinTable
    @ManyToMany
    private List<Customers> customersList;

*/


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    private String uuid="";


    @Column(name = "flat_buil_number")
    private String flat_buil_number = "";

    @Column(name = "locality")
    private String locality = "";

    @Column(name = "city")
    private String city = "";

    @Column(name = "pincode")
    private String pincode = "";

    @Column(name = "state_id")
    private int stateUuid = 0;

    @Column(name = "active")
    private int active = 0;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

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


    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getStateUuid() {
        return stateUuid;
    }

    public void setStateUuid(int stateUuid) {
        this.stateUuid = stateUuid;
    }

 /*   public List<Customers> getCustomersList() {
        return customersList;
    }

    public void setCustomersList(List<Customers> customersList) {
        this.customersList = customersList;
    }*/
}
