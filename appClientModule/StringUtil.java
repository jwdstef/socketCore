

public class StringUtil {

	/**
	 * 字符串转ASCII
	 * @param value
	 * @return
	 */
	public static String stringToAscii(String value)
	{
		StringBuffer sbu = new StringBuffer();
		char[] chars = value.toCharArray(); 
		for (int i = 0; i < chars.length; i++) {
			if(i != chars.length - 1)
			{
				sbu.append((int)chars[i]).append(",");
			}
			else {
				sbu.append((int)chars[i]);
			}
		}
		return sbu.toString();
	}
	
	/**
	 * ASCII转字符串
	 * @param value
	 * @return
	 */
	public static String asciiToString(String value)
	{
		StringBuffer sbu = new StringBuffer();
		
		String[] chars = value.split(",");
		for (int i = 0; i < chars.length; i++) {
			sbu.append(chars[i]);
		}
		return sbu.toString();
	}
    
	public static void main(String[] args) {
		String str="\u0024\u0047\u0047\u004d\u0053\u002c\u0030\u0030\u0035\u002c\u0041\u002c\u0030\u0039\u0034\u0038\u0031\u0033\u002e\u0036\u0034\u0030\u002c\u0030\u0035\u0031\u0031\u0031\u0035\u002c\u0033\u0031\u0035\u0038\u002e\u0034\u0036\u0030\u0038\u002c\u004e\u002c\u0031\u0031\u0038\u0034\u0038\u002e\u0033\u0037\u0033\u0037\u002c\u0045\u002c\u0030\u0034\u0035\u0033\u002e\u0032\u0033\u002c\u0032\u0030\u002c\u0030\u002e\u0030\u0030\u002c\u004b\u002a\u0050\u0050";
		System.out.println(asciiToString(str));
	}

}
