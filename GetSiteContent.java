import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class GetSiteContent {
	private static final String wikipediaURL = "https://de.wikipedia.org";
	public String getSiteContent(String urlNAme) {
		try{
		URL url = new URL(wikipediaURL + urlNAme);
		URLConnection con = url.openConnection();
		InputStream in = con.getInputStream();
		String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
		encoding = encoding == null ? "UTF-8" : encoding;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		int len = 0;
		while ((len = in.read(buf)) != -1) {
		    baos.write(buf, 0, len);
		}
		String body = new String(baos.toByteArray(), encoding);
		return body;
		} catch (Exception e) {
			//System.out.println("some error occured " + e.getMessage() + (wikipediaURL + urlNAme));
		}
		return null;
	}
}
