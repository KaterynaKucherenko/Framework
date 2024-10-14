import com.mjc.school.repository.implementation.UserRepository;
import com.mjc.school.repository.model.Role;
import com.mjc.school.repository.model.UserModel;
import com.mjc.school.service.dtoForUser.JwtAuthenticationResponse;
import com.mjc.school.service.dtoForUser.SignInRequest;
import com.mjc.school.service.dtoForUser.SignUpRequest;
import com.mjc.school.service.services.JwtTokenService;
import com.mjc.school.service.services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private PasswordEncoder passwordEncoder;
    private static SignUpRequest signUpRequest;
    private static SignInRequest signInRequest;
    private static UserModel userModel;
    @InjectMocks
    private static UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, jwtTokenService, authenticationManager);
        userModel = UserModel.builder()
                .username("Barbara")
                .password("password123")
                .role(Role.ROLE_USER)
                .build();
        signUpRequest = new SignUpRequest("Barbara", "password123");
        signInRequest = new SignInRequest("Barbara", "password123");

    }

    @Test
    public void signUpTest() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.findByUsername(signUpRequest.username())).thenReturn(Optional.empty());
        String jwtToken = "jwtToken";
        when(jwtTokenService.generateToken(any(UserModel.class))).thenReturn(jwtToken);
        JwtAuthenticationResponse response = userService.signUp(signUpRequest);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).findByUsername(signUpRequest.username());
        verify(userRepository).save(any(UserModel.class));
        verify(jwtTokenService).generateToken(any(UserModel.class));
        assertEquals(jwtToken, response.getToken());
    }

    @Test
    public void signInTest() {
        String jwtToken = "jwtToken";
        when(authenticationManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("Barbara", "password123"));
        when(userRepository.findByUsername(signInRequest.username())).thenReturn(Optional.of(userModel));
        when(jwtTokenService.generateToken(any())).thenReturn(jwtToken);
        JwtAuthenticationResponse response = userService.signIn(signInRequest);
        assertEquals(jwtToken, response.getToken());
    }

}
