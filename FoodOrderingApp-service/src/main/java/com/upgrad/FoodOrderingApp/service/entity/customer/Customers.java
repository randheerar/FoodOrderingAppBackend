package com.upgrad.FoodOrderingApp.service.entity.customer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "customer")

@SqlResultSetMapping(name="Result", columns = { @ColumnResult(name = "count")})

@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "deleteUserById",
                query   =   "DELETE FROM users WHERE id = ?1",resultSetMapping = "Result"
        ),
        @NamedNativeQuery(
                name    =   "editById",
                query   =   "UPDATE customer SET firstname=?1,lastname=?2 where uuid =?3",resultSetMapping = "Result"
        )
})
@NamedQueries(
        {
                @NamedQuery(name = "userByUuid", query = "select u from Customers u where u.uuid = :uuid"),
                @NamedQuery(name = "userByEmail", query = "select u from Customers u where u.email =:email"),
                @NamedQuery(name = "userByPhone", query = "select u from Customers u where u.contact_number =:contact_number"),
                @NamedQuery(name = "userById", query = "select u from Customers u where u.id =:id"),


        }
)
public class Customers implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;

   /* @OneToMany(targetEntity=CustomerAddress.class,mappedBy = "customers",cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<CustomerAddress> customerAddresses = new HashSet<>();


    public Set<CustomerAddress> getCustomerAddresses() {
        return customerAddresses;
    }

    public void setCustomerAddresses(Set<CustomerAddress> customerAddresses) {
        this.customerAddresses = customerAddresses;
    }*/



    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    String uuid;
    @Column(name = "firstname")
    @NotNull
    @Size(max = 30)
    String firstname;
    @Column(name = "lastname")
    @NotNull
    @Size(max = 30)
    String lastname;
    @Column(name = "email")
    @NotNull
    @Size(max = 50)
    String email;
    @Column(name = "password")
    @NotNull
    @Size(max = 255)
    String password;
    @Column(name = "salt")
    @NotNull
    @Size(max = 200)
    String salt;

    @Column(name = "contact_number")
    @Size(max = 30)
    String contact_number;

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

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }
}
