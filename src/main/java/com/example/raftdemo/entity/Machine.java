package com.example.raftdemo.entity;

import java.util.List;
import java.util.Stack;

/**
 * @Author: XDwan
 * @Date:2022/6/2
 * @Description:
 **/
public class Machine {

    List<StackComputer> instances;

    public int apply(String content) {
        String[] op = content.split(" ");
        StackComputer computer = null;
        switch (op[0]) {
            case "CREATE":
                computer = new StackComputer();
                instances.add(computer);
                return computer.instance_id;
            case "DELETE":
                instances.remove(Integer.parseInt(op[1]));
                return 1;
            case "PUSH":
                if (op.length != 3) return -1;
                computer = instances.get(Integer.parseInt(op[1]));
                computer.stack.push(Integer.parseInt(op[2]));
                return 1;
            case "POP":
                if (op.length != 2) return -1;
                computer = instances.get(Integer.parseInt(op[1]));
                return computer.stack.pop();

            default:
                return -1;
        }
    }

}
