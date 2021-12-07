package com.example.counting_center.repositories;

import com.example.counting_center.entities.ElectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<ElectionResult, Long> {

    @Override
    List<ElectionResult> findAll();
}
