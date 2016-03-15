

import java.io.FileInputStream;
import java.util.Properties;

public class PropertiesUtil {
	
	
	public static String getParam(String str) throws Exception{
		Properties prop = new Properties();
		prop.load(new FileInputStream("D://powernet//config.properties"));
		 return prop.getProperty(str);
	}
	
	public static void main(String args[]) throws Exception{
		System.out.println(getParam("port"));
	}
}
