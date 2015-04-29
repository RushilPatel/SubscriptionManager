package org.subscription.channel;

import org.subscription.channel.abstraction.IChannel;
import org.subscription.channel.factory.ServerSocketChannel;
import org.subscription.channel.factory.WebSocketChannel;

public class ChannelFactory {

    public static final int SERVER_SOCKET_PORT_DEFAULT = 7777;

    public static final int WEB_SOCKET_PORT_DEFAULT = 8888;

    public IChannel buildServerSocketChannel(int port){
        return new ServerSocketChannel(port);
    }

    public IChannel buildServerSocketChannel() {
        return buildServerSocketChannel(SERVER_SOCKET_PORT_DEFAULT);
    }

    public IChannel buildWebSocketChannel(int port){
        return new WebSocketChannel(port);
    }

    public IChannel buildWebSocketChannel() {
        return buildWebSocketChannel(WEB_SOCKET_PORT_DEFAULT);
    }

}
