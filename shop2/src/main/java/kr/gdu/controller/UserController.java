package kr.gdu.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ProcessBuilder.Redirect;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.List;

import jakarta.validation.Valid;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import kr.gdu.exception.ShopException;
import kr.gdu.logic.Sale;
import kr.gdu.logic.User;
import kr.gdu.service.ShopService;
import kr.gdu.service.UserService;



@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private UserService service;
	@Autowired
	private ShopService shopService;

	@GetMapping("*") // Get 방식 모든 요청시 호출
	public ModelAndView form() {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new User());
		return mav; // url과 연동된 뷰를 호출
	}

	@PostMapping("join")
	// BindingResult은 입력값 검증대상 변수의 다음에 와야함
	public ModelAndView userAdd(@Valid User user, BindingResult bresult) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			// 추가 오류 메세지 등록. global error로 추가하기
			bresult.reject("error.input.user");
			bresult.reject("error.input.check");
			return mav;
		}
		// 정상적으로 입력된 경우
		try {
			service.userInsert(user);
		} catch (DataIntegrityViolationException e) {// 키값 중복된 경우
			e.printStackTrace();
			bresult.reject("error.duplicate.user");
			return mav;
		} catch (Exception e) {
			e.printStackTrace();
			return mav;
		}
		mav.setViewName("redirect:login");
		return mav;
	}

	/*
	 * 1. userid 맞는 User를 db에서 조회하기 2. 비밀번호 검증 일치 :
	 * session.setAttribute("loginUser",dbUser) => 로그인 정보 불일치 : 비밀번호를 확인하세요. 출력
	 * (error.login.password) 3. 비밀번호 일치하는 경우 mypage로 페이지 이동 => 404 오류 발생 (임시)
	 */
	@PostMapping("login")
	public ModelAndView login(User user, BindingResult bresult, HttpSession session) {
		// session : 세션 객체 제공
		if (user.getUserid().trim().length() < 3 || user.getUserid().trim().length() > 10) {
			// @Valid 어노테이션에서 등록 방식으로 처리
			// messages.properties 파일에 error.required.userid로 메세지 등록
			bresult.rejectValue("userid", "error.required");
		}
		if (user.getPassword().trim().length() < 3 || user.getPassword().trim().length() > 10) {
			bresult.rejectValue("password", "error.required");
		}
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) { // 등록된 오류 존재?
			// global error 등록
			bresult.reject("error.input.check");
			return mav;
		}
		User dbUser = service.selectUser(user.getUserid());
		if (dbUser == null) { // 아이디 없음
			bresult.reject("error.login.id");
			return mav;
		}
		if (user.getPassword().equals(dbUser.getPassword())) { // 일치
			session.setAttribute("loginUser", dbUser);
			mav.setViewName("redirect:mypage?userid=" + user.getUserid());
		} else {
			bresult.reject("error.login.password");
			return mav;
		}
		return mav;
	}

	/*
	 * AOP 설정필요 : UserLoginAspect 클래스의 userIdCheck 메서드로 구현 1. 로그여부 검증 로그아웃상태인 경우 로그후
	 * 거래메세지 출력. login 페이지로 이동 2. 본인 거래 여부 검증 admin이 아니면서 다른 사용자 정보 출력 불가
	 */
	@RequestMapping("mypage")
	public ModelAndView idCheckMypage(String userid, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User user = service.selectUser(userid);
		List<Sale> salelist = shopService.saleList(userid);
		mav.addObject("salelist", salelist);
		mav.addObject("user", user);
		return mav;
	}

	@RequestMapping("logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:login";
	}

	// 로그인 상태, 본인정보만 조회 검증 => AOP 클래스(UserLoginAspect.userIdCheck() 검증)
	@GetMapping({ "update", "delete" })
	public ModelAndView idCheckUser(String userid, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		User user = service.selectUser(userid);
		mav.addObject("user", user);
		return mav;
	}

	@PostMapping("update")
	public ModelAndView idCheckUpdate(@Valid User user, BindingResult bresult, String userid, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			bresult.reject("error.update.user"); // global error
			return mav;
		}
		// 비밀번호 검증
		User loginUser = (User) session.getAttribute("loginUser");
		if (!loginUser.getPassword().equals(user.getPassword())) {
			bresult.reject("error.login.password");
			return mav;
		}
		// 비밀번호 일치한 경우 실행
		try {
			service.userUpdate(user);
			if (loginUser.getUserid().equals(user.getUserid())) {
				// 로그인 정보의 데이터를 수정된 데이터로 변경
				session.setAttribute("loginUser", user);
			}
			mav.setViewName("redirect:mypage?userid=" + user.getUserid());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ShopException("고객 정보 수정 실패", "update?userid=" + user.getUserid());
		}
		return mav;
	}

	/*
	 * UserLoginAspect.userIdCheck() 메서드 실행 설정 탈퇴 검증 1. 관리자인 경우 탈퇴 불가 2. 비밀번호 검증 =>
	 * 로그인된 비밀번호와 비교 본인탈퇴시 : 본인 비밀번호로 검증 관리자타인탈퇴 : 관리자 비밀번호로 검증 3. 비밀번호 불일치 메세지 출력 후
	 * delete 페이지로 이동 4. 비밀번호 일치 db에서 사용자 정보 삭제 본인탈퇴 : 로그아웃. login 페이지로 이동 관리자 타인 탈퇴
	 * : admin/list 페이지 이동
	 * 
	 */
	@PostMapping("delete")
	public ModelAndView idCheckDelete(String password, String userid, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		// 관리자 탈퇴 불가
		if (userid.equals("admin")) {
			throw new ShopException("관리자는 탈퇴 불가합니다.", "mupage?userid=" + userid);
		}
		User loginUser = (User) session.getAttribute("loginUser");
		if (!password.equals(loginUser.getPassword())) {
			throw new ShopException("비밀번호를 확인하세요.", "delete?userid=" + userid);
		}
		try {
			service.delete(userid);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ShopException("탈퇴 시 오류 발생", "delete?userid=" + userid);
		}
		// 탈퇴 성공
		String url = null;
		if (loginUser.getUserid().equals("admin")) {
			url = "redirect:../admin/list";
		} else {
			session.invalidate();
			url = "redirect:login";
		}
		mav.setViewName(url);
		return mav;
	}

	/*
	 * 본인의 비밀번호만 변경 가능 1. 로그인 검증 => AOP 클래스 UserLoginAspect.loginCheck() pointcut :
	 * UserController.loginCheck*(..) 인 메서드이고, 마지막 변수가 HttpSession인 메서드 advice :
	 * around 2.현재 db의 비밀번호와 파라미터 현재비밀번호 일치 검증 3. 비밀번호 일치 : db 수정. 로그인정보 변경. mypage
	 * 페이지로 이동 4. 비밀번호 불일치 : 오류 메세지 출력. password 페이지로 이동
	 */
	@GetMapping("password")
	public String loginCheckform(HttpSession session) {
		return null;
	}

	@PostMapping("password")
	public String loginCheckPassword(String password, String chgpass, HttpSession session) {
		User loginUser = (User) session.getAttribute("loginUser");
		// password : 입력한 현재 비밀번호
		// loginUser.getPasswrod() : 로그인 정보. db에 저장된 정보
		if (!password.equals(loginUser.getPassword())) {
			throw new ShopException("비밀번호 오류 입니다..", "password");
		}
		try {
			service.userChgpass(loginUser.getUserid(), chgpass);
			loginUser.setPassword(chgpass);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ShopException("비밀번호 수정 실패", "password");
		}
		return "redirect:mypage?userid=" + loginUser.getUserid();
	}

	@PostMapping("{url}search")
	public ModelAndView search(User user, BindingResult bresult, @PathVariable String url) {
		// @PathVariable : {url}의 값을 매개변수로 전달.
		// idsearch 요청 : url = id
		// pwsearch 요청 : url = pw
		ModelAndView mav = new ModelAndView();
		String code = "error.userid.search";
		String title = "아이디 찾기";
		if (url.equals("pw")) {
			title = "비밀번호 초기화";
			code = "error.password.search";
			if (user.getUserid() == null || user.getUserid().trim().equals("")) {
				bresult.rejectValue("userid", "error.required");
			}
			service.getPwReset(user);
		}
		if (user.getEmail() == null || user.getEmail().trim().equals("")) {
			bresult.rejectValue("email", "error.required");
		}
		if (user.getPhoneno() == null || user.getEmail().trim().equals("")) {
			bresult.rejectValue("phoneno", "error.required");
		}
		if (bresult.hasErrors()) {
			bresult.reject("error.input.check");
			return mav;
		}
		if (user.getUserid() != null && user.getUserid().trim().equals("")) {
			user.setUserid(null);
		}
		String result = service.getSearch(user);
		if (result == null) {
			bresult.reject(code);
			return mav;
		}
		mav.addObject("result", result);
		mav.addObject("title", title);
		mav.setViewName("search");
		return mav;
	}
	@GetMapping("login")
	public ModelAndView loginForm(HttpSession session) {
		ModelAndView mav = new ModelAndView();
		String clientId="네이버의 client Id";
		clientId = "WtxSqXVWVvZt5Dh9CGFz";
		String redirectURL = null;
		try {
			redirectURL = URLEncoder.encode("http://localhost:8080/user/naverlogin","UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SecureRandom random = new SecureRandom();
		String state = new BigInteger(130,random).toString();
		String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		apiURL += "&client_id=" + clientId;
		apiURL += "&redirect_uri=" + redirectURL;
		apiURL += "&state=" + state;
		mav.addObject(new User());
		mav.addObject("apiURL", apiURL);
		session.getServletContext().setAttribute("session", session);
		System.out.println("1.session.id=" + session.getId());
		return mav;
	}
	
	@RequestMapping("naverlogin")
	public String naverlogin(String code, String state, HttpSession session) {
		System.out.print("2.sesion.id=" + session.getId());
		String clientId = "클라이언트 아이디값";
		clientId = "WtxSqXVWVvZt5Dh9CGFz";
		String clientSecret = "클라이언트 시크릿값";
		clientSecret = "L_Y_ojolCj";
		String redirectURI = null;
		try {
			redirectURI = URLEncoder.encode("YOUR_CALLBACK_URL","UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String apiURL;
		apiURL = "https://nid.naver.com/oauth2.0/token?grant_type=authorization_code";
		apiURL += "&client_id=" + clientId;
		apiURL += "&client_secret=" + clientSecret;
		apiURL += "&redirect_uri=" + redirectURI;
		apiURL += "&code=" + code;
		apiURL += "&state=" + state;
		System.out.println("code=" + code + ", sate=" + state);
		StringBuffer res = new StringBuffer();
		System.out.println("apiURL=" + apiURL);
		try {
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			int responeseCode = con.getResponseCode();
			BufferedReader br;
			if(responeseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				res.append(inputLine);
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		// JSON 형태의 묹바열 데이터 => json 객체로 변경하기 위한 객체 생성
		JSONParser parser = new JSONParser();
		JSONObject json=null;
		try {
			json = (JSONObject)parser.parse(res.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String token = (String)json.get("access_token");
		String header = "Bearer " + token;
		try {
			apiURL = "https://openapi.naver.com/v1/nid/me";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Authorization", header);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			res = new StringBuffer();
			if(responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			while((inputLine = br.readLine()) != null){
				res.append(inputLine);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			json = (JSONObject)parser.parse(res.toString());
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ShopException("네이버 로그인시 오류 발생", "login");
		}
		JSONObject jsondetail = (JSONObject)json.get("response");
		String userid = jsondetail.get("id").toString();
		User user = service.selectUser(userid);
		if (user == null) {
		    user = new User();
		    user.setUserid(userid);
		    user.setUsername(jsondetail.get("name").toString());
		    String email = jsondetail.get("email").toString();
		    user.setEmail(email);
		    user.setChannel("naver");
		    service.userInsert(user);
		}
		session.setAttribute("loginUser", user);
		return "redirect:mypage?userid="+user.getUserid();

	}
}
