package ex01_hash;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/*
 * 화면에서 아이디, 비밀번호를 입력받기 usercipher 테이블을 읽기 - 아이디없으면 : 아이디없음 출력 - 비밀번호오류 : 비밀번호
 * 오류 - 일치 : 반갑습니다. 이름님 출력
 */
public class Main_03 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gdjdb", "gduser", "1234");
		PreparedStatement pstmt = conn.prepareStatement("select userid,password from useraccount");
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			String id = rs.getString("userid");
			String pass = rs.getString("password");
			if (pass == null) {
				continue;
			}
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String hashpass = "";
			byte[] plain = pass.getBytes();
			byte[] hash = md.digest(plain);
			for (byte b : hash) {
				hashpass += String.format("%02X", b);
			}
			pstmt.close();
			pstmt = conn.prepareStatement("update usercipher set password=? where userid =?");
			pstmt.setString(1, hashpass);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		}

	}
}
