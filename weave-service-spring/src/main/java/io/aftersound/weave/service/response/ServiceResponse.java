package io.aftersound.weave.service.response;

import io.aftersound.weave.service.message.MessageData;

import java.util.List;

public class ServiceResponse {

    private List<MessageData> messages;

    public List<MessageData> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageData> messages) {
        this.messages = messages;
    }

}
