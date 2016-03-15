

public class LngLatUtil {
	
	public double getLatitudeNum(String latitude){
		String dd = latitude.substring(0,2);
		String mm = latitude.substring(2);
		return Double.parseDouble(dd)+Double.parseDouble(mm)/60;
	}
	
	public double getLongitudeNum(String longitude){
		String dd = longitude.substring(0,3);
		String mm = longitude.substring(3);
		return Double.parseDouble(dd)+Double.parseDouble(mm)/60;
	}
	
	public double getAltitudeNum(String altitudeBase,String altitudeSepa){
		return Double.parseDouble(altitudeBase)+Double.parseDouble(altitudeSepa);
	}

	public static void main(String args[]){
		LngLatUtil lu = new LngLatUtil();
		
		String str = "3422.11968718";
		System.out.println(lu.getLatitudeNum(str));
	}
}
