package com.licenta.aplicatie.models;

import jakarta.persistence.*;

@Entity
@Table(name="_admin")
public class Admin {

    @Id
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "created_by_superadmin_id", nullable = false)
    private SuperAdmin createdBySuperAdmin;

    public void setUser(User user) {
        this.user = user;
    }

    public void setCreatedBySuperAdmin(SuperAdmin superAdmin) {
        this.createdBySuperAdmin = superAdmin;
    }

    public void setId(Integer id) {
        this.id=id;
    }
}
