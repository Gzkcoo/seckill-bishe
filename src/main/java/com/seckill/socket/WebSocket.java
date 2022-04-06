package com.seckill.socket;

import lombok.EqualsAndHashCode;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/webSocket/{token}")
@EqualsAndHashCode
public class WebSocket {
    private static CopyOnWriteArraySet<WebSocket> webSocketSet = new CopyOnWriteArraySet();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String token;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(@PathParam("token") String token,Session session) {
        this.session = session;
        webSocketSet.add(this);
        this.token = token;
        System.out.println("新连接("+ token + ")加入！当前在线人数为" + webSocketSet.size());
        sendMessage("Hello");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        System.out.println("有一连接关闭！当前在线人数为" + webSocketSet.size());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);

        //群发消息
        for (WebSocket item : webSocketSet) {
            item.sendMessage(message);
        }
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
        // this.session.getBasicRemote().sendText(message);
    }

    public CopyOnWriteArraySet<WebSocket> getWebSocketSet() {
        return webSocketSet;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}