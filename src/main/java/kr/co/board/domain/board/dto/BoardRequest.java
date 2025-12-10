package kr.co.board.domain.board.dto;

import jakarta.validation.constraints.NotBlank;

public class BoardRequest {
    public record BoardPostRequest(
            @NotBlank String title,
            @NotBlank String content
    ) {}

    public record BoardUpdateRequest(
            @NotBlank String title,
            @NotBlank String content
    ) {}
}
