package camp.computer.clay.model;

import java.util.LinkedList;
import java.util.List;

public class Group<T> {

    protected List<T> elements = new LinkedList<>();

    public void add(T element) {
        this.elements.add(element);
    }

    public void add(int location, T element) {
        this.elements.add(location, element);
    }

    public void add(Group<T> elements) {
        this.elements.addAll(elements.elements);
    }

    public void add(List<T> elements) {
        this.elements.addAll(elements);
    }

    public void set(int location, T element) {
        this.elements.set(location, element);
    }

    public Group<T> remove(T element) {
        this.elements.remove(element);
        return this;
    }

    public boolean contains(T element) {
        return elements.contains(element);
    }

    public int size() {
        return this.elements.size();
    }

//    public Group<T> remove(T element) {
//        this.elements.remove(element);
//        return this;
//    }

    public T get(int index) {
        return this.elements.get(index);
    }

    public T getFirst() {
        if (elements.size() > 0) {
            return elements.get(0);
        }
        return null;
    }

    public T getLast() {
        if (elements.size() > 0) {
            return elements.get(elements.size() - 1);
        }
        return null;
    }

    public int indexOf(T element) {
        return this.elements.indexOf(element);
    }
}