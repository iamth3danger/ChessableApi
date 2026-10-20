package com.chessable.library.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
import com.chessable.library.entity.Account;
import org.springframework.data.repository.query.Param;

public interface AccountRepo extends JpaRepository<Account, UUID> {

    @Query("SELECT a.id FROM Account a WHERE a.newUser = true")
    List<UUID> findAllByNewUserTrue();

    @Query("SELECT a FROM Account a WHERE a.id IN :ids")
    List<Account> findByIDInAccounts(@Param("ids") List<UUID> ids);


}
