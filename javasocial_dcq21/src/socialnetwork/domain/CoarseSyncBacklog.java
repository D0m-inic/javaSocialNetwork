package socialnetwork.domain;

import java.util.Optional;

public class CoarseSyncBacklog implements Backlog {

  private final OrderedLinkedListCoarse<Task> tasks = new OrderedLinkedListCoarse<>();

  @Override
  public boolean add(Task task) {
    synchronized (tasks) {
      return tasks.addTask(task);
    }
  }

  @Override
  public Optional<Task> getNextTaskToProcess() {
    synchronized (tasks) {
      Optional<Task> r = tasks.getNextTask();
      return r;
    }
  }

  @Override
  public int numberOfTasksInTheBacklog() {
    synchronized (tasks) {
      return tasks.size();
    }
  }
}
