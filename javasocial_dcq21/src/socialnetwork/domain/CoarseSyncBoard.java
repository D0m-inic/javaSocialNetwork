package socialnetwork.domain;

import java.util.List;

public class CoarseSyncBoard implements Board {

  private final OrderedLinkedListCoarse<Message> messages = new OrderedLinkedListCoarse<>();

  @Override
  public boolean addMessage(Message message) {
    synchronized (messages) {
      return messages.addMessage(message);
    }
  }

  @Override
  public boolean deleteMessage(Message message) {
    synchronized (messages) {
      return messages.deleteMessage(message);
    }
  }

  @Override
  public int size() {
    synchronized (messages) {
      return messages.size();
    }
  }

  @Override
  public List<Message> getBoardSnapshot() {
    synchronized (messages) {
      return messages.getMessages();
    }
  }
}
