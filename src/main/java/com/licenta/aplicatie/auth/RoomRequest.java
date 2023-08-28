package com.licenta.aplicatie.auth;

import com.licenta.aplicatie.models.RoomType;

public class RoomRequest {

    private String name;
    private Boolean extern;
    private RoomType roomType;

    public String getName() {
        return this.name;
    }

    public Boolean getExtern() {
        return this.extern;
    }

    public RoomType getRoomType() {
        return this.roomType;
    }
}
