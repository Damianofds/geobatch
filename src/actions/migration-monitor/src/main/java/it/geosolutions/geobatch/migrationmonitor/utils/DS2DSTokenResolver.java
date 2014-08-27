/*
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
package it.geosolutions.geobatch.migrationmonitor.utils;

import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.ALLOW_NON_SPATIAL_TABLES;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.CRS;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.DBTYPE;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.INSTANCE;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.MAX_CONN;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.MIN_CONN;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.PASSWORD;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.PORT;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.SCHEMA;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.SERVER;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.TIMEOUT;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.TYPENAME;
import static it.geosolutions.geobatch.migrationmonitor.utils.enums.DS2DSConfigTokens.USER;
import it.geosolutions.geobatch.migrationmonitor.model.MigrationMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * This class is responsible for:
 * *** Load the template of the DS2DS file
 * *** Substitute the token in the template with the values retrieved from the DB mixed with defaults value if no value is aveilable for a certain field 
 * 
 * @author DamianoG
 * 
 */
public class DS2DSTokenResolver {

    protected final static Logger LOGGER = LoggerFactory.getLogger(DS2DSTokenResolver.class);

    // ${typeName_value}
    // ${crs_value}
    // ${dbtype_value}
    // ${server_value}
    // ${port_value}
    // ${instance_value}
    // ${user_value}
    // ${password_value}
    // ${pool.maxConnections_value}
    // ${pool.minConnections_value}
    // ${datastore.allowNonSpatialTables_value}
    // ${pool.timeOut_value}

    private String outputFileContent;
    
    /**
     * Takes an instance of MigrationMonitor and use it (plus the defaults property file) in order to build the DS2DS
     * action input file used to start the migration
     * 
     * @param migMonit
     * @throws IOException
     */
    public DS2DSTokenResolver(MigrationMonitor migMonit, File configDir) throws IOException {

        String output = loadOutputTemplate();
        Properties defaultsValues = loadDefaultValues(configDir);
        
        //TODO map all MigrationMonitor fields on the related DS2DS fields and use the default if the default migmonit value is empty 
        //TODO Use reflection + annotations to make the following associations automagic
        String tmp = "";
        StringBuffer msgToLog = new StringBuffer();
        msgToLog.append("[[");
        
        tmp=migMonit.getTabella();
        tmp=tmp.trim();
        output = output.replace(TYPENAME, tmp);
        msgToLog.append("TYPENAME:");
        msgToLog.append(tmp);
        
        if(migMonit.getEpsg() == null){
            output = output.replace("<crs>"+CRS+"</crs>", "");
        }
        else{
            tmp=Integer.toString(migMonit.getEpsg());
            tmp=tmp.trim();
            output = output.replace(CRS, tmp);
            msgToLog.append(";CRS:");
            msgToLog.append(tmp);
        }
        
        tmp=defaultsValues.getProperty("DBTYPE");
        String dbtype = tmp;
        tmp=tmp.trim();
        output = output.replace(DBTYPE, tmp);
        msgToLog.append(";DBTYPE:");
        msgToLog.append(tmp);
        
        tmp=migMonit.getServerIp();
        tmp=tmp.trim();
        output = output.replace(SERVER, tmp);
        msgToLog.append(";SERVER/HOST:");
        msgToLog.append(tmp);
        if(!dbtype.equalsIgnoreCase("arcsde")){
            output = output.replace("server", "host");
        }
        
        tmp=migMonit.getSchemaNome();
        tmp=tmp.trim();
        output = output.replace(SCHEMA, tmp);
        msgToLog.append(";SCHEMA:");
        msgToLog.append(tmp);
        
        tmp=defaultsValues.getProperty("PORT");
        tmp=tmp.trim();
        output = output.replace(PORT, tmp);
        msgToLog.append(";PORT:");
        msgToLog.append(tmp);
        
        
        String tmpDefault = defaultsValues.getProperty("INSTANCE");
        tmp=(tmpDefault == null || tmpDefault.isEmpty())?migMonit.getDatabase():tmpDefault;
        tmp=tmp.trim();
        output = output.replace(INSTANCE, tmp);
        msgToLog.append(";DATABASE/INSTANCE:");
        msgToLog.append(tmp);
        if(!dbtype.equalsIgnoreCase("arcsde")){
            output = output.replace("instance", "database");
        }
        
        tmp=defaultsValues.getProperty("USER");
        tmp=tmp.trim();
        output = output.replace(USER, tmp);
        msgToLog.append(";USER:");
        msgToLog.append(tmp);
        
        tmp=defaultsValues.getProperty("PASSWORD");
        tmp=tmp.trim();
        output = output.replace(PASSWORD, tmp);
        msgToLog.append(";PASSWORD:");
        msgToLog.append(tmp);
        if(!dbtype.equalsIgnoreCase("arcsde")){
            output = output.replace("password", "passwd");
        }
        
        tmp=defaultsValues.getProperty("MAX_CONN");
        tmp=tmp.trim();
        output = output.replace(MAX_CONN, tmp);
        msgToLog.append(";MAX_CONN:");
        msgToLog.append(tmp);
        
        tmp=defaultsValues.getProperty("MIN_CONN");
        tmp=tmp.trim();
        output = output.replace(MIN_CONN, tmp);
        msgToLog.append(";MIN_CONN:");
        msgToLog.append(tmp);
        
        tmp=defaultsValues.getProperty("ALLOW_NON_SPATIAL_TABLES");
        tmp=tmp.trim();
        output = output.replace(ALLOW_NON_SPATIAL_TABLES, tmp);
        msgToLog.append(";ALLOW_NON_SPATIAL_TABLES:");
        msgToLog.append(tmp);
        
        tmp=defaultsValues.getProperty("TIMEOUT");
        tmp=tmp.trim();
        output = output.replace(TIMEOUT, tmp);
        msgToLog.append(";TIMEOUT:");
        msgToLog.append(tmp);

        msgToLog.append("]]");
        
        outputFileContent = output;
        
        LOGGER.info("Loaded info for current Migration Monitor (custom + defaults):\n"+msgToLog.toString());
    }

