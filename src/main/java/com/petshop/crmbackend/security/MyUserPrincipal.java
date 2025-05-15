//package com.petshop.crmbackend.security;
//
//import com.petshop.crmbackend.entity.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Objects;
//
///**
// * 自定义 UserDetails，实现从 users 表中取出的 User 实体抽取必要字段，
// * 并持有数据库主键 id，以便在 Controller 里通过 @AuthenticationPrincipal 获取。
// */
//public class MyUserPrincipal implements UserDetails {
//
//    private final Long id;                 // 对应 users.id
//    private final String username;         // 对应 users.username
//    private final String password;         // 对应 users.password_hash
//    private final Collection<GrantedAuthority> authorities;
//    private final boolean enabled;         // 基于 users.status
//
//    public MyUserPrincipal(User user) {
//        this.id = user.getId();
//        this.username = user.getUsername();
//        this.password = user.getPasswordHash();
//        this.enabled = "ACTIVE".equalsIgnoreCase(user.getStatus());
//        // Java 8 下，将单个角色封装为单元素列表
//        this.authorities = Collections.singletonList(
//                new SimpleGrantedAuthority(user.getRole())
//        );
//    }
//
//    /** 返回数据库主键 users.id */
//    public Long getId() {
//        return id;
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities;
//    }
//
//    /** 返回 users.password_hash */
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    /** 返回 users.username */
//    @Override
//    public String getUsername() {
//        return username;
//    }
//
//    /** 账户是否未过期 */
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    /** 账户是否未锁定 */
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    /** 凭证（密码）是否未过期 */
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    /** 账户是否可用（基于 users.status） */
//    @Override
//    public boolean isEnabled() {
//        return enabled;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof MyUserPrincipal)) return false;
//        MyUserPrincipal that = (MyUserPrincipal) o;
//        return Objects.equals(id, that.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
//}
