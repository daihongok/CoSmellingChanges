class Tuple<T> {
    private T item1;
    private T item2;

    Tuple(T item1, T item2) {
        this.setItem1(item1);
        this.setItem2(item2);
    }

    T getItem1() {
        return item1;
    }

    private void setItem1(T item1) {
        this.item1 = item1;
    }

    T getItem2() {
        return item2;
    }

    private void setItem2(T item2) {
        this.item2 = item2;
    }
}
