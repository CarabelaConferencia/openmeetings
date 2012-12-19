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
package org.apache.openmeetings.servlet.outputhandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.openmeetings.OpenmeetingsVariables;
import org.apache.openmeetings.batik.beans.PrintBean;
import org.apache.openmeetings.data.basic.Sessionmanagement;
import org.apache.openmeetings.data.record.WhiteboardMapToSVG;
import org.apache.openmeetings.data.user.Usermanagement;
import org.apache.openmeetings.documents.GenerateImage;
import org.apache.openmeetings.remote.PrintService;
import org.apache.openmeetings.remote.red5.ScopeApplicationAdapter;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportToImage extends HttpServlet {
	private static final long serialVersionUID = -3535998254746084297L;
	private static final Logger log = Red5LoggerFactory.getLogger(
			ExportToImage.class, OpenmeetingsVariables.webAppRootKey);

	public Sessionmanagement getSessionManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Sessionmanagement) context.getBean("sessionManagement");
			}
		} catch (Exception err) {
			log.error("[getSessionManagement]", err);
		}
		return null;
	}

	public Usermanagement getUserManagement() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (Usermanagement) context.getBean("userManagement");
			}
		} catch (Exception err) {
			log.error("[getUserManagement]", err);
		}
		return null;
	}

	public GenerateImage getGenerateImage() {
		try {
			if (ScopeApplicationAdapter.initComplete) {
				ApplicationContext context = WebApplicationContextUtils
						.getWebApplicationContext(getServletContext());
				return (GenerateImage) context.getBean("generateImage");
			}
		} catch (Exception err) {
			log.error("[getGenerateImage]", err);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		try {
			if (getUserManagement() == null || getSessionManagement() == null
					|| getGenerateImage() == null) {
				return;
			}

			String sid = httpServletRequest.getParameter("sid");
			if (sid == null) {
				sid = "default";
			}
			log.debug("sid: " + sid);

			String hash = httpServletRequest.getParameter("hash");
			if (hash == null) {
				hash = "";
			}
			log.debug("hash: " + hash);

			String fileName = httpServletRequest.getParameter("fileName");
			if (fileName == null) {
				fileName = "file_xyz";
			}

			String exportType = httpServletRequest.getParameter("exportType");
			if (exportType == null) {
				exportType = "svg";
			}

			Long users_id = getSessionManagement().checkSession(sid);
			Long user_level = getUserManagement().getUserLevelByID(users_id);

			log.debug("users_id: " + users_id);
			log.debug("user_level: " + user_level);

			if (user_level != null && user_level > 0 && hash != "") {

				PrintBean pBean = PrintService.getPrintItemByHash(hash);

				// Whiteboard Objects
				@SuppressWarnings("rawtypes")
				List whiteBoardMap = pBean.getMap();

				// Get a DOMImplementation.
				DOMImplementation domImpl = GenericDOMImplementation
						.getDOMImplementation();

				// Create an instance of org.w3c.dom.Document.
				// String svgNS = "http://www.w3.org/2000/svg";
				String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

				Document document = domImpl.createDocument(svgNS, "svg", null);

				// Get the root element (the 'svg' element).
				Element svgRoot = document.getDocumentElement();

				// Set the width and height attributes on the root 'svg'
				// element.
				svgRoot.setAttributeNS(null, "width", "" + pBean.getWidth());
				svgRoot.setAttributeNS(null, "height", "" + pBean.getHeight());

				log.debug("pBean.getWidth(),pBean.getHeight()"
						+ pBean.getWidth() + "," + pBean.getHeight());

				// Create an instance of the SVG Generator.
				SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

				svgGenerator = WhiteboardMapToSVG.getInstance()
						.convertMapToSVG(svgGenerator, whiteBoardMap);

				// Finally, stream out SVG to the standard output using
				// UTF-8 encoding.
				boolean useCSS = true; // we want to use CSS style attributes
				// Writer out = new OutputStreamWriter(System.out, "UTF-8");

				if (exportType.equals("svg")) {
					// OutputStream out = httpServletResponse.getOutputStream();
					// httpServletResponse.setContentType("APPLICATION/OCTET-STREAM");
					// httpServletResponse.setHeader("Content-Disposition","attachment; filename=\""
					// + requestedFile + "\"");
					Writer out = httpServletResponse.getWriter();

					svgGenerator.stream(out, useCSS);

				} else if (exportType.equals("png") || exportType.equals("jpg")
						|| exportType.equals("gif") || exportType.equals("tif")
						|| exportType.equals("pdf")) {

					File uploadTempDir = OmFileHelper.getUploadTempDir();

					String requestedFileSVG = fileName + "_"
							+ CalendarPatterns.getTimeForStreamId(new Date())
							+ ".svg";
					String resultFileName = fileName + "_"
							+ CalendarPatterns.getTimeForStreamId(new Date())
							+ "." + exportType;

					log.debug("working_dir: " + uploadTempDir);
					log.debug("requestedFileSVG: " + requestedFileSVG);
					log.debug("resultFileName: " + resultFileName);

					File svgFile = new File(uploadTempDir, requestedFileSVG);
					File resultFile = new File(uploadTempDir, resultFileName);

					log.debug("svgFile: " + svgFile.getCanonicalPath());
					log.debug("resultFile: " + resultFile.getCanonicalPath());
					log.debug("svgFile P: " + svgFile.getPath());
					log.debug("resultFile P: " + resultFile.getPath());

					FileWriter out = new FileWriter(svgFile);
					svgGenerator.stream(out, useCSS);

					httpServletResponse.reset();
					httpServletResponse.resetBuffer();
					OutputStream outStream = httpServletResponse
							.getOutputStream();
					httpServletResponse
							.setContentType("APPLICATION/OCTET-STREAM");
					httpServletResponse.setHeader("Content-Disposition",
							"attachment; filename=\"" + resultFileName + "\"");
					httpServletResponse.setHeader("Content-Length",
							"" + resultFile.length());

					OmFileHelper.copyFile(resultFile, outStream);
					outStream.close();

					out.flush();
					out.close();

				}

			}

		} catch (Exception er) {
			log.error("ERROR ", er);
			System.out.println("Error exporting: " + er);
			er.printStackTrace();
		}
	}
}