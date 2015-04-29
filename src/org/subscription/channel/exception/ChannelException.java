package org.subscription.channel.exception;

public class ChannelException extends RuntimeException{
    public ChannelException() {
    }

    public ChannelException(String message) {
        super(message);
    }

    public ChannelException(String message, Throwable t) {
        super(message, t);
    }

    public ChannelException(Throwable t) {
        super(t);
    }

}