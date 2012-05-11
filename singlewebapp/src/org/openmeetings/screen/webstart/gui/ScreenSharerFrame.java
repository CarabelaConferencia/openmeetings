/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.openmeetings.screen.webstart.gui;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.openmeetings.screen.webstart.CoreScreenShare;
import org.openmeetings.screen.webstart.gui.ScreenDimensions.ScreenQuality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenSharerFrame extends JFrame {
	private static final long serialVersionUID = 6892639796386017437L;
	private static final Logger logger = LoggerFactory.getLogger(ScreenSharerFrame.class);
	
	private JPanel contentPane;
	private JPanel panelScreen = new DisabledPanel();
	private JPanel panelRecording = new DisabledPanel();
	private JPanel panelPublish = new DisabledPanel();
	private JLabel lblStatus = new JLabel();
	private int vScreenX = 20;
	private int vScreenY = 20;
	private VerticalSlider upSlider = new VerticalSlider();
	private VerticalSlider downSlider = new VerticalSlider();
	private HorizontalSlider leftSlider = new HorizontalSlider();
	private HorizontalSlider rightSlider = new HorizontalSlider();
	private BlankArea virtualScreen = new BlankArea(new Color(255, 255, 255, 100));
	private JTabbedPane tabbedPane;
	private boolean doUpdateBounds = true;
	private boolean showWarning = true;
	private JButton btnPauseSharing;
	private JButton btnStartSharing;
	private JButton btnStartRecording;
	private JButton btnStopRecording;
	private NumberSpinner spinnerX;
	private NumberSpinner spinnerY;
	private NumberSpinner spinnerWidth;
	private NumberSpinner spinnerHeight;
	private JComboBox comboQuality;
	private JTextField textPublishHost;
	private JTextField textPublishContext;
	private JTextField textPublishId;
	private JLabel lblPublishURL;
	
	private class KeyValue<T> {
		private String key;
		private T value;
		
		public KeyValue(String key, T value) {
			this.key = key;
			this.value = value;
		}
	 
		@SuppressWarnings("unused")
		public String getKey() { return key; }
		public T getValue() { return value; }
	 
		@Override
		public String toString() { return key; }
	 
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof KeyValue) {
				@SuppressWarnings("unchecked")
				KeyValue<T> kv = (KeyValue<T>) obj;
				return (kv.value.equals(this.value));
			}
			return false;
		}
	 
		@Override
		public int hashCode() {
			int hash = 7;
			hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
			return hash;
		}
	}
	
	//this implementation will not allow to Enable Panel in runtime
	private class DisabledPanel extends JPanel {
		private static final long serialVersionUID = -2679640611165728979L;

		@Override
		public void setEnabled(boolean enabled) {
			for (Component c : getComponents()) {
				c.setEnabled(enabled);
			}
			super.setEnabled(enabled);
		}
	}
	
	private class VerticalSlider extends MouseListenerable {
		private static final long serialVersionUID = 6388951979741767971L;

		public VerticalSlider() {
			ImageIcon iUp = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/up.png"));
			ImageIcon iDown = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/down.png"));
			setSize(16, 32);
			JLabel jUp = new JLabel(iUp);
			jUp.setBounds(0, 0, 16, 16);
			add(jUp);
			JLabel jDown = new JLabel(iDown);
			jDown.setBounds(0, 16, 16, 16);
			add(jDown);
		}
	}
	
	private class HorizontalSlider extends MouseListenerable {
		private static final long serialVersionUID = 4630712955901760443L;

		public HorizontalSlider() {
			ImageIcon iLeft = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/previous.png"));
			ImageIcon iRight = new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/next.png"));
			setSize(32, 16);
			JLabel jLeft = new JLabel(iLeft);
			jLeft.setBounds(0, 0, 16, 16);
			add(jLeft);
			JLabel jRight = new JLabel(iRight);
			jRight.setBounds(16, 0, 16, 16);
			add(jRight);
		}
	}
	
	/**
	 * Create the frame.
	 * @throws AWTException 
	 * @throws IOException 
	 */
	public ScreenSharerFrame(final CoreScreenShare core) throws AWTException {
		setTitle(core.label730);
		setBackground(Color.WHITE);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(30, 30, 500, 550);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblStartSharing = new JLabel(core.label731);
		
		btnStartSharing = new JButton(core.label732);
		btnStartSharing.setToolTipText(core.label732);
		btnStartSharing.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/play.png")));
		btnStartSharing.setSize(200, 32);
		btnStartSharing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.startRecording = false;
				core.startStreaming = true;
				core.captureScreenStart();
			}
		});
		
		btnPauseSharing = new JButton(core.label733);
		btnPauseSharing.setEnabled(false);
		btnPauseSharing.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/pause.png")));
		btnPauseSharing.setToolTipText(core.label733);
		btnPauseSharing.setSize(200, 32);
		btnPauseSharing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.stopRecording = false;
				core.stopStreaming = true;
				core.captureScreenStop();
			}
		});
		
		JButton btnStopSharing = new JButton(core.label878);
		btnStopSharing.setToolTipText(core.label878);
		btnStopSharing.setSize(200, 32);
		btnStopSharing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ScreenSharerFrame.this.setVisible(false);
				System.exit(0);
			}
		});
		
		JLabel lblSelectArea = new JLabel(core.label734);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		panelRecording.setBackground(Color.WHITE);
		tabbedPane.addTab(core.label869, null, panelRecording, null);
		tabbedPane.setEnabledAt(0, true);
		panelRecording.setLayout(null);
		panelRecording.setEnabled(false);
		
		JLabel lblRecordingDesc = new JLabel(core.label870);
		lblRecordingDesc.setVerticalAlignment(SwingConstants.TOP);
		lblRecordingDesc.setBounds(10, 10, 447, 60);
		panelRecording.add(lblRecordingDesc);
		
		btnStartRecording = new JButton(core.label871);
		btnStartRecording.setToolTipText(core.label871);
		btnStartRecording.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/record.png")));
		btnStartRecording.setBounds(10, 82, 200, 32);
		btnStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.startRecording = true;
				core.startStreaming = false;
				core.captureScreenStart();
			}
		});
		panelRecording.add(btnStartRecording);
		
		btnStopRecording = new JButton(core.label872);
		btnStopRecording.setEnabled(false);
		btnStopRecording.setToolTipText(core.label872);
		btnStopRecording.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/stop.png")));
		btnStopRecording.setBounds(257, 82, 200, 32);
		btnStopRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				core.stopRecording = true;
				core.stopStreaming = false;
				core.captureScreenStop();
			}
		});
		panelRecording.add(btnStopRecording);
		
		panelPublish.setBackground(Color.WHITE);
		tabbedPane.addTab("#STAB# Publish", null, panelPublish, null);
		tabbedPane.setEnabledAt(1, true);
		panelPublish.setEnabled(false);
		panelPublish.setLayout(null);
		
		JLabel lblPublishHost = new JLabel("#STAB# Host");
		lblPublishHost.setVerticalAlignment(SwingConstants.TOP);
		lblPublishHost.setBounds(10, 10, 86, 20);
		panelPublish.add(lblPublishHost);
		
		JButton btnStartPublish = new JButton("#STAB# start Publish");
		btnStartPublish.setToolTipText("#STAB# start Publish tip");
		//btnStartPublish.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/record.png")));
		btnStartPublish.setBounds(10, 82, 200, 32);
		panelPublish.add(btnStartPublish);
		
		JButton btnStopPublish = new JButton("#STAB# stop Publish");
		btnStopPublish.setEnabled(false);
		btnStopPublish.setToolTipText("#STAB# stop Publish tip");
		//btnStopPublish.setIcon(new ImageIcon(ScreenSharerFrame.class.getResource("/org/openmeetings/screen/stop.png")));
		btnStopPublish.setBounds(257, 82, 200, 32);
		panelPublish.add(btnStopPublish);
		
		textPublishHost = new JTextField();
		textPublishHost.setBounds(10, 38, 86, 20);
		textPublishHost.getDocument().addDocumentListener(
			new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					updateLabelURL();
				}

				public void removeUpdate(DocumentEvent e) {
					updateLabelURL();
				}

				public void insertUpdate(DocumentEvent e) {
					updateLabelURL();
				}
			});

		panelPublish.add(textPublishHost);
		textPublishHost.setColumns(10);
		
		JLabel lblPublishContext = new JLabel("#STAB# Context");
		lblPublishContext.setVerticalAlignment(SwingConstants.TOP);
		lblPublishContext.setBounds(124, 10, 86, 20);
		panelPublish.add(lblPublishContext);
		
		textPublishContext = new JTextField();
		textPublishContext.getDocument().addDocumentListener(
			new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					updateLabelURL();
				}

				public void removeUpdate(DocumentEvent e) {
					updateLabelURL();
				}

				public void insertUpdate(DocumentEvent e) {
					updateLabelURL();
				}
			});
		textPublishContext.setColumns(10);
		textPublishContext.setBounds(124, 38, 86, 20);
		panelPublish.add(textPublishContext);
		
		JLabel lblPublishId = new JLabel("#STAB# Publish Id");
		lblPublishId.setVerticalAlignment(SwingConstants.TOP);
		lblPublishId.setBounds(232, 10, 86, 20);
		panelPublish.add(lblPublishId);
		
		textPublishId = new JTextField();
		textPublishId.setColumns(10);
		textPublishId.setBounds(232, 38, 86, 20);
		textPublishId.getDocument().addDocumentListener(
			new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					updateLabelURL();
				}

				public void removeUpdate(DocumentEvent e) {
					updateLabelURL();
				}

				public void insertUpdate(DocumentEvent e) {
					updateLabelURL();
				}
			});
		panelPublish.add(textPublishId);
		
		lblPublishURL = new JLabel("");
		lblPublishURL.setBounds(10, 63, 447, 14);
		panelPublish.add(lblPublishURL);
		
		panelScreen.setBackground(Color.WHITE);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblStartSharing))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(21)
							.addComponent(btnStartSharing, 200, 200, 200)
							.addGap(52)
							.addComponent(btnPauseSharing, 200, 200, 200))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblSelectArea, GroupLayout.PREFERRED_SIZE, 470, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panelScreen, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 472, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(btnStopSharing, 200, 200, 200)
							.addGap(14))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(7)
							.addComponent(lblStatus)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(7)
					.addComponent(lblStartSharing)
					.addGap(4)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnStartSharing, 32, 32, 32)
						.addComponent(btnPauseSharing, 32, 32, 32))
					.addGap(4)
					.addComponent(lblSelectArea)
					.addGap(4)
					.addComponent(panelScreen, 210, 210, 210)
					.addGap(4)
					.addComponent(tabbedPane, 150, 150, 150)
					.addGap(4)
					.addComponent(btnStopSharing, 32, 32, 32)
					.addGap(4)
					.addComponent(lblStatus)
					.addGap(4))
		);
		panelScreen.setLayout(null);
		
		int width = ScreenDimensions.width;
		int height = ScreenDimensions.height;
		
		//Sliders
		upSlider.addListener(new ScreenYMouseListener(this));
		upSlider.setToolTipText(core.label737);
		panelScreen.add(upSlider);
		downSlider.addListener(new ScreenHeightMouseListener(this));
		downSlider.setToolTipText(core.label737);
		panelScreen.add(downSlider);
		
		leftSlider.addListener(new ScreenXMouseListener(this));
		leftSlider.setToolTipText(core.label735);
		panelScreen.add(leftSlider);
		rightSlider.addListener(new ScreenWidthMouseListener(this));
		rightSlider.setToolTipText(core.label735);
		panelScreen.add(rightSlider);
		
		//Virtual Screen
		virtualScreen.addListener(new ScreenMouseListener(this));
		virtualScreen.setBounds(vScreenX, vScreenY, width, height);
		panelScreen.add(virtualScreen);

		ImageIcon imgBgScreen = new ImageIcon(
			new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()))
				.getScaledInstance(width, height, Image.SCALE_SMOOTH));
		JLabel bgScreen = new JLabel(imgBgScreen);
		bgScreen.setBounds(vScreenX, vScreenY, width, height);
		panelScreen.add(bgScreen);

		//Spinner X
		JLabel labelX = new JLabel();
		labelX.setText(core.label738);
		labelX.setBounds(250, 20, 150, 24);
		panelScreen.add(labelX);

		spinnerX = new NumberSpinner(ScreenDimensions.spinnerX, 0, ScreenDimensions.widthMax, 1);
		spinnerX.setBounds(400, 20, 60, 24);
		spinnerX.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueXSpin();
			}
		});
		panelScreen.add(spinnerX);

		//Spinner Y
		JLabel labelY = new JLabel();
		labelY.setText(core.label739);
		labelY.setBounds(250, 50, 150, 24);
		panelScreen.add(labelY);

		spinnerY = new NumberSpinner(ScreenDimensions.spinnerY, 0, ScreenDimensions.heightMax, 1);
		spinnerY.setBounds(400, 50, 60, 24);
		spinnerY.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueYSpin();
			}
		});
		panelScreen.add(spinnerY);
		
		JLabel vscreenWidthLabel = new JLabel();
		vscreenWidthLabel.setText(core.label740);
		vscreenWidthLabel.setBounds(250, 80, 150, 24);
		panelScreen.add(vscreenWidthLabel);

		spinnerWidth = new NumberSpinner(ScreenDimensions.spinnerWidth, 0, ScreenDimensions.widthMax, 1);
		spinnerWidth.setBounds(400, 80, 60, 24);
		spinnerWidth.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueWidthSpin();
			}
		});
		panelScreen.add(spinnerWidth);

		//Spinner Height
		JLabel labelHeight = new JLabel();
		labelHeight.setText(core.label741);
		labelHeight.setBounds(250, 110, 150, 24);
		panelScreen.add(labelHeight);

		spinnerHeight = new NumberSpinner(ScreenDimensions.spinnerHeight, 0, ScreenDimensions.heightMax, 1);
		spinnerHeight.setBounds(400, 110, 60, 24);
		spinnerHeight.addChangeListener( new ChangeListener(){
			public void stateChanged(ChangeEvent arg0) {
				calcNewValueHeightSpin();
			}
		});
		panelScreen.add(spinnerHeight);
		
		//Quality
		JLabel labelQuality = new JLabel();
		labelQuality.setText(core.label1089);
		labelQuality.setBounds(250, 140, 200, 24);
		panelScreen.add(labelQuality);
		
		comboQuality = new JComboBox();
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1090, ScreenQuality.VeryHigh));
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1091, ScreenQuality.High));
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1092, ScreenQuality.Medium));
		comboQuality.addItem(new KeyValue<ScreenQuality>(core.label1093, ScreenQuality.Low));
		comboQuality.setBounds(250, 170, 200, 24);
		comboQuality.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
		        ScreenDimensions.quality = ((KeyValue<ScreenQuality>)comboQuality.getSelectedItem()).getValue();
		        calcRescaleFactors();
			}
		}); 
		comboQuality.setSelectedIndex(core.defaultQuality);
		panelScreen.add(comboQuality);
		
		contentPane.setLayout(gl_contentPane);
		
		//
		// Background Image
		
		//We have no logo, that is why we need no background, sebawagner 29.04.2012
		
