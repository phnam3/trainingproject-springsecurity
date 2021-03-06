package com.example.module1.service.impl;

import com.example.module1.dto.RegisterUserInfo;
import com.example.module1.repository.UserRepository;
import com.example.module1.repository.specification.AuthUserSpecifications;
import com.example.module1.repository.specification.FilterInfo;
import com.example.module1.service.UserService;
import com.example.module1.service.impl.registration.ConfirmationTokenService;
import com.example.trainingbase.constants.HttpStatusConstants;
import com.example.trainingbase.entity.auth.AuthUser;
import com.example.trainingbase.entity.auth.ConfirmationToken;
import com.example.trainingbase.entity.auth.EStatus;
import com.example.trainingbase.exceptions.BusinessException;
import com.example.trainingbase.payload.BibResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Filter;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthUserSpecifications authUserSpecifications;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new BusinessException(HttpStatusConstants.EMAIL_NOT_EXISTS_CODE, HttpStatusConstants.EMAIL_NOT_EXISTS_MESSAGE));
    }

    @Override
    public String signUpUser(AuthUser authUser){
        if(userRepository.findByEmail(authUser.getEmail()).isPresent())
                throw new BusinessException(HttpStatusConstants.EMAIL_EXIST_CODE,
                                        HttpStatusConstants.EMAIL_EXIST_MESSAGE);
        String encodedPassword = bCryptPasswordEncoder.encode(authUser.getPassword());
        authUser.setPassword(encodedPassword);
        authUser.setStatus(EStatus.INACTIVE.name());
        authUser.setLoginFailCount(0);
        userRepository.save(authUser);
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                                                                    LocalDateTime.now().plusMinutes(15),
                                                                    authUser);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }

    @Override
    public AuthUser findById(Integer id){
        AuthUser authUser = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.INVALID_USER_ID_CODE, HttpStatusConstants.INVALID_USER_ID_MESSAGE)
        );
        return authUser;
    }
    @Override
    public AuthUser findByEmail(String email) {
        AuthUser authUser = userRepository.findByEmail(email).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.EMAIL_NOT_EXISTS_CODE, HttpStatusConstants.EMAIL_NOT_EXISTS_MESSAGE)
        );
        return authUser;
    }

    @Override
    public AuthUser findByCompanyId(Integer companyId) {
        AuthUser authUser = userRepository.findByCompanyId(companyId).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.COMPANY_NOT_EXISTED_CODE, HttpStatusConstants.COMPANY_NOT_EXISTED_MESSAGE)
        );
        return authUser;
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
    @Override
    @Transactional
    public BibResponse updateUserById(Integer id, RegisterUserInfo registerUserInfo) {
        AuthUser authUser = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.INVALID_USER_ID_CODE, HttpStatusConstants.INVALID_USER_ID_MESSAGE)
        );
        authUser.setEmail(registerUserInfo.getEmail());
        authUser.setPhoneNumber(registerUserInfo.getPhoneNumber());
        authUser.setFullName(registerUserInfo.getFullName());
        return new BibResponse(HttpStatusConstants.SUCCESS_CODE, HttpStatusConstants.SUCCESS_MESSAGE, authUser);
    }

    @Override
    @Transactional
    public BibResponse updateUserRoleById(Integer id, String role) {
        AuthUser authUser = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.INVALID_USER_ID_CODE, HttpStatusConstants.INVALID_USER_ID_MESSAGE)
        );
        authUser.setRole(role);
        return new BibResponse(HttpStatusConstants.SUCCESS_CODE, HttpStatusConstants.SUCCESS_MESSAGE, authUser);
    }

    @Override
    @Transactional
    public BibResponse updateUserCompanyById(Integer id, Integer companyId) {
        AuthUser authUser = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.INVALID_USER_ID_CODE, HttpStatusConstants.INVALID_USER_ID_MESSAGE)
        );
        authUser.setCompanyId(companyId);
        return new BibResponse(HttpStatusConstants.SUCCESS_CODE, HttpStatusConstants.SUCCESS_MESSAGE, authUser);
    }

    @Override
    public List<AuthUser> findUser(List<FilterInfo> filterInfos) {
        List<FilterInfo> filterInfoList = new ArrayList<>();
        for(FilterInfo filter : filterInfos){
            FilterInfo filterInfo = FilterInfo.builder()
                    .field(filter.getField())
                    .operator(filter.getOperator())
                    .value(filter.getValue())
                    .values(filter.getValues()).build();
            filterInfoList.add(filterInfo);
        }
        List<AuthUser> authUsers = authUserSpecifications.getQueryResult(filterInfoList);
        return authUsers;
    }

}
