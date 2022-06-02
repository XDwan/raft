package com.example.raftdemo.entity;

import com.example.raftdemo.uttils.RequestUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    public ArrayList<Integer> nextIndex;
    public ArrayList<Integer> matchIndex;

    // 网络设置
    public List<String> peers;
    int lastLogIndex;
    int lastLogTerm;

    // server init
    public State() {
        state = ServerState.FOLLOWER;
        timer = 10; // 初始化为10次计时器
        currentTerm = 0;
        voteFor = -1; // none
        logs = new ArrayList<>();
        nextIndex = new ArrayList<>();
        matchIndex = new ArrayList<>();
        peers = new ArrayList<>();
        commitIndex = 0;
        lastApplied = 0;
    }

    public void changeToLeader() {
        state = ServerState.LEADER;
    }

    public void changeToCandidate() {
        // conversion to candidate
        state = ServerState.CANDIDATE;
        // Increment currentTerm
        currentTerm++;
        // vote for slef
        voteFor = id;
        // reset election timer
        timer = 10;
        // send RequestVote to other servers
        for (String peer : peers) {
            RequestUtil.voteRequest(peer, currentTerm, id, lastLogIndex, lastLogTerm);
        }
    }

    public void changeToFollower() {
        state = ServerState.FOLLOWER;
    }

    public void sendHeartBeat() {
        Entry beat = new Entry();

    }

    public Entry lastLogEntry() {
        return logs.get(logs.size() - 1);
    }

    public boolean checkUpToDate(int candidateID, int lastLogIndex, int lastLogTerm) {
        if (voteFor == -1){
            return true;
        }
        Entry entry = lastLogEntry();
        if (lastLogTerm > entry.term){

        }
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

    public void resetTimer() {
        timer = 10;
    }

    @Override
    public void run() {
        while (true) {
            if (state == ServerState.FOLLOWER) {
                timer--;
                if (timer == 0) {
                    changeToCandidate();
                }
                // 状态转换完后
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("睡眠中止");
                }
            }
            if (state == ServerState.CANDIDATE) {
                timer--;
                // 计数到期 则等待随机时间后 重置随机计数
                if (timer == 0) {
                    int sleepTime = random.nextInt(10);
                    timer = 10;
                    try {
                        sleep(sleepTime * 1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                // 状态转换完后
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("睡眠中止");
                }

            }
            if (state == ServerState.LEADER) {
                timer--;
                // waiting for n second and send a heartbeat
                if (timer == 0) {
                    changeToCandidate();
                }
                // 状态转换完后
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("睡眠中止");
                }
            }

        }
    }
}
