package com.freemind.login.permission.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perm_id")
    private Integer permId;

    @Column(name = "perm_name", length = 50, nullable = false)
    private String permName;

    @Column(name = "perm_detail", length = 200, nullable = false)
    private String permDetail;

    public Permission() {
    }

    public Permission(String permName, String permDetail) {
        this.permName = permName;
        this.permDetail = permDetail;
    }

    public Integer getPermId() {
        return permId;
    }

    public void setPermId(Integer permId) {
        this.permId = permId;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    public String getPermDetail() {
        return permDetail;
    }

    public void setPermDetail(String permDetail) {
        this.permDetail = permDetail;
    }

}
