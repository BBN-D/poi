/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package org.apache.poi.xssf.usermodel;

import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.POILogger;
import org.apache.poi.util.POILogFactory;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDefinedName;

/**
 * Represents a defined named range in a SpreadsheetML workbook.
 * <p>
 * Defined names are descriptive text that is used to represents a cell, range of cells, formula, or constant value.
 * Use easy-to-understand names, such as Products, to refer to hard to understand ranges, such as <code>Sales!C20:C30</code>.
 * </p>
 * Example:
 * <pre><blockquote>
 *   XSSFWorkbook wb = new XSSFWorkbook();
 *   XSSFSheet sh = wb.createSheet("Sheet1");
 *
 *   //applies to the entire workbook
 *   XSSFName name1 = wb.createName();
 *   name1.setNameName("FMLA");
 *   name1.setReference("Sheet1!$B$3");
 *
 *   //applies to Sheet1
 *   XSSFName name2 = wb.createName();
 *   name2.setNameName("SheetLevelName");
 *   name2.setComment("This name is scoped to Sheet1");
 *   name2.setLocalSheetId(0);
 *   name2.setReference("Sheet1!$B$3");
 *
 * </blockquote></pre>
 *
 * @author Nick Burch
 * @author Yegor Kozlov
 */
public class XSSFName implements Name {
    private static POILogger logger = POILogFactory.getLogger(XSSFWorkbook.class);

    /**
     * A built-in defined name that specifies the workbook's print area
     */
    public final static String BUILTIN_PRINT_AREA = "_xlnm.Print_Area";

    /**
     * A built-in defined name that specifies the row(s) or column(s) to repeat
     * at the top of each printed page.
     */
    public final static String BUILTIN_PRINT_TITLE = "_xlnm.Print_Titles";

    /**
     * A built-in defined name that refers to a range containing the criteria values
     * to be used in applying an advanced filter to a range of data
     */
    public final static String BUILTIN_CRITERIA = "_xlnm.Criteria:";


    /**
     * this defined name refers to the range containing the filtered
     * output values resulting from applying an advanced filter criteria to a source
     * range
     */
    public final static String BUILTIN_EXTRACT = "_xlnm.Extract:";

    /**
     * ?an be one of the following
     * <li> this defined name refers to a range to which an advanced filter has been
     * applied. This represents the source data range, unfiltered.
     * <li> This defined name refers to a range to which an AutoFilter has been
     * applied
     */
    public final static String BUILTIN_FILTER_DB = "_xlnm._FilterDatabase:";

    /**
     * A built-in defined name that refers to a consolidation area
     */
    public final static String BUILTIN_CONSOLIDATE_AREA = "_xlnm.Consolidate_Area";

    /**
     * A built-in defined name that specified that the range specified is from a database data source
     */
    public final static String BUILTIN_DATABASE = "_xlnm.Database";

    /**
     * A built-in defined name that refers to a sheet title.
     */
    public final static String BUILTIN_SHEET_TITLE = "_xlnm.Sheet_Title";

    private XSSFWorkbook workbook;
    private CTDefinedName ctName;

    /**
     * Creates an XSSFName object - called internally by XSSFWorkbook.
     *
     * @param name - the xml bean that holds data represenring this defined name.
     * @param workbook - the workbook object associated with the name
     * @see org.apache.poi.xssf.usermodel.XSSFWorkbook#createName()
     */
    protected XSSFName(CTDefinedName name, XSSFWorkbook workbook) {
        this.workbook = workbook;
        this.ctName = name;
    }

    /**
     * Returns the underlying named range object
     */
    protected CTDefinedName getCTName() {
        return ctName;
    }

    /**
     * Returns the name that will appear in the user interface for the defined name.
     *
     * @return text name of this defined name
     */
    public String getNameName() {
        return ctName.getName();
    }

    /**
     * Sets the name that will appear in the user interface for the defined name.
     * Names must begin with a letter or underscore, not contain spaces and be unique across the workbook.
     *
     * @param name name of this defined name
     * @throws IllegalArgumentException if the name is invalid or the workbook already contains this name (case-insensitive)
     */
    public void setNameName(String name) {
        validateName(name);

        //Check to ensure no other names have the same case-insensitive name
        for (int i = 0; i < workbook.getNumberOfNames(); i++) {
            XSSFName nm = workbook.getNameAt(i);
            if (nm != this && nm.getNameName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("The workbook already contains this name: " + name);
            }
        }
        ctName.setName(name);
    }

    /**
     * Returns the reference of this named range, such as Sales!C20:C30.
     *
     * @return the reference of this named range
     */
    public String getReference() {
        return ctName.getStringValue();
    }

