package com.minlessika.db;

import com.lightweight.db.EmbeddedPostgreSQLDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import com.jcabi.jdbc.JdbcSession;
import com.jcabi.jdbc.Outcome;

final class TransactionalDatabaseTest {

	private static Database dbase;

	private Connection trconn;

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

	@BeforeEach
	void startTransaction() throws Exception {
		dbase.startTransaction();
		trconn = dbase.getConnection();
	}
	
	@RepeatedTest(value = 100)
	@Execution(ExecutionMode.CONCURRENT)
	public void runOperations() throws SQLException {
		try(Connection conn = dbase.getConnection()) {
			MatcherAssert.assertThat(
				conn.toString(),
				new IsEqual<>(trconn.toString())
			);
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
		MatcherAssert.assertThat(
			trconn.isClosed(),
			new IsEqual<>(false)
		);
	}
	
	@AfterEach
	void terminateTransaction() throws Exception {
		dbase.commit();
		dbase.terminateTransaction();
		MatcherAssert.assertThat(
			trconn.isClosed(),
			new IsEqual<>(true)
		);
	}
}
