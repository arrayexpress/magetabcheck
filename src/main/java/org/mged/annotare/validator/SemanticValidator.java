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

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mged.magetab.error.ErrorCode;
import org.mged.magetab.error.ErrorItem;
import org.mged.magetab.error.ErrorItemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tigr.microarray.mev.file.StringSplitter;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.AbstractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.graph.Node;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.AbstractSDRFNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ArrayDataMatrixNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ArrayDataNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ArrayDesignNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.AssayNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.DerivedArrayDataMatrixNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.DerivedArrayDataNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.HybridizationNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ImageNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.LabeledExtractNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.NormalizationNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ProtocolApplicationNode;
//import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ProtocolNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SDRFNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SampleNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.ScanNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.SourceNode;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.CharacteristicsAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.FactorValueAttribute;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.sdrf.node.attribute.ParameterValueAttribute;
import uk.ac.ebi.arrayexpress2.magetab.exception.ValidateException;
import uk.ac.ebi.arrayexpress2.magetab.validator.AbstractValidator;

public class SemanticValidator extends AbstractValidator<MAGETABInvestigation> {

	MAGETABInvestigation mti;
	AnnotareError annError;
	ErrorItemFactory eif;
	String tsrStr = null;
	String idfFileName;
	String sdrfFileName;
	boolean pass = false;
	boolean testDebug = false;
	boolean ontologyVal = false;
	boolean dataVal = true;
	Hashtable<Point, String> idfMap;
	Hashtable<Point, String> sdrfMap;
	ArrayList<String> idfHeaders = new ArrayList<String>();
	ArrayList<String> sdrfHeaders = new ArrayList<String>();

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
	 * Constructor call with MAGETABInvestigation object; normal proc
	 * Sets up validation by calling super and getting a copy of MAGETABInvestigation
     *
     * @param errorListObj
	 */
	public SemanticValidator(AnnotareError errorListObj) {
		this();
//		this.annError = errorListObj;
	}
//	private SemanticValidator () {
	public SemanticValidator () {
		super();
		this.eif = ErrorItemFactory.getErrorItemFactory();
//		this.annError = new AnnotareError();
	}
	public SemanticValidator (String idfName) {
		this();
		this.idfFileName = idfName;
		this.idfMap = readTabFile(idfName, idfMap);
	}

    protected Logger getLog() {
        return log;
    }

	Hashtable<Point, String> readTabFile(String fileName, Hashtable<Point, String> map) {
		map = new Hashtable<Point, String>();
		readFile(fileName,map);
		Enumeration<Point> e = map.keys();
		Point key;
		while(e.hasMoreElements()) {
			key = e.nextElement();
			String value = map.get(key);
			char carriageReturn = 0x0D;
			value = value.replaceAll(Character.toString(carriageReturn), " ");
//			System.out.println("line_column: " + key.toString() + " ; data: " + value);
		}

		return map;
	}

	/**
	 * setDataValOn / setDataValOff
	 * Activates / Deactivates Data file validation
	 * dataVal = true by default
	 */
	public void setDataValOn() {
		this.dataVal = true;
	}
	public void setDataValOff() {
		this.dataVal = false;
	}
	/**
	 * setOntologyValOn / setOntologyValOff
	 * Activates / Deactivates external ontology reference checking
	 * ontologyVal is false by default 
	 */
	public void setOntologyValOn() {
		this.ontologyVal = true;
	}
	public void setOntologyValOff() {
		this.ontologyVal = false;
	}
	
	/**
	 * Returns success of last validation process
	 * @return class member 'pass'
	 */
	public boolean getValidateSuccess() {
		return pass;
	}

	/**
	 * read idf/sdrf files and fill hashtable with line numbers
	 * @param: filename
	 * @param: hashtable to use
	 * @return: void
	 */
	private Hashtable<Point, String> readFile(String fileName, Hashtable<Point, String> fileMap) {
		int dataColumn = 0;
		int ctr = 0;
		BufferedReader br;
		String currentLine;
		try{
			br = new BufferedReader(new FileReader(fileName));			
			StringSplitter ss = new StringSplitter((char)0x09);
			ArrayList<String> ch = new ArrayList<String>();
			ArrayList<String> rh = new ArrayList<String>();
			while( (currentLine = br.readLine()) != null) {
				//note: Java's BufferedReader.readLine fails to distinguish between 0x0D and 
				//0x0D/0x0A --the windows carriage return/line feed.  Thus users that place 
				//carriage returns inside their data items will wind up with parsing errors
				//due to 'extra' carriage returns that create extra lines.  Both the parser 
				//and the validator use readLine to read data from files.  
				//There is no resolution of this issue as yet.
				ss.init(currentLine);
				ctr++;
				int fctr = 0;
				while(ss.hasMoreTokens()) {
//Note: need to convert all tags to lowercase with no spaces
					String item = ss.nextToken();
//					item = item.toLowerCase();
//					item = item.replace(" ", "");
					fctr++;
					if (ctr == 1) ch.add(item);
					if(fctr == 1) rh.add(item);
					if(item != null) {
						Point cell = new Point(ctr, fctr);
						fileMap.put(cell, item);
						dataColumn = fctr;
					}
				}
			}
			if(fileName.contains("sdrf") || fileName.contains("SDRF")) {
				sdrfHeaders.addAll(ch);
			} else {
				idfHeaders.addAll(rh);
			}
			br.close();				
		} catch(FileNotFoundException fnfe) {
			getLog().error("File not found: " + fileName, fnfe);
		} catch(IOException ioe) {
			getLog().error("Failed to read file: " + fileName, ioe);
		}
		
		return fileMap;
	}
	
	/**
	 * Return the list of validation errors from content validation
	 * Use AnnotareError for the time being; may switch later.
	 * @return ArrayList of ErrorItem objects
	 */
	public ArrayList<ErrorItem> getErrors () {
		ArrayList<ErrorItem> errorList = annError.getErrorList();
		
		return errorList;
	}
	
	/**
	 * Validate by running a series of checks on IDF, SDRF 
	 * and Refs (ADF in future)
	 */
	public void validate(MAGETABInvestigation investigation)
			throws ValidateException {
		String spath = null;
		try{
			if(this.idfFileName == null) {
				throw new NullPointerException();
			}
		} catch(NullPointerException npe) {
			System.out.println("IDF file name is null.  Code line SemanticValidator:240.");
			createEvent("IDF file name is null.  Code line SemanticValidator:240.", 999, "validate");
			npe.printStackTrace();
			return;
		}
		System.out.println("Semantic validation: validate method");
		this.mti = investigation;
		//get list of SDRF files from IDF
		List<String> sdrfFileList = mti.IDF.sdrfFile;
		for(int i=0; i<sdrfFileList.size();i++) {
			//09-21-2009: can only deal with one SDRF file at this time
			this.sdrfFileName = sdrfFileList.get(0);			
		}
		//read file and get line numbers indexed by name
		if(idfFileName.lastIndexOf(File.separatorChar) > 0) {
			spath = idfFileName.substring(0, this.idfFileName.lastIndexOf(File.separatorChar));
		} else {
			spath = ".";
		}
		this.sdrfMap = readTabFile(spath + File.separatorChar + this.sdrfFileName, sdrfMap);

		//get list of TermSource Names from IDF
		List<String> tsrList = mti.IDF.termSourceName;
		this.tsrStr = tsrList.toString();
		
		boolean success = false;
		boolean failIDF = false;
		boolean failSDRF = false;
		boolean failRefs = false;
		if(mti.IDF != null) 
			failIDF = checkIDF(mti.IDF);
		if(mti.SDRF != null && mti.IDF.sdrfFile != null) 
			failSDRF = checkSDRF(mti.SDRF);
		if(mti.SDRF != null && mti.IDF != null) 
			failRefs = checkRefs(mti);
		if(failIDF == false && failSDRF == false && failRefs == false) {
			success = true;
		}			
		System.out.println("Semantic validation: validate method check complete");
	}

	/**
	 * Checks IDF groups of tags:
	 * Experiment
	 * Person
	 * Submission
	 * QC
	 * Publication
	 * Protocol
	 * Term Source
	 * 
	 * @param idf
	 * @return true if any failure occurs
	 */
	protected boolean checkIDF(IDF idf) {
		boolean fail = false;
		boolean rv = false;
		
		//check Title
		fail = checkTextTag("Investigation Title", idf.investigationTitle);
		if(fail == true) rv = true;

		//check date of experiment
		fail = checkDateTag("Date Of Experiment", idf.dateOfExperiment);
		if(fail == true) rv = true;
		
		//check public release date
		fail = checkDateTag("Public Release Date", idf.publicReleaseDate);
		if(fail == true) rv = true;

		//check experiment description
		fail = checkTextTag("Experiment Description", idf.experimentDescription);
		if(fail == true) rv = true;
		
		//check list of dates earliest first.
		checkDateOrder(idf.dateOfExperiment, idf.publicReleaseDate);
		
		//check for SDRF file
		fail = checkArrayTag("SDRF file", idf.sdrfFile);
		if(fail == true) rv = true;
		
		//check TERM Sources and load as necessary
		fail = checkTermSources("Term Source", idf.termSourceName, 
					idf.termSourceFile, idf.termSourceVersion);
		if(fail == true) rv = true;
		
		//check experimentalDesign and TERM Source
		fail = checkTagAndTermSource("Experimental Design", idf.experimentalDesign, 
				idf.experimentalDesignTermSourceREF);	//idf.experimentalDesignAccession);
		if(fail == true) rv = true;

		
		//check experimentalFactor and TERM Source
		fail = checkTagAndTermSource("Experimental Factor Type", idf.experimentalFactorName, 
				idf.experimentalFactorType, idf.experimentalFactorTermSourceREF);	
		if(fail == true) rv = true;

		//check Persons
		fail = checkPerson("Person", idf);
		if(fail == true) rv = true;
				
		//check Roles
		if(idf.personRoles.contains(";")) {
			//split it up and process
		} else {
			checkTagAndTermSource("Person Roles",idf.personRoles,idf.personRolesTermSourceREF);
		}
		if(fail == true) rv = true;
				
		//QC types: replicates, normalization, quality control
		fail = checkTagAndTermSource("Quality Control Type",idf.qualityControlType,idf.qualityControlTermSourceREF);
		if(fail == true) rv = true;
		fail = checkTagAndTermSource("Replicate Type",idf.replicateType,idf.replicateTermSourceREF);
		if(fail == true) rv = true;
		fail = checkTagAndTermSource("Normalization Type",idf.normalizationType,idf.normalizationTermSourceREF);
		if(fail == true) rv = true;
		
		//check publication info
		fail = checkPub("Publication", idf);
		if(fail == true) rv = true;
		//check publication status and TERM Source
		fail = checkTagAndTermSource("Publication Status",idf.publicationStatus,idf.publicationStatusTermSourceREF);
		if(fail == true) rv = true;
		
		//check protocol
		fail = checkIDFProtocol("Protocol", idf);
		if(fail == true) rv = true;
		
		//check protocol type and TERM source
		fail = checkTagAndTermSource("Protocol Type",idf.protocolType,idf.protocolTermSourceREF);
		if(fail == true) rv = true;
		
		//check extension code for IDF extensions
		//Code extenders must change the String to match an IDF tag
		fail = checkIDFExtensions ("IDF Extension Tests", idf);
		if(fail == true) rv = true;
		
		return rv;
	}

