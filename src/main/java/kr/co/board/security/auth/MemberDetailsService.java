package kr.co.board.security.auth;

import kr.co.board.domain.member.infra.entity.Member;
import kr.co.board.domain.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberDetailsService implements UserDetailsService {
  private final MemberRepository memberRepository;

  @Override
  @Transactional(readOnly = true)
  public MemberDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Member member =
        memberRepository
            .findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
    return new MemberDetails(member);
  }
}
