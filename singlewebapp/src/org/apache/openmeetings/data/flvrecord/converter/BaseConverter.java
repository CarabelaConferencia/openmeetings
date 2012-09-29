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
package org.apache.openmeetings.data.flvrecord.converter;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDataDaoImpl;
import org.apache.openmeetings.data.flvrecord.FlvRecordingMetaDeltaDaoImpl;
import org.apache.openmeetings.documents.GenerateSWF;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecording;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaData;
import org.apache.openmeetings.persistence.beans.flvrecord.FlvRecordingMetaDelta;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.ProcessHelper;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseConverter {
	private static final Logger log = Red5LoggerFactory.getLogger(
			BaseConverter.class, OpenmeetingsVariables.webAppRootKey);

	@Autowired
	private ConfigurationDaoImpl configurationmanagement;
	@Autowired
	private FlvRecordingMetaDataDaoImpl flvRecordingMetaDataDaoImpl;
	@Autowired
	private FlvRecordingMetaDeltaDaoImpl flvRecordingMetaDeltaDaoImpl;

	protected String getPathToFFMPEG() {
		String pathToFFMPEG = configurationmanagement.getConfKey(
				"ffmpeg_path").getConf_value();
		if (!pathToFFMPEG.equals("") && !pathToFFMPEG.endsWith(File.separator)) {
			pathToFFMPEG += File.separator;
		}
		pathToFFMPEG += "ffmpeg";
		return pathToFFMPEG;
	}

	protected String getPathToSoX() {
		String pathToSoX = configurationmanagement.getConfKey("sox_path")
				.getConf_value();
		if (!pathToSoX.equals("") && !pathToSoX.endsWith(File.separator)) {
			pathToSoX += File.separator;
		}
		pathToSoX += "sox";
		return pathToSoX;
	}

	protected String getPathToImageMagick() {
		String pathToImageMagick = this.configurationmanagement.getConfKey(
				"imagemagick_path").getConf_value();
		if (!pathToImageMagick.equals("")
				&& !pathToImageMagick.endsWith(File.separator)) {
			pathToImageMagick += File.separator;
		}
		pathToImageMagick += "convert" + GenerateSWF.execExt;
		return pathToImageMagick;
	}

	protected boolean isUseOldStyleFfmpegMap() {
		return "1".equals(configurationmanagement.getConfValue(
				"use.old.style.ffmpeg.map.option", String.class, "0"));
	}
	
	protected File getStreamFolder() {
		return OmFileHelper.getStreamsHibernateDir();
	}

	protected File getStreamFolder(FlvRecording flvRecording) {
		return OmFileHelper.getStreamsSubDir(flvRecording.getRoom_id());
	}
	
	protected void deleteFileIfExists(String name) {
		File f = new File(name);

		if (f.exists()) {
			f.delete();
		}
	}
	
	protected String[] mergeAudioToWaves(List<String> listOfFullWaveFiles, String outputFullWav) throws Exception {
		String[] argv_full_sox = new String[listOfFullWaveFiles.size() + 3];
		
		log.debug(" listOfFullWaveFiles "+listOfFullWaveFiles.size()+" argv_full_sox LENGTH "+argv_full_sox.length);
		
		argv_full_sox[0] = getPathToSoX();
		argv_full_sox[1] = "-m";

		int i = 0;
		for (;i < listOfFullWaveFiles.size(); i++) {
			log.debug(" i "+i+" = "+listOfFullWaveFiles.get(i));
			argv_full_sox[2 + i] = listOfFullWaveFiles.get(i);
		}
		log.debug(" i + 2 "+(i+2)+" "+outputFullWav);
		
		argv_full_sox[i + 2] = outputFullWav;
		
		return argv_full_sox;
	}
	
	protected void stripAudioFirstPass(FlvRecording flvRecording,
			List<HashMap<String, String>> returnLog,
			List<String> listOfFullWaveFiles, File streamFolder) throws Exception {
		List<FlvRecordingMetaData> metaDataList = flvRecordingMetaDataDaoImpl
				.getFlvRecordingMetaDataAudioFlvsByRecording(flvRecording
						.getFlvRecordingId());
		stripAudioFirstPass(flvRecording, returnLog, listOfFullWaveFiles, streamFolder, metaDataList);
	}
	
	protected void stripAudioFirstPass(FlvRecording flvRecording,
			List<HashMap<String, String>> returnLog,
			List<String> listOfFullWaveFiles, File streamFolder,
			List<FlvRecordingMetaData> metaDataList) {
		try {
			// Init variables
			log.debug("### meta Data Number - " + metaDataList.size());
			log.debug("###################################################");
	
			for (FlvRecordingMetaData flvRecordingMetaData : metaDataList) {
				
				if (flvRecordingMetaData.getStreamReaderThreadComplete() == null) {
					throw new IllegalStateException("StreamReaderThreadComplete Bit is NULL, error in recording");
				}
				
				if (!flvRecordingMetaData.getStreamReaderThreadComplete()) {
					
					log.debug("### meta Stream not yet written to disk" + flvRecordingMetaData.getFlvRecordingMetaDataId());
					boolean doStop = true;
					while(doStop) {
						
						log.debug("### Stream not yet written Thread Sleep - " + flvRecordingMetaData.getFlvRecordingMetaDataId());
						
						flvRecordingMetaData = flvRecordingMetaDataDaoImpl.getFlvRecordingMetaDataById(flvRecordingMetaData.getFlvRecordingMetaDataId());
						
						if (flvRecordingMetaData.getStreamReaderThreadComplete()) {
							log.debug("### Stream now written Thread continue - " );
							doStop = false;
						}
						
						Thread.sleep(100L);
					}
				}
	
				File inputFlvFile = new File(streamFolder, flvRecordingMetaData.getStreamName() + ".flv");
	
				String hashFileName = flvRecordingMetaData.getStreamName()
						+ "_WAVE.wav";
				String outputWav = new File(streamFolder, hashFileName).getCanonicalPath(); //FIXME
	
				flvRecordingMetaData.setWavAudioData(hashFileName);
	
				
				log.debug("FLV File Name: {} Length: {} ", inputFlvFile.getName(), inputFlvFile.length());
	
				if (inputFlvFile.exists()) {
	
					String[] argv = new String[] { this.getPathToFFMPEG(),
							"-async", "1", "-i", inputFlvFile.getCanonicalPath(), outputWav };
	
					log.debug("START stripAudioFromFLVs ################# ");
					for (int i = 0; i < argv.length; i++) {
						log.debug(" i " + i + " argv-i " + argv[i]);
					}
					log.debug("END stripAudioFromFLVs ################# ");
	
					returnLog.add(ProcessHelper.executeScript("generateFFMPEG",
							argv));
	
					// check if the resulting Audio is valid
					File output_wav = new File(outputWav);
	
					if (!output_wav.exists()) {
						flvRecordingMetaData.setAudioIsValid(false);
					} else {
						if (output_wav.length() == 0) {
							flvRecordingMetaData.setAudioIsValid(false);
						} else {
							flvRecordingMetaData.setAudioIsValid(true);
						}
					}
	
				} else {
					flvRecordingMetaData.setAudioIsValid(false);
				}
	
				if (flvRecordingMetaData.getAudioIsValid()) {
					
					// Strip Wave to Full Length
					String outputGapFullWav = outputWav;
	
					// Fix Start/End in Audio
					List<FlvRecordingMetaDelta> flvRecordingMetaDeltas = flvRecordingMetaDeltaDaoImpl
							.getFlvRecordingMetaDeltaByMetaId(flvRecordingMetaData
									.getFlvRecordingMetaDataId());
	
					int counter = 0;
	
					for (FlvRecordingMetaDelta flvRecordingMetaDelta : flvRecordingMetaDeltas) {
	
						String inputFile = outputGapFullWav;
	
						// Strip Wave to Full Length
						String hashFileGapsFullName = flvRecordingMetaData
								.getStreamName()
								+ "_GAP_FULL_WAVE_"
								+ counter
								+ ".wav";
						outputGapFullWav = new File(streamFolder, hashFileGapsFullName).getCanonicalPath();
	
						flvRecordingMetaDelta
								.setWaveOutPutName(hashFileGapsFullName);
	
						String[] argv_sox = null;
	
						if (flvRecordingMetaDelta.getIsStartPadding() != null
								&& flvRecordingMetaDelta.getIsStartPadding()) {
	
							double gapSeconds = Double.valueOf(
									flvRecordingMetaDelta.getDeltaTime()
											.toString()).doubleValue() / 1000;
	
							Double.valueOf(
									flvRecordingMetaDelta.getDeltaTime()
											.toString()).doubleValue();
	
							if (gapSeconds > 0) {
								// Add the item at the beginning
								argv_sox = new String[] { this.getPathToSoX(),
										inputFile, outputGapFullWav, "pad",
										String.valueOf(gapSeconds).toString(),
										"0" };
							}
	
						} else if (flvRecordingMetaDelta.getIsEndPadding() != null
								&& flvRecordingMetaDelta.getIsEndPadding()) {
	
							double gapSeconds = Double.valueOf(
									flvRecordingMetaDelta.getDeltaTime()
											.toString()).doubleValue() / 1000;
	
							if (gapSeconds > 0) {
								// Add the item at the end
								argv_sox = new String[] { this.getPathToSoX(),
										inputFile, outputGapFullWav, "pad",
										"0",
										String.valueOf(gapSeconds).toString() };
							}
						}
	
						if (argv_sox != null) {
							log.debug("START addGapAudioToWaves ################# ");
							log.debug("START addGapAudioToWaves ################# Delta-ID :: "
									+ flvRecordingMetaDelta
											.getFlvRecordingMetaDeltaId());
							String commandHelper = " ";
							for (int i = 0; i < argv_sox.length; i++) {
								commandHelper += " " + argv_sox[i];
							}
							log.debug(" commandHelper " + commandHelper);
							log.debug("END addGapAudioToWaves ################# ");
	
							returnLog.add(ProcessHelper.executeScript("fillGap",
									argv_sox));
	
							this.flvRecordingMetaDeltaDaoImpl
									.updateFlvRecordingMetaDelta(flvRecordingMetaDelta);
							counter++;
						} else {
							outputGapFullWav = inputFile;
						}
	
					}
	
					// Strip Wave to Full Length
					String hashFileFullName = flvRecordingMetaData
							.getStreamName() + "_FULL_WAVE.wav";
					String outputFullWav = new File(streamFolder, hashFileFullName).getCanonicalPath();
	
					// Calculate delta at beginning
					Long deltaTimeStartMilliSeconds = flvRecordingMetaData
							.getRecordStart().getTime()
							- flvRecording.getRecordStart().getTime();
	
					Float startPadding = Float
							.parseFloat(deltaTimeStartMilliSeconds.toString()) / 1000;
	
					// Calculate delta at ending
					Long deltaTimeEndMilliSeconds = flvRecording.getRecordEnd()
							.getTime()
							- flvRecordingMetaData.getRecordEnd().getTime();
	
					Float endPadding = Float
							.parseFloat(deltaTimeEndMilliSeconds.toString()) / 1000;
	
					String[] argv_sox = new String[] { this.getPathToSoX(),
							outputGapFullWav, outputFullWav, "pad",
							startPadding.toString(), endPadding.toString() };
	
					log.debug("START addAudioToWaves ################# ");
					String padString = "";
					for (int i = 0; i < argv_sox.length; i++) {
						padString += " " + argv_sox[i];
					}
					log.debug("padString :: " + padString);
					log.debug("END addAudioToWaves ################# ");
	
					returnLog.add(ProcessHelper.executeScript(
							"addStartEndToAudio", argv_sox));
	
					// Fix for Audio Length - Invalid Audio Length in Recorded
					// Files
					// Audio must match 100% the Video
					log.debug("############################################");
					log.debug("Trim Audio to Full Length -- Start");
					File aFile = new File(outputFullWav);
	
					if (!aFile.exists()) {
						throw new Exception(
								"Audio File does not exist , could not extract the Audio correctly");
					}
					flvRecordingMetaData.setFullWavAudioData(hashFileFullName);
	
					// Finally add it to the row!
					listOfFullWaveFiles.add(outputFullWav);
	
				}
	
				flvRecordingMetaDataDaoImpl
						.updateFlvRecordingMetaData(flvRecordingMetaData);
	
			}
		} catch (Exception err) {
			log.error("[stripAudioFromFLVs]", err);
		}
	}
}
