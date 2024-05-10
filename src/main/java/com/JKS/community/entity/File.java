package com.JKS.community.entity;

import com.JKS.community.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@SQLDelete(sql = "UPDATE file SET enabled = false WHERE file_id = ?")
@SQLRestriction("enabled = true")
public class File extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "file_id")
    private Long id;

    private String originalName;
    private String uuidName;

    private String type;
    private long size;
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static File of(String originalName, String uuidName, String type, long size, String path) {
        File file = new File();
        file.originalName = originalName;
        file.uuidName = uuidName;
        file.type = type;
        file.size = size;
        file.path = path;
        return file;
    }

    public void update(String originalName, String uuidName, String type, long size, String path) {
        this.originalName = originalName;
        this.uuidName = uuidName;
        this.type = type;
        this.size = size;
        this.path = path;
    }
}
