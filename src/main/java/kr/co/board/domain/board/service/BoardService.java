package kr.co.board.domain.board.service;

import kr.co.board.config.exception.CustomException;
import kr.co.board.config.exception.ErrorCode;
import kr.co.board.config.response.PageResponse;
import kr.co.board.domain.board.dto.BoardRequest;
import kr.co.board.domain.board.dto.BoardResponse;
import kr.co.board.domain.board.infra.entity.Board;
import kr.co.board.domain.board.infra.repository.BoardRepository;
import kr.co.board.domain.member.infra.entity.Member;
import kr.co.board.domain.member.infra.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void postBoard(BoardRequest.BoardPostRequest dto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        Board board = Board.builder()
                .member(member)
                .title(dto.title())
                .content(dto.content())
                .build();

        boardRepository.save(board);
    }

    @Transactional(readOnly = true)
    public PageResponse<BoardResponse.BoardListResponse> getBoardList(Pageable pageable) {
        Page<Board> boards = boardRepository.findAll(pageable);
        return PageResponse.from(boards, BoardResponse.BoardListResponse::from);
    }

    @Transactional(readOnly = true)
    public BoardResponse.GetBoardResponse getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return BoardResponse.GetBoardResponse.from(board);
    }

    @Transactional
    public void updateBoard(Long boardId, BoardRequest.BoardUpdateRequest dto, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        if(!board.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        board.update(dto.title(), dto.content());
    }

    @Transactional
    public void deleteBoard(Long boardId, Long memberId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        if(!board.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        boardRepository.delete(board);
    }
}
