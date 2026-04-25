package com.campusflow.infrastructure.persistence.mongo.otp;

import com.campusflow.domain.user.model.OtpPurpose;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpMongoRepository extends MongoRepository<OtpDocument, String> {
    Optional<OtpDocument> findByEmailAndPurposeAndOtpCode(String email, OtpPurpose purpose, String otpCode);

    void deleteByEmailAndPurpose(String email, OtpPurpose purpose);

    boolean existsByEmailAndPurpose(String email, OtpPurpose purpose);
}
