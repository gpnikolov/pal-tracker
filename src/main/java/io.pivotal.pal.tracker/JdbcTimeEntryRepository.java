package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class JdbcTimeEntryRepository implements TimeEntryRepository {
    DataSource ds;
    final String table_time_entries = "time_entries";
    private final JdbcTemplate jdbcTemplate;

    public JdbcTimeEntryRepository (DataSource ds) {
        this.ds = ds;
        this.jdbcTemplate = new JdbcTemplate(ds);
    }
    @Override
    public TimeEntry create(TimeEntry timeEntry)  {
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();
        int autoIncKeyFromApi = -1;
        try {
            Connection conn = ds.getConnection();
            Statement stmt = null;
            String query = " insert into "+table_time_entries+
                    " (project_id, user_id, date, hours)"
                    + " values (?, ?, ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            preparedStmt.setLong (1, timeEntry.getProjectId());
            preparedStmt.setLong (2, timeEntry.getUserId());
            preparedStmt.setDate (3, Date.valueOf(timeEntry.getDate()));
            preparedStmt.setInt (4, timeEntry.getHours());

            // execute the preparedstatement
            preparedStmt.execute();
            ResultSet rs = preparedStmt.getGeneratedKeys();

            if (rs.next()) {
                autoIncKeyFromApi = rs.getInt(1);
            }

            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return find(autoIncKeyFromApi);
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        try {
            Connection con = ds.getConnection();
            Statement stmt = null;
            String query = "select * from "+
                    table_time_entries +
                    " where id="+timeEntryId;

            try {
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    return new TimeEntry(
                            rs.getLong("id"),
                            rs.getLong( "project_id"),
                            rs.getLong("user_id"),
                            rs.getDate("date").toLocalDate(),
                            rs.getInt("hours")
                    );
                }
            } catch (SQLException e ) {
                e.printStackTrace();
            } finally {
                if (stmt != null) { stmt.close(); }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public TimeEntry update(long id, TimeEntry timeEntry) {
        jdbcTemplate.update("UPDATE time_entries " +
                        "SET project_id = ?, user_id = ?, date = ?,  hours = ? " +
                        "WHERE id = ?",
                timeEntry.getProjectId(),
                timeEntry.getUserId(),
                Date.valueOf(timeEntry.getDate()),
                timeEntry.getHours(),
                id);

        return find(id);
    }

    @Override
    public List<TimeEntry> list() {
        return jdbcTemplate.query
                ("SELECT id, project_id, user_id, date, hours FROM time_entries", mapper);
    }

    @Override
    public void delete(long id) {
        jdbcTemplate.update("DELETE FROM time_entries WHERE id = ?", id);
    }

    private final RowMapper<TimeEntry> mapper = (rs, rowNum) -> new TimeEntry(
            rs.getLong("id"),
            rs.getLong("project_id"),
            rs.getLong("user_id"),
            rs.getDate("date").toLocalDate(),
            rs.getInt("hours")
    );

    private final ResultSetExtractor<TimeEntry> extractor =
            (rs) -> rs.next() ? mapper.mapRow(rs, 1) : null;
}
