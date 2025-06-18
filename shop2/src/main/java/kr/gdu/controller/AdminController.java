package kr.gdu.controller;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import kr.gdu.exception.ShopException;
import kr.gdu.logic.Mail;
import kr.gdu.logic.User;
import kr.gdu.service.UserService;

@Controller
@RequestMapping("admin")
public class AdminController {
	@Autowired
	private UserService service;

	@RequestMapping("list")
	public ModelAndView adminCheckList(String sort, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		List<User> list = service.userlist();
		mav.addObject("list", list);
		return mav;
	}

	// 메일 폼
	@GetMapping("mail")
	public ModelAndView mailform(String[] idchks, HttpSession session) {
		ModelAndView mav = new ModelAndView("admin/mail");
		if (idchks == null || idchks.length == 0) {
			throw new ShopException("메일을 보낼 대상자를 선택하세요", "list");
		}
		// list : idchks파라미터에 속한 userid의 User 객체들
		List<User> list = service.getUserList(idchks);
		mav.addObject("list", list);
		Mail mail = new Mail();
		StringBuilder recipient = new StringBuilder();
		for (User u : list) { // test1<test1@aaa.bbb>,
			recipient.append(u.getUsername()).append("<").append(u.getEmail()).append(">,");
		}
		mail.setRecipient(recipient.toString());
		mav.addObject("mail", mail); // recipient 정보만 등록
		return mav;
	}

	// 메일 전송
	@PostMapping("mail")
	public ModelAndView mail(@Valid Mail mail, BindingResult bresult, HttpSession session) {
		ModelAndView mav = new ModelAndView();
		if (bresult.hasErrors()) {
			return mav;
		}
		mav.setViewName("alert");
		if (service.mailSend(mail)) {
			mav.addObject("message", "메일 전송이 완료되었습니다.");
		} else {
			mav.addObject("message", "메일 전송을 실패 했습니다.");
		}
		mav.addObject("url", "list");
		service.mailfileDelete(mail);
		return mav;
	}

}