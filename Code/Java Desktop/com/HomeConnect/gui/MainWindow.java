package com.HomeConnect.gui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.swt.*;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;	

public class MainWindow implements Runnable {
	

	private Display display;
    private Button btnHiThere;
    private Shell shlHomeconnect;
    private ScrolledComposite sc;
    private Button scanBtn;
    private Composite child;
    private Label lblNodDevicesFound;
    private Composite controlPanel;
    
    public Display getDisplay(){
        return display;
    }
    
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
    
    public void run() {
        display = new Display();
        shlHomeconnect = new Shell();
        shlHomeconnect.setText("HomeConnect");
        shlHomeconnect.setSize(772, 504);
        FormLayout fl_shlHomeconnect = new FormLayout();
        shlHomeconnect.setLayout(fl_shlHomeconnect);
        
        sc = new ScrolledComposite(shlHomeconnect, SWT.BORDER | SWT.V_SCROLL);
        FormData fd_sc = new FormData();
        fd_sc.bottom = new FormAttachment(0, 455);
        fd_sc.right = new FormAttachment(0, 178);
        fd_sc.top = new FormAttachment(0, 41);
        fd_sc.left = new FormAttachment(0, 10);
        sc.setLayoutData(fd_sc);
        
        
        
        child = new Composite(sc, SWT.NONE);

        
        RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
        rl_composite.marginHeight = 3;
        rl_composite.center = true;
        rl_composite.fill = true;
        child.setLayout(rl_composite);
        
        lblNodDevicesFound = new Label(child, SWT.NONE);
        lblNodDevicesFound.setText("No devices found, please scan");
        
        
        sc.setContent(child);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        sc.setMinSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        

        
        
        
        scanBtn = new Button(shlHomeconnect, SWT.NONE);
        FormData fd_scanBtn = new FormData();
        fd_scanBtn.right = new FormAttachment(0, 178);
        fd_scanBtn.top = new FormAttachment(0, 10);
        fd_scanBtn.left = new FormAttachment(0, 10);
        scanBtn.setLayoutData(fd_scanBtn);
        scanBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		HomeConnect.scan();
        	}
        });
        scanBtn.setText("Scan");
        
        btnHiThere = new Button(shlHomeconnect,SWT.PUSH);
        FormData fd_btnHiThere = new FormData();
        fd_btnHiThere.top = new FormAttachment(0, 10);
        fd_btnHiThere.left = new FormAttachment(0, 184);
        btnHiThere.setLayoutData(fd_btnHiThere);
        btnHiThere.setText("hi there");
        
        Composite ctrlPanelComp = new Composite(shlHomeconnect, SWT.NONE);
        FormData fd_ctrlPanelComp = new FormData();
        fd_ctrlPanelComp.bottom = new FormAttachment(0, 455);
        fd_ctrlPanelComp.right = new FormAttachment(0, 746);
        fd_ctrlPanelComp.top = new FormAttachment(0, 41);
        fd_ctrlPanelComp.left = new FormAttachment(0, 184);
        ctrlPanelComp.setLayoutData(fd_ctrlPanelComp);
        shlHomeconnect.open();
        shlHomeconnect.layout();

        while (!shlHomeconnect.isDisposed()) {
		    if (!display.readAndDispatch ()){
		    	display.sleep ();
		    }
		    
		    shlHomeconnect.addListener(SWT.Close, new Listener() {
		        public void handleEvent(Event event) {
		          System.exit(0);
		        }
		      });
        }
        display.dispose();
    }

    public synchronized void update(final int value)
    {
        if (display == null || display.isDisposed()) 
            return;
        display.asyncExec(new Runnable() {

            public void run() {
                btnHiThere.setText(""+value);
            }
        });

    }
    
    public synchronized void addList(ArrayList<espDevice> devices){
    	if (display == null || display.isDisposed()) 
            return;
        display.asyncExec(new Runnable() {

            public void run() {
            	Composite temp = null;
                for (int i = 0; i < devices.size(); i++){
                	temp = createDevBox(devices.get(i));
                }
                sc.setContent(temp);
            }
        });
    }
    
    private Composite createDevBox(espDevice device){
    	Composite c = new Composite(sc, SWT.BORDER);
    	c.setLayout(new RowLayout(SWT.VERTICAL));
    	
    	Label id = new Label(c, SWT.BORDER);
    	Label hName = new Label(c, SWT.BORDER);
    	String idStr = "";
    	
    	for (int i = 0; i < device.getDeviceID().length; i++){
    		idStr = idStr + (long)device.getDeviceID()[i];
    	}
    	
    	id.setText(idStr);
    	hName.setText(device.getHumanName());
    	ArrayList<Button> funcBtns = new ArrayList<Button>();
    	
    	for(int i = 0; i < device.getFuncs().size(); i ++){
    		Button b = new Button(c, SWT.PUSH);
    		String text = definitions.getFunc(device.getFuncs().get(i));
    		
    		if (!text.equals(definitions.getFunc(-1))){
    			b.setText(text);
    			b.addSelectionListener(new SelectionAdapter() {
    	        	@Override
    	        	public void widgetSelected(SelectionEvent e) {
    	        		System.out.println("I do stuff");
    	        		if(text.equals("Lighting")){
    	        			
    	        			try {
								createLightingControls(device);
							} catch (Exception e1) {
								System.out.println("couldn't get info for controls");
							}
    	        		}
    	        	}
    	        });
    			
    		} else {
    			b.setText(text);
    		}
    		
    		
    	}
    	return c;
    }
    
    private Composite createLightingControls(espDevice device) throws Exception{
    	device.sendMessage("1 0");
    	/*
    	 byte 0: hue
    	 byte 1: saturation
    	 byte 2: value
    	 byte 3: timing pt1
    	 byte 4: timing pt2
    	 byte 5: timing pt3
    	 byte 6: timing pt4
    	 byte 7: current program
    	 byte 8: dH
    	 byte 9: dS
    	 byte 10: dV
    	 */
    	try {
			byte[] currentSettings = device.readMessage();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
		return new Composite(controlPanel, 0);
    	
    }
}
