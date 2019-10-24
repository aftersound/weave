package io.aftersound.weave.service.batch;

import io.aftersound.weave.service.message.Category;
import io.aftersound.weave.service.message.MessageData;
import io.aftersound.weave.service.message.Param;
import io.aftersound.weave.service.message.Template;

import static io.aftersound.weave.service.message.MessageData.serviceError;

class Messages {

    public static final MessageData EXECUTION_CONTROL_UNEXPECTED = serviceError(
            2000,
            "ServiceMetadata.executionControl is either missing or unexpected"
    );

    public static final MessageData JOB_RUN_LIBRARY_DIRECTORY_UNSPECIFIED = serviceError(
            2001,
            "ServiceMetadata.executionControl.batchLibraryDirectory is either missing or unexpected"
    );

    public static final Template TEMPLATE_JOB_RUN_LIBRARY_DIRECTORY_NOT_EXISTS = new Template(
            2002,
            Category.SERVICE,
            "Batch library directory specified as {directory} does not exist",
            Parameter.Directory
    );

    public static final MessageData JOB_RUN_EXCEPTION_OCCURRED = serviceError(
            2003,
            "Exception occurred when attempting to run job"
    );

    public static final MessageData JOB_RUN_EXTENSION_DIRECTORY_UNSPECIFIED = serviceError(
            2004,
            "ServiceMetadata.executionControl.batchExtensionDirectory is either missing or unexpected"
    );

    public static final Template TEMPLATE_JOB_RUN_EXTENSION_DIRECTORY_NOT_EXISTS = new Template(
            2005,
            Category.SERVICE,
            "Batch extension directory specified as {directory} does not exist",
            Parameter.Directory
    );

    public static final MessageData JOB_RUN_COMMANDS_UNSPECIFIED = serviceError(
            2006,
            "ServiceMetadata.executionControl.commands is either missing or unexpected"
    );

    private enum Parameter implements Param {
        Directory("directory"),
        ;

        private final String name;

        Parameter(final String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

    }
}
