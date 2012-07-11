// This LinkedList class was necessary because all java.util.Collection
// classes use a horrifically inefficient algorithm to merge lists
// (converts one to an array, then adds each element one by one)

package antelope;

public class LinkedList<E> implements Iterable<E> {
    private static class Node<E> {
        public E item;
        public Node next;
        public Node(E i, Node n) { item = i; next = n; }
    }

    private static class NodeIterator<E> implements java.util.Iterator<E> {
        private Node<E> current;
        public NodeIterator(Node<E> n) { current = n; }
        public boolean hasNext() { return current != null; }
        public void remove() { throw new UnsupportedOperationException(); }
        public E next() { E e = current.item; current = current.next; return e; }
    }

    private Node<E> head;
    private Node<E> tail;
    private int length;

    public LinkedList() { clear(); }
    public LinkedList(LinkedList list) { clear(); consume(list); }

    public int size(  ) { return length;    }
    public E getFirst() { return head.item; }
    public E getLast( ) { return tail.item; }
    public void clear() { head = tail = null; length = 0; }
    public java.util.Iterator<E> iterator() { return new NodeIterator<E>(head); }

    public void addFirst(E e) {
        if(head == null) { head = tail = new Node<E>(e, null); }
        else { head = new Node(e, head); }
        length++;
    }

    public void add(E e) {
        if(head == null) { head = tail = new Node<E>(e, null); }
        else { tail.next = new Node(e, null); tail = tail.next; }
        length++;
    }

    public void consume(LinkedList<E> list) {
        if(head == null) {
            if(list.head != null) {
                head = list.head;
                tail = list.tail;
                length = list.length;
                list.clear();
            }
        }
        else if(list.head != null) {
            tail.next = list.head;
            tail = list.tail;
            length += list.length;
            list.clear();
        }
    }
}

