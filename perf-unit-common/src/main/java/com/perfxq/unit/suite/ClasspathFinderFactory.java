/*
 * @author huayq
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package com.perfxq.unit.suite;

public class ClasspathFinderFactory implements ClassesFinderFactory {

	public ClassesFinder create(boolean searchInJars, String[] filterPatterns, SuiteType[] suiteTypes, Class<?>[] baseTypes,
			Class<?>[] excludedBaseTypes, String classpathProperty) {
		ClassTester tester = new ClasspathSuiteTester(searchInJars, filterPatterns, suiteTypes, baseTypes, excludedBaseTypes);
		return new ClasspathClassesFinder(tester, classpathProperty);
	}

}
