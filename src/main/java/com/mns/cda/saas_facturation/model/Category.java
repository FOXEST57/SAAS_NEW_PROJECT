package com.mns.cda.saas_facturation.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long catId;

    @NotBlank
    protected String catName;


    @ManyToOne
    @JoinColumn(name = "cat_parent_id", nullable = true)
    protected Category catParent;

    @OneToMany(mappedBy = "catParent")
    protected List<Category> catChildren;
}
