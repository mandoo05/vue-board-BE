package kr.co.board.domain.board.dto;

import kr.co.board.domain.board.infra.entity.Board;
import lombok.Builder;

public class BoardResponse {

    @Builder
    public record boardResponse(
            Long id,
            String title
    ) {
        public static boardResponse from(Board board) {
            return boardResponse.builder()
                    .title(board.getTitle())
                    .build();
        }
    }
}
