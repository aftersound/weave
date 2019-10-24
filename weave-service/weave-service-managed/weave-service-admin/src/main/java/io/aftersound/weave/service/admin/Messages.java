package io.aftersound.weave.service.admin;

import io.aftersound.weave.service.message.Category;
import io.aftersound.weave.service.message.MessageData;
import io.aftersound.weave.service.message.Param;
import io.aftersound.weave.service.message.Template;

import static io.aftersound.weave.service.message.MessageData.serviceError;

class Messages {

    public static final MessageData EXECUTION_CONTROL_UNEXPECTED = serviceError(
            201,
            "ServiceMetadata.executionControl is either missing or unexpected"
    );

    public static final Template TEMPLATE_SERVICE_METADATA_INFO_UNSPECIFIED = new Template(
            202,
            Category.SERVICE,
            "{info} is required but unspecified or empty",
            Parameter.Info
    );

    public static final Template TEMPLATE_DIRECTORY_NOT_EXISTS = new Template(
            203,
            Category.SERVICE,
            "{type} directory specified as {directory} does not exist",
            Parameter.Type,
            Parameter.Directory
    );

    // START: extension management related
    //        210-219 reserved

    public static final Template TEMPLATE_LIBRARY_REGISTRY_NOT_INITIALIZED = new Template(
            213,
            Category.SERVICE,
            "Library Registry for {scope} is not initialized",
            Parameter.ExtensionScope
    );

    public static final Template TEMPLATE_LIBRARY_ALREADY_INSTALLED = new Template(
            214,
            Category.SERVICE,
            "{library} is already installed",
            Parameter.Library
    );

    public static final Template TEMPLATE_LIBRARY_NO_EXTENSION = new Template(
            215,
            Category.SERVICE,
            "{library} does not have any meta-info of expected extension categories",
            Parameter.Library
    );

    public static final MessageData INSTALL_LIBRARY_EXCEPTION_OCCURRED = serviceError(
            216,
            "Exception occurred when attempting to install library"
    );

    public static final MessageData UNINSTALL_LIBRARY_NOT_FOUND = serviceError(
            217,
            "No library found with specified information"
    );

    public static final MessageData DELETE_LIBRARY_EXCEPTION_OCCURRED = serviceError(
            218,
            "Exception occurred when attempting to uninstall library"
    );

    // END: extension management related

    // START: keytab management related
    //        220-229 reserved

    public static final MessageData SECURITY_DIRECTORY_UNSPECIFIED = serviceError(
            220,
            "ServiceMetadata.executionControl.securityDirectory is missing"
    );

    public static final Template TEMPLATE_SECURITY_DIRECTORY_NOT_EXISTS = new Template(
            221,
            Category.SERVICE,
            "Security directory specified as {security_directory} does not exist",
            Parameter.SecurityDirectory
    );

    public static final MessageData INSTALL_KEYTAB_ALREADY_EXISTS = serviceError(
            222,
            "Keytab with specified name already exists"
    );

    public static final MessageData INSTALL_KEYTAB_EXCEPTION_OCCURRED = serviceError(
            223,
            "Exception occurred when attempting to install Keytab"
    );

    public static final MessageData LIST_KEYTAB_EXCEPTION_OCCURRED = serviceError(
            224,
            "Exception occurred when attempting to list Keytab"
    );

    public static final MessageData UNINSTALL_KEYTAB_NOT_EXISTS = serviceError(
            225,
            "Keytab with specified name does not exist"
    );

    public static final MessageData UNINSTALL_KEYTAB_EXCEPTION_OCCURRED = serviceError(
            226,
            "Exception occurred when attempting to uninstall Keytab"
    );

    // END: keytab management related

    // START: krb5 conf management related
    //        230-239 reserved

    public static final MessageData INSTALL_KRB5_CONF_ALREADY_EXISTS = serviceError(
            230,
            "Krb5 conf with specified name already exists"
    );

    public static final MessageData INSTALL_KRB5_CONF_EXCEPTION_OCCURRED = serviceError(
            231,
            "Exception occurred when attempting to install krb5 conf"
    );

    public static final MessageData LIST_KRB5_CONF_EXCEPTION_OCCURRED = serviceError(
            232,
            "Exception occurred when attempting to list krb5 conf"
    );

    public static final MessageData UNINSTALL_KRB5_CONF_NOT_EXISTS = serviceError(
            233,
            "Krb5 conf with specified name does not exist"
    );

    public static final MessageData UNINSTALL_KRB5_CONF_EXCEPTION_OCCURRED = serviceError(
            234,
            "Exception occurred when attempting to uninstall krb5 conf"
    );

    // END: data client config management related

    // START: data client config management related
    //        240-259 reserved

