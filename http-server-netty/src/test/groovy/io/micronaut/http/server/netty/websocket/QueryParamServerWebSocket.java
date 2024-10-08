package io.micronaut.http.server.netty.websocket;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnClose;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;

@Requires(property = "spec.name", value = "SimpleTextWebSocketSpec")
@ServerWebSocket("/charity")
public class QueryParamServerWebSocket {

    private WebSocketBroadcaster broadcaster;
    private String dinner;
    private WebSocketSession session;

    public QueryParamServerWebSocket(WebSocketBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @OnOpen
    public void onOpen(@QueryValue String dinner, WebSocketSession session) {
        this.dinner = dinner;
        this.session = session;
    }

    @OnMessage
    public void onMessage(
            String dinner,
            WebSocketSession session) {
    }

    @OnClose
    public void onClose() {

    }


    public String getDinner() {
        return dinner;
    }

    public WebSocketSession getSession() {
        return session;
    }
}
