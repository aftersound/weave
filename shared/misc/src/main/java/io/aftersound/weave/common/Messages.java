package io.aftersound.weave.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Messages {

    private final List<Message> messageList = new ArrayList<>();

    private int status;

    public int status() {
        return status;
    }

    public Messages status(int status) {
        this.status = status;
        return this;
    }

    public void addMessage(Message message) {
        if (message != null) {
            messageList.add(message);
        }
    }

    public void addMessages(Collection<Message> messages) {
        if (messages != null) {
            messageList.addAll(messages);
        }
    }

    public void acquire(Messages other) {
        if (other != null) {
            for (Message message : other.messageList) {
                this.addMessage(message);
            }
        }
    }

    public boolean hasAnyMessageWithCodes(Collection<String> codes) {
        boolean anyMatched = false;
        for (Message message : messageList) {
            if (codes.contains(message.getCode())) {
                anyMatched = true;
                break;
            }
        }
        return anyMatched;
    }

    public int size() {
        return messageList.size();
    }

    public List<Message> list() {
        return Collections.unmodifiableList(messageList);
    }

    public Messages getMessagesWithSeverity(Severity severity) {
        Messages messagesWithSpecifiedSeverity = new Messages();
        for (Message message : messageList) {
            if (message.getSeverity() == severity) {
                messagesWithSpecifiedSeverity.addMessage(message);
            }
        }
        return messagesWithSpecifiedSeverity;
    }

    public Message getMessage(String code) {
        Message target = null;
        for (Message message : messageList) {
            if (code.equals(message.getCode())) {
                target = message;
                break;
            }
        }
        return target;
    }

}
