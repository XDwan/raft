package com.example.raftdemo.service.impl;

import com.example.raftdemo.entity.Entry;
import com.example.raftdemo.entity.Result;
import com.example.raftdemo.entity.State;
import com.example.raftdemo.service.RaftService;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class RaftServiceImpl implements RaftService {

    public State state = new State();
    Thread thread = null;

    @Override
    public Result requestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        Result result = new Result();
        // 当前节点不可用
        if (!state.isAvailable) {
            result.success = false;
            return result;
        }
        // reply false if term < currentTerm
        if (term < state.currentTerm) {
            result.success = false;
            return result;
        }

        // if voteFor == -1 || candidateId and log is up-to-date
        if (state.checkUpToDate(candidateID, lastLogIndex, lastLogTerm)) {
            result.success = true;
            // election timeout but vote to candidate
            state.resetTimer();
            state.voteFor = candidateID;
        }
        return result;
    }

    @Override
    public Result appendEntries(int term, int leaderID, int prevLogIndex, int prevLogTerm, Entry newEntry, int leaderCommit) {
        Result result = new Result();
        // 当前节点不可用
        if (!state.isAvailable) {
            result.success = false;
            return result;
        }
        result.term = state.currentTerm;
        // reply false if term < current term
        if (term > state.currentTerm) {
            result.success = false;
            return result;
        }
        state.resetTimer();
        // reply false if log doesn't contain an entry at preLogIndex whose term matches prevLogTerm
        Iterator<Entry> it = state.logs.iterator();
        boolean isIn = false;
        while (it.hasNext()) {
            Entry tmp = it.next();
            if (tmp.term == prevLogTerm && tmp.index == prevLogTerm) {
                isIn = true;
                break;
            }
        }
        if (!isIn) {
            result.success = false;
            return result;
        }
        // if an existing entry conflicts (same index but different term)
        // delete the entry and all entries follow it
        boolean isDel = false;
        boolean isExist = false;
        for (Entry selfEntry : state.logs) {
            if (selfEntry.index == newEntry.index && selfEntry.term != newEntry.term) {
                isDel = true;
                break;
            }
            if (selfEntry.index == newEntry.index) {
                isExist = true;
                break;
            }
        }
        if (isDel) {
            Iterator<Entry> iterator = state.logs.iterator();
            while (iterator.hasNext()) {
                Entry temp = it.next();
                if (temp.index >= newEntry.index) {
                    it.remove();
                }
            }
        }
        // append new entry in the log
        if (!isExist) {
            state.logs.add(newEntry);
        }
        // if leaderCommit > commitIndex
        // let commitIndex = min(leaderCommit, index of last new entry)
        if (leaderCommit > state.commitIndex) {
            state.commitIndex = Math.min(leaderCommit, state.maxIndex());
        }
        return result;
    }

    @Override
    public Result access() {
        return null;
    }

    @Override
    public String stateChange(String operate) {
        String[] args = operate.split(" ");
        // 节点设置为可用
        if (args[0].equals("start")) {
            state.isAvailable = true;
            thread = new Thread(state);
            thread.start();
            System.out.println("节点激活，timer启动");
        }
        // 节点设置为不可用
        if (args[0].equals("stop")) {
            state.isAvailable = false;
            assert thread != null;
            thread.interrupt();
            System.out.println("节点关闭，timer关闭");
        }

        if (args[0].equals("add")) {
            state.peers.add(args[1]);
            System.out.println("新增节点:" + args[1]);
        }

        if (args[0].equals("del")) {
            if (state.peers.contains(args[1])) {
                state.peers.remove(args[1]);
                System.out.println("节点" + args[1] + "已删除");
            } else {
                System.out.println("节点" + args[1] + "不存在");
            }
        }

        if (args[0].equals("show")) {
            System.out.println("节点列表如下：");
            for (String peer : state.peers) {
                System.out.println("- " + peer);
            }
        }

        return new String("success");
    }
}
