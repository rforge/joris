package org.rosuda.mapper.irexp;

import org.rosuda.irconnect.IREXP;
import org.rosuda.irconnect.IRMatrix;
import org.rosuda.mapper.AbstractGenericMapper;
import org.rosuda.mapper.MappedNodeTrace;
import org.rosuda.type.Node;

class MatrixMapper<T> extends AbstractGenericMapper<IREXP, T> {

	private final AbstractGenericMapper<IREXP, T> rootHandler;

	MatrixMapper(final AbstractGenericMapper<IREXP, T> rootHandler) {
		this.rootHandler = rootHandler;
	}

	protected void handleMap(final IREXP source, final Node.Builder<T> parent, final MappedNodeTrace<T> trace) {		
		final Node.Builder<T> matrixHolder = parent.createChild("matrix");
		if (matrixHolder == null)
			return;
		parent.add(matrixHolder);
		final IRMatrix matrix = source.asMatrix();
		for (int row = 0; row < matrix.getRows(); row++) {
			final Node.Builder<T> rowHolder = matrixHolder.createChild(matrix.getRowNameAt(row));
			if (rowHolder == null)
				return;
			matrixHolder.add(rowHolder);
			for (int col = 0; col < matrix.getColumns(); col++) {
				final Node.Builder<T> cellHolder = rowHolder.createChild(matrix.getColumnNameAt(col));
				if (cellHolder == null)
					return;
				rowHolder.add(cellHolder);
				rootHandler.map(matrix.getValueAt(row, col), cellHolder, trace);
			}
		}
	}

}
