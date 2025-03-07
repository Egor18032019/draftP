package ru.kudo.pallets;

import java.util.*;
import java.util.stream.Collectors;

public class UniqueIndexedList<T> {
    private final Set<Integer> set = new HashSet<>();
    private final List<Integer> list = new ArrayList<>();

    public void add(Integer element) {
        if (!set.contains(element)) {
            set.add(element);
            list.add(element);
        }
    }

    public Integer get(int index) {

        Collections.sort(list);



        return list.get(index);
    }

    public int size() {
        return list.size();
    }
}
