package kr.co.board.domain.board.service;

import kr.co.board.config.exception.CustomException;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.domain.board.dto.BoardRequest;
import kr.co.board.domain.board.dto.BoardResponse;
import kr.co.board.domain.board.infra.entity.Board;
import kr.co.board.domain.board.infra.entity.BoardStatus;
import kr.co.board.domain.board.infra.repository.BoardRepository;
import kr.co.board.domain.member.infra.entity.Member;
import kr.co.board.domain.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void postBoard(BoardRequest dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Board board = Board.builder()
                .member(member)
                .title(dto.title())
                .content(dto.content())
                .status(BoardStatus.PUBLIC)
                .build();

        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse.boardList> getBoard(Pageable pageable) {
        return boardRepository.findAll(pageable)
                .map(BoardResponse.boardList::from);
    }
}
