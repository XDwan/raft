package com.example.raftdemo.controller;

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
    public Result appendEntry() {
        Result result = new Result();
        return result;
    }

    @PostMapping("submit")
    public String submit() {
        return new String("success");
    }
}
