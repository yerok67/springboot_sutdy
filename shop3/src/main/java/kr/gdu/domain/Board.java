package kr.gdu.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name="board")
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
}
