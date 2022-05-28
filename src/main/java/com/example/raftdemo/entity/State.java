package com.example.raftdemo.entity;

import java.util.ArrayList;
import java.util.List;

enum ServerState{
    FOLLOWER,CANDIDATE,LEADER;
}

public class State {
    // current state
    ServerState state;

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

    // server init
    public State() {
        state = ServerState.FOLLOWER;
        currentTerm = 0;
        voteFor = -1; // none
        logs = new ArrayList<>();
        commitIndex = 0;
        lastApplied = 0;
    }

    public void changeToLeader(){
        state = ServerState.LEADER;
    }

    public void changeToCandidate(){
        state = ServerState.CANDIDATE;
    }

    public void changeToFollower(){
        state = ServerState.FOLLOWER;
    }
}
