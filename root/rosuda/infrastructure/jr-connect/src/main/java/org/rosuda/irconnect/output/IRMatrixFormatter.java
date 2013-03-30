package org.rosuda.irconnect.output;

import java.util.Map;
import java.util.TreeMap;

import org.rosuda.irconnect.IRMatrix;

public class IRMatrixFormatter extends DelegetableObjectFormatter<IRMatrix>{

	public IRMatrixFormatter(final ObjectFormatter objectFormatter) {
		super(objectFormatter);
	}

	@Override
	public String format(final IRMatrix value) {
		final Map<Integer, Map<Integer, String>> matrixMap = new TreeMap<Integer, Map<Integer,String>>();
		final Map<Integer, Integer> colMax = new TreeMap<Integer, Integer>();
		//TODO check index with impl!
		//TODO : ensure rows, cols names!
		for (int col = 0; col < value.getColumns(); col ++) {
			colMax.put(col, -1);
LOGGER.debug("row = "+col+", maxCols = "+value.getColumns());
			final Map<Integer, String> colMap = new TreeMap<Integer, String>();
			colMap.put(-1, value.getColumnNameAt(col));
			matrixMap.put(col, colMap);
			for (int row = 0; row < value.getRows(); row ++) {
LOGGER.debug("col="+col+",row = "+row+", maxRows = "+value.getRows());
				final String formattedValue = objectFormatter.format(value.getValueAt(row, col));
				if (formattedValue != null) {
					int len = formattedValue.length();
					int currentMax = colMax.get(col);
					if (len > currentMax) {
						colMax.put(col, len);
					}
				}
				colMap.put(row, formattedValue);
			}
		}
		int rowNameMax = 0;
		final Map<Integer, String> rowNames = new TreeMap<Integer, String>();
		matrixMap.put(-1, rowNames);
		rowNames.put(-1, "");
		for (int row = 0; row <= value.getRows(); row ++) {
			final String rowName = value.getRowNameAt(row);
			rowNames.put(row, rowName);
			if (rowName != null && rowName.length() > rowNameMax)
				rowNameMax = rowName.length();
		}
		colMax.put(-1, rowNameMax);
		final StringBuilder stringBuilder = new StringBuilder();
		for (int row=-1; row < value.getRows(); row ++) {
			for (int col = -1 ; col < value.getColumns(); col ++) {
				stringBuilder.append("\t");
				final String currentValue = matrixMap.get(col).get(row);
				for (int i = 0; i < colMax.get(col) - currentValue.length(); i++) {
					stringBuilder.append(" ");
				}
				stringBuilder.append(currentValue);
			}
			stringBuilder.append("\r\n");
		}
		return stringBuilder.toString();
	}
}
