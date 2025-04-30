package com.petshop.crmbackend.controller;

import com.petshop.crmbackend.common.ApiResponse;
import com.petshop.crmbackend.dto.LoginResponse;
import com.petshop.crmbackend.dto.RegisterRequest;
import com.petshop.crmbackend.entity.User;
import com.petshop.crmbackend.model.LoginRequest;
import com.petshop.crmbackend.repository.UserRepository;
import com.petshop.crmbackend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/auth")
@Tag(name = "认证模块")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "用户注册接口")
    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        String username = registerRequest.getUsername();
        String phone = registerRequest.getPhone();


        if (userRepository.existsByUsername(username)) {
            return ApiResponse.error(400, "用户名已存在");
        }

        if (userRepository.existsByUsernameAndPhone(username, phone)) {
            return ApiResponse.error(400, "用户已存在");
        }

        // 生成5位随机userId
        int userId = generateRandomUserId();
        // 密码加密
        String encryptedPassword = passwordEncoder.encode(registerRequest.getPassword());

        // 创建用户对象
        User user = new User();
        user.setUserId(String.valueOf(userId)); // 直接用生成的userId
        user.setUsername(username);
        user.setPasswordHash(encryptedPassword);
        user.setPhone(phone);
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setRole("customer");
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        // 返回注册成功 + 简要信息
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("userId", user.getUserId());
        responseData.put("username", user.getUsername());

        return ApiResponse.success("注册成功", responseData);
    }

    // 生成5位随机数
    private int generateRandomUserId() {
        Random random = new Random();
        return 10000 + random.nextInt(90000);
    }


    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // 查询用户
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return ApiResponse.error(400, "用户不存在");
        }

        // 校验密码
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ApiResponse.error(400, "用户名或密码错误");
        }

        // 生成Token
        String token = JwtUtil.generateToken(username);

        // 正确顺序地封装
        LoginResponse loginResponse = new LoginResponse(
                user.getUserId(),  // userId
                user.getUsername(),  // username
                user.getRole(),  // role
                token  // token
        );

        return ApiResponse.success("登录成功", loginResponse);
    }


    @Operation(summary = "用户退出登录接口")
    @PostMapping("/logout")
    public ApiResponse<?> logout() {
        return ApiResponse.success("退出登录成功", null);
    }


}





