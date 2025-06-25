package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.gdu.logic.User;

public interface UserRepository extends JpaRepository<User,String>{
}
