package org.subscription.channel.abstraction;

import org.subscription.channel.exception.ChannelIOException;

import java.io.Serializable;


public interface IChannel {

    public void publish(Serializable data) throws ChannelIOException;

    public boolean isOpen();

    public abstract void open() throws ChannelIOException;

    public abstract void close() throws ChannelIOException;

    public abstract int getSubscriberCount();

}
