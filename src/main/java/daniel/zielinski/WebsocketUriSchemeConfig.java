package daniel.zielinski;

import io.netty.handler.ssl.SslContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Data
public class WebsocketUriSchemeConfig {

    private final String host;
    private final int port;
    private final SslContext sslCtx;

}