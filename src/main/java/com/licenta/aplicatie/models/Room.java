package com.licenta.aplicatie.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_room")
public class Room {

    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private Boolean extern;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    @JsonBackReference("department-room")
    @ManyToOne
    @JoinColumn(name="department_id", nullable = false)
    private Department department;

}