    public static final MessageData CREATE_DATA_CLIENT_CONFIG_TYPE_UNSPECIFIED = serviceError(
            240,
            "Endpoint.type is not specified"
    );

    public static final MessageData CREATE_DATA_CLIENT_CONFIG_ID_UNSPECIFIED = serviceError(
            241,
            "Endpoint.id is not specified"
    );

    public static final MessageData CREATE_DATA_CLIENT_CONFIG_OPTIONS_UNSPECIFIED = serviceError(
            242,
            "Endpoint.options is not specified"
    );

    public static final MessageData CREATE_DATA_CLIENT_CONFIG_ALREADY_EXISTS = serviceError(
            243,
            "Data client config with specified name already exists"
    );

    public static final MessageData CREATE_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED = serviceError(
            244,
            "Exception occurred when attempting to create data client config"
    );

    public static final MessageData LIST_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED = serviceError(
            245,
            "Exception occurred when attempting to list data client config"
    );

    public static final MessageData UPDATE_DATA_CLIENT_CONFIG_TYPE_UNSPECIFIED = serviceError(
            246,
            "Endpoint.type is not specified"
    );

    public static final MessageData UPDATE_DATA_CLIENT_CONFIG_ID_UNSPECIFIED = serviceError(
            247,
            "Endpoint.id is not specified"
    );

    public static final MessageData UPDATE_DATA_CLIENT_CONFIG_OPTIONS_UNSPECIFIED = serviceError(
            248,
            "Endpoint.options is not specified"
    );

    public static final MessageData UPDATE_DATA_CLIENT_CONFIG_NOT_EXISTS = serviceError(
            249,
            "Data client config with specified name does not exist"
    );

    public static final MessageData UPDATE_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED = serviceError(
            250,
            "Exception occurred when attempting to update data client config"
    );

    public static final MessageData DELETE_DATA_CLIENT_CONFIG_NOT_EXISTS = serviceError(
            251,
            "Data client config with specified name does not exist"
    );

    public static final MessageData DELETE_DATA_CLIENT_CONFIG_EXCEPTION_OCCURRED = serviceError(
            252,
            "Exception occurred when attempting to delete data client config"
    );

    // END: data client config management related

    // START: service metadata management related
    //        260-279 reserved

    public static final MessageData CREATE_SERVICE_METADATA_ID_UNSPECIFIED = serviceError(
            260,
            "ServiceMetadata.id is not specified"
    );

    public static final MessageData CREATE_SERVICE_METADATA_EXECUTION_CONTROL_UNSPECIFIED = serviceError(
            261,
            "ServiceMetadata.executionControl is not specified"
    );

    public static final MessageData CREATE_SERVICE_METADATA_EXECUTION_CONTROL_TYPE_UNSPECIFIED = serviceError(
            262,
            "ServiceMetadata.executionControl.type is not specified"
    );

    public static final MessageData CREATE_SERVICE_METADATA_PARAM_FIELDS_UNSPECIFIED = serviceError(
            263,
            "ServiceMetadata.paramFields is not specified"
    );

    public static final MessageData CREATE_SERVICE_METADATA_ALREADY_EXISTS = serviceError(
            264,
            "Service metadata with specified id already exists"
    );

    public static final MessageData CREATE_SERVICE_METADATA_EXCEPTION_OCCURRED = serviceError(
            265,
            "Exception occurred when attempting to create service metadata"
    );

    public static final MessageData LIST_SERVICE_METADATA_EXCEPTION_OCCURRED = serviceError(
            266,
            "Exception occurred when attempting to list service metadata"
    );

    public static final MessageData UPDATE_SERVICE_METADATA_ID_UNSPECIFIED = serviceError(
            267,
            "ServiceMetadata.id is not specified"
    );

    public static final MessageData UPDATE_SERVICE_METADATA_EXECUTION_CONTROL_UNSPECIFIED = serviceError(
            268,
            "ServiceMetadata.executionControl is not specified"
    );

    public static final MessageData UPDATE_SERVICE_METADATA_EXECUTION_CONTROL_TYPE_UNSPECIFIED = serviceError(
            269,
            "ServiceMetadata.executionControl.type is not specified"
    );

    public static final MessageData UPDATE_SERVICE_METADATA_PARAM_FIELDS_UNSPECIFIED = serviceError(
            270,
            "ServiceMetadata.paramFields is not specified"
    );

    public static final MessageData UPDATE_SERVICE_METADATA_NOT_EXISTS = serviceError(
            271,
            "Service metadata with specified name does not exist"
    );

    public static final MessageData UPDATE_SERVICE_METADATA_EXCEPTION_OCCURRED = serviceError(
            272,
            "Exception occurred when attempting to update service metadata"
    );

