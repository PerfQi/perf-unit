package com.perfxq.unit.dataset.builder;

public interface StringPolicy {
	
	boolean areEqual(String first, String second);
	
	String toKey(String value);

}
