package org.subscription;

import org.subscription.channel.abstraction.IChannel;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.subscription.channel.exception.ChannelIOException;

public class SubscriptionManager {

    private List<IChannel> channels;

    private static SubscriptionManager subscriptionManager;

    public static SubscriptionManager getSubscriptionManager(){
        if(subscriptionManager == null){
            subscriptionManager = new SubscriptionManager();
        }
        return subscriptionManager;
    }

    public boolean addChannels(IChannel... channels){
        boolean anyError = false;
        if(channels != null){
            for(IChannel channel : channels){
                if(!addChannel(channel)){
                    anyError = true;
                }
            }
        }
        return !anyError;
    }

    public boolean addChannel(IChannel channel){
        if(channels == null){
            channels = new LinkedList<IChannel>();
        }
        return channels.add(channel);
    }

    public boolean removeChannel(IChannel channel){
        if(channels == null){
            return false;
        }else{
            return channels.remove(channel);
        }
    }

    public List<IChannel> getChannels(){
        return this.channels;
    }

    public void publish(Serializable data) throws ChannelIOException {
        publish(this.channels, data);
    }

    public void publish(IChannel channel, Serializable data) throws ChannelIOException {
        channel.publish(data);
    }

    public void publish(List<IChannel> channels, Serializable data) throws ChannelIOException {
        for(IChannel channel : channels){
            publish(channel, data);
        }
    }

}
