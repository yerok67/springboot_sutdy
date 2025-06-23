package kr.gdu.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import kr.gdu.service.ShopService;

public class CountScheduler {
	@Autowired
	ShopService service;

	private int cnt;

	/*
	 * execute 1 : 5초마다 실행되는 메서드
	 *     @Scheduled(cron = "0/5 * * * * ?")
	 *     @Scheduled(fixedRate = 5000)  : 밀리초 단위. 이전작업의 시작지점부터 고정간격
	 *     @Scheduled(fixedDelay = 5000) : 밀리초 단위. 이전작업의 종료지점부터 고정간격
	 *     
	 */

	// @Scheduled(fixedRate = 5000)
	public void execute1() {
		System.out.println("cnt:" + cnt++);
	}

	// @Scheduled(initialDelay = 3000, fixedRate = 5000)
	public void execute2() {
		System.out.println("실행 후 3초 후 첫번째 실행");
		System.out.println("5초마다 실행");
	}
	/*
	 * cron
	 *   1. 특정시간, 주기적으로 프로그램을 실행.
	 *   2. 리눅스에서 crontab 명령으로 설정 가능
	 *   
	 *   cron="0/5 * * * *?" => 5초 마다 실행
	 *   
	 *   형식 : 초 분 시 일 월 요일 [년도]
	 *     초 : 0 ~ 59
	 *     분 : 0 ~ 59
	 *     시 : 0 ~ 23
	 *     일 : 1 ~ 31
	 *     월 : 1 ~ 12
	 *     요일 : 1 ~ 7
	 *     
	 *    표현 방식
	 *     * : 매번
	 *     A / B : 주기 A ~ B 마다 실행. 0/5 => 0 ~ 5초 마다 실행
	 *     ? : 설정 없음
	 *     
	 *     
	 *    cron 예시
	 *     0/10 * * * * ?   : 10초마다 한번씩
	 *     0 0/1 * * * ?    : 1분마다 한번씩
	 *     0 20,50 * * * ? : 매시간 20분, 50분 마다 실행
	 *     0 0 0/3 * * ?    : 3시간 마다 한번씩 실행
	 *     0 0 12 ? * 1     : 월요일 12시에 실행
	 *     0 0 12 ? * MON   : 월요일 12시에 실행
	 *     0 0 10 ? * 6,7   : 주말 10시에 실행
	 *     
	 *   corn 작성 사이트 : www.cronmaker.com     
	 *     
	 */
	// 6월 23일 12시 20분

	@Scheduled(cron = "0 20 12 23 6 ?")
	public void execute3() {
		System.out.println("execute3(): 6시 23일 12시 20분");
	}

	/*
	1. 평일 아침 10시 30분에 환율 정보를 조회하여 db에 등록하기
	2. exchange 테이블 생성하기
	
	create table exchange (
		eno int primary key auto_increment,   -- 키값
		code varchar(10),                     -- 통화코드
		name varchar(50),                     -- 통화명
		sellamt float,                        -- 매도율
		buyamt float,                         -- 매입율
		priamt float,                         -- 기준율
		edate varchar(10)                     -- 환율기준일
	)
	
	
	 */
	@Scheduled(cron = "0 30 10 * * 1,2,3,4,5")
	public void exchange() {
		service.exchageCreate();
	}
}
