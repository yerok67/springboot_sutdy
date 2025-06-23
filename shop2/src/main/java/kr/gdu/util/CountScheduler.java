package kr.gdu.util;

import org.springframework.scheduling.annotation.Scheduled;

public class CountScheduler {
    private int cnt;

    /*
     * execute 1 : 5초마다 실행되는 메서드
     *     @Scheduled(cron = "0/5 * * * * ?")
     *     @Scheduled(fixedRate = 5000)  : 밀리초 단위. 이전작업의 시작지점부터 고정간격
     *     @Scheduled(fixedDelay = 5000) : 밀리초 단위. 이전작업의 종료지점부터 고정간격
     *     
     */
    
    
//    @Scheduled(fixedRate = 5000)
    public void execute1() {
        System.out.println("cnt:" + cnt++);
    }
    
//    @Scheduled(initialDelay = 3000, fixedRate = 5000)
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
     */

}
