package com.licenta.aplicatie.auth;

import com.licenta.aplicatie.models.TeachingStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class SubjectRequest {

    private String name;
    private String acronim;
    private List<Integer> teacherIds;


    private List<String> teachingStyles;

    public String getName(){
        return this.name;
    }

    public String getAcronim(){
        return this.acronim;
    }

    public List<Integer> getTeacherIds() {
        return this.teacherIds != null ? this.teacherIds : new ArrayList<>();
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setAcronim(String acronim) {
        this.acronim = acronim;
    }

    public void setTeacherIds(List<Integer> teacherIds) {
        if (teacherIds != null) {
            this.teacherIds = teacherIds;
        } else {
            this.teacherIds = new ArrayList<>();
        }
    }

    public List<String> getTeachingStyles() {
        return this.teachingStyles != null ? this.teachingStyles : new ArrayList<>();
    }

    public void setTeachingStyles(List<String> teachingStyles) {
        if (teachingStyles != null) {
            this.teachingStyles = teachingStyles;
        } else {
            this.teachingStyles = new ArrayList<>();
        }
    }
}


