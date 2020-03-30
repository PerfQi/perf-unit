package com.perfxq.unit;

import com.perfxq.unit.demo.AppServiceTest;
import com.perfxq.unit.demo.PersonServiceTest;
import com.perfxq.unit.runner.SecParallelSuite;
import com.perfxq.unit.suite.ClasspathSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(SecParallelSuite.class)
@Suite.SuiteClasses({AppServiceTest.class, PersonServiceTest.class})
//@Suite.SuiteClasses({AppServiceTest.class, PersonServiceTest.class})
public class ParalellTestSuite {
}
