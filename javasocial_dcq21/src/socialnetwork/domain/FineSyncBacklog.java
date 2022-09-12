package socialnetwork.domain;

import java.util.Optional;

public class FineSyncBacklog implements Backlog {

  private final OrderedLinkedListFineGrain<Task> tasks = new OrderedLinkedListFineGrain<>();

  @Override
  public boolean add(Task task) {
      return tasks.addTask(task);
  }

  @Override
  public Optional<Task> getNextTaskToProcess() {
      Optional<Task> r = tasks.getNextTask();
      return r;
  }

  @Override
  public int numberOfTasksInTheBacklog() {
      return tasks.size();
  }
}
