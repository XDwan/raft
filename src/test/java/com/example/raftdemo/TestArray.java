package com.example.raftdemo;

import com.example.raftdemo.entity.Entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestArray {
    public static void main(String[] args) {
        List<Entry> a = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Entry t = new Entry();
            t.index = i;
            a.add(t);
        }
        Iterator<Entry> it = a.iterator();
        while (it.hasNext()) {
            Entry temp = it.next();
            if (temp.index > 4) {
                it.remove();
            }
        }
        System.out.println(a);
    }
}
