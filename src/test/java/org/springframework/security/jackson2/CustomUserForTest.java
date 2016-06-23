package org.springframework.security.jackson2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class CustomUserForTest implements UserDetails {
    private String username;
    private String password;
    private String customProperty;
    private List<GrantedAuthority> authorities = new ArrayList<>();
    private boolean accountNonExpired = true;
    private boolean accountNonLocked = true;
    private boolean credentialsNonExpired = true;
    private boolean enabled = true;

    @JsonCreator
    CustomUserForTest(@JsonProperty("username") String username,
                      @JsonProperty("password") String password,
                      @JsonProperty("customProperty") String customProperty,
                      @JsonProperty("authorities") List<GrantedAuthority> authorities,
                      @JsonProperty("accountNonExpired") boolean accountNonExpired,
                      @JsonProperty("accountNonLocked") boolean accountNonLocked,
                      @JsonProperty("credentialsNonExpired") boolean credentialsNonExpired,
                      @JsonProperty("enabled") boolean enabled) {
        this.username = username;
        this.password = password;
        this.customProperty = customProperty;
        if (authorities != null) {
            this.authorities.addAll(authorities);
        }
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getCustomProperty() {
        return customProperty;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new ArrayList<>(authorities);
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
