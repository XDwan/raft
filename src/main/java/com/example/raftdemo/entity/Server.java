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

public class Server {

    public ServerState serverState; // 服务状态
    public int serverID; // 服务器ID

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
    public String[] peers; // 其他服务器访问url

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
        resetTimer();
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
                if (count >= (int) (peers.length / 2) + 1) return true;
            }
        }
        return false;
    }

    public boolean heartbeat(){
        for (String url : peers) {
            Result res = RequestUtil.voteRequest(url, currentTerm, serverID, entry.index, entry.term);
            if (res.success) {
                count++;
                if (count >= (int) (peers.length / 2) + 1) return true;
            }
        }
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
}
