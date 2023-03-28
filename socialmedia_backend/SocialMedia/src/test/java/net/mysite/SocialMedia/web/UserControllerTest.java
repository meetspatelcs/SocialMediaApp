package net.mysite.SocialMedia.web;

import net.mysite.SocialMedia.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.mysite.SocialMedia.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateUser() throws Exception {
        String date = "2000-10-10";

        UserDto userDto = new UserDto();
        userDto.setFirstname("test");
        userDto.setLastname("user");
        userDto.setUsername("testuser@gmail.com");
        userDto.setPassword("abc");
        userDto.setCountry("US");
        userDto.setMyState("CA");
        userDto.setIdentification("testuser123");
        userDto.setDob(date);

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("User created successfully!"));

    }

    private static String asJsonString(Object obj) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(obj);
    }
}
