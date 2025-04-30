package com.petshop.crmbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;    // ✨ 缺少这个
import java.sql.Connection;     // ✨ 缺少这个

@RestController
@RequestMapping("/test")
public class TestController {

    private final DataSource dataSource;

    public TestController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/db")
    public String testDBConnection() {
        try (Connection conn = dataSource.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                return "✅ 数据库连接成功！";
            } else {
                return "❌ 数据库连接失败！";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "❌ 数据库连接异常！";
        }
    }
}
