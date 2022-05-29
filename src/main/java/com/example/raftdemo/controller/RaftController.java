package com.example.raftdemo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.raftdemo.entity.Entry;
import com.example.raftdemo.entity.Result;
import com.example.raftdemo.service.RaftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("raft")
public class RaftController {

    @Autowired
    private RaftService raftService;

    @PostMapping("requestVote")
    public Result requestVote(@RequestParam("term") int term,
                              @RequestParam("candidateId") int candidateId,
                              @RequestParam("lastLogIndex") int lastLogIndex,
                              @RequestParam("lastLogTerm") int lastLogTerm
    ) {
        return raftService.requestVote(term, candidateId, lastLogIndex, lastLogTerm);
    }

    @PostMapping("appendEntry")
    public Result appendEntry(@RequestParam("term") int term,
                              @RequestParam("leaderID") int leaderID,
                              @RequestParam("prevLogIndex") int prevLogIndex,
                              @RequestParam("prevLogTerm") int prevLogTerm,
                              @RequestParam("newEntry") String newEntryString,
                              @RequestParam("leaderCommit") int leaderCommit) {

        Entry newEntry = JSONObject.parseObject(newEntryString, Entry.class);
        return raftService.appendEntries(term, leaderID, prevLogIndex, prevLogTerm, newEntry, leaderCommit);
    }

    @PostMapping("submit")
    public String submit() {
        return new String("success");
    }

    // 节点操作
    @PostMapping("args")
    public String args(@RequestParam("operate")String operate){
        return raftService.stateChange(operate);
    }
}
