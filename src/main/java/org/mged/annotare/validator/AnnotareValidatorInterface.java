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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.mged.magetab.error.ErrorItem;

import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;

public interface AnnotareValidatorInterface {

	/**
	 * need to handle the following 
	 * 
1.	Validate IDF only
2.	Validate SDRF only
3.	Validate ADF only
4.	Validate a complete file - report interdependent errors in IDF and SDRF
5.	Report warnings and errors, or report only warnings
6.	Validate a partial file e.g. do not report tags with missing data in IDF
7.	Validate data files
8.	Skip data files
9.	Skip semantic validation - do not report on an OE containing columns/rows
10.	Save file(s) and log warnings but save anyway
11.	Export errors and warnings to a file (or say where they have been saved)
12.	Run standalone; don't connect to any external resources for validation
			Validation Warnings
	 * 
	 */
	
	public BufferedReader openFile (File f) ;
	public BufferedWriter writeFile (String fileName);
	
	//For each MAGE-TAB file type validate and return a list of errors 
	public ArrayList<ErrorItem> parseIDF (File idfFile);
	public ArrayList<ErrorItem> parseSDRF (File sdrfFile);
	public ArrayList<ErrorItem> parseADF (File adfFile);
	public ArrayList<ErrorItem> parseDataFiles (File dataFileDir);
	//assume all files are in 'directory' and validate them
	public ArrayList<ErrorItem> validateAll (String IDF_filename);
	public ArrayList<ErrorItem> semanticValidation (MAGETABInvestigation I);

	/**
	 * writeReport uses the validationErrors vector to flesh out a report 
	 * and send it to a disk file with the supplied name
	 * 
	 * @param: ofName: output file name (Need to modify for path)
	 */
	public void writeReport (String ofName) throws IOException ;
	
	public void readErrorMesgFile (File f);
	
	//collect error list from AnnotareError
	public ArrayList<ErrorItem> collectErrors();

	//return warning messages along with errors
	public void setWarningsOff () ;
	public void setWarningsOn () ;
	public boolean getWarnings () ;
	//do not validate missing data, ie don't report missing data; for partial file validation
	public void setMissingDataOff () ;
	public void setMissingDataOn () ;
	public boolean getMissingData () ;

	//skip data file validation
	public void setDataValidatinOff () ;
	public void setDataValidatinOn () ;
	public boolean getDataValidatin () ;
	//skip ontology resource validation
	public void setOntologyValidationOff () ;
	public void setOntologyValidationOn () ;
	public boolean getOntologyValidation () ;
	
}
