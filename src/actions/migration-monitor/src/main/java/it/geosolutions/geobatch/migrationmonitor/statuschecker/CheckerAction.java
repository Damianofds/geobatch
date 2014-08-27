/*
 *  GeoBatch - Open Source geospatial batch processing system
 *  http://geobatch.geo-solutions.it/
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.geosolutions.geobatch.migrationmonitor.statuschecker;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.filesystemmonitor.monitor.FileSystemEventType;
import it.geosolutions.geobatch.annotations.Action;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;
import it.geosolutions.geobatch.migrationmonitor.dao.MigrationMonitorDAO;
import it.geosolutions.geobatch.migrationmonitor.model.MigrationMonitor;
import it.geosolutions.geobatch.migrationmonitor.utils.enums.MigrationStatus;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * 
 * @author Damiano Giampaoli, GeoSolutions
 * 
 * @version $CheckerAction.java Revision: 0.1 $ 24/07/2014
 */

/**
 * 
 * The checker Action is a custom Action developed for ADBA that is responsible to change the status from INPROGRESS to MIGRATED
 * This action must follow a ds2ds action that is responsible to perform the migration. 
 * If the ds2ds action fails this action won't be invoked so the flag remain in the same status
 * If the ds2ds terminate with a successful outcome this action is invoked. 
 * 
 * @author DamianoG
 *
 */
@Action(configurationClass=CheckerConfiguration.class)
public class CheckerAction extends BaseAction<FileSystemEvent> {

    private CheckerConfiguration configuration;
    
    private MigrationMonitorDAO migrationMonitorDAO;
    
    public CheckerAction(CheckerConfiguration actionConfiguration) {
        super(actionConfiguration);
        this.configuration = actionConfiguration;
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-DAO.xml");
        Object mm = context.getBean("migrationMonitorDAO");
        if(mm instanceof MigrationMonitorDAO){
            setMigrationMonitorDAO((MigrationMonitorDAO)mm);
        }
    }
    
    public void setMigrationMonitorDAO(MigrationMonitorDAO migrationMonitorDAO) {
        this.migrationMonitorDAO = migrationMonitorDAO;
    }
    
    
    @Override
    public Queue<FileSystemEvent> execute(Queue<FileSystemEvent> arg0) throws ActionException {

        // return object
        final Queue<FileSystemEvent> outputEvents = new LinkedList<FileSystemEvent>();
        
        try{
            //gather the input file in order to read the database table name
            File flowTempDirectory = new File(getTempDir().getParent());
            File [] files = flowTempDirectory.listFiles();
            File inputEventFile = null;
            if(files != null && files.length > 0){
                for(File f : files){
                    if(f.isFile() && f.getName().endsWith(".xml")){
                        inputEventFile = f;
                    }
                }
            }
            else{
                throw new Exception("One file, type xml,  is expected in the root of the temp directory");
            }
            
            // set as the action output event the flow input event
            FileSystemEvent fse = new FileSystemEvent(inputEventFile, FileSystemEventType.FILE_ADDED);
            outputEvents.add(fse);
            
            //parse the xml file and get the table name
            String tableName = "";
            String host = "";
            String db = "";
            String schema = "";
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder;
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputEventFile);
                doc.getDocumentElement().normalize();
                NodeList nodes = doc.getElementsByTagName("typeName");
                if(nodes == null || nodes.getLength() != 1){
                    throw new Exception("more than one typeName has been found in the input event... this is not possible...");
                }
                Node n =  nodes.item(0);
                tableName = n.getTextContent();
                NodeList entries = doc.getElementsByTagName("entry");
                host = extractEntry("server", entries);
                if(host == null){
                    host = extractEntry("host", entries);
                }
                db = extractEntry("instance", entries);
                if(db == null){
                    db = extractEntry("database", entries);
                }
                schema = extractEntry("schema", entries);
                LOGGER.info("Changing state to MIGRATED for records with: server_ip:'" + host + "' db:'" + db + "' schema_nome:'" + schema + "' tabella:'" + tableName + "'");
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw new Exception("Error while parsing input file... exception message: " + e.getMessage());
            }
            LOGGER.info("The table name is: " + tableName);
            
            //change the status in the strati_rif table
            MigrationMonitor mm = migrationMonitorDAO.findByTablename(host, db, schema, tableName);
            mm.setStatoMigrazione(MigrationStatus.MIGRATED.toString().toUpperCase());
            migrationMonitorDAO.merge(mm);
        
        } catch (Exception t) {
            final String message = "CheckerAction::execute(): " + t.getLocalizedMessage();
            if (LOGGER.isErrorEnabled())
                LOGGER.error(message, t);
            final ActionException exc = new ActionException(this, message, t);
            listenerForwarder.failed(exc);
            throw exc;
        }

        return outputEvents;
    }

    
    public String extractEntry(String entryName, NodeList entries){
        String text = "";
        boolean found = false;
        for(int i=0; i<entries.getLength(); i++){
            Node n = entries.item(i);
            NodeList cn = n.getChildNodes();
            for(int j=0; j<cn.getLength(); j++){
                Node tmp = cn.item(j);
                if(tmp.getNodeType() == Node.ELEMENT_NODE){
                    String tmpText = tmp.getTextContent(); 
                    if(tmpText.equals(entryName)){
                        found = true;
                    }
                    else{
                        text = tmpText;
                    }
                }
            }
            if(found){
                return text;
            }
        }
        return null;
    }
    
    @Override
    public boolean checkConfiguration() {

        return true;
    }
}
