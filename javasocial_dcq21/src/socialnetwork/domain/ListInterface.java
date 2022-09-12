package socialnetwork.domain;

import java.util.List;
import java.util.Optional;

public interface ListInterface<E> {

  boolean addMessage(Message messageElem);

  boolean addTask(Task taskElem);

  List<Message> getMessages();

  Optional<Task> getNextTask();

  boolean deleteMessage(Message messageElem);

  int size();

  boolean isEmpty();
}
