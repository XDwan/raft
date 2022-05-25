package com.example.raftdemo.entity;

public class Result {
    //currentTerm, for candidate to update itself
    public int term;
    //true means candidate received vote
    public boolean success;
}
