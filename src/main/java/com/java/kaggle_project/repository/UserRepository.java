package com.java.kaggle_project.repository;

import com.java.kaggle_project.object.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "select * From dbo.users where email = ?1", nativeQuery=true)
    public User findByEmail(String email);

}