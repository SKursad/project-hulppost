package nl.novi.hulppost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.novi.hulppost.dto.GetUsersDto;
import nl.novi.hulppost.dto.UserDto;
import nl.novi.hulppost.model.User;
import nl.novi.hulppost.repository.UserRepository;
import nl.novi.hulppost.security.CustomUserDetailsService;
import nl.novi.hulppost.security.JwtAuthenticationEntryPoint;
import nl.novi.hulppost.security.JwtAuthenticationFilter;
import nl.novi.hulppost.security.JwtTokenProvider;
import nl.novi.hulppost.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountService accountService;

    @MockBean
    private RequestService requestService;

    @MockBean
    private ReplyService replyService;

    @MockBean
    private AttachmentService attachmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void givenUserObject_whenCreateUser_thenReturnSavedUser() throws Exception {

        // given - precondition or setup
        User user = User.builder()
                .id(1L)
                .username("DummyAdmin")
                .email("Admin@mail.com")
                .password("Test1234")
                .build();
        given(userService.registerAdmin(any(UserDto.class)))
                .willAnswer((invocation)-> invocation.getArgument(0));

        // when - action that's under test
        ResultActions response = mockMvc.perform(post("/auth/registration/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // then - verify output
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.username",
                        is(user.getUsername())))
                .andExpect(jsonPath("$.email",
                        is(user.getEmail())))
                .andExpect(jsonPath("$.password",
                        is(user.getPassword())));

    }

    // JUnit test for Get All Users REST API
    @Test
    public void givenListOfUsers_whenGetAllUsers_thenReturnUsersList() throws Exception{
        // given
        List<GetUsersDto> listOfUsers = new ArrayList<>();
        listOfUsers.add(GetUsersDto.builder()
                .username("Kursad")
                .email("Kurshda85@gmail.com")
                .build());
        listOfUsers.add(GetUsersDto.builder()
                .username("Test")
                .email("Test@gmail.com")
                .build());
        given(userService.getAllUsers()).willReturn(listOfUsers);

        // when
        ResultActions response = mockMvc.perform(get("/hulppost/users"));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()",
                        is(listOfUsers.size())));

    }

    // positive scenario - valid user id
    // JUnit test for GET user by id REST API
    @Test
    public void givenUserId_whenGetUserById_thenReturnUserObject() throws Exception{

        // given
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .username("Kursad")
                .email("Kurshad85@gmail.com")
                .password("Test1234A")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.of(userDto));

        // when
        ResultActions response = mockMvc.perform(get("/hulppost/users/{userId}", userId));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.username", is(userDto.getUsername())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())))
                .andExpect(jsonPath("$.password", is(userDto.getPassword())));

    }

    // negative scenario - valid user id
    // JUnit test for GET user by id REST API
    @Test
    public void givenInvalidUserId_whenGetUserById_thenReturnEmpty() throws Exception{

        // given
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .username("Kursad")
                .email("Kurshad85@gmail.com")
                .password("Test1234A")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.empty());

        // when
        ResultActions response = mockMvc.perform(get("/hulppost/users/{userId}", userId));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());

    }

    // JUnit test for update user REST API - positive scenario
    @Test
    public void givenUpdatedUser_whenUpdateUser_thenReturnUpdateUserObject() throws Exception{

        // given
        Long userId = 1L;
        UserDto savedUser = UserDto.builder()
                .username("Kursad")
                .email("Kurshad85@gmail.com")
                .build();

        UserDto updatedUser = UserDto.builder()
                .id(1L)
                .username("Salim")
                .email("Kurshad@live.nl")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.of(savedUser));
        given(userService.updateUser(any(UserDto.class), anyLong()))
                .willAnswer((invocation)-> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(put("/hulppost/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));


        // then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(updatedUser.getUsername())))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));
    }

    @Test
    public void givenUpdatedUser_whenUpdateUser_thenReturn404() throws Exception{

        // given
        Long userId = 1L;
        User savedUser = User.builder()
                .username("Kursad")
                .email("Kurshad85@gmail.com")
                .build();

        User updatedUser = User.builder()
                .username("Salim")
                .email("Kurshad@live.nl")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.empty());
        given(userService.updateUser(any(UserDto.class), anyLong()))
                .willAnswer((invocation)-> invocation.getArgument(0));

        // when
        ResultActions response = mockMvc.perform(put("/hulppost/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));

        // then
        response.andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    public void givenUserId_whenDeleteUser_thenReturn200() throws Exception{

        // given
        Long userId = 1L;
        willDoNothing().given(userService).deleteUser(userId);

        // when
        ResultActions response = mockMvc.perform(delete("/hulppost/users/{userId}", userId));

        // then
        response.andExpect(status().isOk())
                .andDo(print());
    }
}