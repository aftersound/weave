package io.aftersound.weave.service;


import io.aftersound.msg.Message;
import io.aftersound.msg.Messages;

import java.util.Collection;

public class ServiceContext {

    private final Messages messages = new Messages();

    private int responseStatus;

    public ServiceContext responseStatus(int statusCode) {
        this.responseStatus = statusCode;
        return this;
    }

    public int responseStatus() {
        return responseStatus;
    }

    public ServiceContext addMessage(Message message) {
        this.messages.addMessage(message);
        return this;
    }

    public ServiceContext addMessages(Message... messages) {
        this.messages.addMessages(messages);
        return this;
    }

    public ServiceContext addMessages(Collection<Message> messages) {
        this.messages.addMessages(messages);
        return this;
    }

    public Messages getMessages() {
        return messages;
    }
}
