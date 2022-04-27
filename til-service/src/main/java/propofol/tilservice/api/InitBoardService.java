package propofol.tilservice.api;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import propofol.tilservice.domain.board.entity.Board;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/** 테스트용 데이터 추가 */
@Component
@Transactional(readOnly = true)
public class InitBoardService {
    @PersistenceContext
    EntityManager em;

    @Transactional
    public void init() {
        for(int i=0; i<50; i++){
            Board board = Board.createBoard()
                    .title(String.valueOf(i))
                    .content(String.valueOf(i))
                    .open(true)
                    .recommend(0)
                    .build();
            em.persist(board);
            em.flush();
            em.clear();
        }
    }
}
