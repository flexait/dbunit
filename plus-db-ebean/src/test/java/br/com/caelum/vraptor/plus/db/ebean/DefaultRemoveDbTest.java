package br.com.caelum.vraptor.plus.db.ebean;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.plus.api.db.RemoveDb;
import br.com.flexait.caelum.vraptor.plus.dbunit.DbUnit;
import br.com.flexait.caelum.vraptor.plus.dbunit.DbUnitEbean;

import com.avaje.ebean.Ebean;

public class DefaultRemoveDbTest {

	private RemoveDb db;
	
	@Before
	public void setUp() throws Exception {
		DbUnit dbUnit = new DbUnitEbean();
		dbUnit.init(MyModel.class);
		db = new DefaultRemoveDb();
	}
	
	@Test
	public void shouldReturnListOfMyModel() {
		int by = db.by(MyModel.class, 1L);
		assertThat(by, equalTo(1));
		assertThat(Ebean.find(MyModel.class, 1L), nullValue());
	}

}