//		Image im_left = ImageIO.read(getClass()
//				.getResource("/org/openmeetings/screen/background.png"));
//		ImageIcon iIconBack = new ImageIcon(im_left);
//
//		JLabel jLab = new JLabel(iIconBack);
//		jLab.setBounds(0, 0, 500, 440);
//		contentPane.add(jLab);
	}

	public void setSharingStatus(boolean status) {
		panelScreen.setEnabled(!status);
		btnStartSharing.setEnabled(!status);
		btnPauseSharing.setEnabled(status);
	}
	
	public void setRecordingStatus(boolean status) {
		panelScreen.setEnabled(!status);
		btnStartRecording.setEnabled(!status);
		btnStopRecording.setEnabled(status);
	}
	
	public void setTabsEnabled(boolean enabled) {
		panelRecording.setEnabled(enabled);
		tabbedPane.setEnabledAt(0, enabled);
		panelPublish.setEnabled(enabled);
		tabbedPane.setEnabledAt(1, enabled);
	}
	
	private void updateLabelURL() {
		lblPublishURL.setText("rtmp://" + textPublishHost.getText() + ":1935/"
				+ textPublishContext.getText() + "/" + textPublishId.getText());
	}
	
	public void setShowWarning(boolean showWarning) {
		this.showWarning = showWarning;
	}
	
	public void setDoUpdateBounds(boolean doUpdateBounds) {
		this.doUpdateBounds = doUpdateBounds;
	}
	
	public void setStatus(String status) {
		lblStatus.setText(status);
	}

	public void setSpinnerX(int val) {
		spinnerX.setValue(val);
	}

	public void setSpinnerY(int val) {
		spinnerY.setValue(val);
	}

	public void setSpinnerWidth(int val) {
		spinnerWidth.setValue(val);
	}

	public void setSpinnerHeight(int val) {
		spinnerHeight.setValue(val);
	}

	void calcNewValueXSpin() {
		if (doUpdateBounds) {
			int newX = spinnerX.getValue();
			if (ScreenDimensions.spinnerWidth + newX > ScreenDimensions.widthMax) {
				newX = ScreenDimensions.widthMax - ScreenDimensions.spinnerWidth;
				spinnerX.setValue(newX);
				if (showWarning) {
					setStatus("Reduce the width of the SharingScreen before you try to move it left");
				}
			} else {
				ScreenDimensions.spinnerX = newX;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerX = spinnerX.getValue();
		}

		calcRescaleFactors();
	}

	void calcNewValueYSpin() {
		if (doUpdateBounds) {
			int newY = spinnerY.getValue();
			if (ScreenDimensions.spinnerHeight + newY > ScreenDimensions.heightMax) {
				newY = ScreenDimensions.heightMax - ScreenDimensions.spinnerHeight;
				spinnerY.setValue(newY);
				if (showWarning) {
					setStatus("Reduce the height of the SharingScreen before you try to move it bottom");
				}
			} else {
				ScreenDimensions.spinnerY = newY;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerY = spinnerY.getValue();
		}

		calcRescaleFactors();
	}

	void calcNewValueWidthSpin() {
		if (doUpdateBounds) {
			int newWidth = spinnerWidth.getValue();
			if (ScreenDimensions.spinnerX + newWidth > ScreenDimensions.widthMax) {
				newWidth = ScreenDimensions.widthMax - ScreenDimensions.spinnerX;
				spinnerWidth.setValue(newWidth);
				if (showWarning) {
					setStatus("Reduce the x of the SharingScreen before you try to make it wider");
				}
			} else {
				ScreenDimensions.spinnerWidth = newWidth;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerWidth = spinnerWidth.getValue();
		}

		calcRescaleFactors();
	}

	void calcNewValueHeightSpin() {
		if (doUpdateBounds) {
			int newHeight = spinnerHeight.getValue();
			if (ScreenDimensions.spinnerY + newHeight > ScreenDimensions.heightMax) {
				newHeight = ScreenDimensions.heightMax - ScreenDimensions.spinnerY;
				spinnerHeight.setValue(newHeight);
				if (showWarning) {
					setStatus("Reduce the y of the SharingScreen before you try to make it higher");
				}
			} else {
				ScreenDimensions.spinnerHeight = newHeight;
				updateVScreenBounds();
			}
		} else {
			ScreenDimensions.spinnerHeight = spinnerHeight.getValue();
		}

		calcRescaleFactors();
	}

	/**
	 * Needs to be always invoked after every re-scaling
	 */
	void calcRescaleFactors() {
		logger.debug("calcRescaleFactors -- ");
		ScreenDimensions.resizeX = spinnerWidth.getValue();
		ScreenDimensions.resizeY = spinnerHeight.getValue();
		switch (ScreenDimensions.quality) {
			case Medium:
				ScreenDimensions.resizeX = (int)(1.0/2 * ScreenDimensions.resizeX);
				ScreenDimensions.resizeY = (int)(1.0/2 * ScreenDimensions.resizeY);
				break;
			case Low:
				ScreenDimensions.resizeX = (int)(3.0/8 * ScreenDimensions.resizeX);
				ScreenDimensions.resizeY = (int)(3.0/8 * ScreenDimensions.resizeY);
				break;
			case VeryHigh:
			case High:
			default:
				break;
		}
		logger.debug("resize: X:" + ScreenDimensions.resizeX + " Y: " + ScreenDimensions.resizeY);
		updateVScreenBounds();
	}

	private void setVScreenBounds(int x, int y, int width, int height) {
		leftSlider.setBounds(x + vScreenX - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		rightSlider.setBounds(x + vScreenX + width - 16, y + vScreenY - 8 + (height / 2), 32, 16);
		upSlider.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16, 16, 32);
		downSlider.setBounds(x + vScreenX + (width / 2) - 8, y + vScreenY - 16 + height, 16, 32);
		
		virtualScreen.setText(ScreenDimensions.spinnerWidth + ":" + ScreenDimensions.spinnerHeight);
		virtualScreen.setBounds(x + vScreenX, y + vScreenY, width, height);
	}
	
	/**
	 * update the bounds of the vScreen
	 * by using the vars from the Spinners
	 *
	 */
	void updateVScreenBounds() {
		double ratio = ((double)ScreenDimensions.width) / ScreenDimensions.widthMax;
		int newWidth = (int)(ScreenDimensions.spinnerWidth * ratio);
		int newX = (int)(ScreenDimensions.spinnerX * ratio);

		int newHeight = (int)(ScreenDimensions.spinnerHeight * ratio);
		int newY = (int)(ScreenDimensions.spinnerY * ratio);

		setVScreenBounds(newX, newY, newWidth, newHeight);
	}
}
