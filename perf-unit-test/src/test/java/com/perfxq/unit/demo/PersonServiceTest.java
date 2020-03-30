package com.perfxq.unit.demo;

import java.util.List;
import com.perfxq.unit.SecJUnitRunner;
import com.perfxq.unit.SecJunitMethodListener;
import com.perfxq.unit.annotation.PrepareDataType;
import com.perfxq.unit.annotation.SecTest;
import com.perfxq.unit.runner.SecParallelSpringRunner;
import com.perfxq.unit.demo.entity.Person;
import com.perfxq.unit.demo.service.PersonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;


@RunWith(SecParallelSpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, SecJunitMethodListener.class })
public class PersonServiceTest extends SecJUnitRunner {
	@Autowired
	private PersonService personService;


	@Test
	@SecTest(enablePrepare=true,prepareDateType = PrepareDataType.XML2DB,prepareDateConfig = {"sampleData.xml"}
		,enableCheck = true,checkConfigFiles = {"CheckConfigFile.json"})
	public void testXml() throws Exception {
		List<Person> personList = this.personService.find("hil");
		Assert.assertEquals(1, personList.size());
		//assertEquals("Phillip", personList.get(0).getFirstName());
	}


	@Test
	@SecTest(enablePrepare=true,prepareDateType = PrepareDataType.EXCEL2DB,prepareDateConfig = {"Person.xlsx"})
	public void testExcel() throws Exception {
		List<Person> all = this.personService.selectAll();
		Assert.assertEquals(2, all.size());
	}

	@Test
	@SecTest(enablePrepare=true,prepareDateType = PrepareDataType.CSV2DB)
	public void testCsv() throws Exception {
		List<Person> all = this.personService.queryByTitle("test_csv");
		Assert.assertEquals(1, all.size());
	}

}