    /**
     * Sets the reference of this named range, such as Sales!C20:C30.
     *
     * @param ref the reference to set
     * @throws IllegalArgumentException if the specified reference is unparsable
     */
    public void setReference(String ref) {
        try {
            ref = AreaReference.isContiguous(ref) ? new AreaReference(ref).formatAsString() : ref;
        } catch (IllegalArgumentException e){
            logger.log(POILogger.WARN, "failed to parse cell reference. Setting raw value");
        }
        ctName.setStringValue(ref);
    }

    /**
     * Tests if this name points to a cell that no longer exists
     *
     * @return true if the name refers to a deleted cell, false otherwise
     */
    public boolean isDeleted(){
        String ref = getReference();
        return ref != null && ref.indexOf("#REF!") != -1;
    }

    /**
     * Tell Excel that this name applies to the worksheet with the specified index instead of the entire workbook.
     *
     * @param sheetId the sheet index this name applies to, -1 unsets this property making the name workbook-global
     */
    public void setLocalSheetId(int sheetId) {
        if(sheetId == -1) ctName.unsetLocalSheetId();
        else ctName.setLocalSheetId(sheetId);
    }

    /**
     * Returns the sheet index this name applies to.
     *
     * @return the sheet index this name applies to, -1 if this name applies to the entire workbook
     */
    public int getLocalSheetId() {
        return ctName.isSetLocalSheetId() ? (int) ctName.getLocalSheetId() : -1;
    }

    /**
     * Indicates that the defined name refers to a user-defined function.
     * This attribute is used when there is an add-in or other code project associated with the file.
     *
     * @param value <code>true</code> indicates the name refers to a function.
     */
    public void setFunction(boolean value) {
        ctName.setFunction(value);
    }

    /**
     * Indicates that the defined name refers to a user-defined function.
     * This attribute is used when there is an add-in or other code project associated with the file.
     *
     * @return <code>true</code> indicates the name refers to a function.
     */
    public boolean getFunction() {
        return ctName.getFunction();
    }

    /**
     * Specifies the function group index if the defined name refers to a function. The function
     * group defines the general category for the function. This attribute is used when there is
     * an add-in or other code project associated with the file.
     *
     * @param functionGroupId the function group index that defines the general category for the function
     */
    public void setFunctionGroupId(int functionGroupId) {
        ctName.setFunctionGroupId(functionGroupId);
    }

    /**
     * Returns the function group index if the defined name refers to a function. The function
     * group defines the general category for the function. This attribute is used when there is
     * an add-in or other code project associated with the file.
     *
     * @return the function group index that defines the general category for the function
     */
    public int getFunctionGroupId() {
        return (int) ctName.getFunctionGroupId();
    }

    /**
     * Get the sheets name which this named range is referenced to
     *
     * @return sheet name, which this named range referred to.
     * Empty string if the referenced sheet name weas not found.
     */
    public String getSheetName() {
        if (ctName.isSetLocalSheetId()) {
            // Given as explicit sheet id
            int sheetId = (int)ctName.getLocalSheetId();
            return workbook.getSheetName(sheetId);
        } else {
            String ref = getReference();
            AreaReference areaRef = new AreaReference(ref);
            return areaRef.getFirstCell().getSheetName();
        }
    }

    /**
     * Is the name refers to a user-defined function ?
     *
     * @return <code>true</code> if this name refers to a user-defined function
     */
    public boolean isFunctionName() {
        return getFunction();
    }

    /**
     * Returns the comment the user provided when the name was created.
     *
     * @return the user comment for this named range
     */
    public String getComment() {
        return ctName.getComment();
    }

    /**
     * Specifies the comment the user provided when the name was created.
     *
     * @param comment  the user comment for this named range
     */
    public void setComment(String comment) {
        ctName.setComment(comment);
    }

    @Override
    public int hashCode() {
        return ctName.toString().hashCode();
    }

    /**
     * Compares this name to the specified object.
     * The result is <code>true</code> if the argument is XSSFName and the
     * underlying CTDefinedName bean equals to the CTDefinedName representing this name
     *
     * @param   o   the object to compare this <code>XSSFName</code> against.
     * @return  <code>true</code> if the <code>XSSFName </code>are equal;
     *          <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this) return true;

        if (!(o instanceof XSSFName)) return false;

        XSSFName cf = (XSSFName) o;
        return ctName.toString().equals(cf.getCTName().toString());
    }

    private static void validateName(String name){
        char c = name.charAt(0);
        if(!(c == '_' || Character.isLetter(c)) || name.indexOf(' ') != -1) {
            throw new IllegalArgumentException("Invalid name: '"+name+"'; Names must begin with a letter or underscore and not contain spaces");
        }
    }
}