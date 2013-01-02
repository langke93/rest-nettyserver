package org.langke.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期转换工具
 */
public class DateUtil
{
    private static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String FORMAT_DATE = "yyyy-MM-dd";
    
    public static boolean isDate(String date){
    	return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
    
    public static String getCurrentDateStr(){
    	return getCurrentDateStr(FORMAT_DATETIME);
    }
    
    public static String getDatetimeStr(long datetime){
    	return format(new Date(datetime),FORMAT_DATETIME);
    }
    
    public static String getDateStr(long datetime){
    	return format(new Date(datetime),FORMAT_DATE);
    }
	
    public static Date getCurrentDate(){
	     Calendar cal = Calendar.getInstance();
	     Date currDate = cal.getTime();
	     return currDate;
    }

    /**
	 * param   2010-09-01
	 */
    public static Date parseStringToDate(String date_str){
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE);
		Date date = null;
		try {
			date = sdf.parse(date_str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
    /**
     * param   2010-09-01 19:29:10
     */
    public static Date parseStringToDateTime(String date_str){
    	SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATETIME);
    	Date date = null;
    	try {
    		date = sdf.parse(date_str);
    	} catch (ParseException e) {
    		e.printStackTrace();
    	}
    	return date;
    }
	
    private static String getCurrentDateStr(String strFormat){
	     Calendar cal = Calendar.getInstance();
	     Date currDate = cal.getTime();
	     return format(currDate, strFormat);
	 }

    private static String format(Date aTs_Datetime, String as_Pattern){
      if (aTs_Datetime == null || as_Pattern == null){
    	  return null;
      }
      SimpleDateFormat dateFromat = new SimpleDateFormat(as_Pattern);
      return dateFromat.format(aTs_Datetime);
    }
	public static void main(String[] args){
		System.out.println(getCurrentDateStr());
		System.out.println(parseStringToDateTime(getCurrentDateStr()).getTime());
		System.out.println(getDatetimeStr(System.currentTimeMillis()));
        System.out.println(getCurrentDateStr("yyyy.MM.dd HH:mm:ss"));
        System.out.println(format(new Date(System.currentTimeMillis()),"yyyy.MM.dd HH:mm:ss"));
	 }
} 


