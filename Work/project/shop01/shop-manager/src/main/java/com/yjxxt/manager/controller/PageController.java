package com.yjxxt.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 跳转页面
 *
*
 * @since 1.0.0
 */
@Controller
public class PageController {
	/**
	 * 公共页面跳转 restful风格
	 * @param page
	 * @return
	 */
	@RequestMapping("/{page}")
	public String page(@PathVariable String page) {
		System.out.println(page);
		return page;
	}
}