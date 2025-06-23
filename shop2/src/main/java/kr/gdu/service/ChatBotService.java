package kr.gdu.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kr.gdu.aop.UserLoginAspect;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ChatBotService {

	private final UserLoginAspect userLoginAspect;

	ChatBotService(UserLoginAspect userLoginAspect) {
		this.userLoginAspect = userLoginAspect;
	}

	public String getChatGPTResponse(String question) throws URISyntaxException, IOException, InterruptedException {
		final String API_KEY = "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";
		final String ENDPOINT = "https://api.openai.com/v1/chat/completions";
		HttpClient client = HttpClient.newHttpClient();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", "gpt-3.5-turbo");
		requestBody.put("messages", new Object[] {
//			new HashMap<String, String>(){{
//					put("role","system");
//					put("content","당신은 전문가 입니다.");
//				}},
				new HashMap<String, String>() {	// 질문 내용
					{
						put("role", "user");
						put("content", question);
					}
				} });
		// JSON 변
		ObjectMapper objectMapper = new ObjectMapper();
		String requestBodyJson = objectMapper.writeValueAsString(requestBody);
		HttpRequest request = HttpRequest.newBuilder().uri(new URI(ENDPOINT)).header("Content-Type", "application/json")
				.header("Authorization", "Bearer " + API_KEY).POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			Map<String, Object> responseBody = objectMapper.readValue(response.body(),
					new TypeReference<Map<String, Object>>() {
					});
			List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
			Map<String, Object> firstChoice = choices.get(0);
			Map<String, String> message = (Map<String, String>) firstChoice.get("message");
			return message.get("content");
		} else {
			throw new RuntimeException("API 요청 실패: " + response.body());
		}
	}
}
