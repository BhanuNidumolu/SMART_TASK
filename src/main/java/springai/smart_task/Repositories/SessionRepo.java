package springai.smart_task.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springai.smart_task.Entities.Session;

@Repository
public interface SessionRepo extends JpaRepository<Session, Long> {
}