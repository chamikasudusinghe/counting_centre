package com.example.counting_center.repositories;

import com.example.counting_center.entities.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    Long countAllByVotedForAndAcceptedTrue(String votedFor);
    Long countAllByBallotIdAndAcceptedTrue(String ballotID);

    @Query("SELECT votedFor FROM Vote GROUP BY votedFor")
    List<String> getAllByVotedForGroup();
}
