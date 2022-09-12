package socialnetwork.domain;

import java.util.List;

public class FineSyncBoard implements Board {

  private final OrderedLinkedListFineGrain<Message> messages = new OrderedLinkedListFineGrain<>();

  @Override
  public boolean addMessage(Message message) {
      return messages.addMessage(message);
  }

  @Override
  public boolean deleteMessage(Message message) {
      return messages.deleteMessage(message);
  }

  @Override
  public int size() {
      return messages.size();
  }

  @Override
  public List<Message> getBoardSnapshot() {
      return messages.getMessages();
  }
}
