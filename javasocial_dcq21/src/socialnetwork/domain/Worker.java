package socialnetwork.domain;

import java.util.Optional;
import socialnetwork.domain.Task.Command;

public class Worker extends Thread {

  private final Backlog backlog;
  private boolean interrupted = false;

  public Worker(Backlog backlog) {
    this.backlog = backlog;
  }

  @Override
  public void run() {
    while (!interrupted) {
      Optional<Task> task = backlog.getNextTaskToProcess();
      if (task.isPresent()) {
        process(task.get());
      } else {
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void interrupt() {
    this.interrupted = true;
  }

  public void process(Task nextTask) {
    Message message = nextTask.getMessage();
    if (nextTask.command == Task.Command.POST) {
      nextTask.getBoard().addMessage(message);
    } else {
      if (!nextTask.getBoard().deleteMessage(message)) {
        backlog.add(nextTask);
      }
    }
  }
}
