package com.example.raftdemo;

import com.alibaba.fastjson.JSONObject;
import com.example.raftdemo.entity.Entry;

import java.util.Random;

/**
 * @Author: XDwan
 * @Date:2022/5/29
 * @Description:
 **/
public class TestJSON {
    public static void main(String[] args) {
        Entry entry = new Entry();
        entry.index = 1;
        entry.term = 1;
        entry.content = "hello";
        System.out.println(entry);
        String entryString  = JSONObject.toJSONString(entry);
        System.out.println(entryString);
        Entry entry1 = JSONObject.parseObject(entryString,Entry.class);
        System.out.println(entry1);
        String a = new String("123");
        System.out.println(a.equals(new String("123")));
        Random random = new Random();
        for (int i=0;i<10;i++){
            int timer = random.nextInt(15);
            System.out.println(timer);
        }
    }
}