	/**
	 * checkTextTag
	 * 
	 */
	private boolean checkTextTag (String label, String tag) {
		boolean check = false;
//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		if(tag.equals("")) {
			Point p = findCell(label, idfMap);
			createEvent("IDF date tag " + label + " is empty", 1015, 
					"validation error", idfFileName, p.x, p.y + 2, 
					"checkTextTag") ;
		}
		return check;
	}
	
	/**
	 * checkDateTag
	 * 
	 */
	private boolean checkDateTag (String label, String tag) {
		boolean check = false;
//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		if(tag.equals("")) {
			Point p = findCell(label, idfMap);
			createEvent("IDF date tag " + label + " is missing", 1015, 
					"validation error", idfFileName, p.x, p.y, "checkDateTag") ;
//			check = true;
		} else {
			//check date format
			String inDate = tag;
			Pattern dateFormat = Pattern.compile("\\d\\d\\d\\d-\\d\\d-\\d\\d");
			Matcher dateMatch = dateFormat.matcher(inDate);
			if(! dateMatch.find()) {
				Point p = findCell(tag, idfMap);
				createEvent("Incorrect date format for " + label + ": " + tag 
						+ "; use format: YYYY-MM-DD", 1008, "validation error",
						idfFileName, p.x, p.y, "checkDateTag");
				check = true;
			}
		}
		return check;
	}
	
	/**
	 * checkDateOrder
	 * Compares date1 to date2; returns true if date1 < date2, false otherwise.
	 */
	private boolean checkDateOrder(String date1, String date2) {
		boolean ordered = false;
		String dateStr = null;
		if(! date1.equals("") && ! date2.equals("")){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try{
				dateStr = date1;
			    Date d1 = df.parse(date1);
			    dateStr = date2;
			    Date d2 = df.parse(date2);
			    if (d2.after(d1) || d2.equals(d1)) {
			    	ordered = true;
			    } else {
			    	//Error Code is fudged; this is not currently a MAGE-TAB error  
			    	Point p = findCell("Date Of Experiment", idfMap);
					createEvent("Experiment date and Publication date "
						+ "are out of sync: Experiment Date logically precedes "
						+ "Publication date.", 1039, "validation warning",
							idfFileName, p.x, p.y + 1, "checkDateOrder");
			    }
			} catch(ParseException pe){
				//This error code is correct for date format.
		    	Point p = findCell(dateStr, idfMap);
				createEvent("Incorrect date format for date: " + dateStr 
						+ "; use format: YYYY-MM-DD", 1008, "validation error",
						idfFileName, p.x, p.y, "checkDateOrder");
				
			}
		}
		return ordered;
	}
	
	/**
	 * checkPerson
	 * specialized method for Person tags
	 * 
	 */
	private boolean checkPerson(String label, IDF idf) {
		boolean check = false;
//		label = label.toLowerCase();
//		label = label.replace(" ", "");

		int people = 0;
		if(idf.personLastName == null) {
			//first Person cannot be null
			createEvent("Error: required IDF tag 'personLastName' is missing", 
				24, "checkPerson");
			check = true;
		} else {
			people = idf.personLastName.size();
			for(int i=0;i< people; i++) {
				String lastName = idf.personLastName.get(i);			
//				label = label.toLowerCase();
//				label = label.replace(" ", "");
				if(lastName.equals("")) {
					Point p = findCell("Person Last Name", idfMap);
					createEvent("Error: lastName in IDF column " + (i+2) 
					+ " is missing", 24, "validation error", idfFileName, 
					p.x, i+2, "checkPerson") ;						
					check = true;
				}
			}
		}
		if(idf.personEmail == null) {
			//email cannot be null; generates line, column = -1,-1 due to defaults in ErrorItem.
			createEvent("Error: tag 'personEmail' in IDF is missing", 24,
					"checkPerson");
			check = true;
		} else {
			if(idf.personEmail.size() < 1) {
				//generates line, column = -1,-1 due to defaults in ErrorItem.
				createEvent("Error: At least one Email address must be provided in IDF", 24,
						"checkPerson");
				check = true;
			} else if(idf.personEmail.size() < people) {
				for(int i=0; i < idf.personEmail.size(); i++) {
					if(idf.personEmail.get(i) == null) {
						Point p = findCell("Person Email", idfMap);
						createEvent("Warning: Email address for " + idf.personLastName.get(i) 
								+ " in IDF is missing", 1015, "validation warning", idfFileName, 
								p.x, i+2, "checkPerson");						
					}
				}
			}
		}
		if(idf.personRoles == null) {
			//role should not be null
			createEvent("Error: IDF tag 'personRole' is missing", 24, "checkPerson");
			check = true;
		} else {
			if(idf.personRoles.size() < 1) {
				Point p = findCell("Person Roles", idfMap);
				createEvent("Error: At least one Role must be provided in IDF", 24, 
						"validation error", idfFileName, p.x, p.y, "checkPerson");
				check = true;
			} else if(idf.personRoles.size() < people) {
				for(int i=0; i < idf.personRoles.size(); i++) {
					if(idf.personRoles.get(i) == null) {
//						String lastName = idf.personLastName.get(i);
//						label = label.toLowerCase();
//						label = label.replace(" ", "");
						Point p = findCell("Person Roles", idfMap);
						createEvent("Error: Role for " + idf.personLastName.get(i) + " in IDF is missing", 
								24, "validation error", idfFileName, p.x, i+2, "checkPerson");
						check = true;
					}
				}
			}
			boolean found = false;
			for(String s : idf.personRoles) {
				if(s.contains("submitter")) { 
					found = true;
					break;
				}
			}
			if(found == false) {
//				label = label.toLowerCase();
//				label = label.replace(" ", "");
				Point p = findCell("Person Roles", idfMap);
				createEvent("Error: IDF: at least one Person must have Role = 'submitter'", 
						24, "validation error", idfFileName,p.x, p.y, "checkPerson");
				check = true;
			}
		}

		Hashtable<String, List> tmp = new Hashtable<String, List>();
//		if(idf.personLastName != null) tmp.put("Person Last Name", idf.personLastName);
		if(idf.personFirstName != null) tmp.put("Person First Name", idf.personFirstName);
		if(idf.personMidInitials != null) tmp.put("Person Mid Initials", idf.personMidInitials);
//		if(idf.personEmail != null) tmp.put("Person Email", idf.personEmail);
		if(idf.personPhone != null) tmp.put("Person Phone", idf.personPhone);
		if(idf.personFax != null) tmp.put("Person Fax", idf.personFax);
		if(idf.personAddress != null) tmp.put("Person Address", idf.personAddress);
		if(idf.personAffiliation != null) tmp.put("Person Affiliation", idf.personAffiliation);
//		if(idf.personRoles != null) tmp.put("Person Roles", idf.personRoles);
		if(idf.personRolesTermSourceREF != null) tmp.put("Person Roles Term Source REF", idf.personRolesTermSourceREF);
		Enumeration<String> e = tmp.keys();
		while(e.hasMoreElements()) {
			String key = e.nextElement();
//			label = label.toLowerCase();
//			label = label.replace(" ", "");
			ArrayList<String> al = (ArrayList<String>) tmp.get(key);
			if(al == null || al.size() == 0){
				Point p = findCell(key, idfMap);		//key ??
				createEvent("Incomplete name information in IDF: " 
						+ key + " is missing", 1015, "validation missingData", idfFileName,
						p.x, p.y, "checkPerson");
			} else {
				String lastName = "";
				int j = 0;
				try{
					for(j=0 ; j < people ; j++) {
						lastName = idf.personLastName.get(j);
						String s = al.get(j);
						if(s == null || s.equals("")) {
							Point p = findCell(key, idfMap);		//key ??
							createEvent("Incomplete information in IDF for " + lastName + "; " 
									+ key + " is empty", 1015, "validation warning", idfFileName,
									p.x, j+2, "checkPerson");
						}
					}
				} catch(NullPointerException npe) {
					Point p = findCell(key, idfMap);
					createEvent("Incomplete information in IDF for " + lastName + "; " + key + " is empty", 
							1015, "validation warning", idfFileName, p.x, j+2, "checkPerson");				
				} catch(IndexOutOfBoundsException iob) {
					Point p = findCell(key, idfMap);
					createEvent("Incomplete information in IDF for " + lastName + "; " + key + " is empty", 
							1015, "validation warning", idfFileName, p.x, j+2, "checkPerson");				
				}	
			}
		}
		return check;
	}
	
	/**
	 * check publication attributes for missing values
	 */
	private boolean checkPub(String label, IDF idf) {
		boolean check = false;
//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		//get the number of documents; pubmed or doi can be supplied
		int numDocs = 0;
		if(idf.pubMedId != null && idf.publicationDOI != null) {
			numDocs = (idf.pubMedId.size() >= idf.publicationDOI.size()) ? idf.pubMedId.size() : idf.publicationDOI.size();
		} else if(idf.pubMedId != null) {
			numDocs = idf.pubMedId.size();
		} else if(idf.publicationDOI != null) {
			numDocs = idf.publicationDOI.size();
		} else {
			//not really an error, but should be noted anyway
			createEvent("Information: IDF, no document identifier information was supplied", 1015, 
					"validation warning", "checkPub");
		}
//System.out.println("numDocs: " + numDocs);
		
		//hash of IDF keys and ArrayList objects
		Hashtable<String, List> tmp = new Hashtable<String, List>();
		if(idf.pubMedId != null) tmp.put("PubMed Id", idf.pubMedId);
		if(idf.publicationDOI != null) tmp.put("Publication DOI", idf.publicationDOI);
		if(idf.publicationAuthorList != null) tmp.put("Publication AuthorList", idf.publicationAuthorList);
		if(idf.publicationTitle != null) tmp.put("Publication Title", idf.publicationTitle);
		if(idf.publicationStatus != null) tmp.put("Publication Status", idf.publicationStatus);
		if(idf.publicationStatusTermSourceREF != null) tmp.put("Publication Status Term Source REF", idf.publicationStatusTermSourceREF);

		//iterate over the hash keys
		Enumeration<String> e = tmp.keys();
		//get the AL and check for missing values
		while(e.hasMoreElements()) {
			String key = e.nextElement();
//			label = label.toLowerCase();
//			label = label.replace(" ", "");
			ArrayList<String> al = (ArrayList<String>) tmp.get(key);
			if(al == null || al.size() == 0){
				Point p = findCell(key, idfMap);		//key ??
				createEvent("Incomplete name information in IDF: " 
						+ key + " is missing", 1015, "validation missingData", idfFileName,
						p.x, p.y, "checkPub");
			} else {
				int j = 0;
				try{
					for(j=0 ; j < numDocs ; j++) {
						String s = al.get(j);
						if(s == null || s.equals("")) {
							Point p = findCell(key, idfMap);		//key ??
							createEvent("Incomplete information in IDF: " + key + " is empty", 
									1015, "validation warning", idfFileName, p.x, j+2, "checkPub");
						}
					}
				} catch(NullPointerException npe) {
					Point p = findCell(key, idfMap);
					createEvent("Incomplete information in IDF: " + key + " is empty", 
							1015, "validation warning", idfFileName, p.x, j+2, "checkPub");
				} catch(IndexOutOfBoundsException iob) {
					Point p = findCell(key, idfMap);
					createEvent("Incomplete information in IDF: " + key + " is empty", 
							1015, "validation warning", idfFileName, p.x, j+2, "checkPub");
				}				
			}
		}
		return check;
	}
	
	/**
	 * check Protocol objects for missing values
	 * 
	 */
	private boolean checkIDFProtocol (String label, IDF idf) {
		boolean check = false;
//		label = label.toLowerCase();
//		label = label.replace(" ", "");

		int numProtocols = 0;
		if(idf.protocolName == null) {
			//Protocol names needed
			createEvent("Error: required IDF tag 'protocolName' is missing", 24, "checkIDFProtocol");
			check = true;
		} else {
			//check that each protocolName has a value, ie no skipped columns
			numProtocols = idf.protocolName.size();
			for(int i=0;i< numProtocols; i++) {
				String protocolName = idf.protocolName.get(i);
				if(protocolName == null || protocolName.equals("")) { 
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point p = findCell("Protocol Name", idfMap);		//key ??
					createEvent("Incomplete information in IDF: Protocol Name is empty", 
							1015, "validation warning", idfFileName, p.x, i+2, "checkIDFProtocol");
				}
			}
		}
		if(idf.protocolType == null) {
			//protocol type is essential
			createEvent("Error: protocolType is missing", 24, "checkIDFProtocol");
			check = true;
		} else {
			//add empty cell where ArrayList is short
			while(idf.protocolType.size() < numProtocols) {
				idf.protocolType.add("");
			}
			int i = 0;
			for(i=0;i< numProtocols; i++) {
				String protocolType = idf.protocolType.get(i);
				String protocolName = idf.protocolName.get(i);
				if(protocolType.equals("")) {
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point p = findCell("Protocol Type", idfMap);		//key ??
					createEvent("Incomplete information in IDF: Protocol Type for " 
					+ protocolName + " is empty", 1015, "validation warning", idfFileName, 
					p.x, i+2, "checkIDFProtocol");
				}
			}
		}

		//hash of Protocol ArrayList objects
		Hashtable<String, List> tmp = new Hashtable<String, List>();
//		if(idf.protocolName != null) tmp.put("Protocol Name", idf.protocolName);
//		if(idf.protocolType != null) tmp.put("Protocol Type", idf.protocolType);
		if(idf.protocolDescription != null) tmp.put("Protocol Description", idf.protocolDescription);
		if(idf.protocolParameters != null) tmp.put("Protocol Parameters", idf.protocolParameters);
		if(idf.protocolHardware != null) tmp.put("Protocol Hardware", idf.protocolHardware);
		if(idf.protocolSoftware != null) tmp.put("Protocol Software", idf.protocolSoftware);
		if(idf.protocolContact != null) tmp.put("Protocol Contact", idf.protocolContact);
		if(idf.protocolTermSourceREF != null) tmp.put("Protocol Term Source REF", idf.protocolTermSourceREF);
		
		//iterate over the hash keys
		Enumeration<String> e = tmp.keys();
		//get the AL and check for missing values
		while(e.hasMoreElements()) {
			String key = e.nextElement();
//			label = label.toLowerCase();
//			label = label.replace(" ", "");
			ArrayList<String> al = (ArrayList<String>) tmp.get(key);
			if(al == null || al.size() == 0){
				Point p = new Point(0,0);
//				Point p = findCell(key, idfMap);		//key ??
				createEvent("Incomplete name information in IDF: " 
						+ key + " not supplied", 1015, "validation missingData", 
						idfFileName, p.x, p.y, "checkIDFProtocol");
			} else {
				while(al.size() < numProtocols) {
					al.add("");
				}
				int j = 0;
				try{
					for(j=0 ; j < numProtocols ; j++) {
						String s = al.get(j);
//						String currProtocol = idf.protocolName.get(j);
						if(s == null || s.equals("")) {
							Point p = findCell(key, idfMap);		//key ??
							createEvent("Incomplete information in IDF: " + key + " for Protocol " 
								+ idf.protocolName.get(j) + " is empty", 
								1015, "validation warning", idfFileName, p.x, j+2, "checkIDFProtocol");
						}
					}
				} catch(NullPointerException npe) {
					Point p = findCell(key, idfMap);
					createEvent("Incomplete information in IDF: " + key + " not supplied", 
							1015, "validation warning", idfFileName, p.x, j+2, "checkIDFProtocol");
				} catch(IndexOutOfBoundsException iob) {
					Point p = findCell(key, idfMap);
					createEvent("Incomplete information in IDF: " + key + " is empty", 
							1015, "validation warning", idfFileName, p.x, j+2, "checkIDFProtocol");
				}
			}
		}
		return check;
	}
	
	/**
	 * checkArrayTag
	 * 
	 */
	private boolean checkArrayTag (String label, List<String> tag) {
		boolean check = false;
		
//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		if(tag == null || tag.equals("")) {
			Point p = findCell(label, idfMap);
			createEvent("IDF tag " + label + " is null", 1015, "validation warning",
					idfFileName, p.x, p.y, "checkArrayTag");
			check = true;
		}

		return check;
	}

	private boolean checkTermSources (String label, List<String> names,
			List<String> urls, List<String> versions) {

//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		boolean check = false;
		Hashtable<String, Point> sourceNames = new Hashtable<String, Point>();
		
		int numNames = 0;
		if(names == null) {
			//At least one type is expected
			createEvent("Error: required IDF tag " + label + " is missing", 24, 
					"checkTermSources");
			check = true;
		} else {
			//check that each Term Source Name has a value, ie no skipped columns
			numNames = names.size();
			if(numNames == 0) {
				Point p = findCell(label, idfMap);
				createEvent("Incomplete information in IDF: " + label + " is missing", 
						1015, "validation warning", idfFileName, p.x, p.y + 2,
						"checkTermSources");
			}
			while(urls.size() < numNames) { urls.add(""); }
			while(versions.size() < numNames) { versions.add(""); }

			for(int i=0;i< numNames; i++) {
				String sourceName = names.get(i);
				String url = urls.get(i);
				String version = versions.get(i);
				
				if(sourceName == null || sourceName.equals("")) {
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point p = findCell("Term Source Name", idfMap);
					createEvent("Incomplete information in IDF: " + label + " is empty", 
							1015, "validation warning", idfFileName, p.x, i+2, "checkTermSources");
				} else {
					//Bandaid method to catch duplicate term source names; NOTE: NOT an error.  
					//Note: could not use mti.getLocationTracker().getIDFLocations() because 
					//it doesn't work (3/25/2010; jaw). 
					Point tagLine = findCell("Term Source Name", idfMap);
					int line = tagLine.x;
					//sourceNames is a hashtable(String, Point), the reverse of idfMap.  It is
					//primarily meant to store unique names from the Term Source Name arraylist.  
					if(sourceNames.containsKey(sourceName)){
						Point p = sourceNames.get(sourceName);
						Point q = new Point(line, i);
						int pcol = p.y +2;	//conveniences for report string concatenation
						int qcol = q.y +2;
						//Error 21 usually refers to an entire column, but is being used temporarily.
						createEvent("Duplicate information in IDF for " + label + ": "
								+ sourceName + " is duplicated in row " + p.x + ", columns: " 
								+ pcol + " and " + qcol + ".",
								21, "validation warning", idfFileName, p.x, i+2, "checkTermSources");						
					} else {
						//keys are sourceName, NOT Point.  Note: could use i+2, but didn't for consistency
						sourceNames.put(sourceName, new Point(line, i));
					}
				}
				if(url == null || url.equals("")) {
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point p = findCell("Term Source File", idfMap);
					createEvent("Incomplete information in IDF: Term Source File is empty", 
							1015, "validation warning", idfFileName, p.x, i+2, "checkTermSources");
				}
				if(version == null || version.equals("")) {
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point p = findCell("Term Source Version", idfMap);
					createEvent("Incomplete information in IDF: Term Source Version is empty", 
							1015, "validation warning", idfFileName, p.x, i+2, "checkTermSources");
				}
			}
		}
		
		return check;
	}
	
	/**
	 * checkTagAndTermSource
	 * 
	 */
	private boolean checkTagAndTermSource (String label, List<String> types, 
			List<String> sources) {
		
//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		boolean check = false;
		//This method is for tags like Experimental Design/Experimental Design Term Source REF, 
		//Quality Control Type/Quality Control Term Source REF, etc.
		//where tags will be 'null'
		check = checkTagAndTermSource(label, null, types, sources);
		return check;		
	}

	/**
	 * checkTagAndTermSource
	 * 
	 */
	private boolean checkTagAndTermSource (String label, List<String> tags, 
				List<String> types, List<String> sources) {
		
//		label = label.toLowerCase();
//		label = label.replace(" ", "");
		boolean check = false;
		int numTypes = 0;
		//This method can accomodate tags with 'Name', eg.: Experimental Factor Name, Protocol Name
		//but is overloaded to handle cases where Name is not present.  
		//tags = Named tags with Term Source REF, eg. Experimental Factor Name  
		//types = a tag, eg Experimental Factor Type, Quality Control Type
		//sources = the Term Source REF associated with the types

		//Check for 'Type' in Term Source REF tag
		Point chk = findCell(label, idfMap);
		if(chk.x == -1) {
			//At least one type is expected
			createEvent("Incomplete information in IDF: " + label + " was not supplied", 
					1015, "validation warning", idfFileName, 0, chk.y + 2, 
					"checkTagAndTermSource");
			check = true;
		} else if(types == null || types.size() == 0) {
			//check that each protocolName has a value, ie no skipped columns
			numTypes = types.size();
			if(numTypes == 0) {
				createEvent("Incomplete information in IDF: " + label + " is empty", 
					1015, "validation warning", idfFileName, chk.x, chk.y + 2, 
					"checkTagAndTermSource");
			}
		} else {
			//to catch situation where end cells are empty in spreadsheet
			while(sources.size() < numTypes) {
				sources.add("");
			}
			if(tags != null) {
				while(tags.size() < numTypes) {
					tags.add("");
				}
			}
			for(int i=0;i< numTypes; i++) {
				//tag names dealt with below
				String typeName = types.get(i);
				if(typeName == "") {
					createEvent("Incomplete information in IDF: " + label + " has no value", 
							1015, "validation warning", idfFileName, chk.x, i+2, 
							"checkTagAndTermSource");						
				}
/*				//The next line doesn't work
				int line = mti.getLocationTracker().getIDFLocations(label);
				//The fix.
				if(line == -1) line = findCell(label, idfMap).x;
*/
				//check each source for a Term Source REF (label -> REFLabel)
				String sourceName = sources.get(i);
				if(sourceName == null || sourceName.equals("")) {
					//if Term Source name is null, search for 'label' and throw warning if not found.
					StringBuffer REFLabel = new StringBuffer(label);
					//strip out 'Type' since Term Source REF tag should not have it. 
					if(REFLabel.indexOf("Type") >0) 
						REFLabel.replace(REFLabel.indexOf(" Type", 0), REFLabel.length(), "");
					REFLabel.append(" Term Source REF");
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point p = findCell(REFLabel.toString(), idfMap);
					if(p.x == -1) {
						Point plabel = findCell(label, idfMap);
						createEvent("Incomplete information in IDF: " + label + " has no Term Source REF", 
								1015, "validation warning", idfFileName, plabel.x, i+2, "checkTagAndTermSource");						
					} else {
						createEvent("Incomplete information in IDF: " + REFLabel.toString() + " has no value", 
								1015, "validation warning", idfFileName, p.x, i+2, "checkTagAndTermSource");						
					}
				} else {
					if(sourceName.contains("Type" )){
						Point p = findCell(sourceName, idfMap);
						createEvent("Incorrect IDF header: Term Source REF tag does not include 'Type'", 
							3, "validation error", idfFileName, p.x, p.y, "checkTagAndTermSource");						
					}

				}
				if(tags != null) {
					String myTag = tags.get(i);
					if(myTag == null || myTag.equals("")) {
						Point p = findCell(label, idfMap);
						createEvent("Incomplete information in IDF: " + label + " is empty", 
								1015, "validation warning", idfFileName, p.x, i+2, "checkTagAndTermSource");						
					}
				}
			}
		}
		return check;		
	}

	/**
	 * Checks SDRF by Object and attribute (column) for:
	 * BioSource
	 * BioSample
	 * Extract
	 * LabeledExtract
	 * Assay
	 * Array
	 * Scan
	 * Normalization
	 * Data files
	 * Matrix files
	 * Image files
	 * FactorValue
	 * 
	 * @param sdrf
	 * @return true if any failure occurs
	 */
	protected boolean checkSDRF(SDRF sdrf) {
		boolean fail = false;
		boolean rv = false;
		System.out.println("Validating SDRF");
		
		//Sources
		Collection<SourceNode> sources = sdrf.lookupNodes(SourceNode.class);
		System.out.println("Validating Sources");
//		fail = checkBiomaterial(sources);
		fail = checkSources(sources);
		if(fail == true) rv = true;
		
		//Samples
		Collection<SampleNode> samples = sdrf.lookupNodes(SampleNode.class);
		System.out.println("Validating Samples");
//		fail = checkBiomaterial(samples);
		fail = checkSamples(samples);
		if(fail == true) rv = true;

		//Extracts
		Collection<ExtractNode> extracts = sdrf.lookupNodes(ExtractNode.class);
		System.out.println("Validating Extracts");
//		fail = checkBiomaterial(extracts);
		fail = checkExtracts(extracts);
		if(fail == true) rv = true;

		//LabeledExtracts
		Collection<LabeledExtractNode> labeledExtracts = sdrf.lookupNodes(LabeledExtractNode.class);
		System.out.println("Validating Labeled Extracts");
//		fail = checkBiomaterial(labeledExtracts);
		fail = checkLabeledExtracts(labeledExtracts);
		if(fail == true) rv = true;

		//Hybridizations
		Collection<HybridizationNode> hybridizations = sdrf.lookupNodes(HybridizationNode.class);
		System.out.println("Validating Hybs");
		fail = checkHybridizations(hybridizations);
		if(fail == true) rv = true;

		//Assays
//		Collection<AssayNode> assays = (List<AssayNode>) sdrf.lookupNodes(AssayNode.class);
		Collection<AssayNode> assays = sdrf.lookupNodes(AssayNode.class);
		System.out.println("Validating Assays");
		fail = checkAssays(assays);
		if(fail == true) rv = true;

		//Scans
		Collection<ScanNode> scans = sdrf.lookupNodes(ScanNode.class);
		System.out.println("Validating Scans");
		fail = checkNodes(scans);
		if(fail == true) rv = true;

		//Images
		Collection<ImageNode> images =  sdrf.lookupNodes(ImageNode.class);
		System.out.println("Validating Images");
		fail = checkNodes(images);
		if(fail == true) rv = true;

		//ArrayDesign
		Collection<ArrayDesignNode> arrayDesigns =  sdrf.lookupNodes(ArrayDesignNode.class);
		System.out.println("Validating ArrayDesign");
		fail = checkArrayDesign(arrayDesigns);
		if(fail == true) rv = true;

		//ArrayData
		Collection<ArrayDataNode> arrayData = sdrf.lookupNodes(ArrayDataNode.class);
		System.out.println("Validating Array Data");
//		Collection<SDRFNode> ad = new ArrayList(); 
//		fail = checkArrayData((Collection<SDRFNode>) ad);
		fail = checkArrayData(arrayData);
		if(fail == true) rv = true;

		//ArrayDataMatrix
		Collection<ArrayDataMatrixNode> arrayDataMatrix = sdrf.lookupNodes(ArrayDataMatrixNode.class);
		System.out.println("Validating Array Data Matrix");
		fail = checkArrayDataMatrix(arrayDataMatrix);
		if(fail == true) rv = true;

		//Normalization
		Collection<NormalizationNode> normalization = sdrf.lookupNodes(NormalizationNode.class);
		System.out.println("Validating Normalization");
		fail = checkNodes(normalization);
		if(fail == true) rv = true;

		//DerivedArrayData
		Collection<DerivedArrayDataNode> derivedArrayData = sdrf.lookupNodes(DerivedArrayDataNode.class);
		System.out.println("Validating Derived Array Data");
//		Collection<SDRFNode> dad = new ArrayList(); 
//		fail = checkArrayData((Collection<SDRFNode>) dad);
//		fail = checkDerivedArrayData(derivedArrayData);
		fail = checkArrayData(derivedArrayData);
		if(fail == true) rv = true;

		//DerivedArrayDataMatrix
		Collection<DerivedArrayDataMatrixNode> derivedArrayDataMatrix = sdrf.lookupNodes(DerivedArrayDataMatrixNode.class);
		System.out.println("Validating Derived Array Data Matrix");
		fail = checkArrayDataMatrix(derivedArrayDataMatrix);
		if(fail == true) rv = true;

		//Protocol
		Collection<ProtocolApplicationNode> protAppNodes = sdrf.lookupNodes(ProtocolApplicationNode.class);
		ArrayList<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
		for (ProtocolApplicationNode protocol : protocols) {
			protocols.add(protocol);
		}

		System.out.println("Validating Protocols");
		fail = checkProtocols(protocols);
		if(fail == true) rv = true;

		// For Code Extenders 
		fail = checkSDRFExtensions(sdrf);
		if(fail == true) rv = true;
		
		return rv;
	}


	/**
	 * checkBioMaterial
	 * checks Biomaterial objects and attributes associated with them
	 * Params: source list
	 * Return: boolean, true if fails
	 * 
	 * NOTE: fails because superclass does not have subclass attributes, and these cannot be checked
	 * Even if superclass is cast to subclass, java throws class cast exception unless the cast is 
	 * inside a type check block (ie if-else).  That being the case, the code for each subtype would have
	 * to be duplicated in the if-else blocks, which is no different than having separate methods 
	 * for each subtype.  
	 */
	/*
	boolean checkBiomaterial(Collection<? extends SDRFNode> materials) {
		boolean check = false;
		
		for (SDRFNode myNode : materials) {
			String nodeName = myNode.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(myNode);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(nodeName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			String methodName = "";
			String fileType = "";
			if(myNode instanceof SourceNode){
				methodName = "checkSources";
				fileType = "Source";
				SourceNode tempSource = (SourceNode) myNode;
				if(tempSource.provider != null && tempSource.provider.getNodeName().equals("")) {
//					label = label.toLowerCase();
//					label = label.replace(" ", "");
					Point pro = findCell("Provider",sdrfMap);
					createEvent("Incomplete information for " + tempSource + "; value for provider is missing", 1016, "validation warning",
							sdrfFileName, pro.x, pro.y, methodName);
				} else if(tempSource.provider == null){
					createEvent("Incomplete information for " + tempSource + "; provider not supplied", 1016, "validation missingData",
							sdrfFileName, p.x, p.y, methodName);
				}
			} else if(myNode instanceof SampleNode){
				methodName = "checkSamples";
				fileType = "Sample";
			} else if(myNode instanceof ExtractNode){
				methodName = "checkExtracts";
				fileType = "Extract";
			} else if(myNode instanceof LabeledExtractNode){
				methodName = "checkLabeledExtracts";
				fileType = "LabeledExtract";				
				LabeledExtractNode tempSource = (LabeledExtractNode) myNode;
				String labelName = tempSource.label.getNodeName();
				if(tempSource.label != null && labelName.equals("")) {
					createEvent("Incomplete information for " + nodeName 
						+ "; value for label is missing", 1016, "validation warning", 
						sdrfFileName, p.x, p.y +1, methodName);
//					check = true;
				} else if(tempSource.label == null){
					createEvent("Incomplete information for " + nodeName + "; label not supplied", 
						1016, "validation missingData", sdrfFileName, p.x, p.y, methodName);
				} else if(tempSource.label != null) {
					Point l = findCell(labelName,sdrfMap);
					if(tempSource.label.termSourceREF != null && tempSource.label.termSourceREF.equals("")) { 
						createEvent("Incomplete information for " + nodeName 
							+ "; Label " + labelName + " has no Term Source", 
							1005, "validation warning", sdrfFileName, p.x, l.y +1, methodName);
					} else if(tempSource.label.termSourceREF == null) {
						createEvent("Incomplete information for " + nodeName 
							+ "; Label Term Source not supplied for " + labelName, 
							1016, "validation missingData", sdrfFileName, p.x, l.y, methodName);
					} else if(! tsrStr.contains(tempSource.label.termSourceREF)) {
						createEvent("Term Source REF, " +  tempSource.label.termSourceREF + ", for Label " 
							+ labelName + " is not declared in the IDF", 6, 
							"validation warning", sdrfFileName, p.x, l.y +1, methodName);						
						check = true;
					}				
				}
			}
			if(nodeName.equals("")) {
				createEvent("Error: at least one " + fileType + " has no name", 25, "validation error",
						sdrfFileName, p.x,p.y, methodName);
				check = true;
			}
//		if(myNode instanceof SampleNode){
			SampleNode sampleNode = (SampleNode) myNode;
			if(sampleNode.materialType != null && sampleNode.materialType.getNodeName().equals("")) {
				createEvent("Incomplete information for " + nodeName + "; value for materialType is missing", 
						1016, "validation warning", sdrfFileName, p.x, p.y +1, methodName);
			} else if(sampleNode.materialType == null){
				createEvent("Incomplete information for " + nodeName + "; materialType not supplied", 1016, 
						"validation missingData", sdrfFileName, p.x, p.y, methodName);
			} else if(sampleNode.materialType != null) {
				String materialName = sampleNode.materialType.getNodeName();
				Point m = findCell(materialName, sdrfMap);
				if(sampleNode.materialType.termSourceREF != null && sampleNode.materialType.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + nodeName 
								+ "; MaterialType " + materialName + " has no Term Source", 1005, "validation warning",
								sdrfFileName, p.x, m.y +1, methodName);
				} else if(sampleNode.materialType.termSourceREF == null) {
					createEvent("Incomplete information for " + nodeName + "; Material Type Term Source not supplied for " 
							+ materialName, 1016, "validation missingData", sdrfFileName, m.x, m.y,
							methodName);
				} else if(! tsrStr.contains(sampleNode.materialType.termSourceREF)) {
					createEvent("Term Source REF, " +  sampleNode.materialType.termSourceREF + ", for Material Type " 
							+ materialName + " is not declared in the IDF", 6, "validation warning",
							sdrfFileName, p.x, m.y +1, methodName);						
				}				
			}

			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : sampleNode.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			List<CharacteristicsAttribute> charList = sampleNode.characteristics;
			for (CharacteristicsAttribute attr : charList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + nodeName 
								+ "; Characteristic " + attrType + " nas no value", 1016, 
								"validation warning",sdrfFileName, p.x, a.y +1, methodName);
				}
				if(attr.termSourceREF != null && attr.termSourceREF.equals("")) {
					createEvent("Incomplete information for " + nodeName 
							+ "; Characteristic " + attrType + " has no Term Source", 1005, 
							"validation warning", sdrfFileName, p.x, a.y +1, methodName);
				} else if(attr.termSourceREF == null) {
					createEvent("Incomplete information for " + nodeName + "; Term Source not supplied for "
							+ attrType, 1016, "validation missingData", sdrfFileName, p.x, a.y,
							methodName);
				} else if(! tsrStr.contains(attr.termSourceREF)) {
					createEvent("Term Source REF, " +  attr.termSourceREF + ", for Characteristic " 
							+ attrType + " is not declared in the IDF", 6, "validation warning"
							, sdrfFileName, p.x, a.y +1, methodName);						
					check = true;
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + nodeName 
							+ "; Characteristic " + attrType + " has no Units", 1016, "validation warning",
							sdrfFileName, p.x, a.y +1, methodName);
//					check = true;
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + nodeName 
								+ "; Characteristic " + attrType + " has no Unit Term Source", 1005, "validation warning",
								sdrfFileName, p.x, u.y + 1, methodName);
//						check = true;
					} else if(attr.unit.termSourceREF == null) {
						createEvent("Incomplete information for " + nodeName + "; Unit Term Source not supplied for " + attrType, 
								1016, "validation missingData", sdrfFileName, p.x, u.y, methodName);
					} else if(! tsrStr.contains(attr.unit.termSourceREF)) {
						createEvent("Unit Term Source REF, " +  attr.unit.termSourceREF
								+ ", for Characteristic " + attrType + " is not declared in the IDF", 6, "validation warning",
								sdrfFileName, p.x, u.y +1, methodName);
						check = true;
					}
				}
			}
			if(check == true) System.out.println(nodeName);
		}
//		}
		return check;
	}
*/
	
	
	/**
	 * checkSources
	 * checks source objects and attributes associated with them
	 * Params: source list
	 * Return: boolean, true if fails
	 */
	boolean checkSources(Collection<SourceNode> sources) {
		boolean check = false;
		
		for (SourceNode source : sources) {
			String sourceName = source.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(source);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(sourceName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(sourceName.equals("")) {
				createEvent("Error: at least one Sample has no Source name", 25, "validation error",
						sdrfFileName, p.x,p.y, "checkSources");
				check = true;
			}
			if(source.materialType != null && source.materialType.getNodeName().equals("")) {
				createEvent("Incomplete information for " + sourceName + "; value for materialType is missing", 
						1016, "validation warning", sdrfFileName, p.x, p.y +1, "checkSources");
			} else if(source.materialType == null){
				createEvent("Incomplete information for " + sourceName + "; materialType not supplied", 1016, 
						"validation missingData", sdrfFileName, p.x, p.y, "checkSources");
			} else if(source.materialType != null) {
				String materialName = source.materialType.getNodeName();
				Point m = findCell(materialName, sdrfMap);
				if(source.materialType.termSourceREF != null && source.materialType.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + sourceName 
								+ "; MaterialType " + materialName + " has no Term Source", 1005, "validation warning",
								sdrfFileName, p.x, m.y +1, "checkSources");
				} else if(source.materialType.termSourceREF == null) {
					createEvent("Incomplete information for " + sourceName + "; Material Type Term Source not supplied for " 
							+ materialName, 1016, "validation missingData", sdrfFileName, m.x, m.y,
							"checkSources");
				} else if(! tsrStr.contains(source.materialType.termSourceREF)) {
					createEvent("Term Source REF, " +  source.materialType.termSourceREF + ", for Material Type " 
							+ materialName + " is not declared in the IDF", 6, "validation warning",
							sdrfFileName, p.x, m.y +1, "checkSources");						
				}				
			}
			
			if(source.provider != null && source.provider.getNodeName().equals("")) {
//				label = label.toLowerCase();
//				label = label.replace(" ", "");
				Point pro = findCell("Provider",sdrfMap);
				createEvent("Incomplete information for " + sourceName + "; value for provider is missing", 1016, "validation warning",
						sdrfFileName, pro.x, pro.y, "checkSources");
			} else if(source.provider == null){
				createEvent("Incomplete information for " + sourceName + "; provider not supplied", 1016, "validation missingData",
						sdrfFileName, p.x, p.y, "checkSources");
			}
			
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : source.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			List<CharacteristicsAttribute> charList = source.characteristics;
			for (CharacteristicsAttribute attr : charList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + sourceName 
								+ "; Characteristic " + attrType + " nas no value", 1016, 
								"validation warning",sdrfFileName, p.x, a.y +1, "checkSources");
				}
				if(attr.termSourceREF != null && attr.termSourceREF.equals("")) {
					createEvent("Incomplete information for " + sourceName 
							+ "; Characteristic " + attrType + " has no Term Source", 1005, 
							"validation warning", sdrfFileName, p.x, a.y +1, "checkSources");
				} else if(attr.termSourceREF == null) {
					createEvent("Incomplete information for " + sourceName + "; Term Source not supplied for "
							+ attrType, 1016, "validation missingData", sdrfFileName, p.x, a.y,
							"checkSources");
				} else if(! tsrStr.contains(attr.termSourceREF)) {
					createEvent("Term Source REF, " +  attr.termSourceREF + ", for Characteristic " 
							+ attrType + " is not declared in the IDF", 6, "validation warning"
							, sdrfFileName, p.x, a.y +1, "checkSources");						
					check = true;
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + sourceName 
							+ "; Characteristic " + attrType + " has no Units", 1016, "validation warning",
							sdrfFileName, p.x, a.y +1, "checkSources");
//					check = true;
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + sourceName 
								+ "; Characteristic " + attrType + " has no Unit Term Source", 1005, "validation warning",
								sdrfFileName, p.x, u.y + 1, "checkSources");
