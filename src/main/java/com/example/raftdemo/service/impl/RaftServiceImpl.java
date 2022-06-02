package com.example.raftdemo.service.impl;

import com.example.raftdemo.entity.*;
import com.example.raftdemo.service.RaftService;
import org.springframework.stereotype.Service;

import java.util.Iterator;


@Service
public class RaftServiceImpl implements RaftService {

    public Server server = new Server();
    Thread thread = null;

    @Override
    public Result requestVote(int term, int candidateID, int lastLogIndex, int lastLogTerm) {
        Result result = new Result();
        // 当前节点不可用
        if (!server.isRun) {
            result.success = false;
            return result;
        }
        result.term = server.currentTerm;
        // reply false if term < currentTerm
        if (term < server.currentTerm) {
            result.success = false;
            return result;
        }
        // if voteFor == -1 || candidateId and log is up-to-date
        if (server.checkUpdate(lastLogIndex, lastLogTerm)) {
            result.success = true;
            // election timeout but vote to candidate
            server.resetTimer();
            server.voteFor = candidateID;
        }
        return result;
    }

    @Override
    public Result appendEntries(int term, int leaderID, int prevLogIndex, int prevLogTerm, Entry newEntry, int leaderCommit) {
        Result result = new Result();
        // 当前节点不可用
        if (!server.isRun) {
            result.success = false;
            return result;
        }
        result.term = server.currentTerm;
        // reply false if term < current term
        if (term > server.currentTerm) {
            result.success = false;
            return result;
        }
        // 接收到rpc 请求得知Leader存活
        server.resetTimer();


        // reply false if log doesn't contain an entry at preLogIndex whose term matches prevLogTerm
        Iterator<Entry> it = server.logs.iterator();
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
        for (Entry selfEntry : server.logs) {
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
            Iterator<Entry> iterator = server.logs.iterator();
            while (iterator.hasNext()) {
                Entry temp = it.next();
                if (temp.index >= newEntry.index) {
                    it.remove();
                }
            }
        }
        // append new entry in the log
        if (!isExist) {
            server.logs.add(newEntry);
        }
        // if leaderCommit > commitIndex
        // let commitIndex = min(leaderCommit, index of last new entry)
        if (leaderCommit > server.commitIndex) {
            server.commitIndex = Math.min(leaderCommit, server.lastEntry().index);
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
            server.isRun = true;
            thread = new Thread(server);
            thread.start();
            System.out.println("节点激活，timer启动");
        }
        // 节点设置为不可用
        if (args[0].equals("stop")) {
            server.isRun = false;
            assert thread != null;
            thread.interrupt();
            System.out.println("节点关闭，timer关闭");
        }

        if (args[0].equals("add")) {
            server.peers.add(args[1]);
            System.out.println("新增节点:" + args[1]);
        }

        if (args[0].equals("del")) {
            if (server.peers.contains(args[1])) {
                server.peers.remove(args[1]);
                System.out.println("节点" + args[1] + "已删除");
            } else {
                System.out.println("节点" + args[1] + "不存在");
            }
        }

        if (args[0].equals("show")) {
            System.out.println("节点列表如下：");
            for (String peer : server.peers) {
                System.out.println("- " + peer);
            }
        }

        if (args[0].equals("apply")) {
            Entry entry = new Entry();
            StringBuilder content = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                content.append(args[i]).append(" ");
            }
            entry.content = content.toString();
            if (server.serverState == ServerState.LEADER) {
                entry.index = server.lastEntry().index + 1;
                entry.term = server.currentTerm;
                // 如果超半数服务器回复，则执行
                if (server.appendEntry(entry)){
                    server.applyEntry();
                }
            }
        }

        return new String("success");
    }
}
