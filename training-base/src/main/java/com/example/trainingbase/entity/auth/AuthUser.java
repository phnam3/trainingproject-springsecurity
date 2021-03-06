package com.example.trainingbase.entity.auth;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@Setter
@Table(name = "auth_user", schema = "auth_bib")
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@Slf4j
public class AuthUser implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id")
    private int id;
    @Basic
    @Column(name = "full_name")
    private String fullName;
    @Basic
    @Column(name = "image")
    private String image;
    @Basic
    @Column(name = "email")
    private String email;
    @Basic
    @Column(name = "password")
    private String password;
    @Basic
    @Column(name = "phone_number")
    private String phoneNumber;
    @Basic
    @Column(name = "country_phone_prefix")
    private String countryPhonePrefix;
    @Basic
    @Column(name = "roles")
    private String role;
    @Basic
    @Column(name = "workspace")
    private int workspace;
    @Basic
    @Column(name = "status")
    private String status;
    @Basic
    @Column(name = "login_fail_count")
    private Integer loginFailCount;
    @Basic
    @Column(name = "company_id")
    private int companyId;
    @Basic
    @Column(name = "last_login")
    private Timestamp lastLogin;
    @Basic
    @Column(name = "password_expired_date")
    private Timestamp expireDate;
    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    @Basic
    @Column(name = "created_by")
    private String createdBy;
    @Basic
    @Column(name = "updated_by")
    private String updatedBy;
    @Basic
    @Column(name = "username")
    private String username;

    //TODO: todo
    public AuthUser(String fullName, String email, String password, String phoneNumber, String role, int companyId) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.companyId = companyId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(EStatus.PENDING.name().equals(this.getStatus())){
            return Collections.singletonList(new SimpleGrantedAuthority(ERoles.SALE_RM.name()));
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(this.getRole());
        return Collections.singletonList(authority);
    }

    @Override
    public boolean isAccountNonExpired() {
        //TODO: todo
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //TODO: todo
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //TODO: todo
        return true;
    }

    @Override
    public boolean isEnabled() {
        //TODO: todo
        return true;
    }
}