//						check = true;
					} else if(attr.unit.termSourceREF == null) {
						createEvent("Incomplete information for " + sourceName + "; Unit Term Source not supplied for " + attrType, 
								1016, "validation missingData", sdrfFileName, p.x, u.y, "checkSources");
					} else if(! tsrStr.contains(attr.unit.termSourceREF)) {
						createEvent("Unit Term Source REF, " +  attr.unit.termSourceREF
								+ ", for Characteristic " + attrType + " is not declared in the IDF", 6, "validation warning",
								sdrfFileName, p.x, u.y +1, "checkSources");
						check = true;
					}
				}
			}
			if(check == true) System.out.println(sourceName);
		}
		
		return check;
	}
	
	boolean checkSamples(Collection<SampleNode> samples) {
		boolean check = false;
		
		for (SampleNode sample : samples) {

			String sampleName = sample.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();

			//get location of the sample
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(sample);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(sampleName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(sampleName.equals("")) {
				createEvent("Warning: at least one Sample has no sample name", 1016, "validation warning",
						sdrfFileName, p.x, p.y, "checkSamples");
//				check = true;
			}
			if(sample.materialType != null && sample.materialType.getNodeName().equals("")) {
				createEvent("Incomplete information for " + sampleName + "; value for materialType is missing", 
						1016, "validation warning", sdrfFileName, p.x, p.y +1, "checkSamples");
				check = true;
			} else if(sample.materialType == null){
				createEvent("Incomplete information for " + sampleName + "; materialType not supplied", 1016, 
						"validation missingData", sdrfFileName, p.x, p.y, "checkSamples");
			} else if(sample.materialType != null) {
				String materialName = sample.materialType.getNodeName();
				Point m = findCell(materialName, sdrfMap);
				if(sample.materialType.termSourceREF != null && sample.materialType.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + sampleName 
						+ "; MaterialType " + materialName + " has no Term Source", 1005, 
						"validation warning", sdrfFileName, p.x, m.y +1, "checkSamples");
				} else if(sample.materialType.termSourceREF == null) {
					createEvent("Incomplete information for " + sampleName + "; Material Type Term Source not supplied for " 
							+ materialName, 1016, "validation missingData", sdrfFileName, p.x, m.y,
							"checkSamples");
				} else if(! tsrStr.contains(sample.materialType.termSourceREF)) {
					createEvent("Term Source REF, " +  sample.materialType.termSourceREF + ", for Material Type " 
							+ materialName + " is not declared in the IDF", 6, "validation warning", sdrfFileName, 
							p.x, m.y +1, "checkSamples");						
				}
			}

			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : sample.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);

			List<CharacteristicsAttribute> charList = sample.characteristics;
			for (CharacteristicsAttribute attr : charList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);
				
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + sampleName 
								+ "; Characteristic " + attrType + " nas no value", 1016, 
								"validation warning", sdrfFileName, p.x, a.y +1, "checkSamples");
				}
				if(attr.termSourceREF != null && attr.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + sampleName 
								+ "; Characteristic " + attrType + " has no Term Source", 1005, 
								"validation warning", sdrfFileName, p.x, a.y +1, "checkSamples");
				} else if(attr.termSourceREF == null) {
					createEvent("Incomplete information for " + sampleName + "; Term Source not supplied for " 
							+ attrType, 1016, "validation missingData", sdrfFileName, p.x, a.y,
							"checkSamples");
				} else if(! tsrStr.contains(attr.termSourceREF)) {
					createEvent("Term Source REF, " +  attr.termSourceREF + ", for Characteristic " 
							+ attrType + " is not declared in the IDF", 6, "validation warning", 
							sdrfFileName, p.x, a.y +1, "checkSamples");						
					check = true;
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + sampleName 
							+ "; Characteristic " + attrType + " has no Units", 1016, "validation warning",
							sdrfFileName, p.x, a.y, "checkSamples");
					check = true;
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + sampleName 
								+ "; Characteristic " + attrType + " has no Unit Term Source", 1005, 
								"validation warning", sdrfFileName, p.x, u.y +1, "checkSamples");
