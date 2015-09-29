package br.com.flexait.dbunit.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.ext.mssql.MsSqlDataTypeFactory;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

public class DbUnit {

	private static final String DATASOURCE_PATH = "src/test/resources/datasets/";
	private final Connection conn;
	private List<IDatabaseConnection> connections = new ArrayList<>();
	private List<IDataSet> dataSets = new ArrayList<>();
	private List<String> pkList = new ArrayList<>();

	public DbUnit(Connection conn) {
		this.conn = conn;
	}

	public DbUnit init(Class<?>... types) throws Exception {
		for (Class<?> type : types) {
			initOne(type);
		}
		return this;
	}

	protected <T> void initOne(Class<T> type) throws DatabaseUnitException,
			SQLException, FileNotFoundException {
		IDataSet dataSet = getDataSet(type);
		IDatabaseConnection connection = getConnection();
		dataSets.add(dataSet);
		connections.add(connection);
		
		DatabaseOperation.REFRESH.execute(connection, dataSet);
	}

	protected IDatabaseConnection getConnection() throws DatabaseUnitException,
			SQLException {
		DatabaseConnection databaseConnection = new DatabaseConnection(conn);
		setDataTypeFactory(databaseConnection);
		return databaseConnection;
	}

	private void setDataTypeFactory(DatabaseConnection databaseConnection)
			throws SQLException {
		DefaultDataTypeFactory dataTypeFactory = null;

		String databaseProductName = conn.getMetaData()
				.getDatabaseProductName();

		switch (databaseProductName) {
		case "HSQL Database Engine":
			dataTypeFactory = new HsqldbDataTypeFactory();
			break;

		case "MySQL":
			dataTypeFactory = new MySqlDataTypeFactory();
			break;

		case "H2":
			dataTypeFactory = new H2DataTypeFactory();
			break;

		case "PostgreSQL":
			dataTypeFactory = new PostgresqlDataTypeFactory();
			break;

		case "Oracle":
			dataTypeFactory = new OracleDataTypeFactory();
			break;

		case "Microsoft SQL Server":
			dataTypeFactory = new MsSqlDataTypeFactory();
			break;

		default:
			throw new SQLException("Unknown DataTypeFactory");
		}

		if (dataTypeFactory != null) {
			databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
			if(!pkList.isEmpty()) {
				databaseConnection.getConfig().setProperty(DatabaseConfig.PROPERTY_PRIMARY_KEY_FILTER, new CustomColumnFilter());
			}
		}
	}

	protected <T> IDataSet getDataSet(Class<T> type)
			throws FileNotFoundException, DataSetException {
		String name = DATASOURCE_PATH + type.getSimpleName() + ".xml";
		FileInputStream fileInputStream = new FileInputStream(name);

		return new FlatXmlDataSetBuilder().build(fileInputStream);
	}

	public void clean() throws Exception {
		for (int i = 0; i < connections.size(); i++) {
			DatabaseOperation.DELETE.execute(connections.get(i), dataSets.get(i));
		}
	}

	public DbUnit configPKs(final String... columns) {
		for (String column : columns) {
			pkList.add(column.toUpperCase());
		}
		
		return this;
	}
	
	class CustomColumnFilter extends DefaultColumnFilter {
		public CustomColumnFilter() {
			pkList.add("ID");
		}
		
		@Override
		public boolean accept(String tableName, Column column) {
			return pkList.contains(column.getColumnName().toUpperCase());
		}
	};
	
}
