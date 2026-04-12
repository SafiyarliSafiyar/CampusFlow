package com.campusflow.infrastructure.persistence.mongo.otp;

import com.campusflow.domain.user.port.OtpRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OtpMongoAdapter implements OtpRepositoryPort {
    private final OtpMongoRepository otpMongoRepository;

    @Override
    public void saveOtp(String email, String otpCode) {
        otpMongoRepository.deleteByEmail(email);
        otpMongoRepository.save(OtpDocument.create(email, otpCode));
    }

    @Override
    public boolean verifyOtp(String email, String otpCode) {
        return otpMongoRepository.findByEmailAndOtpCode(email, otpCode).isPresent();
    }

    @Override
    public void deleteOtp(String email) {
        otpMongoRepository.deleteByEmail(email);
    }

    @Override
    public boolean hasOtp(String email) {
        return otpMongoRepository.existsByEmail(email);
    }
}
