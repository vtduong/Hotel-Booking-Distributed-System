package frontend;

import java.io.IOException;

import ipconfig.IPConfig;
import vspackage.bean.Header;

public class MulticastRM {
	
	private Header header;
	
	MulticastRM(Header header) {
		
		this.header = header;
	}
	
	public void multicast() throws NumberFormatException, IOException {
		
		int totalRM = Integer.parseInt(IPConfig.getProperty("total_rm")); 
		
		String rm_one_addr = IPConfig.getProperty("rm_one");
		String rm_two_addr = IPConfig.getProperty("rm_two");
		String rm_three_addr = IPConfig.getProperty("rm_three");
		String rm_four_addr = IPConfig.getProperty("rm_four");
		
		int rm_one_port = Integer.parseInt(IPConfig.getProperty("port_rm_one"));
		int rm_two_port = Integer.parseInt(IPConfig.getProperty("port_rm_two"));
		int rm_three_port = Integer.parseInt(IPConfig.getProperty("port_rm_three"));
		int rm_four_port = Integer.parseInt(IPConfig.getProperty("port_rm_four"));
		
		
		UnicastRM unicastOne = new UnicastRM(rm_one_addr, rm_one_port, header);
		UnicastRM unicastTwo = new UnicastRM(rm_two_addr, rm_two_port, header);
		UnicastRM unicastThree = new UnicastRM(rm_three_addr, rm_three_port, header);
		UnicastRM unicastFour = new UnicastRM(rm_four_addr, rm_four_port, header);
		
		unicastOne.unicast();
		unicastTwo.unicast();
		unicastThree.unicast();
		unicastFour.unicast();
		
		
	}
}

