package com.example.raftdemo.controller;

import com.example.raftdemo.entity.Result;
import com.example.raftdemo.service.RaftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("raft")
public class RaftController {

    @Autowired
    private RaftService raftService;

    @PostMapping("requestVote")
    public Result requestVote() {
        Result result = new Result();
        return result;
    }

    @PostMapping("appendEntry")
    public Result appendEntry() {
        Result result = new Result();
        return result;
    }

}
