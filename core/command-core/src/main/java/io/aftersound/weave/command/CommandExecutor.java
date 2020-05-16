package io.aftersound.weave.command;

import io.aftersound.weave.common.Result;

public interface CommandExecutor {
    Result execute(CommandHandle commandHandle);
}
