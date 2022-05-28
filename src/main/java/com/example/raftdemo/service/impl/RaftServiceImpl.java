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

    @Override
    public Result requestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        Result result = new Result();
        result.term = state.currentTerm;
        // reply false if term < currentTerm
        if (term < state.currentTerm) {
            result.success = false;
            return result;
        }
        // if voteFor == -1 || candidateId and log is up-to-date
        if (state.voteFor == -1) {
            result.success = true;
        }
        return result;
    }

    @Override
    public Result appendEntries(int term, int leaderID, int prevLogIndex, int prevLogTerm, Entry newEntry, int leaderCommit) {
        Result result = new Result();
        result.term = state.currentTerm;
        // reply false if term < current term
        if (term > state.currentTerm) {
            result.success = false;
            return result;
        }
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
            state.commitIndex = Math.min(leaderCommit, newEntry.index);
        }
        return result;
    }

    @Override
    public Result access() {
        return null;
    }
}
