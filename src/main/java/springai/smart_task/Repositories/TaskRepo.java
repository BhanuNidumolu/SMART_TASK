package springai.smart_task.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springai.smart_task.Entities.Task;
import springai.smart_task.Entities.User;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    List<Task> findByUserIdAndParentTaskIsNull(Long userId);
    List<Task> findByParentTask(Task parentTask);
}