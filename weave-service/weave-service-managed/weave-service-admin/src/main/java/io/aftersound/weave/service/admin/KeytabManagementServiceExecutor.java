package io.aftersound.weave.service.admin;

import com.fasterxml.jackson.databind.JsonNode;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.resources.ManagedResources;
import io.aftersound.weave.utils.Base64;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeytabManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = KeytabManagementExecutionControl.TYPE;

    public KeytabManagementServiceExecutor(ManagedResources managedResources) {
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

        KeytabManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();

        String operation = request.firstWithName("operation").singleValue(String.class);
        if ("install".equals(operation)) {
            return install(executionControl, request, context);
        }

        if ("uninstall".equals(operation)) {
            return uninstall(executionControl, request, context);
        }

        return list(executionControl, request, context);

    }

    private boolean validate(ServiceMetadata serviceMetadata, ServiceContext context) {
        if (!(serviceMetadata.getExecutionControl() instanceof KeytabManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        KeytabManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
        if (executionControl.getSecurityDirectory() == null) {
            context.getMessages().addMessage(Messages.SECURITY_DIRECTORY_UNSPECIFIED);
        }

        Path securityDir = PathHandle.of(executionControl.getSecurityDirectory()).path();
        if (!Files.exists(securityDir)) {
            context.getMessages().add(
                    Messages.TEMPLATE_SECURITY_DIRECTORY_NOT_EXISTS.error(executionControl.getSecurityDirectory())
            );
            return false;
        }

        return true;
    }

    private Object install(
            KeytabManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        String securityDirectory = executionControl.getSecurityDirectory();
        NamedContent keytabRequest = request.firstWithName("keytabRequest").singleValue(NamedContent.class);

        synchronized (securityDirectory) {
            Path keytabFilePath = Helper.keytabFilePath(securityDirectory, keytabRequest.getName());
            if (Files.exists(keytabFilePath)) {
                context.getMessages().addMessage(Messages.INSTALL_KEYTAB_ALREADY_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] decoded = Base64.getDecoder().decode(keytabRequest.getEncodedContent());
                Files.write(keytabFilePath, decoded, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                context.getMessages().addMessage(Messages.INSTALL_KEYTAB_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object list(
            KeytabManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        List<String> names = new ArrayList<>();

        String securityDirectory = executionControl.getSecurityDirectory();
        List<String> targetNames = request.firstWithName("name").multiValues(String.class);

        Path securityDir = PathHandle.of(securityDirectory).path();

        try {
            DirectoryStream<Path> keytabFiles = Files.newDirectoryStream(securityDir, "*.keytab");
            for (Path path : keytabFiles) {
                String fileName = path.getFileName().toString();
                String name = Helper.name(fileName);
                if (targetNames.isEmpty() || targetNames.contains(name)) {
                    names.add(name);
                }
            }
            Map<String, List<String>> result = new HashMap<>();
            result.put("keytab-list", names);
            return result;
        } catch (Exception e) {
            context.getMessages().addMessage(Messages.LIST_KEYTAB_EXCEPTION_OCCURRED);
            return Result.FAILURE;
        }
    }

    private Object uninstall(
            KeytabManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        String securityDirectory = executionControl.getSecurityDirectory();
        String name = request.firstWithName("name").singleValue(String.class);
        synchronized (securityDirectory) {
            Path keytabFilePath = Helper.keytabFilePath(securityDirectory, name);
            if (!Files.exists(keytabFilePath)) {
                context.getMessages().addMessage(Messages.UNINSTALL_KEYTAB_NOT_EXISTS);
                return Result.FAILURE;
            }

            try {
                Files.delete(keytabFilePath);
                return Result.SUCCESS;
            } catch (Exception e) {
                context.getMessages().addMessage(Messages.UNINSTALL_KEYTAB_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private static class Helper {

        private static final String EXTENSION = ".keytab";

        static Path keytabFilePath(String securityDirectory, String name) {
            return PathHandle.of(securityDirectory + "/" + keytabFileName(name)).path();
        }

        static String keytabFileName(String name) {
            return name + EXTENSION;
        }

        static String name(String keytabFileName) {
            return keytabFileName.substring(0, keytabFileName.length() - EXTENSION.length());
        }

        static boolean isKeytabFile(String fileName) {
            return fileName.endsWith(EXTENSION);
        }

    }
}
