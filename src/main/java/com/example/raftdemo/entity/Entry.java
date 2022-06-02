package com.example.raftdemo.entity;

public class Entry {
    public int term;
    public int index;
    public String content;

    public Entry() {
        term = 0;
        index = 0;
        content = "";
    }

    @Override
    public String toString() {
        return "Entry{" +
                "term=" + term +
                ", index=" + index +
                ", content='" + content + '\'' +
                '}';
    }
}
