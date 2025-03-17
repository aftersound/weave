package io.aftersound.service.response;

import io.aftersound.msg.Message;

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
