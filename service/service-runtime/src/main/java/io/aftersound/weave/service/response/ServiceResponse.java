package io.aftersound.weave.service.response;

import io.aftersound.weave.common.Message;

import java.util.List;

public class ServiceResponse {

    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

}
