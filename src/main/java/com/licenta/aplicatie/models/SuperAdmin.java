package com.licenta.aplicatie.models;

import jakarta.persistence.*;

@Entity
@Table(name="_superadmin")
public class SuperAdmin {

    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

}
