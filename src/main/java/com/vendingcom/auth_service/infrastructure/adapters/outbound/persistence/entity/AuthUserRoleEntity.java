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
@Table(name = "auth_user_roles")
public class AuthUserRoleEntity {

    @Id
    @Column("user_role_id")
    private Integer userRoleId;

    @Column("user_id")
    private Integer userId;

    @Column("role_id")
    private Integer roleId;

    @Column("assigned_by_user_id")
    private Integer assignedByUserId;

    @Column("assignment_status")
    private Integer assignmentStatus;

    @Column("assigned_at")
    private LocalDateTime assignedAt;
}