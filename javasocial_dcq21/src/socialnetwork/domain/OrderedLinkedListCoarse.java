package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderedLinkedListCoarse<E> implements ListInterface<E> {

  private Node<Message> messageHead;
  private Node<Task> taskHead;
  private Node<E> head;
  private int size;

  @Override
  public boolean addMessage(Message messageElem) {

    final Node<Message> newNode = new Node<Message>(messageElem);
    if (messageHead == null) {
      messageHead = newNode;
    } else if (messageElem.getMessageId() > messageHead.getElem().getMessageId()) {
      newNode.setNext(messageHead);
      messageHead = newNode;
    } else {
      Node<Message> cursor = messageHead;
      while (cursor.getNext() != null
          && messageElem.getMessageId() < cursor.getNext().getElem().getMessageId()) {
        cursor = cursor.getNext();

        if (messageElem.getMessageId() == cursor.getElem().getMessageId()) {
          return false;
        }
      }

      newNode.setNext(cursor.getNext());
      cursor.setNext(newNode);
    }
    size++;
    return true;
  }

  @Override
  public boolean addTask(Task taskElem) {

    final Node<Task> newNode = new Node<Task>(taskElem);
    if (taskHead == null || taskElem.getId() == taskHead.getElem().getId()) {
      newNode.setNext(taskHead);
      taskHead = newNode;
    } else {
      Node<Task> cursor = taskHead;
      while (cursor.getNext() != null && taskElem.getId() <= cursor.getNext().getElem().getId()) {
        cursor = cursor.getNext();

        if (taskElem.getId() == cursor.getElem().getId()) {
          return false;
        }
      }
      newNode.setNext(cursor.getNext());
      cursor.setNext(newNode);
    }
    size++;
    return true;
  }

  @Override
  public List<Message> getMessages() {
    List<Message> listOfMessages = new ArrayList<Message>();
    if (messageHead == null) {
      return listOfMessages;
    }

    Node<Message> cursor = messageHead;
    while (cursor.getNext() != null) {
      listOfMessages.add(cursor.getElem());
      cursor = cursor.getNext();
    }
    listOfMessages.add(cursor.getElem());
    return listOfMessages;
  }

  public Optional<Task> getNextTask() {
    if (taskHead == null) {
      return Optional.empty();
    }

    Node<Task> cursor = taskHead;

    if (cursor.getNext() == null) {
      Node<Task> lastTask = taskHead;
      taskHead = null;
      size--;
      return Optional.of(lastTask.getElem());
    }

    while (cursor.getNext().getNext() != null) {
      cursor = cursor.getNext();
    }

    Node<Task> lastTask = cursor.getNext();
    cursor.setNext(null);
    size--;
    return Optional.of(lastTask.getElem());
  }

  @Override
  public boolean deleteMessage(final Message messageElem) {
    if (messageHead == null) {
      return false;
    }

    if (messageElem.getMessageId() == messageHead.getElem().getMessageId()) {
      messageHead = messageHead.getNext();
      size--;
      return true;
    } else if (messageElem.getMessageId() > messageHead.getElem().getMessageId()) {
      return false;
    } else {
      Node<Message> parent = messageHead;
      Node<Message> cursor = parent.getNext();

      while (cursor != null && cursor.getElem().getMessageId() > messageElem.getMessageId()) {
        parent = cursor;
        cursor = cursor.getNext();
      }

      if (cursor.getElem().getMessageId() == messageElem.getMessageId()) {
        parent.setNext(cursor.getNext());
        size--;
        return true;
      }
    }
    return false;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  private class Node<E> {

    private E elem;
    private Node<E> next;

    public Node(final E elem) {
      this(elem, null);
    }

    public Node(final E elem, final Node<E> next) {
      this.elem = elem;
      this.next = next;
    }

    public E getElem() {
      if (elem == null) {
        return null;
      }
      return elem;
    }

    public void setElem(final E elem) {
      this.elem = elem;
    }

    public Node<E> getNext() {
      return next;
    }

    public void setNext(final Node<E> next) {
      this.next = next;
    }
  }
}
