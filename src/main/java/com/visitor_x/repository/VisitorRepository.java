package com.visitor_x.repository;

import com.visitor_x.entity.Visitor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {


    Optional<Object> findByEmail(@Email @NotBlank String email);

    Optional<Object> findByMobileNumber(@NotBlank String mobileNumber);
}