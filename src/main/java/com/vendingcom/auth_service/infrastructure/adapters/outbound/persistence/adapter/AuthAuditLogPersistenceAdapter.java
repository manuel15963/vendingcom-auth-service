package com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.adapter;

import com.vendingcom.auth_service.application.port.output.persistence.AuthAuditLogRepositoryPort;
import com.vendingcom.auth_service.domain.model.AuthAuditLog;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.mapper.AuthAuditLogPersistenceMapper;
import com.vendingcom.auth_service.infrastructure.adapters.outbound.persistence.repository.ReactiveAuthAuditLogRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
public class AuthAuditLogPersistenceAdapter implements AuthAuditLogRepositoryPort {

    private final ReactiveAuthAuditLogRepository reactiveAuthAuditLogRepository;
    private final AuthAuditLogPersistenceMapper authAuditLogPersistenceMapper;
    private final DatabaseClient databaseClient;

    public AuthAuditLogPersistenceAdapter(
            ReactiveAuthAuditLogRepository reactiveAuthAuditLogRepository,
            AuthAuditLogPersistenceMapper authAuditLogPersistenceMapper,
            DatabaseClient databaseClient
    ) {
        this.reactiveAuthAuditLogRepository = reactiveAuthAuditLogRepository;
        this.authAuditLogPersistenceMapper = authAuditLogPersistenceMapper;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<AuthAuditLog> save(AuthAuditLog authAuditLog) {
        String sql = """
                INSERT INTO public.auth_audit_logs (
                    affected_user_id,
                    action_type,
                    affected_table_name,
                    affected_record_id,
                    action_description,
                    old_data,
                    new_data,
                    ip_address,
                    user_agent,
                    executed_by_user_id,
                    executed_at
                ) VALUES (
                    :affectedUserId,
                    :actionType,
                    :affectedTableName,
                    :affectedRecordId,
                    :actionDescription,
                    CAST(:oldData AS jsonb),
                    CAST(:newData AS jsonb),
                    :ipAddress,
                    :userAgent,
                    :executedByUserId,
                    :executedAt
                )
                RETURNING
                    audit_log_id,
                    affected_user_id,
                    action_type,
                    affected_table_name,
                    affected_record_id,
                    action_description,
                    old_data::text AS old_data,
                    new_data::text AS new_data,
                    ip_address,
                    user_agent,
                    executed_by_user_id,
                    executed_at
                """;

        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);

        spec = authAuditLog.affectedUserId() == null
                ? spec.bindNull("affectedUserId", Integer.class)
                : spec.bind("affectedUserId", authAuditLog.affectedUserId());

        spec = spec.bind("actionType", authAuditLog.actionType());
        spec = spec.bind("affectedTableName", authAuditLog.affectedTableName());

        spec = authAuditLog.affectedRecordId() == null
                ? spec.bindNull("affectedRecordId", Integer.class)
                : spec.bind("affectedRecordId", authAuditLog.affectedRecordId());

        spec = spec.bind("actionDescription", authAuditLog.actionDescription());

        spec = authAuditLog.oldData() == null
                ? spec.bindNull("oldData", String.class)
                : spec.bind("oldData", authAuditLog.oldData());

        spec = authAuditLog.newData() == null
                ? spec.bindNull("newData", String.class)
                : spec.bind("newData", authAuditLog.newData());

        spec = authAuditLog.ipAddress() == null
                ? spec.bindNull("ipAddress", String.class)
                : spec.bind("ipAddress", authAuditLog.ipAddress());

        spec = authAuditLog.userAgent() == null
                ? spec.bindNull("userAgent", String.class)
                : spec.bind("userAgent", authAuditLog.userAgent());

        spec = authAuditLog.executedByUserId() == null
                ? spec.bindNull("executedByUserId", Integer.class)
                : spec.bind("executedByUserId", authAuditLog.executedByUserId());

        spec = authAuditLog.executedAt() == null
                ? spec.bind("executedAt", LocalDateTime.now())
                : spec.bind("executedAt", authAuditLog.executedAt());

        return spec.map((row, metadata) -> new AuthAuditLog(
                        row.get("audit_log_id", Long.class),
                        row.get("affected_user_id", Integer.class),
                        row.get("action_type", String.class),
                        row.get("affected_table_name", String.class),
                        row.get("affected_record_id", Integer.class),
                        row.get("action_description", String.class),
                        row.get("old_data", String.class),
                        row.get("new_data", String.class),
                        row.get("ip_address", String.class),
                        row.get("user_agent", String.class),
                        row.get("executed_by_user_id", Integer.class),
                        row.get("executed_at", LocalDateTime.class)
                ))
                .one();
    }

    @Override
    public Flux<AuthAuditLog> findByAffectedUserId(Integer affectedUserId) {
        return reactiveAuthAuditLogRepository.findByAffectedUserId(affectedUserId)
                .map(authAuditLogPersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthAuditLog> findByActionType(String actionType) {
        return reactiveAuthAuditLogRepository.findByActionType(actionType)
                .map(authAuditLogPersistenceMapper::toDomain);
    }

    @Override
    public Flux<AuthAuditLog> findAll() {
        return reactiveAuthAuditLogRepository.findAll()
                .map(authAuditLogPersistenceMapper::toDomain);
    }
}