package daniel.zielinski;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class WebsocketClientInitializer extends ChannelInitializer<SocketChannel>{

    private final WebsocketUriSchemeConfig config;
    private final WebSocketClientHandler handler;

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if (config.getSslCtx() != null) {
            pipeline.addLast(config.getSslCtx().newHandler(ch.alloc(), config.getHost(), config.getPort()));
        }
        pipeline.addLast(new HttpClientCodec(), new HttpObjectAggregator(64*1024), WebSocketClientCompressionHandler.INSTANCE, handler);
    }
}