package com.pes.financemanager.pesfinancemanager.repository;

import com.pes.financemanager.pesfinancemanager.model.FinancialProfile;
import com.pes.financemanager.pesfinancemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FinancialProfileRepository extends JpaRepository<FinancialProfile, Long> {
    Optional<FinancialProfile> findByUser(User user);
    Optional<FinancialProfile> findByUserId(Long userId);
}
