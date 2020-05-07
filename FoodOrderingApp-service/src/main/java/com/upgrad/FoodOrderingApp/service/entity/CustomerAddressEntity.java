package com.upgrad.FoodOrderingApp.service.entity;

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
                query = "select ca FROM CustomerAddressEntity ca where ca.customer = :customer"),
        @NamedQuery(
                name = "getCustomerAddressByAddress",
                query = "select ca from CustomerAddressEntity ca where ca.address = :address")
})



public class CustomerAddressEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }
}
