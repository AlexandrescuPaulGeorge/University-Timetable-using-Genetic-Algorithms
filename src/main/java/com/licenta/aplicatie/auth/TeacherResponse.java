package com.licenta.aplicatie.auth;

public class TeacherResponse {

    private String firstname;
    private String lastname;

    public TeacherResponse(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }
}
