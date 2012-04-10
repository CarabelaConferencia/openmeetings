package org.openmeetings.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;
import org.openmeetings.app.installation.ImportInitvalues;
import org.openmeetings.app.installation.InstallationConfig;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.servlet.outputhandler.BackupExport;
import org.openmeetings.servlet.outputhandler.BackupImportController;
import org.openmeetings.utils.OMContextListener;
import org.openmeetings.utils.math.CalendarPatterns;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Admin {
	private static final Logger log = Red5LoggerFactory.getLogger(Admin.class);
	private boolean verbose = false;
	private InstallationConfig cfg = null;
	private Options opts = null;

	private Admin() {
		cfg = new InstallationConfig();
		opts = buildOptions();
	}
	
	@SuppressWarnings("static-access")
	private Options buildOptions() {
		Options options = new Options();
		OptionGroup group = new OptionGroup()
			.addOption(new OmOption("i", "i", "install", false, "Fill DB table, and make OM usable"))
			.addOption(new OmOption("b", "b", "backup", false, "Backups OM"))
			.addOption(new OmOption("r", "r", "restore", false, "Restores OM"))
			.addOption(new OmOption("f", "f", "files", false, "File operations - statictics/cleanup"))
			.addOption(new OmOption("h", "h", "help", false, "prints this message"));
		group.setRequired(true); 
		options.addOptionGroup(group);
		//general
		options.addOption(new OmOption(null, "v", "verbose", false, "verbose error messages"));
		//backup/restore
		options.addOption(new OmOption("b", null, "exclude-files", false, "should backup exclude files [default: include]"));
		options.addOption(new OmOption("b,r", "file", null, true, "file used for backup/restore"));
		//install
		options.addOption(new OmOption("i", "user", null, true, "Login name of the default user, minimum " + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH + " characters"));
		options.addOption(new OmOption("i", "pass", null, true, "Password of the default user, minimum " + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH + " characters (will be prompted if not set)"));
		options.addOption(new OmOption("i", "email", null, true, "Email of the default user"));
		options.addOption(new OmOption("i", "group", null, true, "The name of the default user group"));
		options.addOption(new OmOption("i", "tz", null, true, "Default server time zone, and time zone for the selected user [for ex: 'GMT+10', '-2', 'Chicago']"));
		options.addOption(new OmOption("i", null, "system-email-address", true, "System e-mail address [default: " + cfg.mailReferer + "]"));
		options.addOption(new OmOption("i", null, "smtp-server", true, "SMTP server for outgoing e-mails [default: " + cfg.smtpServer + "]"));
		options.addOption(new OmOption("i", null, "smtp-port", true, "SMTP server for outgoing e-mails [default: " + cfg.smtpPort + "]"));
		options.addOption(new OmOption("i", null, "email-auth-user", true, "Email auth username (anonymous connection will be used if not set)"));
		options.addOption(new OmOption("i", null, "email-auth-pass", true, "Email auth password (anonymous connection will be used if not set)"));
		options.addOption(new OmOption("i", null, "email-use-tls", false, "Is secure e-mail connection [default: no]"));
		options.addOption(new OmOption("i", null, "skip-default-rooms", false, "Do not create default rooms [created by default]"));
		options.addOption(new OmOption("i", null, "disable-frontend-register", false, "Do not allow front end register [allowed by default]"));
		
		return options;
	}
	
	private enum Command {
		install
		, backup
		, restore
		, files
		, usage
	}
	
	private void usage() {
		new OmHelpFormatter().printHelp("admin", opts);
	}
	
	private void handleError(String msg, Exception e) {
		handleError(msg, e, false);
	}
	
	private void handleError(String msg, Exception e, boolean printUsage) {
		if (printUsage) {
			usage();
		}
		if (verbose) {
			log.error(msg, e);
		} else {
			log.error(msg + " " + e.getMessage());
		}
		System.exit(1);
	}
	
	private ClassPathXmlApplicationContext getApplicationContext(final String ctxName) {
		OMContextListener omcl = new OMContextListener();
		omcl.contextInitialized(new ServletContextEvent(new ServletContext() {
			public void setAttribute(String arg0, Object arg1) {
			}
			
			public void removeAttribute(String arg0) {
			}
			
			public void log(String arg0, Throwable arg1) {
			}
			
			public void log(Exception arg0, String arg1) {
			}
			
			public void log(String arg0) {
			}
			
			@SuppressWarnings("rawtypes")
			public Enumeration getServlets() {
				return null;
			}
			
			@SuppressWarnings("rawtypes")
			public Enumeration getServletNames() {
				return null;
			}
			
			public String getServletContextName() {
				return null;
			}
			
			public Servlet getServlet(String arg0) throws ServletException {
				return null;
			}
			
			public String getServerInfo() {
				return null;
			}
			
			@SuppressWarnings("rawtypes")
			public Set getResourcePaths(String arg0) {
				return null;
			}
			
			public InputStream getResourceAsStream(String arg0) {
				return null;
			}
			
			public URL getResource(String arg0) throws MalformedURLException {
				return null;
			}
			
			public RequestDispatcher getRequestDispatcher(String arg0) {
				return null;
			}
			
			public String getRealPath(String arg0) {
				return null;
			}
			
			public RequestDispatcher getNamedDispatcher(String arg0) {
				return null;
			}
			
			public int getMinorVersion() {
				return 0;
			}
			
			public String getMimeType(String arg0) {
				return null;
			}
			
			public int getMajorVersion() {
				return 0;
			}
			
			@SuppressWarnings("rawtypes")
			public Enumeration getInitParameterNames() {
				return null;
			}
			
			public String getInitParameter(String arg0) {
				return null;
			}
			
			public String getContextPath() {
				return ctxName;
			}
			
			public ServletContext getContext(String arg0) {
				return null;
			}
			
			@SuppressWarnings("rawtypes")
			public Enumeration getAttributeNames() {
				return null;
			}
			
			public Object getAttribute(String arg0) {
				return null;
			}
		}));
		ClassPathXmlApplicationContext applicationContext = null;
		try {
			applicationContext = new ClassPathXmlApplicationContext("openmeetings-applicationContext.xml");
		} catch (Exception e) {
			handleError("Unable to obtain application context", e);
		}
		return applicationContext;
	}
	
	private void process(String[] args) {
		String ctxName = System.getProperty("context", "openmeetings");
		File omHome = new File(new File(System.getenv("RED5_HOME"), "webapps"), ctxName);
		
		Parser parser = new PosixParser();
		CommandLine cmdl = null;
		try {
			cmdl = parser.parse(opts, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			usage();
			System.exit(1);
		}
		verbose = cmdl.hasOption('v');

		Command cmd = Command.usage;
		if (cmdl.hasOption('i')) {
			cmd = Command.install;
		} else if (cmdl.hasOption('b')) {
			cmd = Command.backup;
		} else if (cmdl.hasOption('r')) {
			cmd = Command.restore;
		} else if (cmdl.hasOption('f')) {
			cmd = Command.files;
		}

		String file = cmdl.getOptionValue('f', "");
		switch(cmd) {
			case install:
				try {
					String login = cmdl.getOptionValue("user");
					String email = cmdl.getOptionValue("email");
					String group = cmdl.getOptionValue("group");
					String tz = cmdl.getOptionValue("tz");
					if (cmdl.hasOption("skip-default-rooms")) {
						cfg.createDefaultRooms = "0";
					}
					if (cmdl.hasOption("disable-frontend-register")) {
						cfg.allowFrontendRegister = "0";
					}
					if (cmdl.hasOption("system-email-address")) {
						cfg.mailReferer = cmdl.getOptionValue("system-email-address");
					}
					if (cmdl.hasOption("smtp-server")) {
						cfg.smtpServer = cmdl.getOptionValue("smtp-server");
					}
					if (cmdl.hasOption("smtp-port")) {
						cfg.smtpPort = cmdl.getOptionValue("smtp-port");
					}
					if (cmdl.hasOption("email-auth-user")) {
						cfg.mailAuthName = cmdl.getOptionValue("email-auth-user");
					}
					if (cmdl.hasOption("email-auth-pass")) {
						cfg.mailAuthPass = cmdl.getOptionValue("email-auth-pass");
					}
					if (cmdl.hasOption("email-use-tls")) {
						cfg.mailUseTls = "1";
					}
					if (login == null || login.length() < InstallationConfig.USER_LOGIN_MINIMUM_LENGTH) {
						System.out.println("User login was not provided, or too short, should be at least " + InstallationConfig.USER_LOGIN_MINIMUM_LENGTH + " character long.");
						System.exit(1);
					}
					if (group == null || login.length() < 1) {
						System.out.println("User group was not provided, or too short, should be at least 1 character long.");
						System.exit(1);
					}
					String pass = cmdl.getOptionValue("pass");
					if (pass == null || pass.length() < InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH) {
						System.out.print("Please enter password:");
						pass = new BufferedReader(new InputStreamReader(System.in)).readLine();
						if (pass == null || pass.length() < InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH) {
							System.out.println("Password was not provided, or too short, should be at least " + InstallationConfig.USER_PASSWORD_MINIMUM_LENGTH + " character long.");
							System.exit(1);
						}
					}
					ImportInitvalues importInit = getApplicationContext(ctxName).getBean(ImportInitvalues.class);
					importInit.loadAll(new File(omHome, ImportInitvalues.languageFolderName).getAbsolutePath()
							, cfg, login, pass, email, group, tz);
				} catch(Exception e) {
					handleError("Install failed", e);
				}
				break;
			case backup:
				try {
					if (!cmdl.hasOption('f')) {
						file = "backup_" + CalendarPatterns.getTimeForStreamId(new Date()) + ".zip";
						System.out.println("File name was not specified, '" + file + "' will be used");
					}
					boolean includeFiles = Boolean.getBoolean(cmdl.getOptionValue("exclude-files", "true"));
					File backup_dir = new File(omHome, "uploadtemp/" + System.currentTimeMillis());
					backup_dir.mkdirs();
					
					BackupExport export = getApplicationContext(ctxName).getBean(BackupExport.class);
					export.performExport(file, backup_dir, includeFiles, omHome.getAbsolutePath());
					export.deleteDirectory(backup_dir);
					backup_dir.delete();
				} catch (Exception e) {
					handleError("Backup failed", e);
				}
				break;
			case restore:
				try {
					File backup = new File(file);
					if (!cmdl.hasOption('f') || !backup.exists() || !backup.isFile()) {
						System.out.println("File should be specified, and point the the existent zip file");
						usage();
						System.exit(1);
					}
					
					BackupImportController importCtrl = getApplicationContext(ctxName).getBean(BackupImportController.class);
					importCtrl.performImport(new FileInputStream(backup), omHome.getAbsolutePath());
				} catch (Exception e) {
					handleError("Restore failed", e);
				}
				break;
			case files:
				try {
					ClassPathXmlApplicationContext ctx = getApplicationContext(ctxName);
					//user pictures
					//dist/red5/webapps/openmeetings/upload/profiles
					UsersDaoImpl udao = ctx.getBean(UsersDaoImpl.class);
					for (Users u : udao.getAllUsersDeleted()) {
						System.out.println("id == " + u.getUser_id() + "; deleted ? " + u.getDeleted() + "; uri -> " + u.getPictureuri());
					}
					//public files
					//private files
					//public recordings
					//private recordings
				} catch (Exception e) {
					handleError("Files failed", e);
				}
				break;
			case usage:
			default:
				usage();
				break;
		}
		
		System.out.println("... Done");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		new Admin().process(args);
	}
}
