package com.example.module1.service.impl.authentication;

import com.example.module1.repository.RefreshTokenRepository;
import com.example.module1.repository.UserRepository;
import com.example.trainingbase.constants.HttpStatusConstants;
import com.example.trainingbase.entity.auth.AuthUser;
import com.example.trainingbase.entity.auth.RefreshToken;
import com.example.trainingbase.exceptions.BusinessException;
import com.example.trainingbase.payload.BibResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final Integer refreshTokenDurationMs = 6000000;
    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token){
        return repository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Integer id){
        AuthUser authUser = userRepository.findById(id).orElseThrow(
                () -> new BusinessException(HttpStatusConstants.INVALID_USER_ID_CODE,
                                            "User not existed")
        );
        RefreshToken refreshToken = new RefreshToken(authUser,
                UUID.randomUUID().toString(),Instant.now().plusMillis(refreshTokenDurationMs));
        repository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token){
        if(token.getExpiryDate().compareTo(Instant.now()) < 0){
            repository.delete(token);
            throw new BusinessException(HttpStatusConstants.REFRESH_TOKEN_EXPIRED_CODE,
                    "Token is expired");
        }
        return token;
    }

    @Transactional
    public BibResponse deleteByUserId(Integer id){
        repository.deleteByAuthUser(userRepository.findById(id).get());
        return new BibResponse(HttpStatusConstants.SUCCESS_CODE, HttpStatusConstants.SUCCESS_MESSAGE, id);
    }
}
