package com.org.https.api.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.https.api.model.user.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {

}
