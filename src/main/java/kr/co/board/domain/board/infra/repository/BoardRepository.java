package kr.co.board.domain.board.infra.repository;

import kr.co.board.domain.board.infra.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board, Long> {
}
