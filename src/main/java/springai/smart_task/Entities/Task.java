package springai.smart_task.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double duration; // Duration in hours

    @Column
    private LocalDateTime deadline;

    @Column
    private LocalDateTime scheduledTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private Task parentTask;

    @OneToMany(mappedBy = "parentTask", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Prevent serialization loops
    private List<Task> subTasks = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "task_dependencies", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "dependency_name")
    private List<String> dependencies = new ArrayList<>();

    @Column
    private String priority; // e.g., "High", "Medium", "Low"

    public void addSubTask(Task subTask) {
        subTasks.add(subTask);
        subTask.setParentTask(this);
    }
}