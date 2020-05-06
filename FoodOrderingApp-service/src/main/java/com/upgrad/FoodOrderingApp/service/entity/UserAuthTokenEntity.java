package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;




@Entity
@Table(name = "customer_auth")
@SqlResultSetMapping(name="deleteResult", columns = { @ColumnResult(name = "count")})
@NamedNativeQueries({
        @NamedNativeQuery(
                name    =   "deleteAuthTokenById",
                query   =   "DELETE FROM customer_auth WHERE id = ?1",resultSetMapping = "deleteResult"
        )
})



@NamedQueries({

        @NamedQuery(name = "userAuthByAccessToken", query = "select ut from UserAuthTokenEntity ut where ut.accessToken = :accessToken "),
        @NamedQuery(name = "userAuthTokenByUUID", query = "select ut from UserAuthTokenEntity ut where ut.customer.id = :id "),
        @NamedQuery(name = "deleteById", query = "select ut from UserAuthTokenEntity ut where ut.id = :id ")


})
public class UserAuthTokenEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "uuid")
    String uuid;

    @Column(name = "access_token")
    String accessToken;

    @Column(name = "expires_at")
    ZonedDateTime expiresAt;

    @Column(name = "login_at")
    ZonedDateTime loginAt;

    @Column(name = "logout_at")
    ZonedDateTime logoutAt;

    @ManyToOne
    @NotNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public ZonedDateTime getLoginAt() {
        return loginAt;
    }

    public void setLoginAt(ZonedDateTime loginAt) {
        this.loginAt = loginAt;
    }

    public ZonedDateTime getLogoutAt() {
        return logoutAt;
    }

    public void setLogoutAt(ZonedDateTime logoutAt) {
        this.logoutAt = logoutAt;
    }
}
