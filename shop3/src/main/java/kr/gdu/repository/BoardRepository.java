package kr.gdu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import kr.gdu.domain.Board;
import kr.gdu.dto.BoardDto;

// JpaSpecificationExecutor<Board> : 쿼리를 위한 조건 사용할 수 있는 권한.
public interface BoardRepository extends JpaRepository<Board, Integer>, JpaSpecificationExecutor<Board> {

	@Query("Select COALESCE(MAX(b.num),0) FROM Board b")
	int maxNum();

	@Modifying
	@Transactional
	@Query("Update Board b set b.readcnt = b.readcnt + 1 where b.num = :num")
	void addReadcnt(@Param("num") Integer num);

	@Modifying
	@Query("UPDATE Board b SET b.grpstep = b.grpstep + 1 WHERE b.grp = :grp AND b.grpstep > :grpstep")
	void grpStepAdd(@Param("grp") int grp, @Param("grpstep") int grpstep);

}
