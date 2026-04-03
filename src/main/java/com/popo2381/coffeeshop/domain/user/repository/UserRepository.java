package com.popo2381.coffeeshop.domain.user.repository;

import com.popo2381.coffeeshop.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}