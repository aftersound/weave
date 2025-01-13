package io.aftersound.sql;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.mvel2.templates.TemplateRuntime;

@SuppressWarnings("unchecked")
public class SQL {

    /**
     * Definition of parameter used in SQL statement
     */
    public static class Parameter {

        private final String name;
        private final String type;
        private final Map<String, Object> options;

        private Parameter(String name, String type, Map<String, Object> options) {
            this.name = name;
            this.type = type;
            this.options = options;
        }

        private Parameter(String name, String type) {
            this(name, type, Collections.emptyMap());
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public Map<String, Object> getOptions() {
            return Collections.unmodifiableMap(options);
        }

        public <T> T getOption(String name) {
            return (T) options.get(name);
        }

        public <T> T getOption(String name, Class<T> type) {
            Object o = options.get(name);
            return type.isInstance(o) ? type.cast(o) : null;
        }

    }

    /**
     * Result parser which parse the execution result of a SQL statement
     *
     * @param <T> generic type of expected result
     */
    public interface ResultParser<T> {
        T parse(ResultSet rs, ResultSetMetaData rsmd, int updateCount) throws Exception;
    }

    /**
     * SQL query statement
     */
    public static class Statement {

        private final String sql;
        private final List<Parameter> parameters;

        private Statement(String sql, List<Parameter> parameters) {
            this.sql = sql;
            this.parameters = parameters;
        }

        public String getSql() {
            return sql;
        }

        public List<Parameter> getParameters() {
            return parameters;
        }

        public <T> T prepareAndExecute(Connection connection, Map<String, Object> params, ResultParser<T> resultParser) throws Exception {
            String sqlQuery = (String) TemplateRuntime.eval(sql, params);

            try (PreparedStatement ps = connection.prepareStatement(sqlQuery)) {

                // bind parameter
                for (int i = 1; i <= parameters.size(); i++) {
                    Parameter param = parameters.get(i - 1);
                    String paramName = param.getName();
                    switch (param.getType()) {
                        case "BigDecimal":
                            ps.setBigDecimal(i, (BigDecimal) params.get(paramName));
                            break;
                        case "Byte":
                            ps.setByte(i, (Byte) params.get(paramName));
                            break;
                        case "Bytes":
                            ps.setBytes(i, (byte[]) params.get(paramName));
                            break;
                        case "Date":
                            ps.setDate(i, (Date) params.get(paramName));
                            break;
                        case "Double":
                            ps.setDouble(i, (Double) params.get(paramName));
                            break;
                        case "Float":
                            ps.setFloat(i, (Float) params.get(paramName));
                            break;
                        case "Int":
                            ps.setInt(i, (Integer) params.get(paramName));
                            break;
                        case "Long":
                            ps.setLong(i, (Long) params.get(paramName));
                            break;
                        case "Object": {
                            Object v = params.get(paramName);
                            Function<Object, Object> encoder = param.getOption("ENCODER", Function.class);
                            if (encoder != null) {
                                v = encoder.apply(v);
                            }
                            ps.setObject(i, v);
                            break;
                        }
                        case "Short":
                            ps.setShort(i, (Short) params.get(paramName));
                            break;
                        case "String":
                            ps.setString(i, (String) params.get(paramName));
                            break;
                        default:
                            // TODO: more
                    }
                }

                // execute
                ps.execute();

                // parse
                return resultParser.parse(ps.getResultSet(), ps.getMetaData(), ps.getUpdateCount());
            }
        }

    }

    public static Parameter bigDecimalParam(String name) {
        return param(name, "BigDecimal");
    }

    public static Parameter byteParam(String name) {
        return param(name, "Byte");
    }

    public static Parameter bytesParam(String name) {
        return param(name, "Bytes");
    }

    public static Parameter dateParam(String name) {
        return param(name, "Date");
    }

    public static Parameter doubleParam(String name) {
        return param(name, "Double");
    }

    public static Parameter floatParam(String name) {
        return param(name, "Float");
    }

    public static Parameter intParam(String name) {
        return param(name, "Int");
    }

    public static Parameter longParam(String name) {
        return param(name, "Long");
    }

    public static Parameter shortParam(String name) {
        return param(name, "Short");
    }

    public static Parameter stringParam(String name) {
        return param(name, "String");
    }

    public static Parameter objectParam(String name) {
        return param(name, "Object");
    }

    public static Parameter param(String name, String type) {
        return new Parameter(name, type);
    }

    public static Statement statement(String sql, List<Parameter> parameters) {
        return new Statement(sql, parameters);
    }

}