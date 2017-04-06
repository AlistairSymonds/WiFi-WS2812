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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public class MainWindow implements Runnable {
	private Display display;
    private Shell shlHomeconnect;
    private ScrolledComposite sc;
    private Button scanBtn;
    private Composite controlPanel;
    private Composite currentControlPanelChild;

    private int widestDevBox = 0;
    private Text text_1;
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
        shlHomeconnect.setMinimumSize(new Point(400, 300));
        shlHomeconnect.setText("HomeConnect");
        shlHomeconnect.setSize(750, 445);
        shlHomeconnect.setLayout(new FormLayout());
        FormLayout layout = new FormLayout();
        layout.marginWidth = 5;
		layout.marginHeight = 5;

        
        sc = new ScrolledComposite(shlHomeconnect, SWT.BORDER | SWT.V_SCROLL);
        sc.setExpandHorizontal(true);
        FormData fd_sc = new FormData();
        fd_sc.bottom = new FormAttachment(100, -60);
        fd_sc.left = new FormAttachment(0, 10);
        fd_sc.top = new FormAttachment(scanBtn, 50, SWT.BOTTOM);
        sc.setLayoutData(fd_sc);
        sc.setAlwaysShowScrollBars(true);
        Point currentSize = (shlHomeconnect.getSize());
        sc.setExpandVertical(true);
        
        scanBtn = new Button(shlHomeconnect, SWT.NONE);
        fd_sc.right = new FormAttachment(scanBtn, 0, SWT.RIGHT);
        
        FormData fd_scanBtn = new FormData();
        fd_scanBtn.right = new FormAttachment(30);
        fd_scanBtn.left = new FormAttachment(0, 10);
        fd_scanBtn.top = new FormAttachment(0, 10);
        fd_scanBtn.bottom = new FormAttachment(0, 35);
        scanBtn.setLayoutData(fd_scanBtn);
        

        scanBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		if(currentControlPanelChild != null){
    				currentControlPanelChild.dispose();
    			}
        		
        		HomeConnect.scan();
        	}
        });
        scanBtn.setText("Scan");
        
        controlPanel = new Composite(shlHomeconnect, SWT.BORDER);
        
        FormData fd_controlPanel = new FormData();
        fd_controlPanel.left = new FormAttachment(sc, 10);
        fd_controlPanel.right = new FormAttachment(100, -10);
        fd_controlPanel.bottom = new FormAttachment(100, -60);
        fd_controlPanel.top = new FormAttachment(0, 50);
        
        controlPanel.setLayoutData(fd_controlPanel);
        controlPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
        
       
        
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

    public synchronized void addList(ArrayList<espDevice> devices){
    	if (display == null || display.isDisposed()) 
            return;
        display.asyncExec(new Runnable() {

            public void run() {
            	Composite temp = new Composite(sc, SWT.BACKGROUND);
            	RowLayout tempLayout = new RowLayout();
            	tempLayout.wrap = false;
            	tempLayout.pack = true;
            	tempLayout.type = SWT.VERTICAL;
            	temp.setLayout(tempLayout);
                for (int i = 0; i < devices.size(); i++){
                	createDevBox(devices.get(i), temp);
                }
                sc.setContent(temp);
                sc.setMinSize(new Point(500, 500));
                
            }
        });
    }
    
    private Composite createDevBox(espDevice device, Composite parent){
    	Composite c = new Composite(parent, SWT.BORDER);
    	c.setBackground(new Color(display, 30, 64, 150));
    	RowLayout layout = new RowLayout(SWT.VERTICAL);
    	layout.wrap = false;
    	layout.pack = false;
    	layout.fill = true;
    	c.setLayout(layout);
    	
    	Label id = new Label(c, SWT.BORDER);
    	Label hName = new Label(c, SWT.BORDER);
    	
    	String idStr = bytesToHex(device.getDeviceID());
    	
    	id.setText(idStr);
    	hName.setText(device.getHumanName());
    	hName.setText(device.getName());
    	
    	Button setNameBtn = new Button(c, SWT.PUSH);
    	setNameBtn.setText("Set name");
    	
    	setNameBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		try {
        			if(currentControlPanelChild != null){
        				currentControlPanelChild.dispose();
        			}
					currentControlPanelChild = createSetNameBox(device, controlPanel);
					controlPanel.layout();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
        	}
        });
    	
    	ArrayList<Button> funcBtns = new ArrayList<Button>();
    	
    	for(int i = 0; i < device.getFuncs().size(); i ++){
    		Button b = new Button(c, SWT.PUSH);
    		String text = definitions.getFunc(device.getFuncs().get(i));
    		b.setText(text);
    		funcBtns.add(b);
    		
    	}
    	
    	for (int i = 0; i < funcBtns.size(); i++){
    		if (!funcBtns.get(i).equals(definitions.getFunc(-1))){
    			
    			if(funcBtns.get(i).getText().equals("Lighting")){
    				funcBtns.get(i).addSelectionListener(new SelectionAdapter() {
    		        	@Override
    		        	public void widgetSelected(SelectionEvent e) {
    		        		try {
    		        			if(currentControlPanelChild != null){
    		        				currentControlPanelChild.dispose();
    		        			}
								currentControlPanelChild = createLightingControls(device, controlPanel);
								controlPanel.layout();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
    		        	}
    		        });
    			} else if (funcBtns.get(i).getText().equals("GPIO 2")){
    				funcBtns.get(i).addSelectionListener(new SelectionAdapter() {
    		        	@Override
    		        	public void widgetSelected(SelectionEvent e) {
    		        		try {
    		        			if(currentControlPanelChild != null){
    		        				currentControlPanelChild.dispose();
    		        			}
    		        			currentControlPanelChild = createGPIOBox(device, controlPanel);
    		        			controlPanel.layout();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
    		        	}
    		        });
    			} else if(funcBtns.get(i).getText().equals("Debug serial access")){
    				funcBtns.get(i).addSelectionListener(new SelectionAdapter() {
    		        	@Override
    		        	public void widgetSelected(SelectionEvent e) {
    		        		try {
    		        			if(currentControlPanelChild != null){
    		        				currentControlPanelChild.dispose();
    		        			}
    		        			currentControlPanelChild = createDebugSerial(device, controlPanel);
    		        			controlPanel.layout();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
    		        	}
    		        });
    			}
    			
    			
    		}
    	}
    	return c;
    }
    private Composite createGPIOBox(espDevice device, Composite parent) throws Exception{
    	System.out.println("making gpio");
    	Composite c = new Composite(parent, SWT.BORDER);
    	c.setBackground(new Color(display, 30, 64, 150));
    	GridLayout layout = new GridLayout();
    	
    	
    	c.setLayout(layout);
    	
    	Button highBtn = new Button(c, SWT.PUSH);
    	GridData highBtn_GridData = new GridData();
    	highBtn.setLayoutData(highBtn_GridData);
    	
    	Button lowBtn = new Button(c, SWT.PUSH);
    	GridData lowBtn_GridData = new GridData();
    	lowBtn.setLayoutData(lowBtn_GridData);
    	
    	highBtn.setText("HIGH");
    	lowBtn.setText("LOW");
    	
    	highBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		device.sendMessage("1 4 0");
        	}
        });
    	
    	lowBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		device.sendMessage("1 4 1");
        	}
        });
    	
    	return c;
    }
    
    private Composite createLightingControls(espDevice device, Composite parent) throws Exception{
    	Composite c = new Composite(parent, SWT.BORDER);
    	c.setBackground(new Color(display, 30, 64, 0));
        c.setLayout(new FillLayout());
    	
    	Label lblTest = new Label(c, 0);
    	
    	System.out.println("making lighting");
    	device.sendMessage("1 2");
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
			byte[] currentSettings = device.readMessage(50);
			String byteStr = "";
	    	for(int i = 0; i < currentSettings.length; i++){
	    		byteStr = byteStr + "-" + currentSettings[i];
	    	}
	    	lblTest.setText(byteStr);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return c;
    	
    }
    
    private Composite createDebugSerial(espDevice device, Composite parent) throws Exception{
    	System.out.println("making debug serial");
    	Composite c = new Composite(parent, SWT.BORDER);
    	c.setBackground(new Color(display, 30, 64, 16));
    	GridLayout layout = new GridLayout();
    	
    	
    	c.setLayout(layout);
 
    	Text messageBox = new Text(c, 0);
    	GridData messageBox_GridData = new GridData();
    	messageBox.setLayoutData(messageBox_GridData);
    	
    	
    	Button sendBtn = new Button(c, SWT.PUSH);
    	GridData sendBtn_GridData = new GridData();
    	sendBtn.setLayoutData(sendBtn_GridData);
    	sendBtn.setText("Send message");
    	
    	Button rainbowBtn = new Button(c, SWT.PUSH);
    	GridData rainbowBtn_GridData = new GridData();
    	rainbowBtn.setLayoutData(rainbowBtn_GridData);
    	rainbowBtn.setText("make rainbow");
    	
    	Button audioBtn = new Button(c, SWT.PUSH);
    	GridData audioBtn_GridData = new GridData();
    	audioBtn.setLayoutData(audioBtn_GridData);
    	audioBtn.setText("audio modulated");
    	
    	
    	sendBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		device.sendMessage(messageBox.getText());
        	}
        });
    	
    	rainbowBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		device.sendMessage("0 3 0 7");
        	}
        });
    	
    	audioBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		device.sendMessage("0 2 11");
        	}
        });
    	
    	return c;
    }
    
    private Composite createSetNameBox(espDevice device, Composite parent) throws Exception{
    	System.out.println("making name box");
    	Composite c = new Composite(parent, SWT.BORDER);
    	GridLayout layout = new GridLayout();
    	
    	
    	c.setLayout(layout);
 
    	Text nameBox = new Text(c, 0);
    	GridData messageBox_GridData = new GridData();
    	messageBox_GridData.grabExcessHorizontalSpace = true;
    	nameBox.setLayoutData(messageBox_GridData);
    	
    	
    	Button confirmBtn = new Button(c, SWT.PUSH);
    	GridData sendBtn_GridData = new GridData();
    	confirmBtn.setLayoutData(sendBtn_GridData);
    	

    	confirmBtn.setText("Set new name");
    	
    	
    	
    	confirmBtn.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent e) {
        		device.setName(nameBox.getText());
        	}
        });
    	
    	return c;
    }
    
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    
}
