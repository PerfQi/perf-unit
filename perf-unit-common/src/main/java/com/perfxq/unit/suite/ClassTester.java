/*
 * @author huayq
 * 
 * Published under Apache License, Version 2.0 (http://apache.org/licenses/LICENSE-2.0)
 */
package com.perfxq.unit.suite;

public interface ClassTester {
	boolean acceptClass(Class<?> clazz);

	boolean acceptClassName(String className);

	boolean acceptInnerClass();

	boolean searchInJars();
}