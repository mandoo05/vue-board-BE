package kr.co.board.config.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "페이지네이션 응답")
public class PageResponse<T> {

    @Schema(description = "데이터 목록")
    private List<T> content;

    @Schema(description = "현재 페이지 번호 (1부터 시작)")
    private int page;

    @Schema(description = "요청된 페이지 크기")
    private int size;

    @Schema(description = "전체 데이터 개수")
    private long totalElements;

    @Schema(description = "전체 페이지 수")
    private int totalPages;

    @Schema(description = "다음 페이지 여부")
    private boolean hasNext;

    @Schema(description = "이전 페이지 여부")
    private boolean hasPrevious;

    public static <T, R> PageResponse<R> from(Page<T> page, Function<T, R> mapper) {

        List<R> mappedList = page.getContent().stream()
                .map(mapper)
                .toList();

        return PageResponse.<R>builder()
                .content(mappedList)
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
