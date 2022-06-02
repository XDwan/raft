# Raft 实现

## 节点三种状态

- Leader
- follower
- candidate

## 代码结构

使用Http协议做rpc调用

raft-server: 服务器节点

## Rules for Servers

### 所有服务器

```
if (commitIndex > lastApplied){
    lastApplied += 1;
    apply(log[lastApplied]);
}
if(RPC.term > currentTerm){
    currentTerm = RPC.term; 
    state = FOLLOWER;   
}

```

### Follower

```java
class Follower{
    // Respond to RPCs from candidates and leaders
    public void respond(){
        
    }
    // election timeout
    public void convertToCandidate(){
        
    }
}
```

### Candidates
```java
class Candidates{
    // conversion to candidate
    public void conversion(){
        currentTerm++;
        voteFor = selfId;
        
    }
    // reset election timer
    public void resetTimer(){
        timer = 10;
    }
    // send RequestVote RPC to others
    public void sendVotes(){
        int counter = 0;
        timerStart();// new Timer thread
        for (String peer:peers){
            if (answear()) {
                counter++;
            }
        }
    }
    // 启动计时器
    public void timerStart(){
        if (timeout){
            timer = randm.nextInt();
        }
    }
}
```

### Leader

```java

class Leader {
    public void sendHeartbeat() {

    }

    // receive command from client
    public void receive(String content) {
        // set new entry
        Entry entry = new Entry();
        // append entry to local log
        logs.append(entry);
        // send entry to followers
        sendAppendEntry(entry);
        
    }
}
```
