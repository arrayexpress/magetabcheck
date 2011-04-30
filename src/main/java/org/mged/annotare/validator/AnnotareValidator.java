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

import org.mged.magetab.error.ErrorItem;
import org.mged.magetab.error.ErrorItemFactory;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.IDF;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.MAGETABInvestigation;
import uk.ac.ebi.arrayexpress2.magetab.datamodel.SDRF;
import uk.ac.ebi.arrayexpress2.magetab.exception.ParseException;
import uk.ac.ebi.arrayexpress2.magetab.exception.ValidateException;
import uk.ac.ebi.arrayexpress2.magetab.listener.ErrorItemListener;
import uk.ac.ebi.arrayexpress2.magetab.parser.IDFParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.MAGETABParser;
import uk.ac.ebi.arrayexpress2.magetab.parser.SDRFParser;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class AnnotareValidator
        implements AnnotareValidatorInterface {

    boolean debug = false;                //for debugging code if-blocks
    boolean warnings = true;            //for empty data items
    boolean missingData = false;        //for missing nodes/attributes
    boolean dataValidation = true;        //for data file validation
    boolean ontologyValidation = false;    //for ontology term verification
    //must remove the File objects and path references to use InputStream
    String errorMesgList = "AnnotareErrorMessages.txt";    //must get a path somewhere
    String idfPath;
    File idfFile;
    File sdrfFile;
    File errorListFile;        //File containing full list of error Codes and messages
    File eMesgs;            //error codes and messages for AnnotareError
    MAGETABInvestigation investigation;
    AnnotareError annErr;
    //Note: with each successive upload to the Annotare google code site, increment before upload
    private String version = "1.2.2010-11-08";    //major.miner.date(yyyymmdd)

    public AnnotareValidator() {
        investigation = new MAGETABInvestigation();
        this.annErr = new AnnotareError();
        //Create a File object for the error Codes
        errorListFile = new File(errorMesgList);

    }

    //Annotare will have to pass in an ObjectStream
    public AnnotareValidator(String inIDFFileName) {
        this(inIDFFileName, 1);
    }

    /**
     * Additonal constructor to accept verosity level in log output 0: all errors and warnings 1: all errors and empty
     * data items 2: all errors and missing attributes 3: all errors without empty data items and missing attributes
     *
     * @param inIDFFileName
     * @param verbosity
     */
    public AnnotareValidator(String inIDFFileName, int verbosity) {
        this();
        this.idfFile = new File(inIDFFileName);
        setVerbosity(verbosity);
        validateAll(inIDFFileName);
/*		For future use:
		ArrayList<ErrorItem> elist;
		sdrfFile = findSDRF(inIDFFileName);
		if(sdrfFile == null) {
			parseIDF (this.idfFile);
		} else {
			validateAll(inIDFFileName);
		}
*/
    }

    public String getVersion() {
        return this.version;
    }

    /**
     * setVerbosity allows the caller to set the level of ErrorItem and log output 0: all errors and warnings 1: all
     * errors and empty data items 2: all errors and missing attributes 3: all errors without empty data items and
     * missing attributes
     *
     * @param: verbosity (int)
     * @return: void
     */
    public void setVerbosity(int verbosity) {
        switch (verbosity) {
            case 0:
                warnings = true;
                missingData = true;
                break;
            case 1:
                warnings = true;
                missingData = false;
                break;
            case 2:
                warnings = false;
                missingData = true;
                break;
            case 3:
                warnings = false;
                missingData = false;
                break;
            default:
                warnings = true;
                missingData = false;
                break;
        }
    }

    //The following becomes unneccesary when ObjectStreams are implemented

    /**
     * findSDRF checks investigation for sdrfFile, and imputes the SDRF name if no SDRF file name is available
     *
     * @param: IDF file name
     * @return: return the SDRF file name
     */
    private File findSDRF(String idfName) {
        if (idfName == null) {
            System.out.println("IDF file name not supplied or is null; SDRF not found.");
            return null;
        }
        String sdrfName = null;
        this.idfFile = new File(idfName);
        idfPath = idfFile.getParent();
        File sdrfFile = null;
        IDFParser idfParser = new IDFParser();
        if (idfFile != null) {
            URL fileURL;
            try {
                fileURL = new URL("file:///" + idfFile.getAbsolutePath());
                IDF idf = idfParser.parse(fileURL);
                if (idf.sdrfFile != null && idf.sdrfFile.size() > 0) {
                    sdrfName = idf.sdrfFile.get(0);
                }
                idf = null;
            }
            catch (NullPointerException npe) {
                npe.printStackTrace();
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            catch (ParseException pe) {
                pe.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sdrfFile = new File(idfPath + File.separatorChar + sdrfName);
            }
            catch (NullPointerException npe) {
                //do nothing until after next test
            }
        }
        if (sdrfName == null) {
            String textI = null;
            String textS = null;
            if (idfName.endsWith("idf.txt")) {
                textI = "idf.txt";
                textS = "sdrf.txt";
            }
            else if (idfName.endsWith("IDF.txt")) {
                textI = "IDF.txt";
                textS = "SDRF.txt";
            }
            else if (idfName.endsWith("IDF")) {
                textI = "IDF";
                textS = "SDRF";
            }
            else if (idfName.endsWith("idf")) {
                textI = "idf";
                textS = "sdrf";
            }
            else if (idfName.contains("idf")) {
                String suffix = idfName.substring(idfName.indexOf("idf") + 3);
                textI = "idf" + suffix;
                textS = "sdrf" + suffix;
            }
            else {
                textI = "";
                textS = "";
            }
            String basename = null;
            if (idfName.contains(textI)) {
                basename = idfName.substring(0, idfName.lastIndexOf(textI));
            }
            else {
                basename = idfName + ".";
            }
            sdrfName = basename + textS;
            try {
                sdrfFile = new File(sdrfName);
            }
            catch (NullPointerException npe) {
                //do nothing
            }
        }
        if (!sdrfFile.exists()) {
            sdrfFile = null;
            String comment = "No SDRF file was listed in the IDF.";
            String mesg = "The sdrfFile tag in the IDF is empty or has no resolvable value";
            String eType = "walidation error";
            int code = 27;
            ErrorItemFactory eif = ErrorItemFactory.getErrorItemFactory();
            ErrorItem event = eif.generateErrorItem(mesg, code, this.getClass());
            event.setComment(comment);
            event.setErrorType(eType);
            event.setCol(1);
            event.setLine(0);
            System.out.println(event.toString());
            annErr.addErrorItem(event);
            System.out.println(mesg);
        }
        else {
            this.sdrfFile = sdrfFile;
            System.out.println("SDRF file name: " + sdrfName);
        }
        return sdrfFile;
    }

    public InputStreamReader openStream(ObjectInputStream ois) {
        InputStreamReader br;
        try {
            br = new InputStreamReader(new ObjectInputStream(ois));
            return br;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedReader openFile(File f) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(f));
            return br;
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BufferedWriter writeFile(String fname) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fname));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return bw;
    }

    public void append2Log(String fName, String appendMe) {
        BufferedWriter log = writeFile(fName);
        try {
            System.out.println("appendMe: " + appendMe);
            log.write(appendMe);
            log.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Allow caller to request list of errors Returns 'validationErrors' from AnnotareError elist : list of ErrorItem
     * objects
     */
    public ArrayList<ErrorItem> collectErrors() {
        ArrayList<ErrorItem> elist = annErr.getAllItems();
        return elist;
    }

    /**
     * writeReport uses the validationErrors vector to flesh out a report and send it to a disk file with the supplied
     * name
     *
     * @param: ofName: output file name (Need to modify for path)
     */
    public void writeReport(String ofName) throws IOException {
        File f = new File(ofName);
        BufferedWriter report = new BufferedWriter(new FileWriter(f), 1024 * 128);

        Date d = new Date();
        String appVersion = new String("Annotare validator: " + this.getVersion());
        report.write(d + "\t" + appVersion + "\n");
        report.write("Resource file: " + errorListFile.getPath() + "\n");
        report.write("Report output file: " + f.getPath() + "\n");
        report.write("\n");
        ArrayList<ErrorItem> elist = annErr.getErrorList();
        report.write("Error list size: " + elist.size() + "\n");
        System.out.println("Error list size: " + elist.size());
//		report.write("\nError Code\tError Type\tParsed File\tLine\tColumn\tCaller\tMessage\tComment\tLink\n");
        report.write("Error Code\tLine\tColumn\tMessage\tComment\n");

        ArrayList<ErrorItem> al = annErr.getOrderedErrors();
        for (ErrorItem ei : al) {
            try {
                report.write(ei.reportString());
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if (warnings == true || missingData == true) {
            //get the warning list from annErr.validationWarnings
            ArrayList<ErrorItem> wlist = annErr.getWarningList();
            System.out.println("Warning list size: " + wlist.size());
            report.write("\nWarning list size: " + wlist.size() + "\n");
            report.write("Error Code\tLine\tColumn\tMessage\tComment\n");

            al.clear();
            al = annErr.getOrderedWarnings();
            for (ErrorItem ei : al) {
                try {
                    report.write(ei.reportString());
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        report.close();
    }

    /**
     * write ErrorCode list to file
     */
    public void writeErrorCodeList(String outFileName) throws IOException {
        annErr.writeErrorCodeList(outFileName);
    }

    //For each MAGE-TAB file type validate and return a list of errors
    public ArrayList<ErrorItem> parseIDF(File idfFile) {
        clearErrorLists();

        String idfFileName = idfFile.getAbsolutePath();
        idfPath = idfFile.getParent();
        String parseLog = idfFile.getName() + ".log";
        String logFullName = idfPath + File.separatorChar + parseLog;
        IDFParser idfParser = new IDFParser();
        idfParser.addErrorItemListener(getListener());
        String path = idfFile.getParent();
        try {
            URL idfURL = new URL("file:///" + idfFile.getAbsolutePath());
            System.out.println("IDF URL: " + idfURL.toString());
            investigation.IDF.setLocation(idfURL);
            idfParser.parse(idfURL.openStream(), this.investigation.IDF);
        }
        catch (MalformedURLException mfu) {
            System.out.println("Could not find file, "
                                       + idfFile.getName() + ".");
            mfu.printStackTrace();
        }
        catch (ValidateException e) {
            for (ErrorItem err : e.getErrorItems()) {
                annErr.addErrorItem(err);
            }
            e.printStackTrace();
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        semanticValidation(this.investigation);

        return annErr.getErrorList();
    }

    public ArrayList<ErrorItem> parseSDRF(File sdrfFile) {
        clearErrorLists();
        SDRFParser sdrfParser = new SDRFParser();
        sdrfParser.addErrorItemListener(getListener());
        String path = sdrfFile.getParent();
        URL sdrfURL;
        try {
            sdrfURL = new URL("file:///" + path + File.separatorChar + sdrfFile.getAbsolutePath());
            System.out.println("SDRF URL: " + sdrfURL.toString());
            investigation.SDRF.setLocation(sdrfURL);
            sdrfParser.parse(sdrfURL.openStream(), this.investigation.SDRF);
        }
        catch (MalformedURLException mfu) {
            System.out.println("Could not find file, "
                                       + sdrfFile.getName() + ".");
            mfu.printStackTrace();
        }
        catch (ValidateException e) {
            for (ErrorItem err : e.getErrorItems()) {
                annErr.addErrorItem(err);
            }
            e.printStackTrace();
        }
        catch (ParseException pe) {
            pe.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
//        semanticValidation(this.investigation);

        return annErr.getErrorList();
    }

    public ArrayList<ErrorItem> parseADF(File adfFile) {
        return annErr.getErrorList();
    }

    public ArrayList<ErrorItem> parseDataFiles(File dataFileDir) {
        return annErr.getErrorList();
    }

    /**
     * Note: Annotare calls the validator as follows:
     * <p/>
     * > AnnotareValidator av = new AnnotareValidator(); this.clearErrorLists(); this.setVerbosity(0);
     * this.validateAll(inIDFFileName); > av.validateAll(inIDFFileName); > av.collectErrors();
     */

    //assume all files are in 'directory' and validate them
    public ArrayList<ErrorItem> validateAll(String inIDFFileName) {
        clearErrorLists();
        System.out.println("AnnotareValidator version: " + this.getVersion());
        if (inIDFFileName.lastIndexOf(File.separatorChar) < 0) {
            //in case of bare file name with no path
            inIDFFileName = "." + File.separatorChar + inIDFFileName;
        }
        try {
            this.idfFile = new File(inIDFFileName);
            if (!idfFile.exists()) {
                System.out.println("File " + inIDFFileName + " does not exist.");
                throw new FileNotFoundException();
            }
        }
        catch (Exception e) {
            String comment = "No IDF file was supplied.";
            String mesg = "IDF file is required.";
            String eType = "walidation error";
            int code = 10;
            ErrorItemFactory eif = ErrorItemFactory.getErrorItemFactory();
            ErrorItem event = eif.generateErrorItem(mesg, code, this.getClass());
            event.setComment(comment);
            event.setErrorType(eType);
            event.setCol(0);
            event.setLine(0);
            event.setCaller("AnnotareValidator.validateAll");
            System.out.println(event.toString());
            annErr.addErrorItem(event);
            return null;
        }
        idfPath = idfFile.getParent();
        String parseLog = idfFile.getName() + ".log";
        String logFullName = idfPath + File.separatorChar + parseLog;
        this.sdrfFile = findSDRF(inIDFFileName);
        //instantiate the validator and pass it to the parser, instead of 'null'
        // no longer necessary to instantiate and pass, just create parser instead
//        SemanticValidator sv = new SemanticValidator(inIDFFileName);
//        MAGETABParser mageTabParser = new MAGETABParser(sv);
        MAGETABParser mageTabParser = new MAGETABParser();
        mageTabParser.addErrorItemListener(getListener());
        /*
           * class loader that can load class from different jar files
           * also common interface for all of these files
           * declare SV as a generic and use class loader to choose SV based on what's available
           */
//        if (this.dataValidation == true) {
//            sv.setDataValOn();
//        }
//        else {
//            sv.setDataValOff();
//        }
        if (idfFile != null) {
            URL fileURL;
            try {
                fileURL = new URL("file:///" + idfFile.getAbsolutePath());
                investigation.IDF.setLocation(fileURL);
                mageTabParser.parse(fileURL.openStream(), investigation);
//				ArrayList al = collectErrors();
//				append2Log(logFullName, al.toString());
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            catch (ParseException pe) {
                pe.printStackTrace();
            }
            catch (Exception e) {
                String mesg = "Parse failure due to an unforeseen format or event.";
                ErrorItemFactory eif = ErrorItemFactory.getErrorItemFactory();
                ErrorItem event = eif.generateErrorItem(mesg, 999, this.getClass());
                event.setComment(e.getMessage());
                event.setErrorType("walidation error");
                annErr.addErrorItem(event);
                System.out.println(mesg);
                e.printStackTrace();
            }
        }
//		if(this.debug == true) {
        ArrayList<ErrorItem> developerLogList = annErr.getAllItems();//collectErrors();
        System.out.println("Full validation list size() " + developerLogList.size());
        BufferedWriter bw = writeFile(inIDFFileName + ".validator.log");
        try {
            bw.write("AnnotareValidator version: " + this.getVersion() + "\n");
            for (ErrorItem ei : developerLogList) {
                bw.write(ei.toString());
            }
            bw.close();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
//		}
        try {
            writeReport(logFullName);
        }
        catch (IOException ioe) {
            System.out.println("Unable to create parse log" + logFullName);
            ioe.printStackTrace();
        }
        return annErr.getAllItems();
    }

    /**
     * clears AnnotareError validationErrors and validationWarnings
     */
    public void clearErrorLists() {
        annErr.clearAllItems();
    }

    //Call AnnotareError.writeReport() to produce a local file containing a formated error list
    public void generateReport() {
    }

    public void readErrorMesgFile(File f) {
    }

    public AnnotareError getAnnErr() {
        return this.annErr;
    }

    public boolean getDebug() {
        return debug;
    }

    public void setDebugOn() {
        debug = true;
    }

    public void setDebugOff() {
        debug = false;
    }

    public void setWarningsOff() {
        warnings = false;
    }

    public void setWarningsOn() {
        warnings = true;
    }

    public boolean getWarnings() {
        return warnings;
    }

    public void setMissingDataOff() {
        missingData = false;
    }

    public void setMissingDataOn() {
        missingData = true;
    }

    public boolean getMissingData() {
        return missingData;
    }

    public boolean getDataValidatin() {
        return dataValidation;
    }

    public boolean getOntologyValidation() {
        return ontologyValidation;
    }

    public void setDataValidatinOff() {
        dataValidation = false;
    }

    public void setDataValidatinOn() {
        dataValidation = true;
    }

    public void setOntologyValidationOff() {
        ontologyValidation = false;
    }

    public void setOntologyValidationOn() {
        ontologyValidation = true;
    }

    public void setErrorMesgListFileName(String fName) {
        this.errorMesgList = fName;
    }

    public String getErrorMesgListFileName() {
        return this.errorMesgList;
    }

    public String getIDFFileName() {
        return idfFile.getName();
    }

    public String getSDRFFileName() {
        return sdrfFile.getName();
    }

    /*
      * Note from Junmin: method used in merapi call to validator
              AnnotareValidator av = new AnnotareValidator();
             av.clearErrorLists();
             av.setVerbosity(0);
             av.setDataValidationOn();	or .setDataValidationOff()
             av.validateAll(inIDFFileName);
             av.collectErrors();

       *
      */
    public static void main(String[] args) {
        String cwd = System.getProperty("user.dir");
        AnnotareValidator av;
        String fileName = null;
        String outputPath = null;
        int verbosity;
        int dataVal;
        String debugState = "";
        if (args.length > 3) {
            fileName = args[0];
            verbosity = Integer.parseInt(args[1]);
            dataVal = Integer.parseInt(args[2]);
            debugState = args[3];
            if (fileName.lastIndexOf(File.separatorChar) > 0) {
                outputPath = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
            }
            else {
                outputPath = ".";
            }
            //remove last '/' from path
            if (outputPath.endsWith(new Character(File.separatorChar).toString())) {
                String tmp = outputPath.substring(0, outputPath.length() - 1);
                outputPath = tmp;
            }
            System.out.println("Validating " + fileName);
            if (debugState.equals("1")) {
                //debug is 'on'; output ALL Items
                av = new AnnotareValidator();
                av.clearErrorLists();
                av.setVerbosity(0);
                if (dataVal == 0) {
                    av.setDataValidatinOff();
                }
                av.setDebugOn();
                av.validateAll(fileName);
                ArrayList<ErrorItem> elist = av.collectErrors();
                BufferedWriter bw = av.writeFile("test.log");
                for (ErrorItem ei : elist) {
                    try {
                        bw.write(ei.reportString());
                    }
                    catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
                try {
                    bw.close();
                }
                catch (IOException ioe) {
                    //do nothing
                }
                try {
                    av.writeErrorCodeList("AnnotareErrorList.txt");
                }
                catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
            else {
//				av = new AnnotareValidator(fileName,verbosity);
                av = new AnnotareValidator();
                av.clearErrorLists();
                av.setVerbosity(verbosity);
                if (dataVal == 0) {
                    av.setDataValidatinOff();
                }
                av.validateAll(fileName);
                ArrayList<ErrorItem> elist = av.collectErrors();
            }
        }
        else if (args.length > 2) {
            fileName = args[0];
            verbosity = Integer.parseInt(args[1]);
            dataVal = Integer.parseInt(args[2]);
            if (fileName.lastIndexOf(File.separatorChar) > 0) {
                outputPath = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
            }
            else {
                outputPath = ".";
            }
            System.out.println("Validating " + fileName);
            av = new AnnotareValidator();
            av.clearErrorLists();
            av.setVerbosity(verbosity);
            if (dataVal == 0) {
                av.setDataValidatinOff();
            }
            av.validateAll(fileName);
            ArrayList<ErrorItem> elist = av.collectErrors();
        }
        else if (args.length > 1) {
            fileName = args[0];
            verbosity = Integer.parseInt(args[1]);
            if (fileName.lastIndexOf(File.separatorChar) > 0) {
                outputPath = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
            }
            else {
                outputPath = ".";
            }
            System.out.println("Validating " + fileName);
            av = new AnnotareValidator(fileName, verbosity);
            ArrayList<ErrorItem> elist = av.collectErrors();
/*			//test code for writing XML string
			String xmlStr = av.toXML(elist);
			BufferedWriter bw = av.writeFile("testXML.xml");
			try{
				bw.write(xmlStr);
				bw.close();
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
*/
        }
        else if (args.length > 0) {
            fileName = args[0];
            if (fileName.lastIndexOf(File.separatorChar) > 0) {
                outputPath = fileName.substring(0, fileName.lastIndexOf(File.separatorChar));
            }
            else {
                outputPath = ".";
            }
            System.out.println("Validating " + fileName);
            av = new AnnotareValidator();
            av.setVerbosity(1);
            ArrayList<ErrorItem> list1 = av.validateAll(fileName);
        }
        else {
            System.out.println("No input file to validate.");
            av = new AnnotareValidator();
        }
        System.out.println("Output path: " + outputPath);

        ArrayList<ErrorItem> elist = av.annErr.getErrorList();
        System.out.println("\nList of errors: " + elist.size());
        System.out.println("\nError Code\tLine\tColumn\tMessage\tComment");
        for (ErrorItem ei : elist) {
            System.out.println(ei.reportString());
        }
        ArrayList<ErrorItem> wlist = av.annErr.getWarningList();
        ;
        if (av.warnings == true) {
            System.out.println("List of warnings: " + wlist.size());
            System.out.println("\nError Code\tLine\tColumn\tMessage\tComment");
            for (ErrorItem ei : wlist) {
                System.out.println(ei.reportString());
            }
        }
        System.out.println("Validated: " + fileName);
        System.out.println("\nList of errors: " + elist.size());
        System.out.println("List of warnings: " + wlist.size());
//		av.testXML(av.collectErrors());

    }

    public String toXML(ArrayList<ErrorItem> e) {
        String xmlStr = this.annErr.formErrorXML(e);
        return xmlStr;
    }

    public ErrorListener getListener() {
        return new ErrorListener();
    }

    public class ErrorListener implements ErrorItemListener {
        public void errorOccurred(ErrorItem item) {
            if (warnings == true && missingData == true) {
                //verbosity case 0
                if (item.getErrorType() != null && item.getErrorType().contains("warning")) {
                    annErr.addWarningItem(item);
                }
                else if (item.getErrorType() != null && item.getErrorType().contains("missing")) {
                    annErr.addWarningItem(item);
                }
                else {
                    annErr.addErrorItem(item);
                }
            }
            else if (warnings == true) {
                //verbosity case 1
                if (item.getErrorType() != null && item.getErrorType().contains("warning")) {
                    annErr.addWarningItem(item);
                }
                else if (item.getErrorType() != null && item.getErrorType().contains("missing")) {
                    annErr.addToIgnoreList(item);
                }
                else {
                    annErr.addErrorItem(item);
                }
            }
            else if (missingData == true) {
                //verbosity case 2
                if (item.getErrorType() != null && item.getErrorType().contains("missing")) {
                    annErr.addWarningItem(item);
                }
                else if (item.getErrorType() != null && item.getErrorType().contains("warning")) {
                    annErr.addToIgnoreList(item);
                }
                else {
                    annErr.addErrorItem(item);
                }
            }
            else {
                //verbosity case 3
                if (item.getErrorType() != null && item.getErrorType().contains("error")) {
                    annErr.addErrorItem(item);
                }
            }
        }
    }

    public ArrayList<ErrorItem> semanticValidation(MAGETABInvestigation I) {
//        SemanticValidator sv = new SemanticValidator(annErr);
//        //sv.testDebug = true;
//        try {
//            System.out.println("Semantic validation call");
//            sv.validate(I);
//        }
//        catch (ValidateException ve) {
//            ve.printStackTrace();
//        }
//
//        ArrayList<ErrorItem> al = sv.getErrors();
//
//        System.out.println("Semantic validation call: failed");
//        return al;

        return annErr.getAllItems();
    }

}
