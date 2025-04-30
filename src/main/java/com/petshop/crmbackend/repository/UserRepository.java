package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsernameAndPhone(String username, String phone);
    boolean existsByUsername(String username);

    // 根据用户名查找用户
    User findByUsername(String username);

}
