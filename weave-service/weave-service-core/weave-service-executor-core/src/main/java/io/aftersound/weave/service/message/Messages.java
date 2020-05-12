package io.aftersound.weave.service.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class Messages {

    private final List<Message> messageList = new ArrayList<>();

    public int size() {
        return messageList.size();
    }

    public void addMessage(Message message) {
        if (message != null) {
            messageList.add(message);
        }
    }

    public void acquire(Messages other) {
        if (other != null) {
            for (Message message : other.messageList) {
                this.addMessage(message);
            }
        }
    }

    public boolean hasAnyMessageWithId(Collection<Long> messageIds) {
        boolean anyMatched = false;
        for (Message message : messageList) {
            if (messageIds.contains(message.getId())) {
                anyMatched = true;
                break;
            }
        }
        return anyMatched;
    }

    public List<Message> getMessageList() {
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

    public Message getMessage(long id) {
        Message target = null;
        for (Message message : messageList) {
            if (message.getId() == id) {
                target = message;
                break;
            }
        }
        return target;
    }

}
