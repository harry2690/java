package com.harrywu.springweb.controller;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.harrywu.springweb.common.CommonService;
import com.harrywu.springweb.common.CustomResult;
import com.harrywu.springweb.model.User;

@Controller
public class HelloWorldController extends AbstractController{

	@Inject
	@Named("userService")
	private CommonService<User, Long> userService;


	@RequestMapping("/welcome")
	public ModelAndView helloWorld() {

		String message = "<br><div style='text-align:center;'>"
				+ "<h3>********** Hello World, Spring MVC Tutorial</h3>This message is coming from HelloWorldController.java **********</div><br><br>";
		return new ModelAndView("welcome/index", "message", message);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public @ResponseBody
	CustomResult<User> adduser(@PathVariable Long id) throws Exception {
		return userService.query(id, null);
	}

	@RequestMapping(value = "/{userName}", method = RequestMethod.POST)
	public @ResponseBody
	CustomResult<User> adduser(@PathVariable String userName) throws Exception {
		User user = new User();
		user.setUserId(100);
		user.setFirstName(userName);
		user.setLastName("Boo");
		user.setAge(25);
		user.setCreatedDate(new Date());
		return userService.insert(user, null);
	}

	@RequestMapping(value = "/{id}/{userName}", method = RequestMethod.PUT)
	public @ResponseBody
	CustomResult<User> modiftyuser(@PathVariable Long id, @PathVariable String userName) throws Exception {
		User savedUser = userService.query(id, null).getResult();
		savedUser.setFirstName(userName);
		savedUser.setAge(24);
		return userService.update(savedUser,null);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public @ResponseBody
	CustomResult<Object> deleteuser(@PathVariable Long id) throws Exception {
		User deleteUser = userService.query(id,null).getResult();
		return userService.delete(deleteUser,null);
	}
}
