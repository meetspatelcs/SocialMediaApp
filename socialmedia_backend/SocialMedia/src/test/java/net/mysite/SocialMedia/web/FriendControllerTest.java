package net.mysite.SocialMedia.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mysite.SocialMedia.domain.Friend;
import net.mysite.SocialMedia.domain.User;
import net.mysite.SocialMedia.service.FriendService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FriendService friendService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testAddFriend() throws Exception{
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        Date newDate = formatter.parse("10-10-2000");
//
//        User user = new User();
//        user.setId(1L);
//        user.setUsername("testuser@gmail.com");
//        user.setFirstname("test");
//        user.setLastname("user");
//        user.setIdentification("testuser123");
//        user.setDob(newDate);
//        user.setPassword("abc");
//        user.setCreatedOn(LocalDate.now());
//
//        User user2 = new User();
//        user2.setId(2L);
//        user2.setUsername("testuser2@gmail.com");
//        user2.setFirstname("testa");
//        user2.setLastname("usera");
//        user2.setIdentification("testuser1234");
//        user2.setDob(newDate);
//        user2.setPassword("abc");
//        user2.setCreatedOn(LocalDate.now());
//
//        Friend friend = new Friend();
//        friend.setUser(user);
//        friend.setRequestedUser(user2);
//        friend.setStatus("Pending");
//
//        given(friendService.save(friend)).willReturn(friend);
//
//        mockMvc.perform(post("/add/"))
    }
}
