package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	public void createUserShouldWork() throws Exception {
		String requestBody = "{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"passwordValid4\",\"phones\":[{\"number\":\"123456789\",\"countrycode\":\"1\",\"citycode\":\"123\"}]}";

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").exists())
				.andExpect(jsonPath("$.created").exists())
				.andExpect(jsonPath("$.modified").exists())
				.andExpect(jsonPath("$.last_login").exists())
				.andExpect(jsonPath("$.jwt").exists())
				.andExpect(jsonPath("$.isactive").exists());
	}

	@Test
	public void createUserShouldReturnAnErrorForDuplicatedEmail() throws Exception {
		String requestBody = "{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"passwordValid4\",\"phones\":[{\"number\":\"123456789\",\"countrycode\":\"1\",\"citycode\":\"123\"}]}";

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody));

		mockMvc.perform(post("/users")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestBody))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.mensaje").value("el correo ya existe"));
	}
}
