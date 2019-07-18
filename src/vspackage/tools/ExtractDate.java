package vspackage.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ExtractDate {
	
	private List<String> eventID;
	
	public ExtractDate(List<String> eventID) {
		this.eventID = eventID;
	}
	
	public List<Date> getDate() throws ParseException {
		List<Date> dates = new ArrayList<Date>();
		
		for(String str : eventID) {
			
			String unformatted = str.substring(4, str.length());
			String day = unformatted.substring(0,2);
			String month = unformatted.substring(2,4);
			String year = unformatted.substring(4, unformatted.length());
			
			String date = day + "/" + month + "/" + year;
			
			Date obj = new SimpleDateFormat("dd/MM/yyyy").parse(date);
			
			dates.add(obj);
		}
		
		return dates;
	}
	
	
	public List<Date> getSortedDate() throws ParseException {
		
		List temp = getDate();
		Collections.sort(temp);
		return temp;
	}
	
	public int dateDiff() throws ParseException {
		List<Date> date = getSortedDate();
		int diffInDays = (int)( (date.get(date.size() - 1).getTime() - date.get(0).getTime()) 
                / (1000 * 60 * 60 * 24) );
		
		return diffInDays;
	}
	
}
