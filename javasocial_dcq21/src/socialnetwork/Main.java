package socialnetwork;

import java.util.Arrays;
import socialnetwork.domain.Backlog;
import socialnetwork.domain.CoarseSyncBacklog;
import socialnetwork.domain.CoarseSyncBoard;
import socialnetwork.domain.Worker;
import static org.junit.Assert.assertEquals;

public class Main {

  public static void main(String[] args) {
    int NUMBER_OF_WORKERS = 3;
    int NUMBER_OF_USERS = 10;
    final int SEED = 123456;

    Backlog backlog = new CoarseSyncBacklog();
    SocialNetwork socialNetwork = new SocialNetwork(backlog);

    Worker[] workers = new Worker[NUMBER_OF_WORKERS];
    Arrays.setAll(workers, i -> new Worker(backlog));
    Arrays.stream(workers).forEach(Thread::start);

    User[] users = new User[NUMBER_OF_USERS];
    Arrays.setAll(
        users,
        i -> {
          User user = new User("user" + i, socialNetwork);
          return user;
        });
    Arrays.stream(users)
        .forEach(
            u -> {
              socialNetwork.register(u, new CoarseSyncBoard());
              u.start();
            });
    Arrays.stream(users)
        .forEach(
            u -> {
              try {
                u.join();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
    while (backlog.numberOfTasksInTheBacklog() > 0) {
      try {
        System.out.println(backlog.numberOfTasksInTheBacklog() + " tasks left");
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    Arrays.stream(workers).forEach(Worker::interrupt);
    Arrays.stream(workers)
        .forEach(
            w -> {
              try {
                w.join();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            });
    System.out.println(backlog);
    assertEquals(0, backlog.numberOfTasksInTheBacklog());
    System.out.println("Work's done, checking for consistency...");
  }
}
