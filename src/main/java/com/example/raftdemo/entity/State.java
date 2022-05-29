package com.example.raftdemo.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

enum ServerState {
    FOLLOWER, CANDIDATE, LEADER;
}

public class State extends Thread {
    // current state
    public int id;
    public ServerState state;
    public boolean isAvailable;
    public int timer;
    Random random = new Random();
    // persistent state on all servers
    public int currentTerm;
    public int voteFor;
    public List<Entry> logs;

    // volatile state on all servers
    public int commitIndex;
    public int lastApplied;

    // volatile state on leaders
    public List<Integer> nextIndex;
    public List<Integer> matchIndex;

    // 网络设置
    String[] peers;

    // server init
    public State() {
        state = ServerState.FOLLOWER;
        timer = 10; // 初始化为10次计时器
        currentTerm = 0;
        voteFor = -1; // none
        logs = new ArrayList<>();
        commitIndex = 0;
        lastApplied = 0;
    }

    public void changeToLeader() {
        state = ServerState.LEADER;
    }

    public void changeToCandidate() {
        state = ServerState.CANDIDATE;
        // Increment currentTerm
        currentTerm++;
        // vote for slef
        voteFor = id;
        timer = random.nextInt(15);
    }

    public void changeToFollower() {
        state = ServerState.FOLLOWER;
    }

    public boolean checkUpToDate(int candidateID, int lastLogIndex, int lastLogTerm) {
        return false;
    }

    public int maxIndex() {
        int max = 0;
        for (Entry entry : logs) {
            if (entry.index > max) {
                max = entry.index;
            }
        }
        return max;
    }

    @Override
    public void run() {
        while (true) {
            timer--;
            if (timer == 0 && state == ServerState.FOLLOWER) {
                changeToCandidate();
            }
            if (timer == 0 && state == ServerState.CANDIDATE) {
                changeToCandidate();
            }
            // 状态转换完后
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
