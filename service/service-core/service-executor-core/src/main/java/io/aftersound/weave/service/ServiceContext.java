package io.aftersound.weave.service;

import io.aftersound.weave.common.Messages;

public class ServiceContext {

    private final Messages messages = new Messages();

    public Messages getMessages() {
        return messages;
    }
}
