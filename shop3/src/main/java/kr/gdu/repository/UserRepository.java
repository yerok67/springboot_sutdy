package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.gdu.domain.User;

public interface UserRepository extends JpaRepository<User,String>{
/*
 * Spring Data JPA의 기본 제공 메서드
 *    Optional<User> findById
 *    save(User) : insert, update
 *    countBy..  : 레코드수 반환
 *    deleteById : id에 해당하는 레코드 제거 
 *    
 * 사용자 정의 메서드
 *   @Query("select u from usercipher u where u.userid = :userid") =>자동생성   
 *   findByUserid(String userid) : JPQL 자동 생성
 *   해당 객체가 없는 경우 null로 반환함
 */
	User findByUserid(String userid);
}
