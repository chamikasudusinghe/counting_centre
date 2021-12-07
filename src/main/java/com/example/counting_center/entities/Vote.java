package com.example.counting_center.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@ToString
public class Vote {
    private @Id
    @GeneratedValue
    Long id;

    private String ballotId;

    private String votedFor;

    @Setter
    private boolean accepted;

    @Setter
    @Column(columnDefinition="TEXT")
    private String receipt;

    public Vote() {
    }

    public Vote(String ballotId, String votedFor) {
        this.ballotId = ballotId;
        this.votedFor = votedFor;
        this.accepted = false;
    }

    public String getHash() {
        // TODO: implement hashing function
        return "123";
    }
}
