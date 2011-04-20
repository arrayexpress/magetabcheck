/**
* The contents of this file are subject to the MIT License
* (the "License");  you may not use this file except in
*  compliance with the License.  You may obtain a copy of the License  at
* http://www.opensource.org/licenses/mit-license.php 
*
* Software distributed under the License is distributed on an "AS IS" basis,
* WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
* the specific language governing rights and limitations under the License.
*
* Authors are listed at http://code.google.com/p/annotare/people/list
*
* @author Joseph White; Dana-Farber Cancer Institute, Boston MA, USA
* @date 16-Jun-2010
* 
*/
package org.mged.annotare.validator;

import java.util.Date;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ListIterator;
import java.util.Vector;
import java.util.Hashtable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import java.io.IOException;
import org.mged.magetab.error.*;
//import org.mged.magetab.error.ErrorItem;
import org.tigr.microarray.mev.file.StringSplitter;

public class AnnotareError implements AnnotareErrorInterface {

	private Hashtable itemErrorCodeList;	//list of itemNum and error code for each item;
											//validationErrors and itemErrorCodeList MUST be tightly coupled.
	public Vector errorsFound;	//list of ErrorCodes observered during validation
	public ArrayList<ErrorItem> validationErrors;		//list of errors found during validation
	public ArrayList<ErrorItem> validationWarnings;		//list of warnings found during validation
	public ArrayList<ErrorItem> validationIgnoreList;		//list of items ignored during validation
	public Hashtable<Integer,String> eCodes;				//list of error codes and messages
	public Hashtable<Integer,String> eTypes;				//list of error codes and error types

	int itemNum;							//identifier for item creation
	
	/*
	 * Note: it may be necessary to change itemErroCodeList to 
	 * HashMap<Integer,Integer[]> in order to collect the list if 
	 * itemNum having errorCode.  Its also possible to dispense with 
	 * the hash entirely.   
	 * 
	 * getErrorsByType needs to return ErrorItem objects by errorCode,
	 * but AnnotareValidator needs to do this based on ErrorType first.
	 * Perhaps just keep a list of observed ErrorCode values.  That could
	 * help looping.   
	 */
	
	/**
	 * Constructor initializes the data members
	 * 
	 * The AnnotareError object should remain static in the application
	 * 
	 */
	public AnnotareError () {
		validationErrors = new ArrayList<ErrorItem>();
		validationWarnings = new ArrayList<ErrorItem>();
		validationIgnoreList = new ArrayList<ErrorItem>();
		itemErrorCodeList = new Hashtable();
		errorsFound = new Vector();
		itemNum = 0;
	}

