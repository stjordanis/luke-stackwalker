/*
 * This file is part of Luke Stackwalker.
 * https://github.com/bhoeckendorf/luke-stackwalker
 * 
 * Copyright 2012 Burkhard Hoeckendorf <b.hoeckendorf at web dot de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_heidelberg.cos.lukestackwalker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Holds the information associated with a data file. Its location, DataTypes and values.
 */
public class DataFile {
	
	private final Map<String, Integer> fileNameTagValues;
	private final String
		absoluteFilePath,
		dataDir,
		dataSetName;
	private final boolean
		isRecursive;
	private boolean isValid = true;
	private final File file;
	

	/**
	 * Returns a new DataFile instance. 
	 * @param dataDir
	 * @param isRecursive
	 * @param file
	 * @return
	 */
	// TODO use a reference to the respective DataDir instance
	public static DataFile make(String dataDir, boolean isRecursive, File file) {
		DataFile dataFile = new DataFile(dataDir, isRecursive, file);
		if (!dataFile.isValid)
			return null;
		return dataFile;
	}
	

	/**
	 * Constructs a new DataFile. Is private and to be used via {@link DataFile#make(String, boolean, File)}.
	 * @param dataDir 
	 * @param isRecursive
	 * @param file 
	 * @see #make(String, boolean, File)
	 */
	// TODO use a reference to the respective DataDir object instead of these String/boolean fields
	private DataFile(String dataDir, boolean isRecursive, File file) {
		this.dataDir = dataDir;
		this.isRecursive = isRecursive;
		this.file = file;
		
		String filePath = new String();
		try {
			filePath = this.file.getCanonicalPath();
		} catch (IOException e) {
			System.out.println("In constructor for DataFile:");
			e.printStackTrace();
			System.exit(1);
		}
		absoluteFilePath = filePath;
		
		String comparableFileName = absoluteFilePath.replace(dataDir, "").substring(1);//.replace(File.separator, "");
		DataType firstDataType = DataTypeTableModel.getDataTypeOfLevel(true, 0);
		String firstFileNameTag = firstDataType.getFileNameTag();
		dataSetName = comparableFileName.split(firstFileNameTag)[0];

		fileNameTagValues = getFileNameTagValues(comparableFileName);
		if (fileNameTagValues.isEmpty())
			isValid = false;
	}


	// TODO get rid of this method if possible, else document it
	public boolean isValid() {
		return isValid;
	}
	
	
	/**
	 * Returns the file's path.
	 * @return the file's path
	 * @see #getFileName()
	 */
	public String getFilePath() {
		return absoluteFilePath;
	}
	
	
	/**
	 * Returns the file name.
	 * @return the file name
	 * @see #getFilePath()
	 */
	public String getFileName() {
		return file.getName();
	}
	

	/**
	 * Returns the name of the data set that a DataFile instance belongs to.
	 * @return the name of the data set that a DataFile instance belongs to
	 */
	public String getDataSetName() {
		return dataSetName;
	}
	
	
	/**
	 * Returns the value of a {@link DataType} (by name) in a DataFile's file name, or {@code null}.
	 * Example: file name = footag123bar, {@link DataType#getFileNameTag()} = tag, return = 123 
	 * @param dataTypeName a DataType's file name tag ({@link DataType#getFileNameTag()})
	 * @return the value of a DataType (by name) in a DataFile's file name, or {@code null}
	 * @see #getDataTypeValue(int)
	 * @see DataType
	 * @see DataTypeTableModel
	 */
	public int getDataTypeValue(final String dataTypeName) {
		return fileNameTagValues.get(dataTypeName);
	}
	

	/**
	 * Returns the value of the nth {@link DataType} from {@link DataTypeTableModel} in this DataFile's file name, or {@code null}.
	 * Example: file name = footag123bar, {@link DataType#getFileNameTag()} = tag, return = 123 
	 * The index n (= dataTypeLevel) considers only activated data types.
	 * @param dataTypeLevel nth activated DataType in DataTypeTableModel (starting at 0)
	 * @return the value of the nth DataType from DataTypeTableModel in this DataFile's file name, or {@code null}
	 * @see #getDataTypeValue(String)
	 * @see DataType
	 * @see DataTypeTableModel
	 */
	public int getDataTypeValue(final int dataTypeLevel) {
		final String dataTypeName = DataTypeTableModel.getDataTypeName(true, dataTypeLevel); 
		return getDataTypeValue(dataTypeName);
	}
	

	/**
	 * Returns a key-value Map of {@link DataType}s and their respective values in comparableFileName.
	 * If this method runs into trouble, it returns an empty Map. 
	 * @param comparableFileName the subject
	 * @return key-value Map, keys: {@link DataType#getName()}, values: {@link #getValueOfTag(String, String)}
	 * @see DataType
	 */
	private Map<String, Integer> getFileNameTagValues(final String comparableFileName) {
		Map<String, Integer> values = new HashMap<String, Integer>();
		List<DataType> dataTypes = DataTypeTableModel.getDataTypes(true);
		for (DataType dataType : dataTypes) {
			String dataTypeName = dataType.getName();
			String fileNameTag = dataType.getFileNameTag();
			final int value = getValueOfTag(comparableFileName, fileNameTag);
			values.put(dataTypeName, value);
			if (value == -1) {
				values.clear();
				break;
			}
		}
		return values;
	}


	/**
	 * Finds fileNameTag in comparableFileName, and returns any number of digits downstream as {@code int}, which it returns.
	 * Example: comparableFileName = footag123bar, fileNameTag = tag, return = 123 
	 * @param comparableFileName the subject
	 * @param fileNameTag the query
	 * @return the {@code int} in comparableFileNameTag downstream of fileNameTag, or -1 if there is no such thing
	 */
	private int getValueOfTag(final String comparableFileName, final String fileNameTag) {
		String[] parts = comparableFileName.split(fileNameTag);
		if(parts.length != 2)
			return -1;

		final String string = parts[1];
		char[] chars = string.toCharArray();
		int i = 0;
		for (char c : chars) {
			if (Character.isDigit(c))
				++i;
			else
				break;
		}
		
		if (i == 0)
			return -1;
		
		int value = -1;
		try {
			value = Integer.parseInt(string.substring(0, i));
		}
		catch(NumberFormatException e) {
			return value;
		}
		return value;
	}

}
