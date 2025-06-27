package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import kr.gdu.domain.Board;

// JpaSpecificationExecutor<Board> : 쿼리를 위한 조건 사용할 수 있는 권한.
public interface BoardRepository extends JpaRepository<Board, Integer>, JpaSpecificationExecutor<Board> {

	@Query("Select COALESCE(MAX(b.num),0) FROM Board b")
	int maxNum();

}
