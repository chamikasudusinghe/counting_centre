package com.example.counting_center.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Session {
     @Id @GeneratedValue(generator = "UUID")
     private UUID id;

    private String key;

    public Session(String key){
        this.key = key;
    }
}
