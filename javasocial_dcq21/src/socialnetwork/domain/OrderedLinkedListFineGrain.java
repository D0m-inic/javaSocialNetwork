package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderedLinkedListFineGrain<E> implements ListInterface {

  private final LockableNode<Task> headTask, tailTask;
  private final LockableNode<Message> headMessage, tailMessage;
  AtomicInteger size1 = new AtomicInteger(0);

  public OrderedLinkedListFineGrain() {
    headTask = new LockableNode<>(null, null);
    tailTask = new LockableNode<>(null, null);
    headTask.setNext(tailTask);

    headMessage = new LockableNode<>(null, null);
    tailMessage = new LockableNode<>(null, null);
    headMessage.setNext(tailMessage);

  }

  private Position<Message> findMessage(LockableNode<Message> start, int key) {
    LockableNode<Message> pred, curr;
    pred = start;
    pred.lock();
    curr = start.getNext();
    curr.lock();
    LockableNode<Message> emptyNode = new LockableNode<>(null, null);
    if (curr.getElem() == null || curr.equals(emptyNode)) {
      return new Position<>(pred, curr);
    }
    while (curr.getElem().getMessageId() > key) {
      pred.unlock();
      pred = curr;
      curr = curr.getNext();
      curr.lock();
      if (curr.getElem() == null || curr.equals(emptyNode)) {
        return new Position<>(pred, curr);
      }
    }
    return new Position<>(pred, curr);
  }

  private Position<Task> findTask(LockableNode<Task> start, int key) {
    LockableNode<Task> pred, curr;
    pred = start;
    pred.lock();
    curr = start.getNext();
    curr.lock();
    LockableNode<Task> emptyNode = new LockableNode<>(null, null);
    if (curr.getElem() == null || curr.equals(emptyNode)) {
      return new Position<>(pred, curr);
    }
    while (curr.getElem().getId() > key) {
      pred.unlock();
      pred = curr;
      curr = curr.getNext();
      curr.lock();
      if (curr.getElem() == null || curr.equals(emptyNode)) {
        return new Position<>(pred, curr);
      }
    }
    return new Position<>(pred, curr);
  }

  @Override
  public boolean addMessage(Message element) {
    LockableNode<Message> node = new LockableNode<>(element);
    LockableNode<Message> pred = null, curr = null;
    try {
      Position<Message> where = findMessage(headMessage, node.getElem().getMessageId());
      pred = where.pred;
      curr = where.curr;
      if (where.curr.getElem() != null) {
        if (where.curr.getElem().getMessageId() == node.getElem().getMessageId()) {
          return false;
        } else {
          node.setNext(where.curr);
          where.pred.setNext(node);
          size1.incrementAndGet();
          return true;
        }
      } else {
        node.setNext(where.curr);
        where.pred.setNext(node);
        size1.incrementAndGet();
        return true;
      }
    } finally {
      pred.unlock();
      curr.unlock();
    }
  }

  @Override
  public boolean addTask(Task element) {
    LockableNode<Task> node = new LockableNode<>(element);
    LockableNode<Task> pred = null, curr = null;
    try {
      Position<Task> where = findTask(headTask, node.getElem().getId());
      pred = where.pred;
      curr = where.curr;
      if (where.curr.getElem() != null) {
        if (where.curr.getElem().getId() == node.getElem().getId()) {
          return false;
        } else {
          node.setNext(where.curr);
          where.pred.setNext(node);
          size1.incrementAndGet();
          //size++;
          return true;
        }
      } else {
        node.setNext(where.curr);
        where.pred.setNext(node);
        size1.incrementAndGet();
        //size++;
        return true;
      }
    } finally {
      curr.unlock();
      pred.unlock();

    }
  }

  @Override
  public boolean deleteMessage(Message message) {
    LockableNode<Message> node = new LockableNode<>(message);
    LockableNode<Message> pred = null, curr = null;
    try {
      Position<Message> where = findMessage(headMessage,
          node.getElem().getMessageId());
      pred = where.pred;
      curr = where.curr;
      if (where.curr.getElem().getMessageId() < node.getElem().getMessageId()) {
        return false;
      } else {
        where.pred.setNext(where.curr.getNext());
        size1.decrementAndGet();
        // size--;
        return true;
      }
    } finally {
      pred.unlock();
      curr.unlock();
    }
  }

  @Override
  public Optional<Task> getNextTask() {
    LockableNode<Task> pred, curr;
    pred = headTask;
    pred.lock();
    curr = pred.getNext();
    curr.lock();
    try {
      if (curr.getElem() == null) {
        return Optional.empty();
      }
      while (curr.getNext().getElem() != null) {
        pred.unlock();
        pred = curr;
        curr = curr.getNext();
        curr.lock();
      }
      size1.decrementAndGet();
      pred.setNext(tailTask);
      return Optional.of(curr.getElem());
    } finally {
      pred.unlock();
      curr.unlock();
    }
  }

  @Override
  public List<Message> getMessages() {
    List<Message> boardSnapshot = new ArrayList<>();
    LockableNode<Message> curr = headMessage.getNext();
    while (curr != tailMessage) {
      boardSnapshot.add(curr.getElem());
      curr = curr.getNext();
    }
    return boardSnapshot;
  }


  @Override
  public synchronized int size() {
    //return size.get();
    return size1.get();
  }

  @Override
  public synchronized boolean isEmpty() {
    //return size.get() == 0;
    return size1.get() == 0;
  }

  public synchronized int numberOfTasksInTheBacklog() {
    return size1.get();
  }

  private static class Position<E>{
    public final LockableNode<E> pred, curr;

    public Position(LockableNode<E> pred, LockableNode<E> curr) {
      this.pred = pred;
      this.curr = curr;
    }
  }

  private static class LockableNode<E> {

    private Lock lock = new ReentrantLock();

    private E elem;
    private LockableNode<E> next;

    public LockableNode(E elem) {
      this(elem, null);
    }

    public E getElem() {
      return this.elem;
    }

    protected LockableNode(E elem, LockableNode<E> next) {
      this.elem = elem;
      this.next = next;
    }

    public LockableNode<E> getNext() {
      return next;
    }

    public void setNext(LockableNode<E> next) {
      this.next = next;
    }

    public void lock() {
      lock.lock();
    }

    public void unlock() {
      lock.unlock();
    }
  }
}
