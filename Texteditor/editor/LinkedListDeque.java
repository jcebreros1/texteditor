package editor;

import javafx.scene.text.Text;
import java.util.Iterator;

/**
 * Created by jesuscebreros on 3/2/16.
 */
public class LinkedListDeque implements Iterable<Text>{
    public class Node {
        private Text item;     /* Equivalent of first */
        private Node next; /* Equivalent of rest */
        private Node prev;

        public Node(Text i, Node h, Node p) {
            item = i;
            next = h;
            prev = p;
        }
    }

    private Node sentinel;
    private Node cursor;
    private int cursosPosition;
    private int size;
    private int lineWidth;
    private boolean singleLine;

    /** Creates an empty list. */
    public LinkedListDeque() {
        size = 0;
        sentinel = new Node(null, sentinel,sentinel);
        cursor = sentinel;
        cursosPosition = -1;
        singleLine = true;
    }
    public void setSingleLineToFalse(){
        singleLine = false;
    }
    public boolean getIfSingleLetter(){
        return singleLine;
    }
    /**Adds to where cursor is*/
    public void addFirstForWordWarpe(Text x) {
        if (size == 0){
            Node newNode = new Node(x, sentinel,sentinel);
            sentinel.next = newNode;
            sentinel.prev = newNode;
            cursor = newNode;
            size++;
            cursosPosition=size;
        }
        else{
            Node oldFrontNode = sentinel.next;
//            System.out.print(sentinel.prev.item.getText());
//            System.out.print(sentinel.prev.prev.item.getText());
            Node newNode = new Node(x, oldFrontNode,sentinel);
            sentinel.next = newNode;
            //sentinel.prev = newNode;
            cursor = newNode;
            oldFrontNode.prev = newNode;
            //oldFrontNode.next = newNode.next;
            //cursor = cursor.next;
            cursosPosition = size -1;
            size++;}
    }

    public void addFirst(Text x) {
        if (size == 0){
            Node newNode = new Node(x, sentinel,sentinel);
            sentinel.next = newNode;
            cursor = cursor.next;
            cursosPosition++;
            sentinel.prev = newNode;
            size += 1;
        }
        else{
            Node oldFrontNode = sentinel.next;
//            System.out.print(sentinel.prev.item.getText());
//            System.out.print(sentinel.prev.prev.item.getText());
            Node newNode = new Node(x, oldFrontNode,sentinel);
            sentinel.next = newNode;
            //sentinel.prev = newNode;
            cursor = newNode;
            oldFrontNode.prev = newNode;
            //oldFrontNode.next = newNode.next;
            //cursor = cursor.next;
            cursosPosition++;
            size += 1;}
    }

    public void add(Text a) {
        if (size == 0 || cursosPosition == -1){
            addFirst(a);
        }
        else if (cursosPosition == size - 1){
            //Node pointerToLast = sentinel.prev;
            Node pointerToLast = cursor;
            Node nextPointer = cursor.next;
            Node newNode = new Node(a, pointerToLast.next,pointerToLast);
            pointerToLast.next = newNode;
            nextPointer.prev = newNode;
            sentinel.prev = newNode;
            cursor = newNode;
            cursosPosition++;
            size++;
        }
        else{
            addToMiddle(a);
        }
    }
    private void addToMiddle(Text a){
        Node pointerToCurser = cursor;
        Node nextPointer = cursor.next;
        Node newNode = new Node(a, pointerToCurser.next,pointerToCurser);
        pointerToCurser.next = newNode;
        cursor = newNode;
        nextPointer.prev = newNode;
        size++;
        /*for (int i = 0; i < size; i++){
            System.out.print(first.item.getText());
            first = first.next;
        }System.out.println();*/
        cursosPosition++;
        //Node first = sentinel.next;
    }
    public Text delete(){
        if (size == 0){
            throw new IndexOutOfBoundsException("Out of Bounds");
        }
        if (cursosPosition == -1){
            return null;
        }
        if (size == 1){
            Node tempcursor = cursor;
            cursor = sentinel;
            sentinel.prev = sentinel;
            sentinel.next = sentinel;
            Text removed = tempcursor.item;
            tempcursor = null;
            cursosPosition--;
            --size;
            return removed;}
        else if (cursosPosition == size - 1){
            Node last = sentinel.prev;
            sentinel.prev = sentinel.prev.prev;
            //sentinel.prev.next = sentinel;
            cursor = sentinel.prev;
            cursor.next = sentinel;
            Text removed = last.item;
            last = null;
            cursosPosition--;
            --size;
            return removed;}
        else{
            Node deleted = cursor;
            //System.out.print(cursor.item.getText());
            cursor = deleted.prev;
            cursor.next = deleted.next;
            cursor.next.prev = cursor;
            //cursor.prev.next = tempcursor.next;
            //tempcursor.next.prev = tempcursor.prev;
            //System.out.println(cursor.item.getText());
            Text removed = deleted.item;
            deleted = null;
            cursosPosition--;
            --size;
            return removed;
        }
    }
    public Text get(int i){
        if (i>size)
            throw new IndexOutOfBoundsException("Out of Bounds");
        if (i == 0)
            return sentinel.next.item;
        Node p = sentinel.next;
        for (int j = 0; j < i;++j){
            p = p.next;
        }
        return p.item;
    }
    public void decreaseLetterCursorIsCurrentlyOn(){
        if (cursosPosition == -1)
            return;
        cursor = cursor.prev;
        //System.out.println(cursor.item);
        cursosPosition--;
    }
    public void increaseLetterCursorIsCurrentlyOn(){
        if (cursosPosition == size - 1)
            return;
        cursor = cursor.next;
        //System.out.println(cursor.item);
        cursosPosition++;
    }
    public void setWhatCursorPointsToFirst(){
        cursor = sentinel;
        cursosPosition = -1;
    }
    public void setWhatCursorPointsToLast(){
        cursor = sentinel.prev;
        cursosPosition = size-1;
    }
    public int getIndexOfLetterCursorIsOn(){
        return cursosPosition;
    }
    public Text getTextToRightOfCursor(){
        return cursor.next.item;
    }
    public Text getTextLeftOfCursor(){
        //System.out.println(cursor.item.getText());
        return cursor.item;
    }
    public int getLineWidth(){
        return lineWidth;
    }
    public void setLineWidth(int width){
        lineWidth = width;
    }
    /** Checks if linked list is empty*/
    public boolean isEmpty(){
        return size == 0;
    }
    public int size() {
        return size;
    }

    /** Returns the back node of our list. */
    public Text removeFirst() {
        if(size == 0){
            return null;
        }
        //cursor = cursor.next;
        Node first = sentinel.next;
        sentinel.next = first.next;
        sentinel.next.prev = sentinel;
        Text temp = first.item;
        first = null;
        --size;
        return temp;
    }

    /** Returns last item */
    public Text removeLast() {
        if (size == 0){
            return null;
        }
        Node last = sentinel.prev;
        sentinel.prev = sentinel.prev.prev;
        Text temp = last.item;
        last = null;
        --size;
        return temp;
    }
    public Text getLast(){
        return sentinel.prev.item;
    }

    private class Mapper implements Iterator<Text> {
        private Node notionOfWhereHeIs;

        public Mapper() {
            notionOfWhereHeIs = sentinel.next;
        }

        public boolean hasNext() {
            return (notionOfWhereHeIs != sentinel);
        }

        public Text next() {
            Text currentThing = notionOfWhereHeIs.item;
            notionOfWhereHeIs = notionOfWhereHeIs.next;
            return currentThing;
        }
    }
    public Iterator iterator() {
        return new Mapper();
    }
}