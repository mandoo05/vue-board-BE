package kr.co.board.domain.board.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.board.config.response.ApiResponse;
import kr.co.board.domain.board.dto.BoardRequest;
import kr.co.board.domain.board.dto.BoardResponse;
import kr.co.board.domain.board.service.BoardService;
import kr.co.board.security.auth.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/api/board")
@RestController
@RequiredArgsConstructor
@Tag(name = "게시판 API", description = "게시판 관련 API")
public class BoardController {
    private final BoardService boardService;

    @Operation(summary = "게시판 작성 API")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ApiResponse<Void> postBoard(@Valid @RequestBody BoardRequest dto, @AuthenticationPrincipal MemberDetails member) {
        boardService.postBoard(dto, member.getId());
        return ApiResponse.success();
    }

    @Operation(summary = "게시판 목록 조회 API")
    @GetMapping
    public ApiResponse<Page<BoardResponse.boardResponse>> getBoard(
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        var data = boardService.getBoardList(pageable);
        return ApiResponse.success(data);
    }

    @Operation(summary = "특정 게시판 조회 API")
    @GetMapping("/{boardId}")
    public ApiResponse<BoardResponse.boardResponse> getBoard(@PathVariable Long boardId) {
        var data = boardService.getBoard(boardId);
        return ApiResponse.success(data);
    }
}
