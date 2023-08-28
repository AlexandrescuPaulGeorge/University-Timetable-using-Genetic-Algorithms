package com.licenta.aplicatie.models;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "role_tbl")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer role_id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(name = "role_name", length = 60)
    private RoleName roleName;


    public RoleName getRoleName() {
        return roleName;
    }


    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {

        return "Roles [role=" + this.role_id.toString() + ", " + this.roleName.toString() + "]";
    }

}