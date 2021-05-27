package com.minlessika.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;
import com.minlessika.lightweight.db.EmbeddedPostgreSQLDataSource;

final class AutoCommitDatabaseTest {

	private static Database dbase;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		final DataSource source = new EmbeddedPostgreSQLDataSource("dbtest", 2);
		dbase = new BasicDatabase(source);
		try (
			Connection connection = source.getConnection();
		    Statement s = connection.createStatement()
		) {
		    s.execute(
		        String.join(
		            " ",
		            "CREATE TABLE accounting_chart (",
		            "   id BIGSERIAL NOT NULL,",
		            "   type VARCHAR(25) NOT NULL,",
		            "   state VARCHAR(10) NOT NULL,",
		            "   version VARCHAR(10) NOT NULL",
		            ")"
		        )
		    );
		    s.execute(
		        String.join(
		            " ",
		            "INSERT INTO accounting_chart (type, state, version)",
		            "VALUES ('SYSCOHADA', 'ACTIVE', '2018');"
		        )
		    );
		}
	}

	@RepeatedTest(value = 100)
	public void autoCloseConnection() throws SQLException {
		try(Connection conn = dbase.getConnection()) {

		}
		new JdbcSession(dbase)
			.sql(
				String.join(
		            " ",
		            "INSERT INTO accounting_chart (type, state, version)",
		            "VALUES ('SYSCOHADA', 'ACTIVE', '2018');"
		        )
			)
	        .insert(Outcome.VOID);
	}
}
