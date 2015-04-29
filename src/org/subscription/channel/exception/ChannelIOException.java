package org.subscription.channel.exception;

public class ChannelIOException extends Exception{
    public ChannelIOException() {
    }

    public ChannelIOException(String message) {
        super(message);
    }

    public ChannelIOException(String message, Throwable t) {
        super(message, t);
    }

    public ChannelIOException(Throwable t) {
        super(t);
    }

}