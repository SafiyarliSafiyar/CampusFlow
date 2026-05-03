package com.campusflow.infrastructure.persistence.mongo.otp;

import com.campusflow.domain.user.model.OtpPurpose;
import com.campusflow.domain.user.port.OtpRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OtpMongoAdapter implements OtpRepositoryPort {
    private final OtpMongoRepository otpMongoRepository;

    @Override
    public void saveOtp(String email, OtpPurpose purpose, String otpCode) {
        otpMongoRepository.deleteByEmailAndPurpose(email, purpose);
        otpMongoRepository.save(OtpDocument.create(email, purpose, otpCode));
    }

    @Override
    public boolean verifyOtp(String email, OtpPurpose purpose, String otpCode) {
        return otpMongoRepository.findByEmailAndPurposeAndOtpCode(email, purpose, otpCode).isPresent();
    }

    @Override
    public void deleteOtp(String email, OtpPurpose purpose) {
        otpMongoRepository.deleteByEmailAndPurpose(email, purpose);
    }

    @Override
    public boolean hasOtp(String email, OtpPurpose purpose) {
        return otpMongoRepository.existsByEmailAndPurpose(email, purpose);
    }
}
