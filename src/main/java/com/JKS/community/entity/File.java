package com.JKS.community.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter @Setter
public class File {
    @Id @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String originalName;
    private String uuidName;

    private String type;
    private long size;
    private String path;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime uploadTime;

}
