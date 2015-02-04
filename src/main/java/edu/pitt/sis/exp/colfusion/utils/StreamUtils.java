package edu.pitt.sis.exp.colfusion.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;

public class StreamUtils {
	
	private static final String STRING_ENCODING_UTF8 = "UTF-8";
	
	public static InputStream fromString(final String content) {
		return fromString(content, STRING_ENCODING_UTF8);
	}
	
	public static InputStream fromString(final String content, final String encoding) {
		if (content == null) {
			throw new IllegalArgumentException("Content cannot be null");
		}
		
		try {
			return new ByteArrayInputStream(content.getBytes(encoding));
		}
		catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toString(final InputStream inputStream) throws IOException {
		return toString(inputStream, STRING_ENCODING_UTF8);
	}
	
	public static String toString(final InputStream inputStream, final String encoding) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("The input parameter inputStream is Null");
		}
		
		return IOUtils.toString(inputStream, encoding);
	}
}
