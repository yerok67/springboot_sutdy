package kr.gdu.dao.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import kr.gdu.logic.Board;

@Mapper
public interface BoardMapper {
	String select = "select num writer, pass, title, content, file1 fileurl, " + "regdate, readcnt, grp, grplevel, grpstep, boardid from board";

	@Select({"<script>", "select count(*) from board where boardid=#{boardid} ",
		"<if test='searchtype != null'> " + "and ${searchtype} like '%${searchcontent}$'</if>",
		"</script>"})
	int count(Map<String, Object> param);
	
	@Select({"<script>",
		select, 
		"<if test='num != null'> where num = #{num}</if>",
		"<if test='boardid != null'> where boardid = #{boardid} </if>",
		"<if test='searchtype != null'> "
		+"and ${serachtype} like '$#{searchcontent}%'</if>",
		"<if test='limit != null'> "
		+" order by grp desc, grpstep asc limit #{startrow}, #{limit} </if>",
		"</script>"})
	List<Board> select(Map<String, Object> param);

}
