package daniel.zielinski;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import javafx.scene.input.KeyCode;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.almasb.fxgl.dsl.FXGL.onKey;
import static com.almasb.fxgl.dsl.FXGL.spawn;

@Slf4j
public class WebsocketClient extends GameApplication {

    private final GameEntityFactory gameEntityFactory = new GameEntityFactory();
    private Entity player;
    private Entity enemy;
    private static Channel channel;
    private static WebSocketClientHandler handler;

    static final String URL = System.getProperty("url", "ws://127.0.0.1:5544/websocket");

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
        WebsocketUriSchemeParser websocketUriSchemeParser = new WebsocketUriSchemeParser();
        WebsocketUriSchemeConfig config = websocketUriSchemeParser.parse(uri);

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            handler = createWebsocketHandler(uri);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new WebsocketClientInitializer(config, handler));
            channel = bootstrap.connect(uri.getHost(), config.getPort()).sync().channel();
            handler.handshakeFuture().sync();
            launch(args);

        } finally {
            group.shutdownGracefully();
        }
    }

    private static WebSocketClientHandler createWebsocketHandler(URI uri) {
        return new WebSocketClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Basic Game App");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(gameEntityFactory);
        this.player = spawn("player", getAppWidth() / 2 - 15, getAppHeight() / 2 - 15);
        this.enemy = spawn("enemy", getAppWidth() / 2 - 15, getAppHeight() / 2 - 15);
        handler.setPlayer(player);
        handler.setEnemy(enemy);
    }

    protected void initInput() {
        onKey(KeyCode.LEFT, () -> channel.writeAndFlush(new TextWebSocketFrame("moving left")));
        onKey(KeyCode.RIGHT, () -> channel.writeAndFlush(new TextWebSocketFrame("moving right")));
        onKey(KeyCode.UP, () -> channel.writeAndFlush(new TextWebSocketFrame("moving up")));
        onKey(KeyCode.DOWN, () -> channel.writeAndFlush(new TextWebSocketFrame("moving down")));
    }


}