package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
                @NamedQuery(name = "userByUuid", query = "select u from CustomerEntity u where u.uuid = :uuid"),
                @NamedQuery(name = "userByEmail", query = "select u from CustomerEntity u where u.emailAddress =:email"),
                @NamedQuery(name = "userByPhone", query = "select u from CustomerEntity u where u.contactNumber =:contact_number"),
                @NamedQuery(name = "userById", query = "select u from CustomerEntity u where u.id =:id"),


        }
)
public class CustomerEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @NotNull
    @Size(max = 200)
    @Column(name = "uuid", unique = true)
    private String uuid;

    @NotNull
    @Size(max = 30)
    @Column(name = "firstname")
    private String firstName;

    @Size(max = 30)
    @Column(name = "lastname")
    private String lastName;

    @Size(max = 50)
    @Column(name = "email")
    private String emailAddress;

    @NotNull
    @Size(max = 30)
    @Column(name = "contact_number", unique = true)
    private String contactNumber;

    @NotNull
    @Size(max = 255)
    @Column(name = "password")
    private String password;

    @NotNull
    @Size(max = 255)
    @Column(name = "salt")
    private String salt;

    @OneToMany
    @JoinTable(
            name = "customer_address",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id"))
    private List<AddressEntity> address = new ArrayList<>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<AddressEntity> getAddress() {
        return address;
    }

    public void setAddress(List<AddressEntity> address) {
        this.address = address;
    }
}
