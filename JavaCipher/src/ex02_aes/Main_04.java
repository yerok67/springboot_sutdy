package ex02_aes;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/*
 * useraccount 테이블 email을 읽어서 usercipher 테이블에 암호화하여 저장 1. usercipher 테이블의 email
 * 1. 컬럼의 크기를 1000으로 변경하기 2. key는 userid의 해쉬값(SHA-256)의 앞16자리로 설정한다.
 */
public class Main_04 {
	public static void main(String[] args) throws Exception {
		Class.forName("org.mariadb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/gdjdb", "gduser", "1234");
		PreparedStatement pstmt = conn.prepareStatement("select userid,password from useraccount");
		ResultSet rs = pstmt.executeQuery();
		List<String> emailList = new ArrayList<>();
		while (rs.next()) {
			emailList.add = rs.getString("email");
			if (email == null) {
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
			pstmt.executeUpdate();
		}
	}
}
