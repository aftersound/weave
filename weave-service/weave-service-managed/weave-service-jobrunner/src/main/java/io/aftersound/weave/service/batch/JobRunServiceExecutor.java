package io.aftersound.weave.service.batch;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.resources.ManagedResources;
import io.aftersound.weave.utils.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JobRunServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = JobRunServiceExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunServiceExecutor.class);

    public JobRunServiceExecutor(ManagedResources managedResources) {
        super(managedResources);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        if (!validate(serviceMetadata, context)) {
            return null;
        }

        JobRunServiceExecutionControl executionControl = serviceMetadata.getExecutionControl();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(getCommands(executionControl, request));
            final Process process = processBuilder.start();

            Thread commandLineThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        logStream(process.getErrorStream());
                        logStream(process.getInputStream());
                    } catch (IOException ex) {
                        LOGGER.error("{}: exception occurred", ex);
                    }
                }

            });

            commandLineThread.setDaemon(true);
            commandLineThread.start();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("status", "Submitted");
            return result;
        } catch (Exception e) {
            context.getMessages().addMessage(Messages.JOB_RUN_EXCEPTION_OCCURRED);
            return null;
        }
    }

    private boolean validate(ServiceMetadata serviceMetadata, ServiceContext context) {
        if (!(serviceMetadata.getExecutionControl() instanceof JobRunServiceExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        JobRunServiceExecutionControl executionControl = serviceMetadata.getExecutionControl();

        if (executionControl.getBatchLibraryDirectory() == null) {
            context.getMessages().addMessage(Messages.JOB_RUN_LIBRARY_DIRECTORY_UNSPECIFIED);
            return false;
        }

        Path libraryDir = PathHandle.of(executionControl.getBatchLibraryDirectory()).path();
        if (!Files.exists(libraryDir)) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_JOB_RUN_LIBRARY_DIRECTORY_NOT_EXISTS.error(executionControl.getBatchLibraryDirectory())
            );
            return false;
        }

        if (executionControl.getBatchExtensionDirectory() == null) {
            context.getMessages().addMessage(Messages.JOB_RUN_EXTENSION_DIRECTORY_UNSPECIFIED);
            return false;
        }

        Path extensionDir = PathHandle.of(executionControl.getBatchExtensionDirectory()).path();
        if (!Files.exists(extensionDir)) {
            context.getMessages().addMessage(
                    Messages.TEMPLATE_JOB_RUN_EXTENSION_DIRECTORY_NOT_EXISTS.error(executionControl.getBatchLibraryDirectory())
            );
            return false;
        }

        if (executionControl.getCommands() == null || executionControl.getCommands().isEmpty()) {
            context.getMessages().addMessage(Messages.JOB_RUN_COMMANDS_UNSPECIFIED);
            return false;
        }

        return true;
    }

    private String[] getCommands(
            JobRunServiceExecutionControl executionControl,
            ParamValueHolders request) throws Exception {

        Map<String, String> context = createContext(executionControl, request);
        List<String> commands = executionControl.getCommands();
        List<String> normalizedCommands = new ArrayList<>(commands.size());
        for (String command : executionControl.getCommands()) {
            String normalized = StringHandle.of(command, context).value();
            if (!normalized.isEmpty()) {
                normalizedCommands.add(normalized);
            }
        }
        return normalizedCommands.toArray(new String[normalizedCommands.size()]);
    }

    private Map<String, String> createContext(
            JobRunServiceExecutionControl executionControl,
            ParamValueHolders request) throws Exception {
        Map<String, String> context = new HashMap<>();
        context.put("WEAVE_HOME", System.getProperty("WEAVE_HOME"));
        context.put("CLASS_PATH", getClassPath(executionControl));
        context.put("appConfig", request.firstWithName("appConfig").singleValue(String.class));
        context.put("jobSpec", request.firstWithName("jobSpec").singleValue(String.class));
        return context;
    }

    private String getClassPath(JobRunServiceExecutionControl executionControl) throws Exception {
        List<String> jarPaths = new ArrayList<>();

        // batch core libraries
        Path dir = PathHandle.of(executionControl.getBatchLibraryDirectory()).path();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jar")) {
            for (Path path : stream) {
                jarPaths.add(path.toString());
            }
        }

        // batch extension libraries
        dir = PathHandle.of(executionControl.getBatchExtensionDirectory()).path();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.jar")) {
            for (Path path : stream) {
                jarPaths.add(path.toString());
            }
        }

        String os = System.getProperty("os.name").toLowerCase();
        boolean isWindows = os.contains("win");
        char separator = isWindows ? ';' : ':';

        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (String jarPath : jarPaths) {
            if (!isFirst) {
                sb.append(separator);
            } else {
                isFirst = false;
            }
            sb.append(jarPath);
        }
        return sb.toString();
    }

    private void logStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        LOGGER.info(result.toString("UTF-8"));
    }

}
