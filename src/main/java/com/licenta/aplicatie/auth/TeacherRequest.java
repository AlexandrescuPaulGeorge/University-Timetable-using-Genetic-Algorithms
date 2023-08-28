package com.licenta.aplicatie.auth;

import com.licenta.aplicatie.models.RoomType;
import com.licenta.aplicatie.models.TeacherRank;

public class TeacherRequest {

    private String email;
    private String firstname;
    private String lastname;

    public String getEmail() {
        return this.email;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

}
