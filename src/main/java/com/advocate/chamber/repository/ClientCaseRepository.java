package com.advocate.chamber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.advocate.chamber.model.ClientCase;

@Repository
public interface ClientCaseRepository extends JpaRepository<ClientCase, Long> {
}