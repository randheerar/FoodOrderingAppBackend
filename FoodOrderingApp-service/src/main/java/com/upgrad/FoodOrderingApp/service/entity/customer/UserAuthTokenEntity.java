package com.upgrad.FoodOrderingApp.service.entity.customer;

import javax.persistence.*;
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

        @NamedQuery(name = "userAuthTokenByAccessToken", query = "select ut from UserAuthTokenEntity ut where ut.access_token = :accessToken "),
        @NamedQuery(name = "userAuthTokenByUUID", query = "select ut from UserAuthTokenEntity ut where ut.uuid = :uuid "),
        @NamedQuery(name = "deleteById", query = "select ut from UserAuthTokenEntity ut where ut.id = :id ")


})
public class UserAuthTokenEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(name = "UUID")
    String uuid;
    @Column(name = "CUSTOMER_ID")
    int user_id;
    @Column(name = "ACCESS_TOKEN")
    String access_token;
    @Column(name = "EXPIRES_AT")
    ZonedDateTime expires_at;
    @Column(name = "LOGIN_AT")
    ZonedDateTime login_at;
    @Column(name = "LOGOUT_AT")
    ZonedDateTime logout_at;


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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public ZonedDateTime getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(ZonedDateTime expires_at) {
        this.expires_at = expires_at;
    }

    public ZonedDateTime getLogin_at() {
        return login_at;
    }

    public void setLogin_at(ZonedDateTime login_at) {
        this.login_at = login_at;
    }

    public ZonedDateTime getLogout_at() {
        return logout_at;
    }

    public void setLogout_at(ZonedDateTime logout_at) {
        this.logout_at = logout_at;
    }
}