//						check = true;
					} else if(attr.unit.termSourceREF == null) {
						createEvent("Incomplete information for " + sampleName 
							+ "; Unit Term Source not supplied for " + attrType, 1016, 
							"validation missingData", sdrfFileName, p.x, u.y, "checkSamples");
					} else if(! tsrStr.contains(attr.unit.termSourceREF)) {
						createEvent("Unit Term Source REF, " +  attr.unit.termSourceREF
								+ ", for Characteristic " + attrType + " is not declared in the IDF", 
								6, "validation warning", sdrfFileName, p.x, u.y +1, "checkSamples");
						check = true;
					}

				}
			}
			if(check == true) System.out.println(sampleName);
		}
		return check;
	}

	boolean checkExtracts(Collection<ExtractNode> extracts) {
		boolean check = false;

		for (ExtractNode extract : extracts) {
			String extractName = extract.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();

			//get location of the object
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(extract);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(extractName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			
			if(extractName.equals("")) {
				createEvent("Error: at least one extract has no extract name", 25, "validation error",
						sdrfFileName, p.x, p.y, "checkExtracts");
				check = true;
			}
			if(extract.materialType != null && extract.materialType.getNodeName().equals("")) {
				createEvent("Incomplete information for " + extractName + "; value for materialType is missing", 
						1016, "validation warning", sdrfFileName, p.x, p.y +1, "checkExtracts");
			} else if(extract.materialType == null){
				createEvent("Incomplete information for " + extractName + "; materialType not supplied", 
						1016, "validation missingData", sdrfFileName, p.x, p.y, "checkExtracts");
			} else if(extract.materialType != null) {
				String materialName = extract.materialType.getNodeName();
				Point m = findCell(materialName, sdrfMap);
				if(extract.materialType.termSourceREF != null && extract.materialType.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + extractName 
						+ "; MaterialType " + materialName + " has no Term Source", 1005, 
						"validation warning", sdrfFileName, p.x, m.y +1, "checkExtracts");
				} else if(extract.materialType.termSourceREF == null) {
					createEvent("Incomplete information for " + extractName + "; Material Type Term Source not supplied for " 
						+ materialName, 1016, "validation missingData", sdrfFileName, p.x, m.y,
						"checkExtracts");
				} else if(! tsrStr.contains(extract.materialType.termSourceREF)) {
					createEvent("Term Source REF, " +  extract.materialType.termSourceREF + ", for Material Type " 
						+ materialName + " is not declared in the IDF", 6, "validation warning", 
						sdrfFileName, p.x, m.y +1, "checkExtracts");						
					check = true;
				}				
			}

			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : extract.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			List<CharacteristicsAttribute> charList = extract.characteristics;
			for (CharacteristicsAttribute attr : charList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + extractName 
						+ "; Characteristic " + attrType + " nas no value", 
						1016, "validation warning", sdrfFileName, p.x, a.y +1, 
						"checkExtracts");
				}
				if(attr.termSourceREF != null && attr.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + extractName 
						+ "; Characteristic " + attrType + " has no Term Source", 
						1016, "validation warning", sdrfFileName, p.x, a.y +1,
						"checkExtracts");
				} else if(attr.termSourceREF == null) {
					createEvent("Incomplete information for " + extractName 
						+ "; Term Source not supplied for " + attrType, 1016, 
						"validation missingData", sdrfFileName, p.x, a.y,
						"checkExtracts");
				} else if(! tsrStr.contains(attr.termSourceREF)) {
					createEvent("Term Source REF, " +  attr.termSourceREF + ", for Characteristic " 
						+ attrType + " is not declared in the IDF", 6, 
						"validation warning", sdrfFileName, p.x, a.y +1, 
						"checkExtracts");						
					check = true;
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + extractName 
							+ "; Characteristic " + attrType + " has no Units", 1016, 
							"validation warning", sdrfFileName, p.x, a.y +1, "checkExtracts");
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + extractName 
							+ "; Characteristic " + attrType + " has no Unit Term Source", 
							1005, "validation warning", sdrfFileName, p.x, u.y +1,
							"checkExtracts");
