package io.aftersound.weave.service;

import io.aftersound.weave.service.message.Messages;

public class ServiceContext {

    private final Messages messages = new Messages();

    public Messages getMessages() {
        return messages;
    }
}
