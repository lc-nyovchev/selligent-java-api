package com.nyo.selligent.java.api.examples;

import com.nyo.selligent.java.api.SelligentServiceFactory;
import java.util.List;
import javax.xml.ws.Holder;
import org.apache.commons.lang3.StringUtils;
import org.tempuri.ArrayOfInt;
import org.tempuri.ArrayOfListInfo;
import org.tempuri.IndividualSoap;
import org.tempuri.ListInfo;

/**
 *
 * @author nyo
 */
public class Example {
	
	private static final ArrayOfInt ALL_LISTS_ARRAY = new ArrayOfInt();
	
	public static void main(String [] args) {
		String endpoint = "https://yourserver/automation/individual.asmx";
		String username = "yourUserName";
		String password = "yourPassword";
		
		SelligentServiceFactory selligentServiceFactory = new SelligentServiceFactory();
		
		IndividualSoap instance = selligentServiceFactory.getInstance(endpoint, username, password);
		
		List<ListInfo> userLists = getUserLists(instance);
		for ( ListInfo info : userLists ){
			System.out.println(info);
		}
	}
	
	/**
	 * Simple call to list all the user lists
	 * @param instance
	 * @return 
	 */
	private static List<ListInfo> getUserLists(IndividualSoap instance){
		Holder<Integer> result = new Holder<>();
		Holder<ArrayOfListInfo> listsInfo = new Holder<>();
		Holder<String> errorHolder =new Holder<>();
		
		instance.getLists(ALL_LISTS_ARRAY, null, result, listsInfo, errorHolder);
		if ( StringUtils.isNotBlank(errorHolder.value) ){
			throw new RuntimeException("Error while making Selligent call: " + errorHolder.value);
		}
		return listsInfo.value.getListInfo();
	}
}
