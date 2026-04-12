package com.campusflow.infrastructure.persistence.mongo.otp;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "otps")
@Getter
@Setter
@NoArgsConstructor
public class OtpDocument {
    @Id
    private String id;
    private String email;
    private String otpCode;
    private LocalDateTime createdAt;
    @Indexed(expireAfterSeconds = 600)
    private LocalDateTime expiresAt;

    public static OtpDocument create(String email, String otpCode) {
        OtpDocument doc = new OtpDocument();
        doc.setEmail(email);
        doc.setOtpCode(otpCode);
        doc.setCreatedAt(LocalDateTime.now());
        doc.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        return doc;
    }
}
