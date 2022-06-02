package com.example.raftdemo.uttils;

import com.alibaba.fastjson.JSONObject;
import com.example.raftdemo.entity.Entry;
import com.example.raftdemo.entity.Result;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: XDwan
 * @Date:2022/5/31
 * @Description:
 **/
public class RequestUtil {


    public static String sendPost(String url, Map<String, Object> parameters) {
        url = "http://" + url;
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(String.valueOf(parameters.get(name)),
                                    "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(
                            java.net.URLEncoder.encode(String.valueOf(parameters.get(name)),
                                    "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL
                    .openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(httpConn
                    .getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static Result voteRequest(String ip, int term, int id, int lastLogIndex, int lastLogTerm) {
        Map<String, Object> params = new HashMap<>();
        params.put("term", term);
        params.put("candidateId", id);
        params.put("lastLogIndex", lastLogIndex);
        params.put("lastLogTerm", lastLogTerm);
        String res = sendPost(ip, params);
        return JSONObject.parseObject(res, Result.class);
    }

    public static Result appendEntry(String ip, int term, int id, int prevLogIndex, int prevLogTerm,Entry entry, int commitID){
        Map<String, Object> params = new HashMap<>();
        params.put("term", term);
        params.put("leaderID", id);
        params.put("prevLogIndex",prevLogIndex);
        params.put("prevLogTerm",prevLogTerm);
        params.put("newEntry", JSONObject.toJSONString(entry));
        params.put("leaderCommit",commitID);
        String res = sendPost(ip,params);
        return JSONObject.parseObject(res, Result.class);
    }


    public static void main(String[] args) {
        Result res = voteRequest("127.0.0.1:8123/raft/requestVote", 0, 1, 1, 1);
        System.out.println(res);
    }
}
