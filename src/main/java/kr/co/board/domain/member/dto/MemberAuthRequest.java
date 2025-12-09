package kr.co.board.domain.member.dto;

import kr.co.board.config.validation.ValidName;
import kr.co.board.config.validation.ValidPassword;
import kr.co.board.config.validation.ValidUsername;

public record MemberAuthRequest(
        @ValidUsername
        String username,
        @ValidPassword
        String password,
        @ValidPassword
        String confirmPassword,
        @ValidName String nickname
) {}
