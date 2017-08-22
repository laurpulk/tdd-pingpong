package pingis.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pingis.entities.Challenge;
import pingis.entities.CodeStatus;
import pingis.entities.Task;
import pingis.entities.TaskInstance;
import pingis.entities.TaskType;
import pingis.entities.User;
import pingis.repositories.TaskInstanceRepository;
import pingis.repositories.TaskRepository;
import pingis.repositories.UserRepository;

@Service
public class TaskInstanceService {

  @Autowired
  private TaskRepository taskRepository;
  @Autowired
  private TaskInstanceRepository taskInstanceRepository;
  @Autowired
  private UserRepository userRepository;


  public TaskInstance getCorrespondingTestTaskInstance(
      TaskInstance implTaskInstance) {
    return taskInstanceRepository
        .findByTaskAndUser(
            taskRepository
                .findByIndexAndChallengeAndType(implTaskInstance.getTask().getIndex(),
                    implTaskInstance.getTask().getChallenge(), TaskType.TEST),
            userRepository.findById(0L).get());
  }


  public TaskInstance getCorrespondingImplTaskInstance(TaskInstance testTaskInstance) {
    return taskInstanceRepository
        .findByTaskAndUser(
            taskRepository.findByIndexAndChallengeAndType(testTaskInstance.getTask().getIndex(),
                testTaskInstance.getTask().getChallenge(), TaskType.IMPLEMENTATION),
            userRepository.findById(0L).get());
  }

  public TaskInstance findOne(long taskInstanceId) {
    Optional<TaskInstance> opt = taskInstanceRepository.findById(taskInstanceId);
    if (opt.isPresent()) {
      return opt.get();
    } else {
      return null;
    }
  }

  @Transactional
  public TaskInstance updateTaskInstanceCode(Long taskInstanceId, String taskInstanceCode) {
    TaskInstance taskInstanceToUpdate = taskInstanceRepository.findById(taskInstanceId).get();
    taskInstanceToUpdate.setCode(taskInstanceCode);
    return taskInstanceToUpdate;
  }

  public TaskInstance createEmpty(User user, Task task) {
    TaskInstance newTaskInstance = new TaskInstance(user, "", task);
    newTaskInstance.setCode(task.getCodeStub());
    return taskInstanceRepository.save(newTaskInstance);
  }
  
  public List<TaskInstance> getByUserAndChallenge(User user, Challenge challenge) {
    List<TaskInstance> taskInstances = new ArrayList<>();
    for (Task task : challenge.getTasks()) {
      taskInstances.add(taskInstanceRepository.findByTaskAndUser(task, user));
    }
    return taskInstances;
  }


  @Transactional
  public TaskInstance markAsDone(TaskInstance taskInstance) {
    taskInstance.setStatus(CodeStatus.DONE);
    return taskInstance;
  }

  public TaskInstance save(TaskInstance taskInstance) {
    return taskInstanceRepository.save(taskInstance);
  }

  public List<TaskInstance> findAll() {
    return (List<TaskInstance>) taskInstanceRepository.findAll();
  }

  public List<TaskInstance> getAllByChallenge(Challenge challenge) {
    List<TaskInstance> taskInstances = new ArrayList();
    List<TaskInstance> allTaskInstances = (List<TaskInstance>) taskInstanceRepository.findAll();
    for (TaskInstance current : allTaskInstances) {
      if (current.getTask().getChallenge().getId() == challenge.getId()) {
        taskInstances.add(current);
      }
    }
    return taskInstances;
  }


  public int getNumberOfDoneTaskInstancesInChallenge(Challenge challenge) {
    int count = 0;
    List<TaskInstance> allTaskInstances = (List<TaskInstance>) taskInstanceRepository.findAll();
    for (TaskInstance current : allTaskInstances) {
      if (current.getTask().getChallenge().getId() == challenge.getId()
          && current.getStatus() == CodeStatus.DONE) {
        count++;
      }
    }
    return count;
  }

  public TaskInstance getByTaskAndUser(Task task, User user) {
    return taskInstanceRepository.findByTaskAndUser(task, user);
  }
  
  public boolean canContinue(TaskInstance taskInstance, User user) {
    if (taskInstance.getStatus() == CodeStatus.DONE || !taskInstance.getUser().equals(user)) {
      return false;
    } 
    return true;
  }

}
