package com.campusflow.infrastructure.persistence.mongo.otp;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OtpMongoRepository extends MongoRepository<OtpDocument, String> {
    Optional<OtpDocument> findByEmailAndOtpCode(String email, String otpCode);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);
}
