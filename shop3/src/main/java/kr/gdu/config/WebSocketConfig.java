package kr.gdu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

//import kr.gdu.websocket.EchoHandler;
//
//@Configuration
//@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
//	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//		// chatting : 웹소켓에서 호출되는 url 호출 정보
//		// ws://localhost:8080/chatting2
//		registry.addHandler(new EchoHandler(), "chatting").setAllowedOrigins("*");
	}
}
