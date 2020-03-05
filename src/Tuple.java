public class Tuple<T> {
    private T item1;
    private T item2;

    public Tuple(T item1, T item2) {
        this.setItem1(item1);
        this.setItem2(item2);
    }

    public T getItem1() {
        return item1;
    }

    public void setItem1(T item1) {
        this.item1 = item1;
    }

    public T getItem2() {
        return item2;
    }

    public void setItem2(T item2) {
        this.item2 = item2;
    }
}
