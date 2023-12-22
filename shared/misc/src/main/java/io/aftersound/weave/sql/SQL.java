package io.aftersound.weave.sql;

import org.mvel2.templates.TemplateRuntime;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SQL {

    /**
     * Definition of parameter used in SQL PreparedStatement
     */
    public static class Parameter {
        private final String name;
        private final String type;

        private Parameter(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }

    public interface ResultParser<T> {
        T parse(ResultSet rs, ResultSetMetaData rsmd, int updatedCount) throws Exception;
    }

    public static class InsertResultParser implements ResultParser<Boolean> {

        private final int expectedCount;

        public InsertResultParser(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public Boolean parse(ResultSet rs, ResultSetMetaData rsmd, int updatedCount) throws Exception {
            return updatedCount == expectedCount;
        }

    }

    public static class UpdateResultParser implements ResultParser<Boolean> {

        private final int expectedCount;

        public UpdateResultParser(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public Boolean parse(ResultSet rs, ResultSetMetaData rsmd, int updatedCount) throws Exception {
            return updatedCount == expectedCount;
        }

    }

    public static class Statement {

        private final String sql;
        private final List<Parameter> parameters;

        private Statement(String sql, List<Parameter> parameters) {
            this.sql = sql;
            this.parameters = parameters;
        }

        public <T> T prepareAndExecute(Connection connection, Map<String, Object> params, ResultParser<T> parser) throws Exception {
            String sqlTemplate = (String) TemplateRuntime.eval(sql, params);

            PreparedStatement ps = connection.prepareStatement(sqlTemplate);

            // bind parameters
            for (int i = 1; i <= params.size(); i++) {
                Parameter p = parameters.get(i - 1);
                Object v = params.get(p.getName());
                switch (p.getType()) {
                    case "BigDecimal":
                        ps.setBigDecimal(i, checkAndCast(p.getName(), v, BigDecimal.class));
                        break;
                    case "Boolean":
                        ps.setBoolean(i, checkAndCast(p.getName(), v, Boolean.class));
                        break;
                    case "Blob":
                        ps.setBlob(i, checkAndCast(p.getName(), v, Blob.class));
                        break;
                    case "Byte":
                        ps.setByte(i, checkAndCast(p.getName(), v, Byte.class));
                        break;
                    case "Bytes":
                        ps.setBytes(i, checkAndCast(p.getName(), v, byte[].class));
                        break;
                    case "Clob":
                        ps.setClob(i, checkAndCast(p.getName(), v, Clob.class));
                        break;
                    case "Date":
                        ps.setDate(i, checkAndCast(p.getName(), v, Date.class));
                        break;
                    case "Double":
                        ps.setDouble(i, checkAndCast(p.getName(), v, Double.class));
                        break;
                    case "Float":
                        ps.setFloat(i, checkAndCast(p.getName(), v, Float.class));
                        break;
                    case "Int":
                        ps.setInt(i, checkAndCast(p.getName(), v, Integer.class));
                        break;
                    case "Long":
                        ps.setLong(i, checkAndCast(p.getName(), v, Long.class));
                        break;
                    case "Short":
                        ps.setShort(i, checkAndCast(p.getName(), v, Short.class));
                        break;
                    case "String":
                        ps.setString(i, checkAndCast(p.getName(), v, String.class));
                        break;
                    default:
                        throw new Exception("'" + p.getType() + "' is not supported");
                }
            }

            // execute
            ps.execute();

            // parse
            return parser.parse(ps.getResultSet(), ps.getMetaData(), ps.getUpdateCount());
        }

        private <T> T checkAndCast(String paramName, Object paramValue, Class<T> targetType) {
            if (paramValue == null || targetType.isInstance(paramValue)) {
                return targetType.cast(paramValue);
            }

            throw new ClassCastException(
                    String.format(
                            "Parameter '%s' expects type '%s', but is given an instance of '%s'",
                            paramName,
                            targetType.getName(),
                            paramValue.getClass().getClass()
                    )
            );
        }

    }

    public static final class PredefinedStatement {
        private String id;
        private String sql;
        private List<Map<String, String>> parameters;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public List<Map<String, String>> getParameters() {
            return parameters;
        }

        public void setParameters(List<Map<String, String>> parameters) {
            this.parameters = parameters;
        }

    }

    public static final class PredefinedStatements {

        private final Map<String, Statement> byId;

        private PredefinedStatements(Map<String, Statement> statementById) {
            this.byId = Collections.unmodifiableMap(statementById);
        }

        public static PredefinedStatements from(PredefinedStatement[] predefinedStatements) {
            final Map<String, Statement> byId = new HashMap<>(predefinedStatements.length);
            Arrays.stream(predefinedStatements).forEach(
                    ps -> {
                        List<Parameter> parameterList = new ArrayList<>();
                        ps.getParameters().forEach(
                                p -> parameterList.add(SQL.param(p.get("name"), p.get("type")))
                        );
                        byId.put(ps.getId(), SQL.statement(ps.getSql(), parameterList));
                    }
            );
            return new PredefinedStatements(byId);
        }

        public Statement getStatement(String id) {
            return byId.get(id);
        }

    }

    public static Parameter param(String name, String type) {
        return new Parameter(name, type);
    }

    public static Statement statement(String sql, List<Parameter> parameterList) {
        return new Statement(sql, parameterList);
    }

}
