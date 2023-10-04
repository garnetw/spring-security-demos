package com.garnet.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Table(name = "user")
public class UserInfo implements UserDetails, Serializable {

    private int id;
    private String username;
    private String password;

    @Transient
    private String token;

    @JsonIgnore
    @Transient
    private List<Role> roleList;

    @Transient
    private List<Integer> resourceList;

    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if(resourceList == null || resourceList.size() == 0) {
            return new ArrayList<SimpleGrantedAuthority>();
        }

        /* 获取当前用户的权限数据，转换为 SimpleGrantedAuthority对象 */
        return resourceList.stream()
                .map(String::valueOf)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
