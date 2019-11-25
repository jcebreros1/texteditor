package editor;

public class ArrayDeque<Item> {
    /* the stored integers */
    private Item[] items;
    private int size;
    private int front;
    private int back;

    private static int RFACTOR = 2;

    /** Creates an empty list. */
    public ArrayDeque() {
        size = 0;
        front = 0;
        back = -1;
        items = (Item[]) new Object[8];
    }

    /** Resize our backing array so that it is
     * of the given capacity. */
    private void resize(int capacity) {
        Item[] a = (Item[]) new Object[capacity];
        for (int i = 0; i < size;++i){
            a[i] = items[front];
            front = (front +1)% items.length;
        }
        front = 0;
        back = size -1;
        items = a;
    }
    public void addLast(Item a){
        if (isFull())
            resize(RFACTOR * size);
        back = (back + 1) % items.length;
        items[back] = a;
        ++size;
    }
    public void addFirst(Item a){
        if (isFull())
            resize(RFACTOR * size);
        if (front == 0) {
            if (size == 0){
                back = (back+1)%items.length;
                items[front] = a;}
            else{
                front = items.length-1;
                items[front] = a;}
        }
        else{
            front = front - 1;
            items[front] = a;
        }
        ++size;
    }
    public boolean isEmpty(){
        return size == 0;
    }

    /** Returns the number of items in the list. */
    public int size() {
        return size;
    }
    public void printDeque(){
        for (int i = 0;i < size; i++){
            System.out.println(get(i));
        }
    }

    /** Deletes item from back of the list and
     * returns deleted item. */
    public Item removeFirst(){
        if (isEmpty()){
            return null;
        }
        Item temp = items[front];
        //items[front] = null;
        front = (front + 1) % items.length;
        --size;
        if (size <= (double) items.length/4 && items.length > 16)
            resize(items.length/2);
        return temp;
    }

    /**Removes the last*/
    public Item removeLast(){
        if (isEmpty()){
            return null;
        }
        Item temp = items[back];
        //items[back] = null;
        if (back == 0 && size > 1)
            back = items.length-1;
        else if (back >= 0)
            back -= 1;
        --size;
        if (size <= (double) items.length/4 && items.length > 16)
            resize(items.length/2);
        return temp;
    }

    /** Gets the ith item in the list (0 is the front). */
    public Item get(int i) {
        if (isEmpty()){
            return null;
        }
        return items[(front + i) % items.length];
    }
    private boolean isFull(){
        return size == items.length;
    }
}