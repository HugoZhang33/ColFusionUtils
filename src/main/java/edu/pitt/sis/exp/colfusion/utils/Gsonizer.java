package edu.pitt.sis.exp.colfusion.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

public class Gsonizer {
	
	private static final Logger logger = LogManager.getLogger(Gsonizer.class.getName());
	
	private static final GsonBuilder gsonBuilder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls();
	private static Gson gson = gsonBuilder.create();
	private static final GsonBuilder gsonBuilderAll = new GsonBuilder().serializeNulls();
	private static Gson gsonAll = gsonBuilderAll.create();
	
	public static final Gsonizer instance = new Gsonizer();
	
	/**
	 * This needs to be done at the application start
	 * @param clazz
	 * @param jsonSerializer
	 */
	public static void registerTypeAdapter(final Class<?> clazz, final Object jsonSerializer) {
		gsonBuilder.registerTypeAdapter(clazz, jsonSerializer);
		gson = gsonBuilder.create();
		
		gsonBuilderAll.registerTypeAdapter(clazz, jsonSerializer);
		gsonAll = gsonBuilderAll.create();
	}
	
	public static String toJson(final Object o, final boolean excludeNotExposed) {
		
		if (o == null) {
			logger.info("Got null object to serialize. Going to return empty string.");
			return "";
		}
		
		if (excludeNotExposed) {
			return gson.toJson(o);
		}
		else {
			return gsonAll.toJson(o);
		}
	}
	
	public static <T> T fromJson(final String json, final Class<T> clazz, final boolean excludeNotExposed) {
		
		logger.info(String.format("About to deserialize this json string with length '%d' to class '%s' "
				+ "with excludeNotExposed set tot %b", json.length(), clazz, excludeNotExposed));
		
		if (excludeNotExposed) {
			return gson.fromJson(json, clazz);
		}
		else {
			return gsonAll.fromJson(json, clazz);
		}
	}
	
	public static <T> TypeAdapter<T> getTypeAdapter(final Class<T> clazz) {
		return gson.getAdapter(clazz);
	}
}
