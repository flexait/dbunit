package br.com.caelum.vraptor.plus.db.ebean;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.plus.api.db.FindDb;
import br.com.caelum.vraptor.plus.api.db.pagination.Page;
import br.com.caelum.vraptor.plus.api.test.MyModel;
import br.com.caelum.vraptor.plus.dbunit.DbUnit;
import br.com.caelum.vraptor.plus.dbunit.DbUnitEbean;

public class DefaultFindDbTest {

	private FindDb db;
	
	@Before
	public void setUp() throws Exception {
		DbUnit dbUnit = new DbUnitEbean();
		dbUnit.init(MyModel.class);
		db = new DefaultFindDb();
	}
	
	@Test
	public void shouldReturnListOfMyModel() {
		List<MyModel> all = db.all(MyModel.class);
		assertThat(all, notNullValue());
		assertThat(all.get(0).getId(), equalTo(1L));
	}
	
	@Test
	public void shouldReturnMyModel() {
		MyModel model = db.by(MyModel.class, 1L);
		assertThat(model.getId(), equalTo(1L));
	}
	
	@Test
	public void shouldReturnListPaginateOfMyModelPage1() {
		Page<MyModel> page = db.paginate(MyModel.class, 1, 2);
		assertThat(page.getList(), not(empty()));
		assertThat(page.getList().get(1).getId(), equalTo(2L));
		assertThat(page.getPageSize(), equalTo(2));
	}
	
	@Test
	public void shouldReturnListPaginateOfMyModelPage2() {
		Page<MyModel> page = db.paginate(MyModel.class, 2, 2);
		assertThat(page.getList(), not(empty()));
		assertThat(page.getList().get(0).getId(), equalTo(3L));
		assertThat(page.getPageSize(), equalTo(1));
	}
	
	@Test(expected = Exception.class)
	public void shouldThrowExceptionIfPageNotExists() {
		db.paginate(MyModel.class, 3, 2);
	}

}