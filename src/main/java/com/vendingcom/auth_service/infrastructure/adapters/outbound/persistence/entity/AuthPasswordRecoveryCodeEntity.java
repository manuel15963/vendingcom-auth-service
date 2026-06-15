package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "auth_password_recovery_codes")
public class AuthPasswordRecoveryCodeEntity {

    @Id
    @Column("recovery_code_id")
    private Long recoveryCodeId;

    @Column("user_id")
    private Integer userId;

    @Column("email")
    private String email;

    @Column("code_hash")
    private String codeHash;

    @Column("used")
    private Boolean used;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("used_at")
    private LocalDateTime usedAt;

    @Column("attempts")
    private Integer attempts;

    @Column("max_attempts")
    private Integer maxAttempts;
}