package com.example.raftdemo.entity;

import java.util.Stack;

/**
 * @Author: XDwan
 * @Date:2022/6/2
 * @Description:
 **/
public class StackComputer {
    static int id = 0;
    int instance_id;
    Stack<Integer> stack;

    public StackComputer(){
        instance_id = id;
        id++;
        stack = new Stack<>();
    }
}
