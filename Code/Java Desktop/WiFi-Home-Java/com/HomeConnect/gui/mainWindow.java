package com.HomeConnect.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class mainWindow {

	protected Shell shlHomeConnect;

	/**
	 * Launch the application.
	 * @param args
	 * @wbp.parser.entryPoint
	 */
	public static void createWindow() {
		try {
			mainWindow window = new mainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shlHomeConnect.open();
		shlHomeConnect.layout();
		while (!shlHomeConnect.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shlHomeConnect = new Shell();
		shlHomeConnect.setSize(450, 300);
		shlHomeConnect.setText("Home Connect");
		
		Button btnScan = new Button(shlHomeConnect, SWT.NONE);
		btnScan.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				HomeConnect.scan();
			}
		});
		btnScan.setBounds(46, 43, 75, 25);
		btnScan.setText("Scan");

	}
}
