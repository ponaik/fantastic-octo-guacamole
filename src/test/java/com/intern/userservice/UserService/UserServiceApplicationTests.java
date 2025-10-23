package com.intern.userservice.UserService;

import com.intern.userservice.dto.UserCreateDto;
import com.intern.userservice.dto.UserResponse;
import com.intern.userservice.model.User;
import com.intern.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class   UserServiceApplicationTests {

    @Autowired
    private UserService userservice;

	@Test
	void contextLoads() {

        UserCreateDto create = new UserCreateDto("bob", "bob", LocalDate.of(1928, 6, 6),
                "bobthebuilder@email.com");

        UserResponse user = userservice.createUser(create);

        System.out.println(user);
    }

}
