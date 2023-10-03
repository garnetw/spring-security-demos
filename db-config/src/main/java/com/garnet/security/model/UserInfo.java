package com.garnet.security.model;

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

@Data
@Table(name = "user")
public class UserInfo implements UserDetails, Serializable {

    private int id;
    private String username;
    private String password;

    // private List<Resource> resourceList;

    @Transient
    private List<Role> roleList;

    public UserInfo(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<GrantedAuthority> auth = new ArrayList<>();

        if(roleList != null && !roleList.isEmpty()) {
            for(Role role : roleList) {
                auth.add(new SimpleGrantedAuthority(String.valueOf(role.getCode())));
            }
        }

        return auth;
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
