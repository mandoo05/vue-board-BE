package kr.co.board.domain.board.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.co.board.domain.board.infra.entity.Board;
import lombok.Builder;

import java.time.LocalDateTime;

public class BoardResponse {

    @Builder
    public record BoardListResponse(
            @Schema(description = "게시글 ID")
            Long id,
            @Schema(description = "게시글 제목")
            String title,
            @Schema(description = "작성자 이름")
            String writer,
            @Schema(description = "작성일")
            LocalDateTime createdAt,
            @Schema(description = "수정일")
            LocalDateTime updatedAt
    ) {
        public static BoardListResponse from(Board board) {
            return BoardListResponse.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .writer(board.getMember().getNickname())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
        }
    }

    @Builder
    public record GetBoardResponse(
            @Schema(description = "게시글 ID")
            Long id,
            @Schema(description = "게시글 ID")
            Long memberId,
            @Schema(description = "게시글 제목")
            String title,
            @Schema(description = "게시글 내용")
            String content,
            @Schema(description = "작성자 이름")
            String writer,
            @Schema(description = "게시글 작성일")
            LocalDateTime createdAt,
            @Schema(description = "게시글 수정일")
            LocalDateTime updatedAt
    ) {
        public static GetBoardResponse from(Board board) {
            return GetBoardResponse.builder()
                    .id(board.getId())
                    .memberId(board.getMember().getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .writer(board.getMember().getNickname())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
        }
    }
}
