package kr.gdu.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("naver")
public class NaverController {

	@GetMapping("*")
	public String naver() {
		return null;
	}

	@RequestMapping("naversearch")
	@ResponseBody
	public JSONObject naversearch(String data, int display, int start, String type) {
		String clientId = "애플리케이션 클라이언트 아이디값";
		clientId = "KBVAU3luYLn1hvd9_Snu";
		String clientSecret = "애플리케이션 클라이언트 시크릿값";
		clientSecret = "cLXQfU_C1l"; // 애플리케이션 클라이언트 시크릿값
		StringBuffer json = new StringBuffer();
		int cnt = (start - 1) * display + 1;
		String text = null;
		try {
			text = URLEncoder.encode(data, "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/" + type + "?query=" + text + "&display=" + display
					+ "&start=" + cnt;
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-ID", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			}
			String inputLine = null;
			while ((inputLine = br.readLine()) != null) {
				json.append(inputLine);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = null;
		try {
			jsonObj = (JSONObject) parser.parse(json.toString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
}