	/**
	 * readErrorCodeList
	 * 
	 *  Reads error code properties file and imports semantic rules 
	 *  into a hash.
	 *  @param: properties file name, pfile
	 *  @return: error code hash, eCodes
	 *  NOTE: we need at least 3 items in the properties list, but
	 *  I don't recall the specific data items.  Will have to read 
	 *  though the email exchange.
	 */
	public Hashtable readErrorCodeList (String pfilename) {
		Hashtable<Integer, String> eCodes = new Hashtable<Integer, String>();
		File pfile = new File(pfilename);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(pfile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String currentLine = "";
		try{
			int ctr = 0;
			while ((currentLine = br.readLine()) != null) {
				StringSplitter ss = new StringSplitter((char)0x09);
				ss.init(currentLine);
				ctr++;
            	ss.nextIntToken();
	            while(ss.hasMoreTokens()) {
	            	int code = Integer.parseInt(ss.nextToken());
	            	String mesg = ss.nextToken();
	            	String prop = ss.nextToken();
	            	String eType = ss.nextToken();	//'error', 'warning', 'missing'
	            	if(! eCodes.containsKey(code)) {
	            		eCodes.put(code, mesg);
	            	}
	            }
			}
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		try{
			br.close();
		} catch (FileNotFoundException e) {
		} catch (IOException ioe) {
			//do nothing here
		}
		return eCodes;
	}
	
	/**
	 * write ErrorCode list to file
	 * 
	 */
	public void writeErrorCodeList (String outFileName) throws IOException {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(outFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		bw.write("AnnotareValidator error codes\n");
		for(ErrorCode e: ErrorCode.values() ) {		
			int code = e.getIntegerValue();
			String mesg = e.getErrorMessage();
			bw.write(code + "\t" + mesg);
			bw.append("\n");
		}
		bw.close();
	}
	
/**
 * Create an error object 
 * @return
	public ErrorItem createItem (String inFile, int inErrorNum, int line, 
			int col, String caller) {
		itemNum++;
		String mesg = (String) errorMesgs.get(inErrorNum);
		
		ErrorItem e = new ErrorItem(itemNum, inFile, inErrorNum, line, 
				col, mesg, caller);
		validationErrors.add(itemNum,e);
		//for fast searching by error number
		itemErrorCodeList.put(itemNum,inErrorNum);
		//must set up if-else block to set errorType
		//e.errorType = "";
		return e;
	}
 */
	
	/**
	 * Convert ErrorItem objects to XML formatted string.
	 * 
	 * @Param:  ArrayList<ErrorItem> list of ErrorItem objects
	 * @return: String containing XML representation of the errors
	 */
	public String formErrorXML (ArrayList<ErrorItem> errorList) {
		StringBuffer tmpString = new StringBuffer();
		String xmlString = "";
		//Start forming XML with the XML declaration and DTD
		tmpString.append("<?xml version='1.0' encoding='UTF-8'?>\n");
		tmpString.append("<!DOCTYPE ErrorItemList [ \n");
		tmpString.append("<!ELEMENT ErrorItemList (ErrorItem*) >\n");
		tmpString.append("<!ELEMENT ErrorItem EMPTY >\n");
		tmpString.append("<!ATTLIST ErrorItem_Attrs \n");
		tmpString.append("	itemId CDATA #REQUIRED \n");
		tmpString.append("	errorCode CDATA #REQUIRED \n");
		tmpString.append("	errorType CDATA #REQUIRED \n");
		tmpString.append("	errorFile CDATA #IMPLIED \n");
		tmpString.append("	errorLine CDATA #IMPLIED \n");
		tmpString.append("	errorCol CDATA #IMPLIED \n");
		tmpString.append("	errorMsg CDATA #REQUIRED \n");
		tmpString.append("	errorCaller CDATA #REQUIRED \n");
		tmpString.append("	errorComment CDATA #IMPLIED \n");
		tmpString.append("	errorLink CDATA #IMPLIED \n");
		tmpString.append("> ]> \n");
		//root tag
		tmpString.append("<ErrorItemList> \n");
		//Now loop over the error list and make one ErrorItem tag for each object
		for(ErrorItem e: errorList) {
			//opening tag
			tmpString.append("<ErrorItem ");

			tmpString.append("itemId=\"" + e.getItemId() + "\" ");
			tmpString.append("errorCode=\"" + e.getErrorCode() + "\" ");
			tmpString.append("errorType=\"" + e.getErrorType() + "\" ");
			tmpString.append("errorFile=\"" + e.getParsedFile() + "\" ");
			tmpString.append("errorLine=\"" + e.getLine() + "\" ");
			tmpString.append("errorCol=\"" + e.getCol() + "\" ");
			tmpString.append("errorMsg=\"" + e.getMesg() + "\" ");
			tmpString.append("errorCaller=\"" + e.getCaller() + "\" ");
			tmpString.append("errorComment=\"" + e.getComment() + "\" ");		
			tmpString.append("errorLink=\"" + e.getLink() + "\" ");		
			tmpString.append(">\n");		
			//closing tag
			tmpString.append("</ErrorItem>\n");
		}
		//close root tag
		tmpString.append("</ErrorItemList> \n");			
		xmlString = tmpString.toString();
		return xmlString;
	}
	
	/**
	 * Adds an ErrorItem object to the validationErrors list
	 * The object is obtained from the parser via a listener callback
	 */
	public void addErrorItem (ErrorItem item) {
		int id = ++itemNum;
		item.setItemId(id);
//		System.out.println("Item id: " + id + "; Error code: " + item.getErrorCode());
		int ec = (Integer) item.getErrorCode();
		//store the item number and error code for each event
		itemErrorCodeList.put(id,ec);
		//keep track of the types of errors found
		if(! errorsFound.contains(ec)) {
			errorsFound.add(ec);
		}
//		System.out.println(item.toString());
		validationErrors.add(item);
	}
	
	/**
	 *  Adds an ErrorItem object to the validationWarnings list
	 * The object is obtained from the parser via a listener callback
	 */
	public void addWarningItem (ErrorItem item) {
		int id = ++itemNum;
		item.setItemId(id);
		int ec = (Integer) item.getErrorCode();
		//store the item number and error code for each event
		itemErrorCodeList.put(id,ec);
		//keep track of the types of errors found
		if(! errorsFound.contains(ec)) {
			errorsFound.add(ec);
		}
		validationWarnings.add(item);
	}
	
	
	/**
	 *  Adds an ErrorItem object to the validationWarnings list
	 * The object is obtained from the parser via a listener callback
	 */
	public void addToIgnoreList (ErrorItem item) {
		int id = ++itemNum;
		item.setItemId(id);
		int ec = (Integer) item.getErrorCode();
		//store the item number and error code for each event
		itemErrorCodeList.put(id,ec);
		//keep track of the types of errors found
		if(! errorsFound.contains(ec)) {
			errorsFound.add(ec);
		}
		validationIgnoreList.add(item);
	}
	
	/**
	 * 
	 * parseAnnotareData
	 * create a list of ErrorItem objects based on objects supplied by the main Annotare application
	 * Input: report object
	 * @return: ArrayList of ErrorItem having itemNum appropriately generated by ErrorItem
	 * Note: can change type of report once we know the type of Annotare objects supplied to the method or cast
	 */
	public ArrayList<ErrorItem> parseAnnotareData (Object report) {
		ArrayList<ErrorItem> items = new ArrayList<ErrorItem>();
		
		//Parse report object and create ErrorItem objects
		//while(...) {
		//	parse file, errorNum, line, col, mesg from input
		//	ErrorItem e = new ErrorItem(++itemNum, file, errorNum, line, col, mesg, "parseAnnotareData");
		//	validationErrors.add(itemNum,e);
		//	itemErrorCodeList.add(itemNum,errorNum);
		//	items.add(e);
		//}
		return items;
	}
	
	/**
	 * removeError
	 * overloaded to remove item based on index or the object itself
	 * @param errIndex: validationErrors index
	 * @param e: error object
	 * @return true if removed
	 */
	public boolean removeError(int itemNum) {
		boolean done = false;
		for(int i=0; i< validationErrors.size();i++) {
			ErrorItem item = validationErrors.get(i);
			if(item.getItemId() == itemNum) {
				validationErrors.remove(i);
				itemErrorCodeList.remove(itemNum);
				done = true;
				break;
			}
		}
		for(int i=0; i< validationWarnings.size();i++) {
			ErrorItem item = validationWarnings.get(i);
			if(item.getItemId() == itemNum) {
				validationWarnings.remove(i);
				itemErrorCodeList.remove(itemNum);
				done = true;
				break;
			}
		}
		return done;
	}
	public boolean removeError(ErrorItem e) {
		if(validationErrors.contains(e) == true) {
			int num = e.getItemId();
			validationErrors.remove(e);
			itemErrorCodeList.remove(num);			
			return true;
		} else if(validationWarnings.contains(e) == true) {
				int num = e.getItemId();
				validationWarnings.remove(e);
				itemErrorCodeList.remove(num);			
				return true;
		} else {
			return false;
		}		
	}

	/**
	 * Clears all items from validationErrors and validationWarnings 
	 * and set itemId to 0
	 * 
	 */
	protected void clearAllItems () {
		validationErrors.clear();
		validationWarnings.clear();
		validationIgnoreList.clear();
		itemErrorCodeList.clear();
		errorsFound.clear();
		this.itemNum = 0;
	}
	
	/**
	 * getItem: returns specific error object based on unique identifier
	 * 
	 * @param key: int representing the item id
	 * var. index: index into the validationErrors Vector
	 * @return: ErrorItem object
	 */
	public ErrorItem getItem (int key) {
		ErrorItem item = null;
		for(int i=0; i< validationErrors.size();i++) {
			ErrorItem tmpItem = (ErrorItem) validationErrors.get(i);
			if(tmpItem.getItemId() == key) {
				item = tmpItem;
				break;
			}
		}
		if(item == null) {
			for(int i=0; i< validationWarnings.size();i++) {
				ErrorItem tmpItem = (ErrorItem) validationWarnings.get(i);
				if(tmpItem.getItemId() == key) {
					item = tmpItem;
					break;
				}
			}
		}
		return item;
	}

	/**
	 * getErrorType
	 * 
	 * Note: this will likely be an expensive method to run, so 
	 * it should not be run frequently.
	 * 
	 * @param errNum
	 * @return Vector of ErrorItem containing all instances of errNum 
	 */
	public ArrayList<ErrorItem> getErrorsByType(int inErrCode) {
		ArrayList<ErrorItem> errList = new ArrayList();
		ArrayList<ErrorItem> tmpList = new ArrayList(); 
		tmpList.addAll(validationErrors);
		tmpList.addAll(validationWarnings);
		for(int i=0; i< tmpList.size(); i++) {
			ErrorItem e = tmpList.get(i);
			if(e.getErrorCode() == inErrCode) {
				errList.add(e);				
			}
		}
		return errList;
	}
	
	/**
	 * @Return: the validationErrors list ordered by ErrorCode
	 */
	public ArrayList<ErrorItem> getOrderedErrors() {
		ArrayList<ErrorItem> orderedByErrorCode = new ArrayList<ErrorItem>();
		ListIterator<Integer> foundErrors = errorsFound.listIterator();
		Hashtable<Integer,ArrayList<ErrorItem>> bins = new Hashtable<Integer,ArrayList<ErrorItem>>();

		for(ErrorItem ei: validationErrors) {
			int ec = ei.getErrorCode();
			if(! bins.containsKey(ec)) {
				ArrayList<ErrorItem> al = new ArrayList<ErrorItem>();
				al.add(ei);
				bins.put(ec, al);
			} else {
				bins.get(ec).add(ei);
			}
		}
		Enumeration binkeys = bins.keys();
		while(binkeys.hasMoreElements()) {
			int ec = (Integer) binkeys.nextElement();
			ArrayList<ErrorItem> ecList = bins.get(ec);
			orderedByErrorCode.addAll(ecList);
		}
/*		
		while(foundErrors.hasNext()) {
			int ec = (Integer) foundErrors.next();
			for(int i=0; i< validationErrors.size(); i++) {
				ErrorItem e = validationErrors.get(i);
				if(e.getErrorCode() == ec) {
					orderedByErrorCode.add(e);				
				}
			}
		}
*/
		return orderedByErrorCode;
	}
	
	/**
	 * @Return: the validationWarnings list ordered by ErrorCode
	 */
	public ArrayList<ErrorItem> getOrderedWarnings() {
		ArrayList<ErrorItem> orderedByErrorCode = new ArrayList<ErrorItem>();
		ListIterator<Integer> foundErrors = errorsFound.listIterator();
		Hashtable<Integer,ArrayList<ErrorItem>> bins = new Hashtable<Integer,ArrayList<ErrorItem>>();

		for(ErrorItem ei: validationWarnings) {
			int ec = ei.getErrorCode();
			if(! bins.containsKey(ec)) {
				ArrayList<ErrorItem> al = new ArrayList<ErrorItem>();
				al.add(ei);
				bins.put(ec, al);
			} else {
				bins.get(ec).add(ei);
			}
		}
		Enumeration binkeys = bins.keys();
		while(binkeys.hasMoreElements()) {
			int ec = (Integer) binkeys.nextElement();
			ArrayList<ErrorItem> ecList = bins.get(ec);
			orderedByErrorCode.addAll(ecList);
		}
		return orderedByErrorCode;
	}
	
	public ArrayList<ErrorItem> getErrorList () {
		return validationErrors;
	}
	
	public ArrayList<ErrorItem> getWarningList () {
		return validationWarnings;
	}
	
	public ArrayList<ErrorItem> getIgnoreList () {
		return validationIgnoreList;
	}
	
	public int countErrors () {
		return validationErrors.size();
	}

	public int countWarnings () {
		return validationWarnings.size();
	}

	public ArrayList<ErrorItem> getAllItems() {
		ArrayList<ErrorItem> tmpList = new ArrayList(); 
		tmpList.addAll(validationErrors);
		tmpList.addAll(validationWarnings);
		tmpList.addAll(validationIgnoreList);
		return tmpList;
	}
	
	public int countItems() {
		return (validationErrors.size() + validationWarnings.size());
	}
}
