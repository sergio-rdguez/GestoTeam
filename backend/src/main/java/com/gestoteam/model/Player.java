package com.gestoteam.model;

import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surnameFirst;

    private String surnameSecond;

    @Transient
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;

    @Column(nullable = false)
    private int number;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private Boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateFullName() {
        this.fullName = String.join(" ", name, surnameFirst, surnameSecond != null ? surnameSecond : "").trim();
    }
}
