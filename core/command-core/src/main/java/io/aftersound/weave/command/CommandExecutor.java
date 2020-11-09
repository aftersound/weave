package io.aftersound.weave.command;

import io.aftersound.weave.common.Context;

public interface CommandExecutor {
    void execute(CommandHandle commandHandle, Context context);
}
