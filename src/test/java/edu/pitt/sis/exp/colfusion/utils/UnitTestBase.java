package edu.pitt.sis.exp.colfusion.utils;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import edu.pitt.sis.exp.colfusion.utils.ConfigManager;
import edu.pitt.sis.exp.colfusion.utils.ResourceUtils;


/**
 * <p>Some functionality that is commonly used by many unit tests.</p>
 * 
 * <p>This class has BeforeClass method {@link #prepareUp()}, AfterClass method {@link #afterClass()} and 
 * After method {@link #afterMethod()}.</p>
 * 
 * <p>Use {@link #tempFolder} to create temporary folder and files for tests.</p>
 * <p>To redefine system property just for one test use {@link #redefineSystemPropertyForMethod(String, String)}</p>
 * <p>To redefine system property for the whole class use {@link #redefineSystemPropertyForClass(String, String)}</p>
 * 
 * @author Evgeny
 *
 */
public abstract class UnitTestBase  {
	
	static final Logger logger = LogManager.getLogger(UnitTestBase.class.getName());
	
	/**
	 * Use this to create temporary folder and files for tests. JUnit will automatically clean up after test finishes.
	 */
	@Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
	
	/**
	 * Holds system properties names that were redefined with {@link #redefineSystemPropertyForClass(String, String)}.
	 */
	private static final Set<String> systemPropertiesToCleanAfterClass = new HashSet<String>();
	
	/**
	 * Holds system properties names that were redefined with {@link #redefineSystemPropertyForMethod(String, String)}.
	 */
	private final Set<String> systemPropertiesToCleanAfterMethod = new HashSet<String>();
	
	/**
	 * Loads test properties via {@link ConfigManager#loadTestProperties()}
	 */
	@BeforeClass
	public static void prepareUp() {
		//TODO find a better way to do that because currently the same properties
		// are reloaded foreach test class.
		ConfigManager.getInstance().loadTestProperties();
		// just to see if this method called for each test class
		logger.info("Loaded test properties called from UniteTestBase.prepareUp method which is marked as BeforeClass");
	}
	
	/**
	 * Clear all system properties that were redefined with {@link #redefineSystemPropertyForClass(String, String)}.
	 * @throws Exception
	 */
	@AfterClass
	public static void afterClass() throws Exception {
		for (String sysProperty : systemPropertiesToCleanAfterClass) {
			System.clearProperty(sysProperty);
		};
		
		systemPropertiesToCleanAfterClass.clear();
	}
	
	/**
	 * Clear all system properties that were redefined with {@link #redefineSystemPropertyForMethod(String, String)}.
	 * @throws Exception
	 */
	@After
	public void afterMethod() throws Exception {
		for (String sysProperty : systemPropertiesToCleanAfterMethod) {
			System.clearProperty(sysProperty);
		};
		
		systemPropertiesToCleanAfterMethod.clear();
	}
	
	/**
	 * Get resource by given name and returns its URI as string.
	 * 
	 * @param resourceName
	 * 		the name of the resource.
	 * @return the absolute location of the file.
	 * Note: the file might be inside of a jar archive, thus not suitable as input to new File().
	 */
	protected String getResourceAsAbsoluteURI(final String resourceName) {
		return ResourceUtils.getResourceAsFileLocation(this.getClass(), resourceName);
	}
	
	/**
	 * Get resource by given name and returns it as {@link InputStream}.
	 * 
	 * @param resourceName
	 * 		the name of the resource.
	 * @return resource as {@link InputStream}.
	 */
	protected InputStream getResourceAsStream(final String resourceName) {
		return ResourceUtils.getResourceAsStream(this.getClass(), resourceName);
	}
	
	/**
	 * Sets provided system property to provided value. The property will be cleaned after the test method.
	 * 
	 * @param propertyName
	 * 		the name of the property to set.
	 * @param value
	 * 		the value to set.
	 */
	protected void redefineSystemPropertyForMethod(final String propertyName, final String value) {
		System.setProperty(propertyName, value);
		systemPropertiesToCleanAfterMethod.add(propertyName);
	}
	
	/**
	 * Sets provided system property to provided value. The property will be cleaned after all tests in the class run.
	 * 
	 * @param propertyName
	 * 		the name of the property to set.
	 * @param value
	 * 		the value to set.
	 */
	protected static void redefineSystemPropertyForClass(final String propertyName, final String value) {
		System.setProperty(propertyName, value);
		systemPropertiesToCleanAfterClass.add(propertyName);
	}
	
	/**
	 * Check if OS is windows or not.
	 * @return true if OS is Windows, false otherwise.
	 */
	protected boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		
		if (os.indexOf("win") >= 0) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Run assertEquals on provided expected and actual stings but remoing all white spaces from them beforehand.
	 * 
	 * @param message 
	 * 		the string that will be printed is assertion fails.
	 * @param expected
	 * 		the expected value.
	 * @param actual
	 * 		the actual value.
	 */
	protected void assertEqualsIgnoreWhiteSpaces(final String message, 
			final String expected, final String actual) {
		assertEquals(message, expected.replaceAll("\\s+",""), actual.replaceAll("\\s+",""));	
	}
}
