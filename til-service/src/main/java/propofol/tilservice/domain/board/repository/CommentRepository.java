package propofol.tilservice.domain.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import propofol.tilservice.domain.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
