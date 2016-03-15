
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiThreadServer {
    private static int port=0;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final int POOL_SIZE=1;
    private static String ip="";
    
    static{
    	try {
			port = Integer.parseInt(PropertiesUtil.getParam("port"));
			ip = PropertiesUtil.getParam("ip");
		} catch (Exception e) {
			//e.printStackTrace();
			System.out.println("port error!");
		}
    }
    
    public MultiThreadServer() throws IOException{
        serverSocket=new ServerSocket();
        serverSocket.bind(new InetSocketAddress(ip,port));
        executorService=Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*50);
    }
    
    public void service(){
        while(true){
            Socket socket=null;
            try {
                socket=serverSocket.accept();
                executorService.execute(new Handler(socket));
                
            } catch (Exception e) {
                //e.printStackTrace();;
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        new MultiThreadServer().service();
    }

}

class Handler implements Runnable{
    private Socket socket;
    public Handler(Socket socket){
        this.socket=socket;
    }
    private PrintWriter getWriter(Socket socket) throws IOException{
        OutputStream socketOut=socket.getOutputStream();
        return new PrintWriter(socketOut,true);
    }
    private BufferedReader getReader(Socket socket) throws IOException{
        InputStream socketIn=socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn));
    }
    public String echo(String msg){
        return "echo:"+msg;
    }
    
    /******************************connpool****************************************/
    public static DbConnectionBroker myBroker = null; 

    static { 
            try { 
                    myBroker = new DbConnectionBroker("com.mysql.jdbc.Driver", 
                                    "jdbc:mysql://localhost:3306/profl?useUnicode=true&characterEncoding=UTF-8", 
                                    "root", "123", 10, 30, 
                                    "d:\\testdb.log", 0.01); 
            } catch (IOException e) { 
                    //e.printStackTrace();; 
            } 
    } 
    
    
    /**
     * getdata
     * @param str
     * @return
     */
    public Map<String,String> getData(String str){
    	Map<String,String> data = new HashMap<String,String>();
    	if(str != null && !("").equals(str)){
    		String[] dataArr = str.split(",");
    		data.put("sysCode", dataArr[0]);
    		data.put("lengthcount",dataArr[1]);
    		data.put("lineCode", dataArr[2]);
    		data.put("towerCode", dataArr[3]);
    		data.put("pointCode", dataArr[4]);
    		
    		data.put("cardState", dataArr[5]);
    		data.put("cardBackcount", dataArr[6]);
    		data.put("wifiBackcount", dataArr[7]);
    		data.put("mcuCount", dataArr[8]);
    		
    		data.put("base_time", dataArr[9]);
    		data.put("bain_day", dataArr[10]);
    		data.put("bain_month", dataArr[11]);
    		data.put("bain_year", dataArr[12]);
    		
    		data.put("latitude_num", dataArr[13]);
    		data.put("latitude_code", dataArr[14]);
    		data.put("longitude_num", dataArr[15]);
    		data.put("longitude_code", dataArr[16]);
    		
    		data.put("position_flag", dataArr[17]);
    		data.put("sate_num", dataArr[18]);
    		data.put("altitude_num", dataArr[19]);
    		data.put("altitude_unit", dataArr[20]);
    		data.put("altitude_sepa", dataArr[21]);
    		
    		data.put("sepa_unit", dataArr[22]);
    		data.put("north_flag", dataArr[23]);
    		data.put("head_flag", dataArr[24]);
    		data.put("earth_speed", dataArr[25]);
    		
    		data.put("espeed_unit", dataArr[26]);
    		data.put("position_model", dataArr[27]);
    		data.put("position_pdop", dataArr[28]);
    		data.put("position_hdop", dataArr[29]);
    		data.put("position_vdop", dataArr[30]);
    		data.put("un_satenum", dataArr[31]);
    		data.put("hori_error", dataArr[32]);
    		data.put("vertical_error", dataArr[33]);
    		data.put("bain_crc", dataArr[34]);
    		String baseCode = data.get("lineCode")+"_"+data.get("towerCode")+"_"+data.get("pointCode");
    		data.put("base_code", baseCode);
    		//data.put("remark", dataArr[34]);
    		//data.put("end_flag", dataArr[36]);
    	}
    	
    	return data;
    }
    
    //得到CRC原始数据
	public String getCRCBaseData(String str) {
		StringBuilder sb = new StringBuilder("");
		if (str != null && !("").equals(str)) {
			String[] dataArr = str.split(",");
			for (int i = 2; i < dataArr.length - 1; i++) {
				sb.append(dataArr[i]).append(",");
			}
		}
		return sb.toString();
	}
    
    public String insertDb(String str){
    	String baseCode = "";
    	try {
    		//CRC校验数据
			String crcBaseData = getCRCBaseData(str);
			
			CrcUtil cu = new CrcUtil();
			String crcValue ="*"+cu.crcValue(crcBaseData);
			
    		 Connection con = myBroker.getConnection(); 
			String sql = "insert into fl_powernet_baseinfo(sys_code,lengthcount,line_code,tower_code,point_code,"
					+ "card_state,card_backcount,wifi_backcount,mcu_count,base_time,"
					+ "bain_day,bain_month,bain_year,latitude_num,latitude_code,"
					+ "longitude_num,longitude_code,position_flag,sate_num,altitude_num,"
					+ "altitude_unit,altitude_sepa,sepa_unit,north_flag,head_flag,"
					+ "earth_speed,espeed_unit,position_model,position_pdop,position_hdop,"
					+ "position_vdop,un_satenum,hori_error,vertical_error,remark,"
					+ "bain_crc,end_flag,base_code)"
					+ " values(?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "?,?,?,?,?,"
					+ "?,?,?)";   
			PreparedStatement pstmt = con.prepareStatement(sql);  
			
			Map<String,String> data = getData(str);
			
			if(crcValue.equals(data.get("bain_crc"))){
			
			//算法计算北东天并且插入fl_data库
			meticData(data);
			
			pstmt.setString(1, data.get("sysCode"));
			pstmt.setString(2, data.get("lengthcount"));
			pstmt.setString(3, data.get("lineCode"));   
			pstmt.setString(4, data.get("towerCode"));   
			pstmt.setString(5, data.get("pointCode"));
    		
			pstmt.setString(6, data.get("cardState"));
			pstmt.setString(7, data.get("cardBackcount"));
			pstmt.setString(8, data.get("wifiBackcount"));   
			pstmt.setString(9, data.get("mcuCount"));  
			
			
			pstmt.setString(10, data.get("base_time"));
			pstmt.setString(11, data.get("bain_day"));
			pstmt.setString(12, data.get("bain_month"));   
			pstmt.setString(13, data.get("bain_year"));   
			pstmt.setString(14, data.get("latitude_num"));  
    		
			pstmt.setString(15, data.get("latitude_code"));   
			pstmt.setString(16, data.get("longitude_num"));   
			pstmt.setString(17, data.get("longitude_code"));   
			pstmt.setString(18, data.get("position_flag"));   
			pstmt.setString(19, data.get("sate_num"));   
			pstmt.setString(20, data.get("altitude_num")); 
			pstmt.setString(21, data.get("altitude_unit"));
			
			pstmt.setString(22, data.get("altitude_sepa"));
			pstmt.setString(23, data.get("sepa_unit"));
			pstmt.setString(24, data.get("north_flag"));
			pstmt.setString(25, data.get("head_flag"));
			pstmt.setString(26, data.get("earth_speed"));
			
			pstmt.setString(27, data.get("espeed_unit"));
			pstmt.setString(28, data.get("position_model"));
			pstmt.setString(29, data.get("position_pdop"));
			pstmt.setString(30, data.get("position_hdop"));
			pstmt.setString(31, data.get("position_vdop"));
			
			pstmt.setString(32, data.get("un_satenum"));
			pstmt.setString(33, data.get("hori_error"));
			pstmt.setString(34, data.get("vertical_error"));
			
			pstmt.setString(35, data.get("remark"));
			pstmt.setString(36, data.get("bain_crc"));
			pstmt.setString(37, data.get("end_flag"));
			//唯一标识编号线+塔+点
			baseCode = data.get("lineCode")+"_"+data.get("towerCode")+"_"+data.get("pointCode");
			
			//查看该节点是否已经存在没有的话插入到菜单数据
			boolean flag = isExist(data);
			
			//插入全局值(开始时间)
			if(flag == true){
				insertGlobalinfo(data);
			}
			updateState("0",baseCode);
			
			pstmt.setString(38, baseCode);
			pstmt.executeUpdate();
			}
			pstmt.close(); 
            myBroker.freeConnection(con);
            

		} catch (Exception e) {
			//e.printStackTrace();;
			System.out.println("MYSQL INFO:" + e.getMessage());
		}
    	return baseCode;
    }
    
    public boolean isExist(Map<String,String> data){
    	boolean flag = true;
    	try {
    		//统计基础站点数据到菜单表
            Connection con = myBroker.getConnection(); 
    		String sql = "select count(1)  ct from fl_powernet_baseinfo t where t.sys_code='"+data.get("sysCode")+"' and "
    				+ "t.line_code='"+data.get("lineCode")+"' and t.tower_code='"+data.get("towerCode")+"' and "
    				+ "t.point_code='"+data.get("pointCode")+"'";   
    		PreparedStatement pstmt = con.prepareStatement(sql);  
    		
    		ResultSet rs = pstmt.executeQuery(sql);
    		//处理结果
            while(rs.next()){
                String ct = rs.getString("ct");
                if(ct != null && Integer.parseInt(ct)>0){
                	flag = false;
                }
            }
            pstmt.close(); 
            myBroker.freeConnection(con);
            
            if(flag){
            	insertLocalInfo(data);
            }
		} catch (Exception e) {
			//e.printStackTrace();;
		}
    	return flag;
    }
 
    public void meticData(Map<String,String> data){
    	/******************************算法开始*************************************/
    	//纬度
    	String latitude_num = data.get("latitude_num");
    	//经度
    	String longitude_num = data.get("longitude_num");
    	//高度
    	String altitude_num = data.get("altitude_num");
    	//分离度
    	String altitude_sepa = data.get("altitude_sepa");
    	
      	double northNum = 0d;
    	double eastNum = 0d;
    	double udayNum = 0d;
    	
    	//水平距离
		double dist = 0d;
		//三维距离
		double tdist = 0d; 
    	
		data.put("type", "0");
    	data.put("tsort", "0");
    	updateData(data);
    	
    	LngLatUtil lngLatUtil = new LngLatUtil();
    	if(latitude_num != null && longitude_num != null && altitude_num != null && altitude_sepa!=null){
    		double latitude = lngLatUtil.getLatitudeNum(latitude_num);
    		double longitude = lngLatUtil.getLongitudeNum(longitude_num);
    		double altitude = lngLatUtil.getAltitudeNum(altitude_num,altitude_sepa);
    		MathExt me = new MathExt(longitude,latitude,altitude);
    		northNum = me.getNorthValue();
    		eastNum = me.getEastValue();
    		udayNum = me.getUValue();
    		dist = Math.sqrt(northNum*northNum+eastNum*eastNum);
    		tdist = me.get3Dist();
    	}
    	
    	//北-站心坐标
    	data.put("yaxis", String.valueOf(northNum));
    	data.put("xdate", data.get("base_time"));
    	data.put("change_avalue", "88");
    	data.put("change_shot", "67");
    	//变形量和变形均值
    	Map<String,String> indata = selectFlData(data);
    	if(indata.get("inum") == null){
    		data.put("inum","1");
    		data.put("mean",String.valueOf(northNum));
    		data.put("deflection", "0"); 
    		data.put("defmean", "0"); 
    		//监测精度
    		data.put("depepre", "0");
    		data.put("change_avalue", "0");
    		data.put("change_shot", "0");
    	}else{
    		int i = Integer.parseInt(indata.get("inum"))+1;
    		data.put("inum",String.valueOf(i));
    		double mean = ((i-1)*Double.parseDouble(indata.get("mean"))+northNum)/i;
    		data.put("mean",String.valueOf(mean));
    		double deflection = northNum - Double.parseDouble(indata.get("mean"));
    		data.put("deflection",String.valueOf(deflection*100));
    		//变形量均值
    		double defmean = ((i-1)*Double.parseDouble(indata.get("defmean"))+deflection*100)/i;
    		data.put("defmean", String.valueOf(defmean));
    		//监测精度
    		double ddepepre = (i-1)*Math.pow(Double.parseDouble(indata.get("depepre")),2)+Math.pow((deflection*100-Double.parseDouble(indata.get("defmean"))),2);
    		double depepre = Math.sqrt(ddepepre/i);
    		data.put("depepre", String.valueOf(depepre));
    		
    		//水平距离
    		data.put("change_avalue", String.valueOf(dist));
    		//三维距离
    		data.put("change_shot", String.valueOf(tdist));
    	}
    	insertData(data);
    	
    	
    	//东-站心坐标
    	data.put("type", "1");
    	data.put("tsort", "0");
    	updateData(data);
    	
    	data.put("yaxis", String.valueOf(eastNum));
    	data.put("xdate", data.get("base_time"));
    	data.put("change_avalue", "88");
    	data.put("change_shot", "67");
    	//变形量和变形均值
    	Map<String,String> iedata = selectFlData(data);
    	if(iedata.get("inum") == null){
    		data.put("inum","1");
    		data.put("mean",String.valueOf(eastNum));
    		data.put("deflection", "0"); 
    		data.put("defmean", "0"); 
    		//监测精度
    		data.put("depepre", "0");
    		data.put("change_avalue", "0");
    		data.put("change_shot", "0");
    	}else{
    		int i = Integer.parseInt(iedata.get("inum"))+1;
    		data.put("inum",String.valueOf(i));
    		double mean = ((i-1)*Double.parseDouble(iedata.get("mean"))+eastNum)/i;
    		data.put("mean",String.valueOf(mean));
    		double deflection = eastNum - Double.parseDouble(iedata.get("mean"));
    		data.put("deflection",String.valueOf(deflection*100));
    		//变形量均值
    		double defmean = ((i-1)*Double.parseDouble(iedata.get("defmean"))+deflection*100)/i;
    		data.put("defmean", String.valueOf(defmean));
    		//监测精度
    		double ddepepre = (i-1)*Math.pow(Double.parseDouble(iedata.get("depepre")),2)+Math.pow((deflection*100-Double.parseDouble(iedata.get("defmean"))),2);
    		double depepre = Math.sqrt(ddepepre/i);
    		data.put("depepre", String.valueOf(depepre));
    		
    		//水平距离
    		data.put("change_avalue", String.valueOf(dist));
    		//三维距离
    		data.put("change_shot", String.valueOf(tdist));
    	}
    	insertData(data);
 
    	
    	//天-站心坐标
    	data.put("type", "2");
    	data.put("tsort", "0");
    	updateData(data);
    	
    	data.put("yaxis", String.valueOf(udayNum));
    	data.put("xdate", data.get("base_time"));
    	data.put("change_avalue", "88");
    	data.put("change_shot", "67");
    	Map<String,String> iudata = selectFlData(data);
    	//变形量和变形均值
    	if(iudata.get("inum") == null){
    		data.put("inum","1");
    		data.put("mean",String.valueOf(udayNum));
    		data.put("deflection", "0"); 
    		data.put("defmean", "0"); 
    		//监测精度
    		data.put("depepre", "0");
    		data.put("change_avalue", "0");
    		data.put("change_shot", "0");
    	}else{
    		int i = Integer.parseInt(iudata.get("inum"))+1;
    		data.put("inum",String.valueOf(i));
    		double mean = ((i-1)*Double.parseDouble(iudata.get("mean"))+udayNum)/i;
    		data.put("mean",String.valueOf(mean));
    		double deflection = udayNum - Double.parseDouble(iudata.get("mean"));
    		data.put("deflection",String.valueOf(deflection*100));
    		//变形量均值
    		double defmean = ((i-1)*Double.parseDouble(iudata.get("defmean"))+deflection*100)/i;
    		data.put("defmean", String.valueOf(defmean));
    		//监测精度
    		double ddepepre = (i-1)*Math.pow(Double.parseDouble(iudata.get("depepre")),2)+Math.pow((deflection*100-Double.parseDouble(iudata.get("defmean"))),2);
    		double depepre = Math.sqrt(ddepepre/i);
    		data.put("depepre", String.valueOf(depepre));
    		
    		//水平距离
    		data.put("change_avalue", String.valueOf(dist));
    		//三维距离
    		data.put("change_shot", String.valueOf(tdist));
    	}
    	insertData(data);
    	/******************************算法结束*************************************/
    }
    
    public Map<String,String> selectFlData(Map<String,String> data){
    	Map<String,String> idata = new HashMap<String,String>(); 
        	try {
        		//统计基础站点数据到菜单表
                Connection con = myBroker.getConnection(); 
        		String sql = "select t.inum as inum,t.mean as mean,t.defmean as defmean,t.depepre as depepre"
        				+ " from fl_data t where t.type='"+data.get("type")+"' and "
        				+ "t.base_code='"+data.get("base_code")+"' ORDER BY CREATE_date desc limit 1";   
        		PreparedStatement pstmt = con.prepareStatement(sql);  
        		ResultSet rs = pstmt.executeQuery(sql);
        		boolean flag = true;
        		//处理结果
                while(rs.next()){
                    String inum = rs.getString("inum");
                    String mean = rs.getString("mean");
                    String defmean = rs.getString("defmean");
                    String depepre = rs.getString("depepre");
                    idata.put("inum", inum);
                    idata.put("mean", mean);
                    idata.put("defmean", defmean);
                    idata.put("depepre", depepre);
                }
                pstmt.close(); 
                myBroker.freeConnection(con);
    		} catch (Exception e) {
    			//e.printStackTrace();;
    		}
        	return idata;
        }
    
	public void insertGlobalinfo(Map<String, String> data) {
		try {
			Connection con = myBroker.getConnection();
			String sql = "insert into fl_globalinfo(start_time,create_time,base_code) values(?,?,?)";
			PreparedStatement pstmt = con.prepareStatement(sql);

			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

			String strt = data.get("base_time");
			String str = data.get("bain_year") + "-" + data.get("bain_month") + "-" + data.get("bain_day") + " "
					+ strt.substring(0, 2) + ":" + strt.substring(2, 4) + ":" + strt.substring(4, 6);
			pstmt.setString(1, str);
			pstmt.setString(2, sdf.format(date));
			// 唯一标识编号线+塔+点
			String baseCode = data.get("lineCode") + "_" + data.get("towerCode") + "_" + data.get("pointCode");
			pstmt.setString(3, baseCode);
			pstmt.executeUpdate();
			pstmt.close();
			myBroker.freeConnection(con);
		} catch (Exception e) {
			//e.printStackTrace();;
		}
	}
	
    public void insertData(Map<String,String> data){
    		try {
       		 Connection con = myBroker.getConnection(); 
    			String sql = "insert into fl_data(yaxis,xdate,type,tsort,change_avalue,change_shot,"
    					+ "create_date,flag,base_code,sys_code,inum,mean,deflection,defmean,depepre,fdatetime,iflag,card_state,position_flag)"
    					+ " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";   
    			PreparedStatement pstmt = con.prepareStatement(sql);  
    			
    			Date date = new Date(); 
    			SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); 
    			
    			pstmt.setString(1, data.get("yaxis"));
    			pstmt.setString(2, data.get("xdate"));   
    			pstmt.setString(3, data.get("type"));
    			
    			pstmt.setString(4, data.get("tsort"));
    			pstmt.setString(5, data.get("change_avalue"));   
    			pstmt.setString(6, data.get("change_shot"));  
    			
    			pstmt.setString(7, sdf.format(date));
    			pstmt.setString(8, "0");   
    			pstmt.setString(10, data.get("sysCode"));
    			//唯一标识编号线+塔+点
	   			String baseCode = data.get("lineCode")+"_"+data.get("towerCode")+"_"+data.get("pointCode");
	   			pstmt.setString(9, baseCode);
	   			pstmt.setString(11, data.get("inum"));
	   			pstmt.setString(12, data.get("mean"));
	   			pstmt.setString(13, data.get("deflection"));
	   			pstmt.setString(14, data.get("defmean"));
	   			pstmt.setString(15, data.get("depepre"));
	   			
	   			String strt = data.get("base_time");
				String str = data.get("bain_year") + "-" + data.get("bain_month") + "-" + data.get("bain_day") + " "
						+ strt.substring(0, 2) + ":" + strt.substring(2, 4) + ":" + strt.substring(4, 6);
				pstmt.setString(16, str);
				pstmt.setString(17, "1");
				pstmt.setString(18, data.get("cardState"));
				pstmt.setString(19, data.get("position_flag"));
    			pstmt.executeUpdate();
    			pstmt.close(); 
                myBroker.freeConnection(con);
   		} catch (Exception e) {
   			//e.printStackTrace();;
   		}
    }
    
    public void updateData(Map<String,String> data){
    	Long start = System.currentTimeMillis();
		try {
			//唯一标识编号线+塔+点
   			String baseCode = data.get("lineCode")+"_"+data.get("towerCode")+"_"+data.get("pointCode");
   			Connection cons = myBroker.getConnection(); 
			String sql = "UPDATE fl_data  SET iflag='2' where iflag='1' and type=? and tsort=? and base_code=?"; 
			PreparedStatement pstmt = cons.prepareStatement(sql);  
			pstmt.setString(1, data.get("type"));
			pstmt.setString(2, data.get("tsort"));
			pstmt.setString(3, baseCode);
			pstmt.executeUpdate();
			pstmt.close(); 
            myBroker.freeConnection(cons);
		} catch (Exception e) {
			//e.printStackTrace();;
		}
		Long end = System.currentTimeMillis(); 
        System.out.println("单条执行----------->共耗时：" + (end - start) / 1000f + "----毫秒！");
}
    
    public void insertLocalInfo(Map<String,String> data){
    	try {
    		 Connection con = myBroker.getConnection(); 
 			String sql = "insert into fl_powernet_localinfo(sys_code,line_code,tower_code,point_code,base_code,states)"
 					+ " values(?,?,?,?,?,?)";   
 			PreparedStatement pstmt = con.prepareStatement(sql);  
 			
 			pstmt.setString(1, data.get("sysCode"));
 			pstmt.setString(2, data.get("lineCode"));   
 			pstmt.setString(3, data.get("towerCode"));   
 			pstmt.setString(4, data.get("pointCode"));
 			//唯一标识编号线+塔+点
			String baseCode = data.get("lineCode")+"_"+data.get("towerCode")+"_"+data.get("pointCode");
			pstmt.setString(5, baseCode);
			pstmt.setString(6, "0");
 			pstmt.executeUpdate();
 			pstmt.close(); 
             myBroker.freeConnection(con);
		} catch (Exception e) {
			//e.printStackTrace();;
		}
    }
    
    public void updateState(String state,String baseCode){
    	try {
    		Connection con = myBroker.getConnection(); 
 			String sql = "UPDATE fl_powernet_localinfo SET states=? where base_code=?";   
 			PreparedStatement pstmt = con.prepareStatement(sql);  
 			pstmt.setString(1, state);
 			pstmt.setString(2, baseCode);
 			pstmt.executeUpdate();
 			pstmt.close(); 
            myBroker.freeConnection(con);
		} catch (Exception e) {
			//e.printStackTrace();;
		}
    }
    
    public void writeFile(String content){
    try {
    	   File file = new File("d:\\Result.txt");
    	   // if file doesnt exists, then create it
    	   if (!file.exists()) {
    	    file.createNewFile();
    	   }
    	   //SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
           FileOutputStream out=new FileOutputStream(file,true);    
           StringBuffer sb=new StringBuffer();
           sb.append(content+"\n");
           out.write(sb.toString().getBytes("utf-8"));
           out.close();
    	  } catch (IOException e) {
    	   //e.printStackTrace();;
    	  }
    
    }
    
    public void run(){
    	String msg=null;
    	String strs = "";
    	String addr = "";
        try {
        	socket.setTcpNoDelay(true);
        	socket.setReceiveBufferSize(172);
        	socket.setSendBufferSize(172);
            BufferedReader br=getReader(socket);
            System.out.println(Thread.currentThread().getName());
            System.out.println("New connection accepted "+socket.getInetAddress()+":"+socket.getPort()+socket.getSoTimeout()+"hc"+socket.hashCode());
            addr = socket.getInetAddress().toString();
            while((msg=br.readLine())!=null){
            	strs = insertDb(msg);
            	writeFile(msg);
                //pw.println(echo(msg));
            	socket.setTcpNoDelay(true);
            	socket.setReceiveBufferSize(172);
            	socket.setSendBufferSize(172);
            	socket.setKeepAlive(true);
            	socket.setSoTimeout(15000);
            }
        } catch (IOException e) {
        	updateState("1",strs);
        	System.out.println(addr+"-断开连接，更新成功");
        }finally{
            try {
            		//updateState("1",strs);
                	socket.close();
                	System.out.println("socket.isClosed()"+socket.isClosed());
            } catch (IOException e) {
                //e.printStackTrace();;
            }
        }
    }
}