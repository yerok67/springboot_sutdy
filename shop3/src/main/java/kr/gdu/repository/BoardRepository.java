package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.gdu.domain.Board;
//JpaSpecificationExecutor<Board> : 쿼리를 위한 조건 사용할 수 있는 권한.
public interface BoardRepository extends JpaRepository<Board, Integer>,
                                JpaSpecificationExecutor<Board>{

}
