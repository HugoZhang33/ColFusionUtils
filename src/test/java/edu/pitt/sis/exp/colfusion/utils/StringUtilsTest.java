package edu.pitt.sis.exp.colfusion.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class StringUtilsTest extends UnitTestBase {
	
	@Test
	public void testIsSpecified() {
		assertEquals(false, StringUtils.isSpecified(""));
		assertEquals(false, StringUtils.isSpecified(null));
		
		assertEquals(true, StringUtils.isSpecified("abc"));
	}
	
	@Test
	public void testIsNullOrEmpty() {
		assertEquals(true, StringUtils.isNullOrEmpty(""));
		assertEquals(true, StringUtils.isNullOrEmpty(null));
		
		assertEquals(false, StringUtils.isNullOrEmpty("abc"));
	}
	
	@Test
	public void testReplaceSpaces() {
		assertEquals("", StringUtils.replaceSpaces(""));
		assertEquals("abc", StringUtils.replaceSpaces("abc"));
		
		assertEquals("a_b_c", StringUtils.replaceSpaces("a b c"));
		
		assertEquals("a_b_c", StringUtils.replaceSpaces("a      b c"));
		
		assertEquals("_a_b_c_", StringUtils.replaceSpaces(" a      b c  "));
		assertEquals("_a___b_c_", StringUtils.replaceSpaces(" a  _   b c  "));
	}
	
	@Test
	public void testMakeShortUnique() {
		assertEquals("", StringUtils.makeShortUnique(""));
		assertEquals("abc" + "abc".hashCode(), StringUtils.makeShortUnique("abc"));
		
		System.out.println("Sheet1".hashCode());
		
		//The change only in the last digit
		assertFalse(StringUtils.makeShortUnique("123456789123456789123456789123456789123456789123456789123456789123456789")
				.equals(StringUtils.makeShortUnique("123456789123456789123456789123456789123456789123456789123456789123456780")));
	}
	
	@Test
	public void testRemovePrefix() {
		assertEquals("hibernate.default_catalog", StringUtils.removePrefix("colfusion.hibernate.default_catalog", "colfusion."));
		
		assertEquals("hibernate.default_catalog", StringUtils.removePrefix("hibernate.default_catalog", "colfusion."));
		
		assertEquals("hibernate.default_catalog.colfusion.test", 
				StringUtils.removePrefix("hibernate.default_catalog.colfusion.test", "colfusion."));
	}
}
