
public class MathExt {
	
	public static final Double E = 0.0818191909289069;
	public static final Double PI = 3.14159265358979323846;

	// 经度度数
	double longitude = 109.22156579d;
	// 纬度度数
	double latitude = 34.36867596d;

	// 大地高度（天线海拔高度+海平面分离度）
	double hight = 463.835d;
		
	public MathExt(){
			try {
				longitude = Double.parseDouble(PropertiesUtil.getParam("longitude"));
				latitude = Double.parseDouble(PropertiesUtil.getParam("latitude"));
				hight = Double.parseDouble(PropertiesUtil.getParam("hight"));
			} catch (Exception e) {
				// TODO: handle exception
			}
	}
	public MathExt(double longitude,double latitude,double hight){
		this.longitude = longitude;
		this.latitude = latitude;
		this.hight = hight;
	}

	public double getNValue(){
		double sintv = Math.sin(PI*latitude/180);
		double a = 6378137;
		double sind = E*E*sintv*sintv;
		double sint = 1d - sind;
		double sinj = Math.sqrt(sint);
		double n = a/sinj;
		return n;
	}
	
	public double getXValue(){
	    double costv = Math.cos(PI*latitude/180);
	    double coslv = Math.cos(PI*longitude/180);
		return (getNValue()+hight)*costv*coslv;
	}
	
	public double getYValue(){
		   double costv = Math.cos(PI*latitude/180);
		    double sinlv = Math.sin(PI*longitude/180);
		return (getNValue()+hight)*costv*sinlv;
	}
	
	public double getZValue(){
		 double sintv = Math.sin(PI*latitude/180);
		return ((getNValue()*(1-E*E))+hight)*sintv;
	}
	
	static double longitude0 = 0d;
	static double latitude0 = 0d;
	static double hight0 = 0d;
	static{
		try {
			longitude0 = Double.parseDouble(PropertiesUtil.getParam("longitude"));
			latitude0 = Double.parseDouble(PropertiesUtil.getParam("latitude"));
			hight0 = Double.parseDouble(PropertiesUtil.getParam("hight"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	//3d距离
	public double get3Dist() {
		MathExt me = new MathExt();
		double tdist=0d;
		try {
			double x = getXValue()-me.getXValue();
			double y = getYValue()-me.getYValue();
			double z = getZValue()-me.getZValue();
			tdist = Math.sqrt(x*x+y*y+z*z);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return tdist;
	}
	
	
	//北
	public double getNorthValue() {
		MathExt me = new MathExt();
		double northValue=0d;
		try {
			double sintn = Math.sin(PI*latitude0/180);
			double ta = sintn*Math.cos(PI*longitude0/180)*(getXValue()-me.getXValue());
			double tb = sintn*Math.sin(PI*longitude0/180)*(getYValue()-me.getYValue());
			double tc = Math.cos(PI*latitude0/180)*(getZValue()-me.getZValue());
			northValue = tc-ta-tb;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return northValue;
	}
	
	//东
	public double getEastValue(){
		MathExt me = new MathExt();
		double northValue=0d;
		try {
			double ya = Math.sin(PI*longitude0/180)*(getXValue()-me.getXValue());
			double yb = Math.cos(PI*longitude0/180)*(getYValue()-me.getYValue());
			northValue = yb - ya;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return northValue;
	}
	
	//天
	public double getUValue(){
		MathExt me = new MathExt();
		double uValue=0d;
		try {
			double sintn = Math.cos(PI*latitude0/180);
			double ta = sintn*Math.cos(PI*longitude/180)*(getXValue()-me.getXValue());
			double tb = sintn*Math.sin(PI*longitude0/180)*(getYValue()-me.getYValue());
			double tc = Math.sin(PI*latitude0/180)*(getZValue()-me.getZValue());
			uValue = tc+ta+tb;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return uValue;
	}
	
	//变形量
	double param = 0d;
	//变形均值
	double pj = 0d;
	
//	public double getBxjd(){
//		
//	}
		
	public static void main(String[] args) {
		LngLatUtil lu = new LngLatUtil();
		MathExt me = new MathExt(lu.getLongitudeNum("10913.2936005"),
				lu.getLatitudeNum("3422.1204815"),lu.getAltitudeNum("491.603", "-29.011"));
		
		System.out.println(me.getNorthValue());
		System.out.println(me.getEastValue());
		System.out.println(me.getUValue());
	}

}
