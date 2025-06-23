package kr.gdu.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import kr.gdu.logic.Item;
import kr.gdu.service.ShopService;


@Controller   //@Component + Controller 기능. 객체화됨
@RequestMapping("item")  
public class itemController {
	@Autowired
	private ShopService service;

	@RequestMapping("list") //Get, Post 방식 요청시 호출 
	public ModelAndView list() {
		ModelAndView mav = new ModelAndView();
		List<Item> itemList = service.itemList();
		mav.addObject("itemList",itemList);
		return mav;
	}
	@GetMapping({"detail","update","delete"})
	public ModelAndView detail(Integer id) { 
		ModelAndView mav = new ModelAndView();
		Item item = service.getItem(id);
		if (item == null) {
	        throw new IllegalArgumentException("상품이 존재하지 않음: id=" + id);
	    }
		mav.addObject("item",item);
		return mav;
	}
	@GetMapping("create")  //Get 방식 요청
	public ModelAndView create() {
		ModelAndView mav = new ModelAndView();
		mav.addObject(new Item());
		return mav;
	}
	@PostMapping("create")
	public ModelAndView register(@Valid Item item,BindingResult bresult,
			HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		if(bresult.hasErrors()) { //입력값 검증시 오류 발생
			return mav;  // item/create.jsp 페이지로 이동
		}
		service.itemCreate(item,request); //db에 등록 + 이미지파일 업로드
		mav.setViewName("redirect:list");  //list 재 요청
		return mav;
	}
}
