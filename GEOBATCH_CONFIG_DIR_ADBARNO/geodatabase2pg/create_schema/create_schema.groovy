package it.geosolutions.geobatch.action.scripting.adbarno

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.action.scripting.ScriptingAction;

import it.geosolutions.geobatch.task.TaskExecutor;
import it.geosolutions.geobatch.task.TaskExecutorConfiguration;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.sun.xml.internal.org.jvnet.mimepull.MIMEEvent.EVENT_TYPE;

public Map execute(Map argsMap) throws Exception {

    Logger LOGGER = Logger.getLogger(ScriptingAction.class);

//    final String OUT_FORMAT_PLH = "#{out_format}";
    LOGGER.info("-----------------------------------------------------------------");
    LOGGER.info("---------------------- Groovy Action START! ---------------------");
    LOGGER.info("-----------------------------------------------------------------");
    
    // Setup constants to use later
    final String ZIPPED_FILE_ABS_PATH_PLH = "_zipped_file_abs_path_";
    final String OUT_FORMAT = "_out_format_";

    final String SCHEMA_NAME = "_schemaname_";
    final String HOST_IP = "_host_ip_";
    final String PORT = "_port_";
    final String USER = "_user_";
    final String DB_NAME = "_db_name_";
    final String PASSWORD = "_password_";
    
    final String dbconnection = "\"PostgreSQL\" PG:\"host=" + HOST_IP + " port=" + PORT + " schemas=_schemaname_ user="+ USER + " dbname=" + DB_NAME + " password=" + PASSWORD + "\"";

    dbconnection = dbconnection.replaceFirst(HOST_IP, host_config);
    dbconnection = dbconnection.replaceFirst(PORT, port_config);
    dbconnection = dbconnection.replaceFirst(USER, user_config);
    dbconnection = dbconnection.replaceFirst(DB_NAME, dbname_config);
    dbconnection = dbconnection.replaceFirst(PASSWORD, password_config); 
   


    //Gather all kind of environmental variables (GB and OS)
//    private final ScriptingConfiguration configuration=argsMap.get(ScriptingAction.CONFIG_KEY);
    File temporaryDir=argsMap.get(ScriptingAction.TEMPDIR_KEY);
    File configDir=argsMap.get(ScriptingAction.CONFIGDIR_KEY);
    String prova=argsMap.get("PROVA");

    //Get the input event
    List events=argsMap.get(ScriptingAction.EVENTS_KEY);
    FileSystemEvent ev=(FileSystemEvent) events.get(0);

    LOGGER.info("scripting action is started... going to check if the schema for the migration exists and in case create it");
    LOGGER.info("temp dir is: '" + temporaryDir.getAbsolutePath() + "'");
    LOGGER.info("config dir is: '" + configDir + "'");
    
    String evnt_path = ev.getSource().getAbsolutePath();
    String evnt_name = ev.getSource().getName();
    LOGGER.info("event source is: '" + evnt_name + "' trying to remove the extension and check if the name is allowed...");
    
    String [] evnt_name_splitted = evnt_name.split("\\.");
    
    LOGGER.info("evnt_name_splitted: '" + evnt_name_splitted + "'");
    LOGGER.info("evnt_name length: '" + evnt_name_splitted.length + "'");

    if(evnt_name_splitted != null && evnt_name_splitted.length != 3){
        throw new IllegalArgumentException("the filename '" + evnt_name + " is not allowed..");
    }
    String schema_name = evnt_name_splitted[0];
    
    LOGGER.info("The filename without extension is: '" + schema_name + "'");
    LOGGER.info("Checking if the file name is made of only letters, digits and underscore, as requested to be used as database schema name...")
    Pattern pattern = Pattern.compile("\\w*");
    Matcher matcher = pattern.matcher(schema_name);
    if(!matcher.matches()){
        throw new IllegalArgumentException("The name extracted from the input filename cannot be used as DB schema name. Please use only numbers, letters and underscore.")
    }
    
//    //going to run internally a task executor action to create a schema

    //load the xml input event template
    File tplFileCS = new File(configDir, "create_schema.xml.tpl");
    FileInputStream tplStreamCS = new FileInputStream(tplFileCS);
    String tplCS = IOUtils.toString(tplStreamCS);
    IOUtils.closeQuietly(tplStreamCS);

    //sobstitute tokens in the template
    tplCS = tplCS.replaceFirst(SCHEMA_NAME, schema_name);
    tplCS = tplCS.replaceFirst(HOST_IP, host_config);
    tplCS = tplCS.replaceFirst(PORT, port_config);
    tplCS = tplCS.replaceFirst(USER, user_config);
    tplCS = tplCS.replaceFirst(DB_NAME, dbname_config);
    tplCS = tplCS.replaceFirst(PASSWORD, password_config);

LOGGER.info("xml after token substitution:");
LOGGER.info("-----------------------------");
LOGGER.info(tplCS);
LOGGER.info("-----------------------------");

    File fCS = new File(temporaryDir, "evntCS.xml");
    FileUtils.writeStringToFile(fCS, tplCS, "UTF-8");
    LOGGER.info("Wrote on file...");

    // Run tthe action
    FileSystemEvent createschemaEvt = new FileSystemEvent(fCS,FileSystemEventType.FILE_ADDED);
    Queue queue=new LinkedList();
    queue.add(createschemaEvt);

LOGGER.info("going to instanziate the Task Executor config...");
    TaskExecutorConfiguration teConfig = new TaskExecutorConfiguration("create_schema","create_schema","create_schema");
LOGGER.info("...TE config instanziated!");
    teConfig.setDefaultScript("");
    teConfig.setErrorFile(create_schema_error_log);
    teConfig.setExecutable(create_schema_executable);
    teConfig.setFailIgnored(false);
    teConfig.setTimeOut(120000);
    teConfig.setXsl("/opt/gb_config_dir/geodatabase2pg/create_schema/create_schema.xsl");
LOGGER.info("going to instanziate the Task Executor action...");    
    TaskExecutor tea=new TaskExecutor(teConfig);
LOGGER.info("...TE action instanziated!");
    tea.setTempDir(temporaryDir);
    tea.setConfigDir(configDir);
LOGGER.info("going to EXECUTE the Task Executor action...");    
queue=tea.execute(queue);
LOGGER.info("...TE action EXECUTED!");
    
    //going to prepare the output xml file to run another task executor (configured as the next action in the flow)
    
    //load the xml input event template
    File tplFile = new File(configDir, "ogr2ogr.xml.tpl");
    FileInputStream tplStream = new FileInputStream(tplFile);
    String tpl = IOUtils.toString(tplStream);
    IOUtils.closeQuietly(tplStream);
    
    //sobstitute tokens in the template
    tpl = tpl.replaceFirst(ZIPPED_FILE_ABS_PATH_PLH, evnt_path);
    dbconnection = dbconnection.replaceFirst(SCHEMA_NAME, schema_name);
    tpl = tpl.replaceFirst(OUT_FORMAT, dbconnection);
    
    File f = new File(temporaryDir, "evnt.xml");
    FileUtils.writeStringToFile(f, tpl, "UTF-8");
    
    // return object
    List results = new ArrayList();
    results.add(f);
    Map retMap = new HashMap();
    retMap.put(ScriptingAction.RETURN_KEY, results);

    LOGGER.info("-----------------------------------------------------------------");
    LOGGER.info("------------------- Groovy Action FINISHED  ---------------------");
    LOGGER.info("-----------------------------------------------------------------");    
    return retMap;
    
}

