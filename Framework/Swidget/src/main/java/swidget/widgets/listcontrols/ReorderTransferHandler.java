package swidget.widgets.listcontrols;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

public abstract class ReorderTransferHandler extends TransferHandler {

	private JTable table;
	private String id = UUID.randomUUID().toString();
	
	public ReorderTransferHandler(JTable table) {
		this.table = table;
	}

	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	public Transferable createTransferable(JComponent c) {
		JTable table = (JTable) c;

		if (table != this.table) {
			throw new RuntimeException("Attempting to transfer data from table other than the one specified");
		}

		return new StringSelection(table.getSelectedRow() + ":" + id);
	}

	public void exportDone(JComponent c, Transferable t, int action) {
		//No action is really taken here, it should all be done in the onMove callback
	}

	public boolean canImport(TransferHandler.TransferSupport support) {
		try {
			JTable table = (JTable) support.getComponent();
			if (table != this.table) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}

		if (!support.isDrop()) {
			return false;
		}

		if (!Arrays.asList(support.getDataFlavors()).contains(DataFlavor.stringFlavor)) {
			return false;
		}

		try {
			int sourceRow = getSourceRow(support);
			if (sourceRow == -1) { return false; }
		} catch (Exception e) {
			return false;
		}

		// if MOVE is not in the bitmask for supported actions
		if ((MOVE & support.getSourceDropActions()) != MOVE) {
			return false;

		}
		support.setDropAction(MOVE);

		return true;
	}
	
	private int getSourceRow(TransferHandler.TransferSupport support) throws UnsupportedFlavorException, IOException {
		String data = (String)support.getTransferable().getTransferData(DataFlavor.stringFlavor);
		String[] parts = data.split(":");
		if (! parts[1].equals(this.id)) {
			return -1;
		}
		int sourceRow = Integer.parseInt(parts[0]);
		return sourceRow;
	}

	public boolean importData(TransferHandler.TransferSupport support) {

		if (!canImport(support)) {
			return false;
		}

		try {
			
			int sourceRow = getSourceRow(support);
			if (sourceRow == -1) { return false; }
			
			JTable.DropLocation location = (javax.swing.JTable.DropLocation) support.getDropLocation();
			int targetRow = location.getRow();
			
			move(sourceRow, targetRow);
			return true;
			
		} catch (Exception e) {
			return false;
		}
	}
	
	public abstract void move(int from, int to);

}
