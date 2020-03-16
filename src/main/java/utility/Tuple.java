package utility;

import java.util.Objects;

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

    private void setItem1(T item1) {
        this.item1 = item1;
    }

    public T getItem2() {
        return item2;
    }

    private void setItem2(T item2) {
        this.item2 = item2;
    }

    public String toString() {
        return "(" + item1.toString() + ", " + item2.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?> tuple = (Tuple<?>) o;
        return Objects.equals(item1, tuple.item1) &&
                Objects.equals(item2, tuple.item2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item1, item2);
    }
}
