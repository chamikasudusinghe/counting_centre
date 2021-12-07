package com.example.counting_center.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ElectionResult {

    private  @Id String candidateID;
    private long count;
}
