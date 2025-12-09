package kr.co.board.domain.member.infra.entity;

import jakarta.persistence.*;
import kr.co.board.domain.BaseSoftDeleteTimeEntity;
import kr.co.board.security.auth.MemberRole;
import kr.co.board.security.auth.MemberStatus;
import lombok.*;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.SQLDelete;

import java.util.UUID;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Member extends BaseSoftDeleteTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;
}

