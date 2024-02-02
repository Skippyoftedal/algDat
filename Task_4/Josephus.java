
public class Josephus {

  public static void main(String[] args) {
    System.out.printf("Optimal position: %d\n", optimalPosition(11, 10));
  }

  public static int optimalPosition(int nPeople, int mInterval){
    if (mInterval < 1){
      throw new IllegalArgumentException("Interval has to be 1 or larger");
    }
    if (mInterval == 1){
      return nPeople;
    }

    Node last = new Node(nPeople, null);
    Node current = last;

    for (int i = nPeople - 1; i > 0; i--) {
      current = new Node(i, current);
    }
    last.setNode(current);

    current = last;
    Node previous = null;

    while (current.getNext() != current){
      for (int i = 0; i < mInterval; i++) {
        previous = current;
        current = current.getNext();
      }
      previous.removeNext();
    }

    return current.getElement();
  }
}


class Node{
  private final int element;
  private Node next;

  public Node(int e, Node n){
    element = e;
    next = n;
  }

  public int getElement(){
    return element;
  }

  public Node getNext(){
    return next;
  }

  public void setNode(Node n){
    next = n;
  }


  public void removeNext(){
    setNode(getNext().getNext());
  }

}
