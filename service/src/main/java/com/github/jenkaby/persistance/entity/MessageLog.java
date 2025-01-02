package com.github.jenkaby.persistance.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "message_log")
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "encoding_type", nullable = false, length = 10)
    private String encodingType;

    @Column(name = "raw_header")
    @Type(JsonType.class)
    private String rawHeader;

    @Column(name = "raw_message", columnDefinition = "jsonb", nullable = false)
    @Type(JsonType.class)
    private String rawMessage;

    @ColumnDefault("now()")
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "source", nullable = false, length = Integer.MAX_VALUE)
    private String source;

    @Column(name = "modified_by", nullable = false, length = Integer.MAX_VALUE)
    private String modifiedBy;

}