package kr.co.board.domain.board.dto;

import kr.co.board.domain.board.infra.entity.Board;
import lombok.Builder;

public class BoardResponse {

    @Builder
    public record boardList(
            Long id,
            String title
    ) {
        public static boardList from(Board board) {
            return boardList.builder()
                    .title(board.getTitle())
                    .build();
        }
    }
}
