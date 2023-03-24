package io.aftersound.weave.job;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Date;
import java.util.*;

public class JobManager {

    public interface Columns {
        String ID = "job_id";
        String DETAILS = "details";
        String RUNNER = "runner";
        String STATUS = "status";
        String CREATED = "created";
        String UPDATED = "updated";
    }

    private interface SQL {
        String CREATE_JOB = "INSERT INTO runner_job (job_id,details,status,created,updated) VALUES (?,?,?,?,?)";
        String GET_JOB = "SELECT * FROM runner_job WHERE job_id=?";
        String ASSIGN_JOB_RUNNER = "UPDATE runner_job SET runner=?,status=?,updated=? WHERE job_id=? AND status != 'ASSIGNED' AND runner IS NULL";
        String REASSIGN_JOB_RUNNER = "UPDATE runner_job SET runner=?,status=?,updated=? WHERE job_id=?";
        String UPDATE_JOB_STATUS = "UPDATE runner_job SET status=?,updated=? WHERE job_id=?";
        String DELETE_JOB = "DELETE FROM runner_job WHERE job_id=?";
        String FIND_JOB_WITH_ASSIGNED_RUNNER = "SELECT * FROM runner_job WHERE runner=? AND status=ASSIGNED ORDER BY updated ASC LIMIT ?";
    }

    private static class Record {

        private final String id;
        private final String details;
        private final String runner;
        private final String status;
        private final Timestamp created;
        private final Timestamp updated;

        public Record(
                String id,
                String details,
                String runner,
                String status,
                Timestamp created,
                Timestamp updated) {
            this.id = id;
            this.details = details;
            this.runner = runner;
            this.status = status;
            this.created = created;
            this.updated = updated;
        }

        public Job toJob() {
            Job job = new Job();
            job.setId(id);
            job.setDetails(Helper.parseAsMap1(details));
            job.setRunner(runner);
            job.setStatus(status);
            job.setCreated(new Date(created.getTime()));
            job.setUpdated(new Date(updated.getTime()));
            return job;
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JobManager.class);

    private final DataSource dataSource;
    private final String table;

    public JobManager(DataSource dataSource, String table) {
        this.dataSource = dataSource;
        this.table = table;
    }

    public String createJob(JsonNode jobRequest) throws Exception {
        final String jobId = UUID.randomUUID().toString();
        final String details = Helper.MAPPER.writeValueAsString(jobRequest);
        final Timestamp created,updated;
        created = updated = new Timestamp(System.currentTimeMillis());

        Record record = new Record(
                jobId,
                details,
                null,
                Status.NEW.name(),
                created,
                updated
        );

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL.CREATE_JOB)) {
                preparedStatement.setString(1, record.id);
                preparedStatement.setString(2, record.details);
                preparedStatement.setString(3, record.status);
                preparedStatement.setTimestamp(4, record.created);
                preparedStatement.setTimestamp(5, record.updated);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return jobId;
    }

    public Job getJob(String jobId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.GET_JOB)) {

            preparedStatement.setString(1, jobId);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                Record record = new Record(
                        rs.getString(Columns.ID),
                        rs.getString(Columns.DETAILS),
                        rs.getString(Columns.RUNNER),
                        rs.getString(Columns.STATUS),
                        rs.getTimestamp(Columns.CREATED),
                        rs.getTimestamp(Columns.UPDATED)
                );
                return record.toJob();
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean assignJobRunner(String jobId, String runner) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.ASSIGN_JOB_RUNNER)) {

            preparedStatement.setString(1, runner);
            preparedStatement.setString(2, Status.ASSIGNED.name());
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(4, jobId);

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            LOGGER.error("failed to assign runner", e);
            return false;
        }
    }

    public boolean reassignJobRunner(String jobId, String runner) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.REASSIGN_JOB_RUNNER)) {
            preparedStatement.setString(1, runner);
            preparedStatement.setString(2, Status.ASSIGNED.name());
            preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(4, jobId);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            LOGGER.error("failed to reassign runner", e);
            return false;
        }
    }

    public boolean updateStatus(String jobId, Status status) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_JOB_STATUS)) {

            preparedStatement.setString(1, status.name());
            preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setString(3, jobId);

            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            LOGGER.error("failed to update job status", e);
            return false;
        }
    }

    public boolean deleteJob(String jobId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.DELETE_JOB)) {
            preparedStatement.setString(1, jobId);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            LOGGER.error("failed to delete job", e);
            return false;
        }
    }

    public List<Job> findJobsWithAssignedRunner(String runner, int fetchCount) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.FIND_JOB_WITH_ASSIGNED_RUNNER)) {

            preparedStatement.setString(1, runner);
            preparedStatement.setInt(2, fetchCount);

            ResultSet rs = preparedStatement.executeQuery();
            List<Job> jobs = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                Record record = new Record(
                        rs.getString(Columns.ID),
                        rs.getString(Columns.DETAILS),
                        rs.getString(Columns.RUNNER),
                        rs.getString(Columns.STATUS),
                        rs.getTimestamp(Columns.CREATED),
                        rs.getTimestamp(Columns.UPDATED)
                );
                jobs.add(record.toJob());
            }
            return jobs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Job> findNewJobsByRunnerLabels(Map<String, String> runnerLabels, int fetchCount) {
        List<String> conditions = new ArrayList<>(runnerLabels.size());
        conditions.add(String.format("status='%s'", Status.NEW.name()));
        for (Map.Entry<String, String> e : runnerLabels.entrySet()) {
            conditions.add(String.format("JSON_EXTRACT(details,'$.jobRunner.%s')='%s'", e.getKey(), e.getValue()));
        }

        String whereClause = "";
        if (conditions.size() > 0) {
            StringJoiner stringJoiner = new StringJoiner(" AND ");
            for (String condition : conditions) {
                stringJoiner.add(condition);
            }
            whereClause = "WHERE " + stringJoiner + " ";
        }

        final String sql = String.format(
                "SELECT * FROM runner_job %s ORDER BY updated LIMIT %d",
                whereClause,
                fetchCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Job> jobs = new ArrayList<>();
            int count = 0;
            while (rs.next()) {
                count++;
                Record record = new Record(
                        rs.getString(Columns.ID),
                        rs.getString(Columns.DETAILS),
                        rs.getString(Columns.RUNNER),
                        rs.getString(Columns.STATUS),
                        rs.getTimestamp(Columns.CREATED),
                        rs.getTimestamp(Columns.UPDATED)
                );
                jobs.add(record.toJob());
            }
            LOGGER.info("QUERY: {} | RESULT: {}", sql, count);
            return jobs;
        } catch (SQLException e) {
            LOGGER.error("failed to get jobs with expected runner labels", e);
            return Collections.emptyList();
        }
    }

}
