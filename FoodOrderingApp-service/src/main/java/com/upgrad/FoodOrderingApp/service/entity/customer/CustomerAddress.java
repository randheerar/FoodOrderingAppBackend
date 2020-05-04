package com.upgrad.FoodOrderingApp.service.entity.customer;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "customer_address")

@SqlResultSetMapping(name="AddressResultById", columns = { @ColumnResult(name = "id")})
@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "getAddressbyId",
                query   =   "select customer.* as customer,address.* from customer_address inner join customer on customer_address.customer_id=customer.id inner join address on customer_address.address_id=address.id where customer.id=?1",resultSetMapping = "Result"
        ),

})
@NamedQueries({
        @NamedQuery(
                name = "getCustomerAddressByCustomer",
                query = "select ca FROM CustomerAddress ca where ca.customer = :customer"),
        @NamedQuery(
                name = "getCustomerAddressByAddress",
                query = "select ca from CustomerAddress ca where ca.address = :address")
})



public class CustomerAddress implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private Customers customer;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    private Address address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
