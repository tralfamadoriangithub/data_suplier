package com.example.data_suplier.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "information")
@Data
//@Builder
public class Information {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    public Information() {

    }
}