//						check = true;
					} else if(attr.unit.termSourceREF == null) {
						createEvent("Incomplete information for " + extractName 
							+ "; Unit Term Source not supplied for " + attrType, 
							1016, "validation missingData", sdrfFileName, p.x, u.y,
							"checkExtracts");
					} else if(! tsrStr.contains(attr.unit.termSourceREF)) {
						createEvent("Unit Term Source REF, " +  attr.unit.termSourceREF
							+ ", for Characteristic " + attrType + " is not declared in the IDF", 
							6, "validation warning", sdrfFileName, p.x, u.y +1, "checkExtracts");						
						check = true;
					}
				}
			}
			if(check == true) System.out.println(extractName);
		}
		return check;
	}

	boolean checkLabeledExtracts(Collection<LabeledExtractNode> labeledExtracts) {
		boolean check = false;

		for (LabeledExtractNode labeledExtract : labeledExtracts) {
			String labeledExtractName = labeledExtract.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();

			//get location of the object
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(labeledExtract);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(labeledExtractName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(labeledExtractName == null) {
				createEvent("Error: at least one labeledExtract has no labeledExtract name", 25, 
					"validation error", sdrfFileName, p.x, p.y, "checkLabeledExtracts");
				check = true;
			}
			if(labeledExtract.materialType != null && labeledExtract.materialType.getNodeName().equals("")) {
				createEvent("Incomplete information for " + labeledExtractName + "; value for materialType is missing", 
					1016, "validation warning", sdrfFileName, p.x, p.y +1, "checkLabeledExtracts");
//				check = true;
			} else if(labeledExtract.materialType == null){
				createEvent("Incomplete information for " + labeledExtractName + "; materialType not supplied", 
					1016, "validation missingData", sdrfFileName, p.x, p.y, "checkLabeledExtracts");
			} else if(labeledExtract.materialType != null) {
				String materialName = labeledExtract.materialType.getNodeName();
				Point m = findCell(materialName, sdrfMap);
				if(labeledExtract.materialType.termSourceREF != null && labeledExtract.materialType.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; MaterialType " + materialName + " has no Term Source", 1005, 
						"validation warning", sdrfFileName, p.x, m.y +1, "checkLabeledExtracts");
				} else if(labeledExtract.materialType.termSourceREF == null) {
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Material Type Term Source not supplied for " + materialName, 
						1016, "validation missingData", sdrfFileName, p.x, m.y, "checkLabeledExtracts");
				} else if(! tsrStr.contains(labeledExtract.materialType.termSourceREF)) {
					createEvent("Term Source REF, " +  labeledExtract.materialType.termSourceREF 
						+ ", for Material Type " + materialName + " is not declared in the IDF", 
						6, "validation warning", sdrfFileName, p.x, m.y +1, "checkLabeledExtracts");
					check = true;
				}				
			}
			String labelName = labeledExtract.label.getNodeName();
			if(labeledExtract.label != null && labelName.equals("")) {
				createEvent("Incomplete information for " + labeledExtractName 
					+ "; value for label is missing", 1016, "validation warning", 
					sdrfFileName, p.x, p.y +1, "checkLabeledExtracts");
//				check = true;
			} else if(labeledExtract.label == null){
				createEvent("Incomplete information for " + labeledExtractName + "; label not supplied", 
					1016, "validation missingData", sdrfFileName, p.x, p.y, "checkLabeledExtracts");
			} else if(labeledExtract.label != null) {
				Point l = findCell(labelName,sdrfMap);
				if(labeledExtract.label.termSourceREF != null && labeledExtract.label.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Label " + labelName + " has no Term Source", 
						1005, "validation warning", sdrfFileName, p.x, l.y +1, "checkLabeledExtracts");
				} else if(labeledExtract.label.termSourceREF == null) {
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Label Term Source not supplied for " + labelName, 
						1016, "validation missingData", sdrfFileName, p.x, l.y, "checkLabeledExtracts");
				} else if(! tsrStr.contains(labeledExtract.label.termSourceREF)) {
					createEvent("Term Source REF, " +  labeledExtract.label.termSourceREF + ", for Label " 
						+ labelName + " is not declared in the IDF", 6, 
						"validation warning", sdrfFileName, p.x, l.y +1, "checkLabeledExtracts");						
					check = true;
				}				
			}

			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : labeledExtract.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			List<CharacteristicsAttribute> charList = labeledExtract.characteristics;
			for (CharacteristicsAttribute attr : charList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);
				
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Characteristic " + attrType + " nas no value", 1016, 
						"validation warning", sdrfFileName, p.x, a.y +1, 
						"checkLabeledExtracts");
				}
				if(attr.termSourceREF != null && attr.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Characteristic " + attrType + " has no Term Source", 
						1016, "validation warning", sdrfFileName, p.x, a.y +1, 
						"checkLabeledExtracts");
				} else if(attr.termSourceREF == null) {
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Term Source not supplied for " + attrType, 1016, 
						"validation missingData", sdrfFileName, p.x, a.y, 
						"checkLabeledExtracts");
				} else if(! tsrStr.contains(attr.termSourceREF)) {
					createEvent("Term Source REF, " +  attr.termSourceREF + ", for Characteristic " 
						+ attrType + " is not declared in the IDF", 6, 
						"validation warning", sdrfFileName, p.x, a.y +1, 
						"checkLabeledExtracts");
					check = true;
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + labeledExtractName 
						+ "; Characteristic " + attrType + " has no Units", 1016, 
						"validation warning", sdrfFileName, p.x, a.y +1, "checkLabeledExtracts");
//					check = true;
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + labeledExtractName 
							+ "; Characteristic " + attrType + " has no Unit Term Source", 
							1005, "validation warning", sdrfFileName, p.x, u.y +1,
							"checkLabeledExtracts");
//						check = true;
					} else if(attr.unit.termSourceREF == null) {
						createEvent("Incomplete information for " + labeledExtractName 
							+ "; Unit Term Source not supplied for " + attrType, 1016, 
							"validation missingData", sdrfFileName, p.x, u.y,
							"checkLabeledExtracts");
					} else if(! tsrStr.contains(attr.unit.termSourceREF)) {
						createEvent("Unit Term Source REF, " +  attr.unit.termSourceREF
							+ ", for Characteristic " + attrType + " is not declared in the IDF", 
							6, "validation warning", sdrfFileName, p.x, u.y +1, 
							"checkLabeledExtracts");
						check = true;
					}
				}
			}
			if(check == true) System.out.println(labeledExtractName);
		}
		return check;
	}

	boolean checkHybridizations(Collection<HybridizationNode> hybridizations) {
		boolean check = false;
		
		for (HybridizationNode hybridization : hybridizations) {
			
			String hybridizationName = hybridization.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();

			//get location of the object
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(hybridization);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(hybridizationName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(hybridizationName.equals("")) {
				createEvent("Error: at least one hybridization has no hybridization name", 
					25, "validation error", sdrfFileName, p.x,p.y, "checkHybridizations");
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : hybridization.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			List<FactorValueAttribute> fvList = hybridization.factorValues;
			for (FactorValueAttribute attr : fvList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);

				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + hybridizationName 
						+ "; Factor value " + attrType + " nas no value", 1016, 
						"validation warning", sdrfFileName, p.x, a.y +1,
						"checkHybridizations");
				}
				if(attr.termSourceREF != null && attr.termSourceREF.equals("")) { 
					createEvent("Incomplete information for " + hybridizationName 
						+ "; Factor value " + attrType + " has no Term Source", 1005, 
						"validation warning", sdrfFileName, p.x, a.y +1, 
						"checkHybridizations");
				} else if(attr.termSourceREF == null) {
					createEvent("Incomplete information for " + hybridizationName 
						+ "; Term Source not supplied for " + attrType, 1016, "validation missingData", 
						sdrfFileName, p.x, a.y, "checkHybridizations");
				} else if(! tsrStr.contains(attr.termSourceREF)) {
					createEvent("Term Source REF, " +  attr.termSourceREF + ", for Factor value " 
						+ attrType + " is not declared in the IDF", 6, "validation warning",
						sdrfFileName, p.x, a.y +1, "checkHybridizations");	
					check = true;
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + hybridizationName 
						+ "; Factor value " + attrType + " has no Units", 1016, 
						"validation warning", sdrfFileName, p.x, a.y +1, 
						"checkHybridizations");
//					check = true;
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + hybridizationName 
							+ "; Factor value " + attrType + " has no Unit Term Source", 1005, 
							"validation warning", sdrfFileName, p.x, u.y +1, "checkHybridizations");
//						check = true;
					} else if(attr.termSourceREF == null) {
						createEvent("Incomplete information for " + hybridizationName 
							+ "; Unit Term Source not supplied for " + attrType, 1016, 
							"validation missingData", sdrfFileName, p.x, u.y, "checkHybridizations");
					} else if(! tsrStr.contains(attr.termSourceREF)) {
						createEvent("Unit Term Source REF, " +  attr.unit.termSourceREF 
							+ ", for Factor value " + attrType + " is not declared in the IDF", 
							6, "validation warning", sdrfFileName, p.x, u.y +1,
							"checkHybridizations");
						check = true;
					}
				}
			}
			if(check == true) System.out.println(hybridizationName);
		}
		return check;
	}