    /**
     * @return the resolved DS2DStemplate file
     */
    public String getOutputFileContent() {
        return outputFileContent;
    }
    
    private String loadOutputTemplate() throws IOException{
        
        StringWriter writer = null;
        InputStream is = null;
        Reader r = null;
        String output = null;        
        try {
            is = getClass().getResourceAsStream("template.txt");
            r = new InputStreamReader(is);
            writer = new StringWriter();
            IOUtils.copy(is, writer);
            output = writer.toString();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new IOException("Error while loading the DS2DSTemplate...");
        } finally {
            try {
                r.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            try {
                is.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
            try {
                writer.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return output;
    }
    
    /**
     * Try to load the default values properties from the GB config dir, if it fails load the default defaultValues file inside the webapp
     * 
     * @return
     * @throws IOException
     */
    private Properties loadDefaultValues(File configDir) throws IOException{
        
        LOGGER.info("The provided config dir is: " + configDir.getAbsolutePath());
        if(configDir == null || !configDir.isDirectory() || !configDir.canRead()){
            LOGGER.warn("The provided config dir is null, it is not a directory or it cannot be read... skipping to load the internal defaultValues file...");
        }
        else{
            File f = new File(configDir + File.separator + "DS2DSDefaultsValues.properties");
            if(f == null || !f.exists() || f.isDirectory()){
                LOGGER.warn("Cannot find or read the defaultFile in the configDir... skipping to load the internal defaultValues file...");
            }
            else{
                Properties prop = new Properties();
                InputStream in = new FileInputStream(f);
                prop.load(in);
                LOGGER.info("Loaded defaultValues from GB configDir...");
                return prop;
            }
        }
        
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = getClass().getResourceAsStream("DS2DSDefaultsValues.properties");
            prop.load(input);
            LOGGER.info("Loaded defaultValues from the wepapp...");
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw new IOException("Error while loading the Default properties file...");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return prop;
    }
}
