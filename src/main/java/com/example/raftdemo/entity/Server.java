package com.example.raftdemo.entity;

import com.example.raftdemo.uttils.RequestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

/**
 * @Author: XDwan
 * @Date:2022/6/2
 * @Description:
 **/

public class Server implements Runnable {

    public ServerState serverState; // 服务状态
    public int serverID; // 服务器ID
    public boolean isRun; // 服务器是否运行
    public Machine machine; // 状态机
    public int currentTerm;
    public Integer voteFor;
    public List<Entry> logs;

    // 要提交最高索引
    public int commitIndex;
    // 已提交最高索引
    public int lastApplied;

    // 对于每个follower，Leader要发送的下一个entry的index
    public List<Integer> nextIndex;
    // 对于每个follower，Leader已知已复制的entry的index
    public List<Integer> matchIndex;

    public int timer; // 服务器计时器
    public List<String> peers; // 其他服务器访问url

    // 服务器初始化
    public Server() {
        currentTerm = 0;
        voteFor = null;
        logs = new ArrayList<>();
        logs.add(new Entry()); // first index is 1
        commitIndex = 0;
        lastApplied = 0;
        transformToFollower();
    }

    // 转变为Follower
    public void transformToFollower() {
        serverState = ServerState.FOLLOWER;
        resetTimer();
    }

    // 转变为Candidate
    public void transformToCandidate() {
        serverState = ServerState.CANDIDATE;
        currentTerm++;
        voteFor = serverID;
        resetTimer();
        // 如果投票通过
        if (getVote()) {
            transformToLeader();
        }
    }

    // 转变为Leader
    public void transformToLeader() {
        serverState = ServerState.LEADER;
        nextIndex = new ArrayList<>();
        matchIndex = new ArrayList<>();
        Entry entry = lastEntry();
        for (int i = 1; i < peers.size(); i++) {
            nextIndex.add(entry.index);
            matchIndex.add(0);
        }
        resetTimer();
        heartbeat();
    }

    public void resetTimer() {
        timer = 10;
    }

    public Entry lastEntry() {
        return logs.get(logs.size() - 1);
    }

    public boolean getVote() {
        int count = 0;
        Entry entry = lastEntry();
        for (String url : peers) {
            pause(); // 模拟访问的延迟
            Result res = RequestUtil.voteRequest(url, currentTerm, serverID, entry.index, entry.term);
            if (res.success) {
                count++;
            }
        }
        return count >= (peers.size() / 2) + 1;
    }

    public void heartbeat() {
        Entry entry = lastEntry();
        Entry beat = new Entry();
        beat.term = currentTerm;
        beat.index = entry.index;
        beat.content = "beat";
        for (String url : peers) {
            Result res = RequestUtil.appendEntry(url, currentTerm, serverID, entry.index, entry.term, beat, commitIndex);
        }
    }

    public boolean appendEntry(Entry newEntry) {
        int count = 0;
        for (String url : peers) {
            Result res = RequestUtil.appendEntry(url, currentTerm, serverID, newEntry.index, newEntry.term, newEntry, commitIndex);
            if (res.success) count++;
        }
        return count >= (peers.size() / 2) + 1;
    }

    public void applyEntry() {
        if (commitIndex > lastApplied) {
            machine.apply(logs.get(lastApplied).content);
        }
        lastApplied++;
    }

    public boolean checkUpdate(int lastLogID, int lastLogTerm) {
        Entry lastEntry = lastEntry();
        if (voteFor == null) {
            return true;
        }
        if (lastLogTerm > lastEntry.term) {
            return true;
        }
        if (lastLogTerm == lastEntry.term && lastLogID > lastEntry.index) {
            return true;
        }
        return false;
    }

    public void pause() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void randPause() {
        Random random = new Random();
        try {
            sleep(100 * random.nextInt(5));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        // timer 启动
        while (true) {
            // 如果不处于运行状态，则不计时
            if (!isRun) continue;

            if (serverState == ServerState.FOLLOWER) {
                timer--;
                // 若计时结束仍然没有重置计数器 则进入candidate状态
                if (timer == 0) {
                    transformToCandidate();
                    pause();
                    continue;
                }
                // 执行log
                applyEntry();
            }

            if (serverState == ServerState.CANDIDATE) {
                timer--;
                // 若在计时截止仍然为Candidate，则重新选举
                if (timer == 0) {
                    transformToCandidate();
                }
            }

            if (serverState == ServerState.LEADER) {
                timer--;
                if (timer % 2 == 0) {
                    // 周期发送heartbeat
                    heartbeat();
                    resetTimer();
                }
            }

        }
    }
}
