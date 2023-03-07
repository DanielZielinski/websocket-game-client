package daniel.zielinski;

import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.concurrent.EventExecutorGroup;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    public WebSocketClientHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client channelActive!");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("WebSocket Client disconnected!");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            String message = ((TextWebSocketFrame) frame).text();
            System.out.println("Received message: " + message);
        } else if (frame instanceof PongWebSocketFrame) {
            System.out.println("Received pong");
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("WebSocket Client received closing");
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}