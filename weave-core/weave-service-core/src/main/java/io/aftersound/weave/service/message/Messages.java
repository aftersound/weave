package io.aftersound.weave.service.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Messages extends ArrayList<MessageData> {

    public void addMessage(MessageData message) {
        if (message != null) {
            this.add(message);
        }
    }

    public void acquire(Messages other) {
        if (other != null) {
            for (MessageData message : other) {
                this.addMessage(message);
            }
        }
    }

    public boolean hasAnyMessageWithId(Collection<Long> messageIds) {
        boolean anyMatched = false;
        for (MessageData messageData : this) {
            if (messageIds.contains(messageData.getId())) {
                anyMatched = true;
                break;
            }
        }
        return anyMatched;
    }

    public List<MessageData> getMessageList() {
        return Collections.unmodifiableList(this);
    }

    public Messages getMessagesWithSeverity(Severity severity) {
        Messages messagesWithSpecifiedSeverity = new Messages();
        for (MessageData message : this) {
            if (message.getSeverity() == severity) {
                messagesWithSpecifiedSeverity.addMessage(message);
            }
        }
        return messagesWithSpecifiedSeverity;
    }

    public MessageData getMessage(long id) {
        MessageData target = null;
        for (MessageData message : this) {
            if (message.getId() == id) {
                target = message;
                break;
            }
        }
        return target;
    }

}
