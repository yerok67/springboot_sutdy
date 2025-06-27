package kr.gdu.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import kr.gdu.dto.BoardDto;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Data
@NoArgsConstructor
public class Board {
	@Id
	private int num;
	private String boardid;
	private String writer;
	private String pass;
	private String title;
	private String content;
	private String file1; //파일 이름
	private Date regdate;
	private int readcnt;
	private int grp;
	private int grplevel;
	private int grpstep;

	public Board(BoardDto boardDto) {
		this.num = boardDto.getNum();
		this.writer = boardDto.getWriter();
		this.boardid = boardDto.getBoardid();
		this.pass = boardDto.getPass();
		this.title = boardDto.getTitle();
		this.content = boardDto.getContent();
		this.file1 = boardDto.getFileurl();
		this.regdate = boardDto.getRegdate();
		this.readcnt = boardDto.getReadcnt();
		this.grp = boardDto.getGrp();
		this.grplevel = boardDto.getGrplevel();
		this.grpstep = boardDto.getGrpstep();
	}
	@PrePersist
	public void onPrePersist() { //save() 함수 호출직전에 먼저호출
		this.regdate = new Date();
	}
}
