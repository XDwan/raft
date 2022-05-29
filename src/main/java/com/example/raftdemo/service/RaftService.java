package com.example.raftdemo.service;


import com.example.raftdemo.entity.Entry;
import com.example.raftdemo.entity.Result;

public interface RaftService {
    // Invoked by candidates to gather votes
    public Result requestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm);

    //Invoked by leader to replicate log entries ; also used as heartbeat.
    public Result appendEntries(int term, int leaderID, int prevLogIndex, int prevLogTerm, Entry entry, int leaderCommit);

    public Result access();

    public String stateChange(String operate);
}
