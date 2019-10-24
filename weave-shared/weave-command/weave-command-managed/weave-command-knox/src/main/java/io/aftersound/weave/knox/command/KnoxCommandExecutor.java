package io.aftersound.weave.knox.command;

import io.aftersound.weave.command.CommandExecutor;
import io.aftersound.weave.command.CommandHandle;
import io.aftersound.weave.common.Result;
import org.apache.knox.gateway.shell.KnoxSession;

public class KnoxCommandExecutor implements CommandExecutor {

    private final KnoxSession knoxSession;

    public KnoxCommandExecutor(KnoxSession knoxSession) {
        this.knoxSession = knoxSession;
    }

    @Override
    public Result execute(CommandHandle commandHandle) {
        return Result.failure("Not implemented yet");
    }
}
