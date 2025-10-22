package com.example.version1.repository;

import com.example.version1.model.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryLocal extends JpaRepository<Local, Long> {
}
