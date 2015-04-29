package org.subscription.channel.factory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.subscription.channel.abstraction.IChannel;
import org.subscription.channel.exception.ChannelException;
import org.subscription.channel.exception.ChannelIOException;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

public class WebSocketChannel implements IChannel{

    private class WebSocketChannelServer extends WebSocketServer{

        private List<WebSocket> subscribers;

        public WebSocketChannelServer(int port){
            super(new InetSocketAddress(port));
            subscribers = new LinkedList<WebSocket>();

        }

        @Override
        public void onOpen(WebSocket subscriber, ClientHandshake handshake)
        {
            if(this.subscribers == null){
                subscribers = new LinkedList<WebSocket>();
            }
            subscribers.add(subscriber);
        }

        @Override
        public void onClose(WebSocket subscriber, int code, String reason, boolean remote)
        {
            subscribers.remove(subscriber);
        }

        @Override
        public void onMessage(WebSocket subscriber, String message)
        {
            System.out.println("Received message from subscriber: " + message);
        }

        public List<WebSocket> getSubscribers(){
            return this.subscribers;
        }

        @Override
        public void onError(WebSocket subscriber, Exception ex)
        {
            onClose(subscriber, 0, "Error", true);
        }

        public void disconnectSubscriber(WebSocket subscriber){
            if(subscriber != null && subscriber.isOpen() && !subscriber.isClosed()){
                subscriber.close();
                subscribers.remove(subscriber);
            }
        }

    }


    private WebSocketChannelServer server;
    private int port;

    public WebSocketChannel(int port){
        this.port = port;
    }

    @Override
    public void publish(Serializable data) throws ChannelIOException {
        if(isOpen()){
            if(this.server.getSubscribers() != null){
                for(WebSocket socket : this.server.getSubscribers()){
                    if(socket != null && socket.isOpen() && !socket.isClosed()){
                        socket.send(data.toString());
                    }else{
                        this.server.disconnectSubscriber(socket);
                    }
                }
            }
        }else{
            throw new ChannelException("Channel is closed");
        }
    }

    public int getPort(){
        return this.port;
    }

    @Override
    public boolean isOpen() {
        return this.server != null;
    }

    @Override
    public void open() throws ChannelIOException {
        if(isOpen()){
            throw new ChannelException("Channel is already open");
        }else{
            this.server = new WebSocketChannelServer(getPort());
            this.server.start();
        }
    }

    public void flushSubscribers(){
        if(server.getSubscribers() != null){
            for(WebSocket subscriber : server.getSubscribers()){
                server.disconnectSubscriber(subscriber);
            }
        }
    }

    @Override
    public void close() throws ChannelIOException {
        try{
            if(isOpen()){
                flushSubscribers();
                server.stop();
            }else{
                throw new ChannelException("Channel is already closed");
            }
        }catch (IOException e){
            throw  new ChannelIOException(e);
        }catch (InterruptedException e){
            throw  new ChannelIOException(e);
        }finally {
            server = null;
        }

    }

    @Override
    public int getSubscriberCount() {
        if(server != null && this.server.getSubscribers() != null){
            return this.server.getSubscribers().size();
        }else{
            return 0;
        }
    }
}
