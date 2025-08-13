package com.example.hanaro.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.example.hanaro.global.entity.BaseEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name= "`User`")
@SQLDelete(sql = "UPDATE `User` SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@SuperBuilder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String role;

    private String refreshToken;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}