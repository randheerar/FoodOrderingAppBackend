package com.upgrad.FoodOrderingApp.service.entity.customer;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "customer_address")

@SqlResultSetMapping(name="AddressResultById", columns = { @ColumnResult(name = "id")})
@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "getAddressbyId",
                query   =   "select customer.* as customer,address.* from customer_address inner join customer on customer_address.customer_id=customer.id inner join address on customer_address.address_id=address.id where customer.id=?1",resultSetMapping = "Result"
        )
})



public class CustomerAddress implements Serializable {

   /* @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "address_id")
    private  Address address;


    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id")
    private  Customers customers;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }*/

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
