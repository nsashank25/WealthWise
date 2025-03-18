package com.pes.financemanager.pesfinancemanager.repository;

import com.pes.financemanager.pesfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); // Custom query method
}