    public static final MessageData DELETE_SERVICE_METADATA_NOT_EXISTS = serviceError(
            273,
            "Service metadata with specified name does not exist"
    );

    public static final MessageData DELETE_SERVICE_METADATA_EXCEPTION_OCCURRED = serviceError(
            274,
            "Exception occurred when attempting to delete service metadata"
    );

    // END: service metadata management related

    // START: job spec management related
    //        280-299

    public static final MessageData CREATE_JOB_SPEC_TYPE_UNSPECIFIED = serviceError(
            280,
            "JobSpec.type is not specified"
    );

    public static final MessageData CREATE_JOB_SPEC_ID_UNSPECIFIED = serviceError(
            281,
            "JobSpec.id is not specified"
    );

    public static final MessageData CREATE_JOB_SPEC_ALREADY_EXISTS = serviceError(
            282,
            "Job spec with specified id already exists"
    );

    public static final MessageData CREATE_JOB_SPEC_EXCEPTION_OCCURRED = serviceError(
            283,
            "Exception occurred when attempting to create job spec"
    );

    public static final MessageData LIST_JOB_SPEC_EXCEPTION_OCCURRED = serviceError(
            284,
            "Exception occurred when attempting to list job spec"
    );

    public static final MessageData UPDATE_JOB_SPEC_TYPE_UNSPECIFIED = serviceError(
            285,
           "JobSpec.type is not specified"
    );

    public static final MessageData UPDATE_JOB_SPEC_ID_UNSPECIFIED = serviceError(
            286,
            "JobSpec.id is not specified"
    );

    public static final MessageData UPDATE_JOB_SPEC_NOT_EXISTS = serviceError(
            287,
            "Job spec with specified id does not exist"
    );

    public static final MessageData UPDATE_JOB_SPEC_EXCEPTION_OCCURRED = serviceError(
            288,
            "Exception occurred when attempting to update job spec"
    );

    public static final MessageData DELETE_JOB_SPEC_NOT_EXISTS = serviceError(
            289,
            "Job spec with specified id does not exist"
    );

    public static final MessageData DELETE_JOB_SPEC_EXCEPTION_OCCURRED = serviceError(
            290,
            "Exception occurred when attempting to delete job spec"
    );

    // END: job spec management related

    // START: batch app config management related
    //        300-319

    public static final MessageData BATCH_APP_CONFIG_DIRECTORY_UNSPECIFIED = serviceError(
            300,
            "ServiceMetadata.executionControl.appConfigDirectory is not specified"
    );

    public static final Template TEMPLATE_BATCH_APP_CONFIG_DIRECTORY_NOT_EXISTS = new Template(
            301,
            Category.SERVICE,
            "App Config directory specified as {directory} does not exist",
            Parameter.Directory
    );

    public static final Template BATCH_APP_CONFIG_REQUEST_MISSING_FIELD = new Template(
            302,
            Category.REQUEST,
            "Request.{field} is missing",
            Parameter.Field
    );

    public static final Template BATCH_APP_CONFIG_REQUEST_FIELD_NOT_ARRAY = new Template(
            303,
            Category.REQUEST,
            "Request.{field} is expected to be array, but not",
            Parameter.Field
    );

    public static final MessageData CREATE_BATCH_APP_CONFIG_ALREADY_EXISTS = serviceError(
            304,
            "Batch app config with specified id already exists"
    );

    public static final MessageData CREATE_BATCH_APP_CONFIG_EXCEPTION_OCCURRED = serviceError(
            305,
            "Exception occurred when attempting to create batch app config"
    );

    public static final MessageData LIST_BATCH_APP_CONFIG_EXCEPTION_OCCURRED = serviceError(
            306,
            "Exception occurred when attempting to list batch app config"
    );

    public static final MessageData UPDATE_BATCH_APP_CONFIG_NOT_EXISTS = serviceError(
            307,
            "Batch app config with specified id does not exist"
    );

    public static final MessageData UPDATE_BATCH_APP_CONFIG_EXCEPTION_OCCURRED = serviceError(
            308,
            "Exception occurred when attempting to update batch app config"
    );

    public static final MessageData DELETE_BATCH_APP_CONFIG_NOT_EXISTS = serviceError(
            309,
            "Batch app config with specified id does not exist"
    );

    public static final MessageData DELETE_BATCH_APP_CONFIG_EXCEPTION_OCCURRED = serviceError(
            310,
            "Exception occurred when attempting to delete batch app config"
    );

    // END: batch app config management related

    private enum Parameter implements Param {
        Info("info"),
        Type("type"),
        Directory("directory"),
        ExtensionDirectory("extension_directory"),
        ExtensionScope("extension_scope"),
        Library("library"),
        SecurityDirectory("security_directory"),
        Field("field"),
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
