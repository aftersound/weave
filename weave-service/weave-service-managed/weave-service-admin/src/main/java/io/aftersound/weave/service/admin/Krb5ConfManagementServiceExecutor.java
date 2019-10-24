package io.aftersound.weave.service.admin;

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

public class Krb5ConfManagementServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = Krb5ConfManagementExecutionControl.TYPE;

    public Krb5ConfManagementServiceExecutor(ManagedResources managedResources) {
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

        Krb5ConfManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();

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
        if (!(serviceMetadata.getExecutionControl() instanceof Krb5ConfManagementExecutionControl)) {
            context.getMessages().addMessage(Messages.EXECUTION_CONTROL_UNEXPECTED);
            return false;
        }

        Krb5ConfManagementExecutionControl executionControl = serviceMetadata.getExecutionControl();
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
            Krb5ConfManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        String securityDirectory = executionControl.getSecurityDirectory();
        NamedContent krb5ConfRequest = request.firstWithName("krb5ConfRequest").singleValue(NamedContent.class);

        synchronized (securityDirectory) {
            Path krb5ConfFilePath = Helper.krb5ConfFilePath(securityDirectory, krb5ConfRequest.getName());
            if (Files.exists(krb5ConfFilePath)) {
                context.getMessages().addMessage(Messages.INSTALL_KRB5_CONF_ALREADY_EXISTS);
                return Result.FAILURE;
            }

            try {
                byte[] decoded = Base64.getDecoder().decode(krb5ConfRequest.getEncodedContent());
                Files.write(krb5ConfFilePath, decoded, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                return Result.SUCCESS;
            } catch (Exception e) {
                context.getMessages().addMessage(Messages.INSTALL_KRB5_CONF_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private Object list(
            Krb5ConfManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        List<String> names = new ArrayList<>();

        String securityDirectory = executionControl.getSecurityDirectory();
        List<String> targetNames = request.firstWithName("name").multiValues(String.class);

        Path securityDir = PathHandle.of(securityDirectory).path();

        try {
            DirectoryStream<Path> filePaths = Files.newDirectoryStream(securityDir, "*-krb5.conf");
            for (Path path : filePaths) {
                String fileName = path.getFileName().toString();
                String name = Helper.name(fileName);
                if (targetNames.isEmpty() || targetNames.contains(name)) {
                    names.add(name);
                }
            }

            Map<String, List<String>> result = new HashMap<>();
            result.put("krb5-conf-list", names);
            return result;
        } catch (Exception e) {
            context.getMessages().addMessage(Messages.LIST_KRB5_CONF_EXCEPTION_OCCURRED);
            return Result.FAILURE;
        }
    }

    private Object uninstall(
            Krb5ConfManagementExecutionControl executionControl,
            ParamValueHolders request,
            ServiceContext context) {
        String securityDirectory = executionControl.getSecurityDirectory();
        String name = request.firstWithName("name").singleValue(String.class);
        synchronized (securityDirectory) {
            Path filePath = Helper.krb5ConfFilePath(securityDirectory, name);
            if (!Files.exists(filePath)) {
                context.getMessages().addMessage(Messages.UNINSTALL_KRB5_CONF_NOT_EXISTS);
                return Result.FAILURE;
            }

            try {
                Files.delete(filePath);
                return Result.SUCCESS;
            } catch (Exception e) {
                context.getMessages().addMessage(Messages.UNINSTALL_KRB5_CONF_EXCEPTION_OCCURRED);
                return Result.FAILURE;
            }
        }
    }

    private static class Helper {

        private static final String SUFFIX = "-krb5";
        private static final String EXTENSION = ".conf";

        static Path krb5ConfFilePath(String securityDirectory, String name) {
            return PathHandle.of(securityDirectory + "/" + krb5ConfFileName(name)).path();
        }

        static String krb5ConfFileName(String name) {
            return name + SUFFIX + EXTENSION;
        }

        static String name(String krb5ConfFileName) {
            return krb5ConfFileName.substring(0, krb5ConfFileName.length() - SUFFIX.length() - EXTENSION.length());
        }

        static boolean isKrb5ConfFile(String fileName) {
            return fileName.endsWith(SUFFIX + EXTENSION);
        }

    }

}