//	boolean checkAssays(List<AssayNode> assays) {
	boolean checkAssays(Collection<AssayNode> assays) {
		boolean check = false;
		
		for (AssayNode assay : assays) {

			String assayName = assay.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
//			List<ProtocolNode> protocols = new ArrayList<ProtocolNode>();

			//get location of the object
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(assay);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(assayName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(assayName.equals("")) {
				createEvent("Error: at least one assay has no assay name", 25,
					"validation error", sdrfFileName, p.x, p.y, "checkAssays");
				check = true;
			}
			if(assay.technologyType != null && assay.technologyType.getNodeName().equals("")) {
				createEvent("Incomplete information for " + assayName 
					+ "; value for technologyType is missing", 1026, 
					"validation error", sdrfFileName, p.x, p.y +1, "checkAssays");
				check = true;
			} else if(assay.technologyType == null){
				createEvent("Incomplete information for " + assayName 
					+ "; technologyType not supplied", 1026, "validation error",
					sdrfFileName, p.x, p.y, "checkAssays");
				check = true;
			} else {
				if(assay.technologyType.termSourceREF != null && assay.technologyType.termSourceREF.equals("")) {
					createEvent("Incomplete information for " + assayName 
						+ "; value for technologyType Term Source is missing", 1016, 
						"validation warning", sdrfFileName, p.x, p.y +1, "checkAssays");
//					check = true;
				} else if(assay.technologyType.termSourceREF == null) {
					createEvent("Incomplete information for " + assayName 
						+ "; technologyType Term Source not supplied", 1016, "validation missingData", 
						sdrfFileName, p.x, p.y, "checkAssays");
				} else if(! tsrStr.contains(assay.technologyType.termSourceREF)) {
					createEvent("Term Source REF, " +  assay.technologyType.termSourceREF + ", for technologyType " 
						+ assay.technologyType + " is not declared in the IDF", 6, "validation warning", 
						sdrfFileName, p.x, p.y +1, "checkAssays");
					check = true;
				}
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : assay.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(assayName);
		}
		return check;
	}

	boolean checkNodes(Collection<? extends SDRFNode> nodes) {
		boolean check = false;
		
		for (SDRFNode myNode : nodes) {
			String nodeName = myNode.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
//			List<ProtocolNode> protocols = new ArrayList<ProtocolNode>();
			Point p = findCell(nodeName, sdrfMap);
			// Set up error messages based on SDRFNode type.  
			String methodName = "";
			String fileType = "";
			if(myNode instanceof ScanNode){
				methodName = "checkScans";
				fileType = "scan";
			} else if(myNode instanceof ImageNode){
				methodName = "checkImages";
				fileType = "image";
			} else if(myNode instanceof NormalizationNode){
				methodName = "checkNormalization";
				fileType = "normalization entry";
				Set<Point> locations = mti.getLocationTracker().getSDRFLocations(myNode);
				Object[] alist = locations.toArray();
				if(alist.length == 0) {
					p = findCell(nodeName, sdrfMap);
				} else {
					Point tmp = (Point) alist[0];
					p = new Point(tmp.y,tmp.x);
				}
			}
			if(nodeName.equals("")) {
				createEvent("Warning: at least one " + fileType + " has no name", 1016, "validation warning",
					sdrfFileName, p.x, p.y, methodName);
				check = true;
			}

			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node child : myNode.getChildNodes()) {
				if (child instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) child;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(nodeName);
		}
		return check;
	}

	
	boolean checkScans(Collection<ScanNode> scans) {
		boolean check = false;
		
		for (ScanNode scan : scans) {
			String scanName = scan.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
//			List<ProtocolNode> protocols = new ArrayList<ProtocolNode>();
			Point p = findCell(scanName, sdrfMap);
			if(scanName.equals("")) {
				createEvent("Warning: at least one scan has no scan name", 1016, "validation warning",
					sdrfFileName, p.x, p.y, "checkScans");
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : scan.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(scanName);
		}
		return check;
	}

	boolean checkImages(Collection<ImageNode> images) {
		boolean check = false;
		for (ImageNode image : images) {
			String imageName = image.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
//			List<ProtocolNode> protocols = new ArrayList<ProtocolNode>();
			Point p = findCell(imageName, sdrfMap);
			if(imageName == null) {
				createEvent("Warning: at least one image has no image name", 1016, "validation warning",
						sdrfFileName, p.x, p.y, "checkScans");
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : image.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(imageName);
		}
		return check;
	}

	boolean checkArrayDesign(Collection<ArrayDesignNode> arrayDesigns) {
		boolean check = false;
		for (ArrayDesignNode arrayDesign : arrayDesigns) {
			String arrayDesignName = arrayDesign.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
//			List<ProtocolNode> protocols = new ArrayList<ProtocolNode>();
			Point p = findCell(arrayDesignName, sdrfMap);
			if(arrayDesignName.equals("")) {
				createEvent("Error: at least one arrayDesign has no arrayDesign name", 25,
					"validation error", sdrfFileName, p.x, p.y, "checkArrayDesign");
				check = true;
			}
			if(arrayDesign.termSourceREF != null && arrayDesign.termSourceREF.equals("")) {
				createEvent("Incomplete information for " + arrayDesignName 
					+ "; value for termSourceREF is missing", 1016, "validation warning",
					sdrfFileName, p.x, p.y +1, "checkArrayDesign");
			} else if(arrayDesign.termSourceREF == null){
				createEvent("Incomplete information for " + arrayDesignName 
					+ "; termSourceREF not supplied", 1016, "validation missingData",
					sdrfFileName, p.x, p.y, "checkArrayDesign");
			} else if(! tsrStr.contains(arrayDesign.termSourceREF)) {
				createEvent("Term Source REF, " +  arrayDesign.termSourceREF + ", for Array Design " 
					+ arrayDesignName + " is not declared in the IDF", 6, 
					"validation warning", sdrfFileName, p.x, p.y +1, "checkArrayDesign");						
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : arrayDesign.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(arrayDesignName);
		}
		return check;
	}

	boolean checkNormalization(Collection<NormalizationNode> normalization) {
		boolean check = false;
		
		for (NormalizationNode method : normalization) {
			String normalizationName = method.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(method);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(normalizationName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(normalizationName == null) {
				createEvent("Warning: at least one normalization entry has no name", 1016,
					"validation warning", sdrfFileName, p.x, p.y,
					"checkNormalization");
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : method.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(normalizationName);
		}
		return check;
	}

/*
 * 	2010-08-26: (jaw) changed parameter type for checkArrayData and checkArrayDataMatrix
 * to accept generic SDRFNode so that one method will handle both types of ArrayDataNode
 * and both types of ArrayDataMatrixNode. 
 */
//	boolean checkArrayData(Collection<ArrayDataNode> arrayData) {
	boolean checkArrayData(Collection<? extends SDRFNode> arrayData) {
		boolean check = false;
		String sourcePath;
//		Collection<ArrayDataNode> arrayData1 = new (Collection<ArrayDataNode>) arrayData;
//		for (ArrayDataNode dataSource : arrayData) {
		for (SDRFNode dataSource : arrayData) {
			String dataSourceName = dataSource.getNodeName();
			if(dataSourceName.lastIndexOf(File.separatorChar) > 0) {
				sourcePath = dataSourceName.substring(0, dataSourceName.lastIndexOf(File.separatorChar));
				dataSourceName = dataSourceName.substring(dataSourceName.lastIndexOf(File.separatorChar)+1);
			} else {
				sourcePath = this.idfFileName.substring(0, this.idfFileName.lastIndexOf(File.separatorChar));
			}
			String sourceFullName = sourcePath + File.separatorChar + dataSourceName;
			System.out.println("DataSource: " + dataSourceName + "; " + sourceFullName);

			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
//			List<ProtocolNode> protocols = new ArrayList<ProtocolNode>();
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(dataSource);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(dataSourceName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			String methodName = "";
			String fileType = "";
			if(dataSource instanceof ArrayDataNode){
				methodName = "checkArrayData";
				fileType = "Array Data file";
			} else if(dataSource instanceof DerivedArrayDataNode){
				methodName = "checkDerivedArrayData";
				fileType = "Derived Array Data file";
			}
			if(dataSourceName == null) {
				createEvent("Error: at least one " + fileType + " entry has no name", 25,
					"validation error", sdrfFileName, p.x, p.y, methodName);
//					"validation error", sdrfFileName, p.x, p.y);
				check = true;
			}
			File f = new File(sourceFullName);
			if(! f.exists()) {
				createEvent(fileType + " " + dataSourceName + " is missing", 1031, 
					"validation error", sdrfFileName, p.x, p.y, methodName);
//					"validation warning", sdrfFileName, p.x, p.y);
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : dataSource.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(dataSourceName);
		}
		return check;
	}

//	boolean checkArrayDataMatrix(Collection<ArrayDataMatrixNode> arrayDataMatrix) {
	boolean checkArrayDataMatrix(Collection<? extends SDRFNode> arrayDataMatrix) {
		boolean check = false;
		
		String sourcePath;
//		for (ArrayDataMatrixNode dataSource : arrayDataMatrix) {
		for (SDRFNode dataSource : arrayDataMatrix) {
			String dataSourceName = dataSource.getNodeName();
			if(dataSourceName.lastIndexOf(File.separatorChar) > 0) {
				sourcePath = dataSourceName.substring(0, dataSourceName.lastIndexOf(File.separatorChar));
				dataSourceName = dataSourceName.substring(dataSourceName.lastIndexOf(File.separatorChar)+1);
			} else {
				sourcePath = this.idfFileName.substring(0, this.idfFileName.lastIndexOf(File.separatorChar));
			}
			String sourceFullName = sourcePath + File.separatorChar + dataSourceName;
			System.out.println("DataSource: " + dataSourceName + "; " + sourceFullName);

			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(dataSource);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(dataSourceName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			String methodName = "";
			String fileType = "";
			if(dataSource instanceof ArrayDataMatrixNode){
				methodName = "checkArrayDataMatrix";
				fileType = "Array Data matrix";
			} else if(dataSource instanceof DerivedArrayDataMatrixNode){
				methodName = "checkDerivedArrayDataMatrix";
				fileType = "Derived Array Data matrix";
			}
			if(dataSourceName == null) {
				createEvent("Error: at least one sample has no " + fileType + " name", 25,
					"validation error", sdrfFileName, p.x, p.y, methodName);
//					"validation error", sdrfFileName, sloc.y, p.y);
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : dataSource.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(dataVal == true) {
				Collection nodes;
				if(this.mti.SDRF.lookupNodes(SourceNode.class) != null) {
					nodes = this.mti.SDRF.lookupNodes(SourceNode.class);
				} else if(this.mti.SDRF.lookupNodes(SampleNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(SampleNode.class);
				} else if(this.mti.SDRF.lookupNodes(ExtractNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(ExtractNode.class);
				} else if(this.mti.SDRF.lookupNodes(LabeledExtractNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(LabeledExtractNode.class);
				} else if(this.mti.SDRF.lookupNodes(HybridizationNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(HybridizationNode.class);
				} else if(this.mti.SDRF.lookupNodes(AssayNode.class) != null) {
					nodes = this.mti.SDRF.lookupNodes(AssayNode.class);
				} else {
					nodes = new ArrayList<LabeledExtractNode>();
					createEvent("Unable to match Biomaterial names to " + fileType + " columns",1024,
						"validation error", sdrfFileName, p.x, p.y, methodName);
//						"validation error", sdrfFileName, sloc.y, p.y);
				}
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(sourceFullName));
					StringSplitter ss = new StringSplitter((char)0x09);
					String currentLine;
					int ctr = 0;
					Hashtable<String, Integer> header1 = new Hashtable<String, Integer>();
					ArrayList<String> header2 = new ArrayList<String>();
					while ((currentLine = br.readLine()) != null) {
						ctr++;
//				          fix empty tabbs appending to the end of line by wwang; removes tabs at end of line.
			            while(currentLine.endsWith("\t")){
			            	currentLine=currentLine.substring(0,currentLine.length()-1);
			            }
			            ss.init(currentLine);
			            int fctr = 0;
			            if(ctr == 1) {
			            	//throw away first row header
			            	fctr++;
			            	ss.nextIntToken();
				            while(ss.hasMoreTokens()) {
				            	fctr++;
				            	String value = ss.nextToken();
				            	if(! header1.containsKey(value)) {
				            		header1.put(value, fctr);
				            	}
				            }
				            //if(nodes 
			            	for(Object n : nodes) {
			            		String name = "";
			            		if(n instanceof SourceNode) {
			            			name = ((SourceNode) n).getNodeName();
			            		} else if(n instanceof SampleNode) {
			            			name = ((SampleNode) n).getNodeName();
			            		} else if(n instanceof ExtractNode) {
			            			name = ((ExtractNode) n).getNodeName();
			            		} else if(n instanceof LabeledExtractNode) {
			            			name = ((LabeledExtractNode) n).getNodeName();
			            		} else if(n instanceof HybridizationNode) {
			            			name = ((HybridizationNode) n).getNodeName();
			            		} else if(n instanceof AssayNode) {
			            			name = ((AssayNode) n).getNodeName();
			            		} else {
			            			name = "[is null]";		            			
			            		}
			            		if(! header1.containsKey(name)) {
			            			createEvent("BioMaterial name " + name 
			            			+ " not found in Array Data matrix file " 
			            			+ dataSourceName, 1024, "validation error",
			            			sdrfFileName, p.x, p.y, methodName);
//			            			sdrfFileName, sloc.y, p.y);
			            		}
			            	}			            
			            } else if(ctr ==2) {
			            	//throw away first row header
			            	fctr++;
			            	ss.nextIntToken();
			            	header2.add("null");
				            while(ss.hasMoreTokens()) {
				            	fctr++;
				            	String value = ss.nextToken();
				            	if(value.contains("value")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("Value")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("VALUE")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("Signal")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("signal")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("ratio")) {
					            	header2.add("number");			            	
				            	} else {
					            	header2.add("string");			            	
				            	}
				            }
			            } else {
			            	//discard first cell value; row header
			            	fctr++;
			            	ss.nextToken();
				            while(ss.hasMoreTokens()) {
				            	fctr++;
				            	String value = ss.nextToken();
				            	String dtype = header2.get(fctr - 1);
				            	if(value != null) {
					            	try{
					            		if(dtype.equals("number")) {
						            		//this must be split up and handled better
					            			if(! value.contains("e")) {
						            			if(! value.contains(".") && ! value.contains(",")){
								            		int i = Integer.parseInt(value);				            				
						            			} else {
								            		float f = Float.parseFloat(value);				            				
						            			}				            				
					            			} else {
					            				//todo
					            			}
					            		}
//					            		System.out.println(ctr + ", " + fctr + ", " + value);
					            	} catch(NumberFormatException nfe) {
					            		createEvent("Number Format Exception, value= " + value, 1038, 
					            			"validation warning", dataSourceName, ctr, fctr, 
					            			methodName);
					            		System.out.println("NumberFormatException in " + dataSourceName 
					            			+ "; line " + ctr + ", column " + fctr + "; value " + value);
					            	}			            		
				            	} else {
				            		createEvent("Empty data cell in " + fileType, 1038, 
				            			"validation warning", dataSourceName, ctr, fctr, 
				            			methodName);			            		
				            	}			            
				            }
			            }
					}
				} catch (FileNotFoundException e) {
					createEvent("Data file " + dataSourceName + " was not found", 8, 
						"validation error", sdrfFileName, p.x, p.y, 
						methodName);
				} catch (IOException ioe) {
					createEvent("Data file " + dataSourceName + " was not readable", 8, 
						"validation error", sdrfFileName, p.x, p.y, 
						methodName);
				}				
			}
			if(check == true) System.out.println(dataSourceName);
		}
		return check;
	}

// No longer needed since generics now handle the raw vs. derived Nodes
/*
	boolean checkDerivedArrayData(Collection<DerivedArrayDataNode> derivedArrayData) {
		boolean check = false;
		String sourcePath;
		for (DerivedArrayDataNode dataSource : derivedArrayData) {
			String dataSourceName = dataSource.getNodeName();
			if(dataSourceName.lastIndexOf(File.separatorChar) > 0) {
				sourcePath = dataSourceName.substring(0, dataSourceName.lastIndexOf(File.separatorChar));
				dataSourceName = dataSourceName.substring(dataSourceName.lastIndexOf(File.separatorChar)+1);
			} else {
				sourcePath = this.idfFileName.substring(0, this.idfFileName.lastIndexOf(File.separatorChar));
			}
			String sourceFullName = sourcePath + File.separatorChar + dataSourceName;
			System.out.println("DataSource: " + dataSourceName + "; " + sourceFullName);

			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(dataSource);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(dataSourceName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(dataSourceName == null) {
				createEvent("Error: at least one Derived Array Data file entry has no name", 25,
					"validation error", sdrfFileName, p.x, p.y, "checkDerivedArrayData");
				check = true;
			}
			File f = new File(sourceFullName);
			if(! f.exists()) {
				createEvent("Derived Array Data file " + dataSourceName + " is missing", 1031, 
					"validation error", sdrfFileName, p.x, p.y, "checkDerivedArrayData");
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : dataSource.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(check == true) System.out.println(dataSourceName);
		}
		return check;
	}

	boolean checkDerivedArrayDataMatrix(Collection<DerivedArrayDataMatrixNode> derivedArrayDataMatrix) {
		boolean check = false;
		String sourcePath;
		for (DerivedArrayDataMatrixNode dadm : derivedArrayDataMatrix) {
			String dataSourceName = dadm.getNodeName();
			if(dataSourceName.lastIndexOf(File.separatorChar) > 0) {
				sourcePath = dataSourceName.substring(0, dataSourceName.lastIndexOf(File.separatorChar));
				dataSourceName = dataSourceName.substring(dataSourceName.lastIndexOf(File.separatorChar)+1);
			} else {
				sourcePath = this.idfFileName.substring(0, this.idfFileName.lastIndexOf(File.separatorChar));
			}
			String sourceFullName = sourcePath + File.separatorChar + dataSourceName;
			System.out.println("DataSource: " + dataSourceName + "; " + sourceFullName);

			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(dadm);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(dataSourceName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			if(dataSourceName == null) {
				//Note that the parser supplies Point as P(column,row); row = line.  Therefore sloc.y = line.
				createEvent("Error: at least one Derived Array Data Matrix file entry has no name", 25,
					"validation error", sdrfFileName, p.x, p.y, "checkDerivedArrayDataMatrix");
				check = true;
			}
			//changes for the new magetab_parser.jar
			//must check 'type' and lookup protocolref nodes in the SDRF
			boolean fail = false;
			for (Node node : dadm.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			
			if(dataVal == true) {
				Collection nodes;
				if(this.mti.SDRF.lookupNodes(SourceNode.class) != null) {
					nodes = this.mti.SDRF.lookupNodes(SourceNode.class);
				} else if(this.mti.SDRF.lookupNodes(SampleNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(SampleNode.class);
				} else if(this.mti.SDRF.lookupNodes(ExtractNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(ExtractNode.class);
				} else if(this.mti.SDRF.lookupNodes(LabeledExtractNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(LabeledExtractNode.class);
				} else if(this.mti.SDRF.lookupNodes(HybridizationNode.class) != null) {	 
					nodes = this.mti.SDRF.lookupNodes(HybridizationNode.class);
				} else if(this.mti.SDRF.lookupNodes(AssayNode.class) != null) {
					nodes = this.mti.SDRF.lookupNodes(AssayNode.class);
				} else {
					nodes = new ArrayList<LabeledExtractNode>();
					
					createEvent("Unable to match Biomaterial names to Derived Array Data matrix columns",1024,
						"validation error", sdrfFileName, p.x, p.y, "checkDerivedArrayDataMatrix");
				}
				BufferedReader br;
				try {
					br = new BufferedReader(new FileReader(sourceFullName));
					StringSplitter ss = new StringSplitter((char)0x09);
					String currentLine;
					int ctr = 0;
					Hashtable<String, Integer> header1 = new Hashtable<String, Integer>();
					ArrayList<String> header2 = new ArrayList<String>();
					while ((currentLine = br.readLine()) != null) {
						ctr++;
//				          fix empty tabbs appending to the end of line by wwang; removes tabs at end of line.
			            while(currentLine.endsWith("\t")){
			            	currentLine=currentLine.substring(0,currentLine.length()-1);
			            }
			            ss.init(currentLine);
			            int fctr = 0;
			            if(ctr == 1) {
			            	//throw away first row header
			            	fctr++;
			            	ss.nextIntToken();
				            while(ss.hasMoreTokens()) {
				            	fctr++;
				            	String value = ss.nextToken();
				            	if(! header1.containsKey(value)) {
				            		header1.put(value, fctr);
				            	}
				            }
				            //if(nodes 
			            	for(Object n : nodes) {
			            		String name = "";
			            		if(n instanceof SourceNode) {
			            			name = ((SourceNode) n).getNodeName();
			            		} else if(n instanceof SampleNode) {
			            			name = ((SampleNode) n).getNodeName();
			            		} else if(n instanceof ExtractNode) {
			            			name = ((ExtractNode) n).getNodeName();
			            		} else if(n instanceof LabeledExtractNode) {
			            			name = ((LabeledExtractNode) n).getNodeName();
			            		} else if(n instanceof HybridizationNode) {
			            			name = ((HybridizationNode) n).getNodeName();
			            		} else if(n instanceof AssayNode) {
			            			name = ((AssayNode) n).getNodeName();
			            		} else {
			            			name = "[is null]";		            			
			            		}
			            		if(! header1.containsKey(name)) {
			            			createEvent("BioMaterial name " + name 
					            		+ " not found in Derived Array Data matrix file " 
					            		+ dataSourceName, 1024, "validation error",
					            		sdrfFileName, p.x, p.y, "checkDerivedArrayDataMatrix");
//					            		sdrfFileName, sloc.y, p.y);
			            		}
			            	}			            
			            } else if(ctr ==2) {
			            	//throw away first row header
			            	fctr++;
			            	ss.nextIntToken();
			            	header2.add("null");
				            while(ss.hasMoreTokens()) {
				            	fctr++;
				            	String value = ss.nextToken();
				            	if(value.contains("value")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("Value")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("VALUE")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("Signal")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("signal")) {
					            	header2.add("number");			            	
				            	} else if(value.contains("ratio")) {
					            	header2.add("number");			            	
				            	} else {
					            	header2.add("string");			            	
				            	}
				            }
			            } else {
			            	//discard first cell value; row header
			            	fctr++;
			            	ss.nextToken();
				            while(ss.hasMoreTokens()) {
				            	fctr++;
				            	String value = ss.nextToken();
				            	String dtype = header2.get(fctr - 1);
//				            	System.out.println("data type and value for " + fctr + ": " + dtype 
//				            			+ " " + value);
				            	if(value != null) {
					            	try{
					            		if(dtype.equals("number")) {
						            		//this must be split up and handled better
					            			if(! value.contains("e")) {
						            			if(! value.contains(".") && ! value.contains(",")){
								            		int i = Integer.parseInt(value);				            				
						            			} else {
								            		float f = Float.parseFloat(value);				            				
						            			}				            				
					            			} else {
					            				//todo
					            			}
					            		}
//					            		System.out.println(ctr + ", " + fctr + ", " + value);
					            	} catch(NumberFormatException nfe) {
					            		createEvent("Number Format Exception, value= " + value, 1038, 
					            			"validation warning", dataSourceName, ctr, fctr, 
					            			"checkDerivedArrayDataMatrix");
					            		System.out.println("NumberFormatException in " + dataSourceName 
					            			+ "; line " + ctr + ", column " + fctr + "; value " + value);
					            	}			            		
				            	} else {
				            		createEvent("Empty data cell in data matrix", 1038, 
				            			"validation warning", dataSourceName, ctr, fctr, 
				            			"checkDerivedArrayDataMatrix");			            		
				            	}			            
				            }
			            }
					}
				} catch (FileNotFoundException e) {
					createEvent("Data file " + dataSourceName + " was not found", 8, 
						"validation error", sdrfFileName, p.x, p.y, "checkDerivedArrayDataMatrix");
				} catch (IOException ioe) {
					createEvent("Data file " + dataSourceName + " was not readable", 8, 
						"validation error", sdrfFileName, p.x, p.y, "checkDerivedArrayDataMatrix");
				}
			}

			if(check == true) System.out.println(dataSourceName);
		}
		return check;
	}
*/

	boolean checkProtocols(List<ProtocolApplicationNode> protocols) {
		boolean check = false;
		for (ProtocolApplicationNode protocol : protocols) {
			
			String protocolAppName = protocol.getNodeName();
			String parentName = null;
			if(protocolAppName.lastIndexOf(File.separatorChar) > 0) {
				parentName = protocolAppName.substring(0, protocolAppName.indexOf(':'));
			} else {
				parentName = "";
			}
			String protocolName = protocol.protocol;
/*
			Collection<Node> parents = protocol.getParentNodes();
			parentName = null;
			for(Node myParent: parents) {
				parentName = myParent.getNodeName();
				Set<Point> parentLoc = mti.getLocationTracker().getSDRFLocations(protocol);
				Object[] alist = locations.toArray();
			}
*/
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(protocol);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(protocolName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}

			if(protocolName.equals("")) {
				createEvent("Error: at least one protocol has no protocol name", 25,
					"validation error", sdrfFileName, p.x, p.y, "checkProtocols");
				check = true;
			}
			if(protocol.date != null && protocol.date.equals("")) {
				createEvent("Incomplete information for " + protocolName + "; value for date is missing", 
					1016, "validation warning", sdrfFileName, p.x, p.y, "checkProtocols");
			} else if(protocol.date == null){
				createEvent("Incomplete information for " + protocolName + "; date not supplied", 1016, 
					"validation missingData", sdrfFileName, p.x, p.y, "checkProtocols");
			} else {
				boolean fail = checkDateTag(protocolName, protocol.date);
				if(fail = true) check = true ;
			}
			if(protocol.termSourceREF != null && protocol.termSourceREF.equals("")) {
				createEvent("Incomplete information for " + protocolName 
					+ "; value for termSourceREF is empty", 1016, 
					"validation warning", sdrfFileName, p.x, p.y, "checkProtocols");
			} else if(protocol.termSourceREF == null){
				createEvent("Incomplete information for " + protocolName 
					+ "; termSourceREF is missing", 1016, 
					"validation missingData", sdrfFileName, p.x, p.y, "checkProtocols");
			} else if(! tsrStr.contains(protocol.termSourceREF)) {
				createEvent("Term Source REF, " +  protocol.termSourceREF + ", for Protocol " 
					+ protocolName + " is not declared in the IDF", 6, 
					"validation warning", sdrfFileName, p.x, p.y, "checkProtocols");
				check = true;
			}
			if(protocol.performer != null && protocol.performer.toString().equals("")) {
				createEvent("Incomplete information for " + protocolName 
					+ "; value for performer is missing", 1016, "validation warning",
					sdrfFileName, p.x, p.y, "checkProtocols");
			} else if(protocol.performer == null){
				createEvent("Incomplete information for " + protocolName 
					+ "; performer not supplied", 1016, "validation missingData",
					sdrfFileName, p.x, p.y, "checkProtocols");
			}
			List<ParameterValueAttribute> paramList = protocol.parameterValues;
			for (ParameterValueAttribute attr : paramList) {
				String attrType = attr.type;
				Point a = findCell(attr.getNodeName(),sdrfMap);

				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for " + protocolName 
						+ "; Parameter Value " + attrType + " nas no value", 1016, 
						"validation warning", sdrfFileName, p.x, a.y +1, "checkProtocols");
				}
				if(attr.unit != null && attr.unit.getNodeName().equals("")) {
					createEvent("Incomplete information for " + protocolName 
						+ "; Parameter Value " + attrType + " has no Units", 1016, 
						"validation warning", sdrfFileName, p.x, a.y +1, "checkProtocols");
				} else if(attr.unit != null) {
					Point u = findCell(attr.unit.getNodeName(),sdrfMap);
					if(attr.unit.termSourceREF != null && attr.unit.termSourceREF.equals("")) {
						createEvent("Incomplete information for " + protocolName 
							+ "; Parameter Value " + attrType + " has no Unit Term Source", 1005, 
							"validation warning", sdrfFileName, p.x, u.y +1, "checkProtocols");
					} else if(attr.unit.termSourceREF == null){
						createEvent("Incomplete information for " + attr.unit 
							+ "; termSourceREF not supplied", 1016, "validation missingData",
							sdrfFileName, p.x, u.y, "checkProtocols");
					} else if(! tsrStr.contains(attr.unit.termSourceREF)) {
						createEvent("Term Source REF, " +  attr.unit.termSourceREF 
							+ ", for ParameterValue " + attr.unit.getNodeName() 
							+ " is not declared in the IDF", 6, "validation warning",
							sdrfFileName, p.x, u.y +1, "checkProtocols");
						check = true;
					}
				}
			}
			if(check == true) System.out.println(protocolName);
		}
		return check;
	}

	
	/**
	 * Checks the References between IDF and SDRF:
	 * ExperimentalFactor <-> FactorValue
	 * Protocol <-> ProtocolREF
	 * Parameter <-> ParameterValue
	 * 
	 * @param mti
	 * @return true if there are any missing or undeclared values
	 */
	protected boolean checkRefs(MAGETABInvestigation mti) {
		boolean fail = true;
		//Use locLine to cover for null sloc; set to sloc.y or p.x
		int locLine = 0; 
		/**
		 * Check ExperimentalFactor <=> FactorValue header
		 * check ProtocolName <=> Protocol REF
		 * check ParameterValue <=> ParameterValue
		 * Note: Must validate IDF TermSoureName <=> SDRF TermSourceREF in each 
		 * instance rather than as a group because TSR is not a node or an 
		 * attribute, but is a slot on a node.  
		*/
		List<String> ef = mti.IDF.experimentalFactorName;
		String efStr = ef.toString();
		//usedEF is to check for declared factors that are not used in SDRF
		ArrayList<String> usedEF = new ArrayList<String>();
		List<String> pn = mti.IDF.protocolName;
		String pnStr = pn.toString();
		List<String> pv = mti.IDF.protocolParameters;
		String pvStr = pv.toString();
		
		//FactorValues
		Collection<HybridizationNode> hybridizations = mti.SDRF.lookupNodes(HybridizationNode.class);
		for(HybridizationNode hybridization: hybridizations) {
			String hybridizationName = hybridization.getNodeName();
			List<FactorValueAttribute> fvList = hybridization.factorValues;

			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(hybridization);
			Object[] alist = locations.toArray();
			// 2009-12-13: added guard against empty list --mm 
			Point sloc;
			if(alist.length == 0) {
				sloc = new Point();
			} else {
				sloc = (Point) alist[0];
			}
			for (FactorValueAttribute attr : fvList) {
				String attrName = attr.getNodeName();
				String attrType = attr.type;
				if(! usedEF.contains(attrType)) {
					usedEF.add(attrType);
				}
				Point p = findCell(attrName, sdrfMap);
				if(sloc.y == 0) {
					locLine = p.x;
				} else {
					locLine = sloc.y;
				}
				if(!efStr.contains(attrType)) {
					System.out.println("name: " + attrName + "; type: " + attrType);
					createEvent("Error: Factor value " + attrType + " is not declared in the IDF", 
						5, "validation error", sdrfFileName, locLine, p.y, "checkRefs");
					fail = true;
				}
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for Hybridization " + hybridizationName 
						+ "; Factor value " + attrType + " nas no value", 1027, 
						"validation warning", sdrfFileName, locLine, p.y, "checkRefs");
				}
			}
		}
		//check ef list for unused factors; report as warnings
		for(String s: ef) {
			if(! usedEF.contains(s)){
				Point p = findCell(s, idfMap);
				createEvent("Warning: ExperimentalFactor " + s + " is declared in the IDF but not used in the SDRF", 
					5, "validation warning", idfFileName, p.x, p.y, "checkRefs");
			}
		}
		
		ArrayList<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
		Collection<ProtocolApplicationNode> protAppNodes = mti.SDRF.lookupNodes(ProtocolApplicationNode.class);
		for (ProtocolApplicationNode protocol : protAppNodes) {
			protocols.add(protocol);
		}

		for(ProtocolApplicationNode protocol: protocols) {
			String protocolName = protocol.protocol;

			//Note: 2/9/10: getSDRFLocations is returning a null locations list
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(protocol);
			Object[] alist = locations.toArray();
			//Temporary patch to avoid null alist[] array.  
			Point sloc;
			if(alist.length == 0) {
				sloc = new Point();
			} else {
				sloc = (Point) alist[0];
			}

			if(!pnStr.contains(protocolName)) {
				Point p;
				//Catch "Unknown Protocol" due to parser problem
				if(protocolName.equalsIgnoreCase("Unknown Protocol")){
					p = new Point(0,0);
					locLine = p.x;
				} else {
					p = findCell(protocolName, sdrfMap);
					if(sloc.y == 0) {
						locLine = p.x;
					} else {
						locLine = sloc.y;
					}
					
				}
				System.out.println("Protocol name not found in Protocol list: " + protocolName);
				for(Node node: protocol.getParentNodes()){
					System.out.println(node.getNodeName() + ' ');
				}
				createEvent("Error: Protocol " + protocolName + " is not declared in the IDF", 
					7, "validation error", sdrfFileName, locLine, p.y, "checkRefs");
				fail = true;				
			}
			List<ParameterValueAttribute> pvList = protocol.parameterValues;
			for (ParameterValueAttribute attr : pvList) {
				String attrName = attr.getNodeName();
				String attrType = attr.type;
				Point p = findCell(attrName, sdrfMap);
				if(sloc.y == 0) {
					locLine = p.x;
				} else {
					locLine = sloc.y;
				}
				if(!pvStr.contains(attrType)) {
					System.out.println("name: " + attrName + "; type: " + attrType);
					createEvent("Error: Parameter value " + attrType + " is not declared in the IDF", 
						13, "validation error", sdrfFileName, locLine, p.y, "checkRefs");
					fail = true;
				}
				if(attr != null && attr.getNodeName().equals("")) { 
					createEvent("Incomplete information for Protocol " + protocolName 
							+ "; Parameter value " + attrType + " nas no value", 1016, 
							"validation warning", sdrfFileName, locLine, p.y, "checkRefs");
					fail = true;
				}
			}
		}
		
		if(testDebug == true) {
			ErrorItem event = eif.generateErrorItem("Check Refs test", ErrorCode.APPLICATION_TEST, 
					this.getClass());
//			annError.addErrorItem(event);
			fireErrorItemEvent(event);
			createEvent("test passed", 998, "checkRefs");
		}
		
		return fail;
	}
	
	/**
	 * checkSDRFExtensions
	 * 
	 * Provided as example method for code extenders to modify
	 * @param s: SDRF object
	 * @return: boolean for test failure
	 */
	protected boolean checkSDRFExtensions (SDRF s) {
		boolean fail = false;
		/*
		//////////////// Modify the code below ////////////////////////
		System.out.println("Validating Extension code");

		//Extenders must change the following NormalizationNode to get the class of nodes to test.
		Collection<NormalizationNode> normalization = s.lookupNodes(NormalizationNode.class);
		for (NormalizationNode method : normalization) {
			String nodeName = method.getNodeName();
			List<ProtocolApplicationNode> protocols = new ArrayList<ProtocolApplicationNode>();
			//Get the location of the node to be checked
			// NOTE: the getSDRFLocations method does not always return the line and column (alist == 0), 
			//therefore the findCell method is used to find them a different way.  
			Set<Point> locations = mti.getLocationTracker().getSDRFLocations(method);
			Object[] alist = locations.toArray();
			Point p;
			if(alist.length == 0) {
				p = findCell(nodeName, sdrfMap);
			} else {
				Point tmp = (Point) alist[0];
				p = new Point(tmp.y,tmp.x);
			}
			//Extenders must implement the test code here and set 'fail' appropriately
			//the basic test
			//Note: The Error Code, Error type, and message must be altered as appropriate.
			if(nodeName == null) {
				createEvent("Warning: at least one normalization entry has no name", 1016,
					"validation warning", sdrfFileName, p.x, p.y,
					"checkSDRFExtensions");
				fail = true;
			}
			//lookup protocolref nodes in the SDRF
			for (Node node : method.getChildNodes()) {
				if (node instanceof ProtocolApplicationNode) {
					ProtocolApplicationNode protocol = (ProtocolApplicationNode) node;
					if(protocol != null) {
						protocols.add(protocol);
					}
				}
			}
			fail = checkProtocols(protocols);
			//report error to StdOut 
			if(fail == true) System.out.println("Failed Extension code: " + nodeName);
		}
		///////////////////////// End code modifications ///////////////////////////
*/
		return fail;
	}
	
	protected boolean checkIDFExtensions (String label, IDF i) {
		boolean fail = false;
		////////////////Modify the code below ////////////////////////
/*		
		//Extenders must change the following personLastName to get the class of nodes to test.
		List<String> tag = i.personLastName;
		//Extenders must implement the test code here and set 'fail' appropriately
		//Note: The Error Code, Error type, and message must be altered as appropriate.
		if(tag == null || tag.equals("")) {
			Point p = findCell(label, idfMap);
			createEvent("IDF tag " + label + " is null", 1015, "validation warning",
					idfFileName, p.x, p.y, "checkArrayTag");
			fail = true;
		}
		////////////////////// End code modifications ///////////////////////////
*/
		return fail;
	}
	
	private Point findCell (String errDatum, Hashtable<Point, String> map) {
		Point errCell = new Point(-1,-1);
		//The keys are Point() objects. The values are String objects.
		Enumeration<Point> keys = map.keys();
		while(keys.hasMoreElements()) {
			Point p = keys.nextElement();
			String value = map.get(p);
			if(value.equalsIgnoreCase(errDatum)){
				errCell = p;
				break;
			}
		}	
		if(testDebug == true) {
			if(errCell.x == -1) {
				System.out.println(errDatum);
			}
		}
		return errCell;
	}
	
	private void createEvent(String comment, int code, String caller) {
		createEvent(comment, code, "validation error", caller);
	}
	
	private void createEvent(String comment, int code, String eType, String caller) {
		ErrorCode ec = ErrorCode.getErrorFromCode(code);
		String mesg = ec.getErrorMessage(); 
		ErrorItem event = eif.generateErrorItem(mesg, code, this.getClass());
		event.setComment(comment);
		event.setErrorType(eType);
		String fileName = null;
		if(mesg.contains("IDF") || mesg.contains("idf")) {
			fileName = idfFileName;
		} else if(mesg.contains("SDRF") || mesg.contains("sdrf")) {
			fileName = sdrfFileName;
		} else if(comment.contains("IDF") || comment.contains("idf")) {
			fileName = idfFileName;			
		} else if(comment.contains("SDRF") || comment.contains("sdrf")) {
			fileName = sdrfFileName;			
		} else {
			fileName = "";
		}
		event.setParsedFile(fileName);
		event.setCaller(caller);
//		annError.addErrorItem(event);
		fireErrorItemEvent(event);
	}

	/**
	 * Create an event with row and column information
	 * @param comment
	 * @param code
	 * @param eType: error type, One-of: validation error, validation warning, validation missingData
	 * @param fileName
	 * @param line
	 * @param column
	 */
	private void createEvent(String comment, int code, String eType, String fileName, int line, int column, String caller) {
		ErrorCode ec = ErrorCode.getErrorFromCode(code);
		String mesg = ec.getErrorMessage(); 
		ErrorItem event = eif.generateErrorItem(mesg, code, this.getClass());
		event.setComment(comment);
		event.setErrorType(eType);
		event.setLine(line);
		event.setCol(column);
		event.setParsedFile(fileName);
		event.setCaller(caller);
		fireErrorItemEvent(event);
	}
}
