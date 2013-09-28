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
package org.apache.openmeetings.test.selenium;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestSeleniumSmokeTest {

	public static String BASE_URL = "http://localhost:5080/openmeetings";
	public static String username = "swagner";
	public static String userpass = "qweqwe";
	private static final String orgname = "seleniumtest";
	private static final String email = "selenium@openmeetings.apache.org";

	public WebDriver driver = null;

	// setting this to false can be handy if you run the test from inside
	// Eclipse, the browser will not shut down after the test so you can start
	// to diagnose the test issue
	public boolean doTearDownAfterTest = false;

	@Before
	public void setUp() {
		driver = new FirefoxDriver();
	}

	@Test
	public void smokeTest() throws Exception {
		try {
			driver.get(BASE_URL);
			
			testWebSite();
			
			SeleniumUtils.inputText(driver, "login", username);
			SeleniumUtils.inputText(driver, "pass", userpass);

			WebElement signInButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), 'Sign in')]]", true);
			signInButton.click();

			SeleniumUtils.elementExists(driver,
					"//h3[contains(text(), 'Help and support')]", true);
		} catch (Exception e) {
			SeleniumUtils.makeScreenShot(this.getClass().getSimpleName(), e,
					driver);
			throw e;
		}
	}

	private void testWebSite() throws Exception {
		
		WebElement wicketExtensionsWizardHeaderTitle = SeleniumUtils.findElement(driver,
				"wicketExtensionsWizardHeaderTitle", false);
		if (wicketExtensionsWizardHeaderTitle == null) {
			return;
		}
		if (wicketExtensionsWizardHeaderTitle.getText().contains("Installation")) {
			System.out.println("Do Installation");
			doInstallation();
		}
		
	}
	
	private void doInstallation() throws Exception {
		Thread.sleep(3000L);
		
		List<WebElement> buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);
		
		buttons_next.get(1).sendKeys(Keys.RETURN);
		
		Thread.sleep(1000L);
		
		SeleniumUtils.inputText(driver, "view:cfg.username", username);
		SeleniumUtils.inputText(driver, "view:cfg.password", userpass);
		SeleniumUtils.inputText(driver, "view:cfg.email", email);
		SeleniumUtils.inputText(driver, "view:cfg.group", orgname);
		
		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);
		
		buttons_next.get(1).sendKeys(Keys.RETURN);
		
		Thread.sleep(1000L);
		
		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);
		
		buttons_next.get(1).sendKeys(Keys.RETURN);
		
		Thread.sleep(1000L);
		
		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);
		
		buttons_next.get(1).sendKeys(Keys.RETURN);
		
		Thread.sleep(1000L);
		
		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);
		
		buttons_next.get(1).sendKeys(Keys.RETURN);
		
		Thread.sleep(2000L);
		
		List<WebElement> elements = SeleniumUtils.findElements(driver, "buttons:finish", true);
		
		elements.get(1).sendKeys(Keys.RETURN);
		
		long maxMilliSecondsWait = 120000;
		
		while (maxMilliSecondsWait > 0) {
			
			//check if installation is complete by searching for the link on the success page
			WebElement enterApplicationLink = SeleniumUtils.findElement(driver, 
								"//a[contains(@href,'install')]", false);
			
			if (enterApplicationLink == null) {
				System.out.println("Installation running - wait 3 more seconds and check again");
				
				Thread.sleep(3000L);
				maxMilliSecondsWait -= 3000;
			} else {
				maxMilliSecondsWait = 0;
				
				enterApplicationLink.click();
				
				return;
			}
		}
		
		throw new Exception("Timeout during installation");
	}

	@After
	public void tearDown() throws Exception {
		if (doTearDownAfterTest) {
			driver.close();
			driver.quit();
		}
	}

}
