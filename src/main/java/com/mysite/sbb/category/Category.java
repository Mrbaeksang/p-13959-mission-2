package com.mysite.sbb.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique=true, nullable=false)
    private String name;
    private String description;

}
