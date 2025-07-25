package com.choigoda.choigoda.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.choigoda.choigoda.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
