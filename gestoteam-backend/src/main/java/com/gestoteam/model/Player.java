package com.gestoteam.model;

import com.gestoteam.enums.PlayerStatus;
import com.gestoteam.enums.Position;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surnameFirst;

    private String surnameSecond;

    @Transient
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Position position;

    @Column(nullable = false)
    private int number;

    @Transient
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlayerStatus status;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Boolean deleted = false;

    @Column(nullable = false)
    private LocalDate birthDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Team team;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerMatchStats> matchStats = new ArrayList<>();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Injury> injuries = new ArrayList<>();

    @PostLoad
    @PostPersist
    @PostUpdate
    public void calculateFields() {
        calculateFullName();
        calculateAge();
    }

    private void calculateFullName() {
        this.fullName = String.join(" ", name, surnameFirst, surnameSecond != null ? surnameSecond : "").trim();
    }

    private void calculateAge() {
        if (birthDate != null) {
            this.age = Period.between(birthDate, LocalDate.now()).getYears();
        } else {
            this.age = 0;
        }
    }
}