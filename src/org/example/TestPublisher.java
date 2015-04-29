package org.example;

import org.subscription.SubscriptionManager;
import org.subscription.channel.ChannelFactory;
import org.subscription.channel.abstraction.IChannel;
import org.subscription.channel.exception.ChannelIOException;

import java.util.Random;


public class TestPublisher {

    /**
     * A publisher should use Subscription Manager to publish data to all channel
     * Subscription Manager should keep track of all available channels
     * Channel should publish to its subscribers
     *
     * Channels could be added/removed to/from Subscription manager as needed
     * A class/thread that is publishing data should simply publish using Subscription manger, and should assume
     * that some other class is managing/validating available channels in the Subscription manager
     *
     * Below is a quick example that demonstrates all responsibilities mentioned above
     */
    public static void main(String [] args){


        ChannelFactory factory = new ChannelFactory();

        //websocket subscribers can subscribe on port 8888
        IChannel websocketChannel = factory.buildWebSocketChannel(8888);

        //socket subscribers can subscribe on port 7777
        IChannel serverSocketChannel = factory.buildServerSocketChannel(7777);

        try {
            websocketChannel.open();
            serverSocketChannel.open();
        }catch (ChannelIOException e){
            e.printStackTrace();
        }

        //add channels to subscription manager
        final SubscriptionManager manager = SubscriptionManager.getSubscriptionManager(); //singleton
        manager.addChannels(websocketChannel, serverSocketChannel);


        //publishing thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Random random = new Random();

                for(int i = 0; i < 10; i++){
                    try{
                        manager.publish(random);
                    }catch (ChannelIOException e){
                        e.printStackTrace();
                    }
                }
            }
        });


        //remove channels from subscription manager
        manager.removeChannel(websocketChannel);
        manager.removeChannel(serverSocketChannel);

        try{
            websocketChannel.close();
            serverSocketChannel.close();
        }catch (ChannelIOException e){
            e.printStackTrace();
        }

    }
}
