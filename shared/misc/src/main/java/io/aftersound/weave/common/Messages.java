package io.aftersound.weave.common;

import java.util.*;

public class Messages {

    private final List<Message> messageList = new ArrayList<>();

    public Messages addMessage(Message message) {
        if (message != null) {
            messageList.add(message);
        }
        return this;
    }

    public Messages addMessages(Collection<Message> messages) {
        if (messages != null) {
            messageList.addAll(messages);
        }
        return this;
    }

    public Messages addMessages(Message... messages) {
        if (messages != null) {
            return addMessages(Arrays.asList(messages));
        }
        return this;
    }

    public Messages acquire(Messages other) {
        if (other != null) {
            this.messageList.addAll(other.messageList);
        }
        return this;
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
