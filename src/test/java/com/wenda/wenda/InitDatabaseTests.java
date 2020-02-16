package com.wenda.wenda;

import com.wenda.wenda.dao.UserDao;
import com.wenda.wenda.model.User;

import com.wenda.wenda.service.WendaServive;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class InitDatabaseTests {
	@Autowired
	UserDao userDao;
	@Autowired
	WendaServive wendaServive;

	@Test
	public void initDatabase() {
		for (int i = 0; i < 10; i++) {
			User user = new User();
			user.setName(String.format("user%d",i));
			user.setPassword("");
			user.setSalt("111");
			user.setHeadUrl("q111");
			wendaServive.getMessage(2);
			userDao.addUser(user);

		}
	}

}
