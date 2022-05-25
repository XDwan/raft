package com.example.raftdemo.service.impl;

import com.example.raftdemo.entity.Entry;
import com.example.raftdemo.entity.Result;
import com.example.raftdemo.service.RaftService;
import org.springframework.stereotype.Service;

@Service
public class RaftServiceImpl implements RaftService {
    @Override
    public Result RequestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        return null;
    }

    @Override
    public Result AppendEntries(int term, int leaderID, int prevLogIndex, int prevLogTerm, Entry entry, int leaderCommit) {
        return null;
    }
}
