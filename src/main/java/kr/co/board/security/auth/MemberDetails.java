package kr.co.board.security.auth;

import kr.co.board.domain.member.infra.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class MemberDetails implements UserDetails {

  private final Member member;

  public String getNickname() {
    return member.getNickname();
  }

  public Long getId() {
    return member.getId();
  }

  public MemberStatus getStatus() {
    return member.getStatus();
  }

  public Boolean isAdmin() {
    return member.getRole().equals(MemberRole.ROLE_ADMIN);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(member.getRole().toString()));
  }

  @Override
  public String getPassword() {
    return member.getPassword();
  }

  @Override
  public String getUsername() {
    return member.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return MemberStatus.ACTIVE.equals(member.getStatus());
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return MemberStatus.ACTIVE.equals(member.getStatus());
  }
}
