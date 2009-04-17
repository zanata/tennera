package org.fedorahosted.tennera.jgettext;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.fedorahosted.tennera.jgettext.catalog.parse.ExtendedCatalogParser;
import org.fedorahosted.tennera.jgettext.catalog.write.CatalogWriter;
import org.junit.Test;


public class RoundTripTests {
	
	@Test
	public void testRoundtrip1() throws Throwable{
		File original = getResource("/roundtrip/sample.po");
		testRoundTrip(original);
	}

	@Test
	public void testWordWrappingInMsgId() throws Throwable{
		File original = getResource("/roundtrip/msgid_wordwrap.po");
		testRoundTrip(original);
	}
	
	@Test
	public void testEmptyLineNote() throws Throwable{
		File original = getResource("/roundtrip/translate-toolkit/emptylines_notes.po");
		testRoundTrip(original);
	}
	
	@Test
	public void testMalformedObsoleteUnits() throws Throwable{
		File original = getResource("/roundtrip/translate-toolkit/malformed_obsoleteunits.po");
		testRoundTrip(original);
	}
	
	@Test
	public void testMalformedUnits() throws Throwable{
		File original = getResource("/roundtrip/translate-toolkit/malformed_units.po");
		testRoundTrip(original);
	}
	
	@Test
	public void testNonAsciiHeader() throws Throwable{
		File original = getResource("/roundtrip/translate-toolkit/nonascii_header.po");
		testRoundTrip(original);
	}
	
	@Test
	public void testMultilineContext() throws Throwable{
		File original = getResource("/roundtrip/translate-toolkit/multiline_context.po");
		testRoundTrip(original);
	}
	
	private void testRoundTrip(String message, File f) throws Throwable{
		String output = roundtrip(f);
		String originalString = readToString(f); 
		assertEquals(message, originalString, output);
	}
	
	private void testRoundTrip(File f) throws Throwable{
		testRoundTrip(null, f);
	}
	
	private String roundtrip(File original) throws Throwable{
		ExtendedCatalogParser parser = new ExtendedCatalogParser(original);
		parser.catalog();
		CatalogWriter writer = new CatalogWriter(parser.getCatalog());
		StringWriter outputWriter = new StringWriter();
		writer.writeTo(outputWriter);
		outputWriter.flush();
		return outputWriter.toString();
	}
	
	private File getResource(String file){
		return new File( getClass().getResource(file).getFile() );
	}
	
    private static String readToString(File file) {

    	BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
	    	reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	 
	        String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
            	reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
 
        return sb.toString();
    }

	
}