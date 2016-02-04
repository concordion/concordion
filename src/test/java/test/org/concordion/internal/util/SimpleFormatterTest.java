package test.org.concordion.internal.util;

import static org.junit.Assert.*;

import org.concordion.internal.util.SimpleFormatter;
import org.junit.Test;

public class SimpleFormatterTest {

	@Test
	public void testFormatWithoutSpecifiers() {
		String format = "some text";
		String formattedString = SimpleFormatter.format(format);
		assertEquals(format, formattedString);
	}
	
	@Test
	public void testFormatWithStartingConversion() {
		String format = "%s specifier";
		String formattedString = SimpleFormatter.format(format, "text with");
		assertEquals("text with specifier", formattedString);
	}
	
	@Test
	public void testFormatWithConversion() {
		String format = "text %s specifier";
		String formattedString = SimpleFormatter.format(format, "with");
		assertEquals("text with specifier", formattedString);
	}
	
	@Test
	public void testFormatWithEndingConversion() {
		String format = "text %s";
		String formattedString = SimpleFormatter.format(format, "formatted");
		assertEquals("text formatted", formattedString);
	}
	
	@Test
	public void testFormatWithConversionAtStartAndEnd() {
		String format = "%s text %s the %s";
		String formattedString = SimpleFormatter.format(format, "some", "in", "middle");
		assertEquals("some text in the middle", formattedString);
	}
	
	@Test
	public void testFormatWithMissingArgs() {
		String format = "%s specifier";
		String formattedString = SimpleFormatter.format(format);
		assertEquals(" specifier", formattedString);
	}
	
	@Test
	public void testFormatWithNullArgs() {
		String format = "%s specifier";
		String formattedString = SimpleFormatter.format(format, (Object[])null);
		assertEquals(" specifier", formattedString);
	}
	
	@Test
	public void testWithoutSpecifiersButArgs() {
		String format = "Unexpected match failure for ''";
		String formattedString = SimpleFormatter.format(format, "commandValueAndAttributes");
		assertEquals(format, formattedString);
	}

}
