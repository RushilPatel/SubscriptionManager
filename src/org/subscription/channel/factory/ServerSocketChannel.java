package org.subscription.channel.factory;

import org.subscription.channel.abstraction.IChannel;
import org.subscription.channel.exception.ChannelException;
import org.subscription.channel.exception.ChannelIOException;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ServerSocketChannel implements IChannel {

    private ServerSocket serverSocket;
    private int port;
    private List<Socket> subscribers;

    public ServerSocketChannel(int port) {
        this.port = port;
    }

    public int getPort(){
        return this.port;
    }

    @Override
    public void publish(Serializable data) throws ChannelIOException {

        if(isOpen()){
            if(this.subscribers != null){
                boolean anyError = false;
                for(Socket socket : this.subscribers){
                    if(socket != null && socket.isConnected() && !socket.isClosed()){
                        try{
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(data);
                            oos.flush();
                        }catch (IOException e){
                            try{
                                socket.close();
                            }catch (IOException ex){
                                socket = null;
                            }finally {
                                anyError = true;
                                this.subscribers.remove(socket);
                            }
                        }
                    }else{
                        this.subscribers.remove(socket);
                    }
                }
                if(anyError){
                    throw new ChannelIOException("Could not publish to all subscribers. Erroneous subscribers have been disconnected");
                }
            }
        }else{
            throw new ChannelException("Channel is not open");
        }
    }

    @Override
    public int getSubscriberCount() {
        if(this.subscribers != null){
            return this.subscribers.size();
        }else{
            return 0;
        }
    }

    @Override
    public boolean isOpen(){
        if(serverSocket != null){
            return !serverSocket.isClosed();
        }else{
            return false;
        }
    }

    @Override
    public void open() throws ChannelIOException {
        try{
            if(isOpen()){
                throw new ChannelException("Channel is already open");
            }else{
                this.serverSocket = new ServerSocket(getPort());
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        while (isOpen()){
                            Socket socket = serverSocket.accept();
                            if(subscribers == null){
                                subscribers = new ArrayList<Socket>();
                            }
                            subscribers.add(socket);
                        }
                    }catch (SocketException e){
                        System.out.println(e.getMessage());
                    }catch (IOException e){
                        throw new ChannelException(e);
                    }
                }
            }).start();


        }catch (IOException e){
            throw new ChannelIOException(e);
        }
    }

    public void flushSubscribers() {
        if(this.subscribers != null){
            for(Socket subscriber : this.subscribers){
                try{
                    if(subscriber != null && subscriber.isConnected() && !subscriber.isClosed()){
                        subscriber.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            this.subscribers.clear();
            this.subscribers = null;
        }
    }

    @Override
    public void close() throws ChannelIOException{
        try{
            if(isOpen()){
                flushSubscribers();
                serverSocket.close();
            }else{
                throw new ChannelException("Channel is already closed");
            }
        }catch (IOException e){
            throw new ChannelIOException(e);
        }finally {
            serverSocket = null;
        }
    }
}
