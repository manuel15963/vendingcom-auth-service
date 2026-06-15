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
@Table(schema = "public", name = "auth_audit_logs")
public class AuthAuditLogEntity {

    @Id
    @Column("audit_log_id")
    private Long auditLogId;

    @Column("affected_user_id")
    private Integer affectedUserId;

    @Column("action_type")
    private String actionType;

    @Column("affected_table_name")
    private String affectedTableName;

    @Column("affected_record_id")
    private Integer affectedRecordId;

    @Column("action_description")
    private String actionDescription;

    @Column("old_data")
    private String oldData;

    @Column("new_data")
    private String newData;

    @Column("ip_address")
    private String ipAddress;

    @Column("user_agent")
    private String userAgent;

    @Column("executed_by_user_id")
    private Integer executedByUserId;

    @Column("executed_at")
    private LocalDateTime executedAt;
}