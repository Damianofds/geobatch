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

package it.geosolutions.geobatch.migrationmonitor.monitor;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.geobatch.annotations.Action;
import it.geosolutions.geobatch.annotations.CheckConfiguration;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;
import it.geosolutions.geobatch.migrationmonitor.dao.MigrationMonitorDAO;
import it.geosolutions.geobatch.migrationmonitor.model.MigrationMonitor;
import it.geosolutions.geobatch.migrationmonitor.utils.DS2DSTokenResolver;
import it.geosolutions.geobatch.migrationmonitor.utils.enums.MigrationStatus;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 
 * 
 * @author Damiano Giampaoli, GeoSolutions
 * 
 * @version $MonitorAction.java Revision: 0.1 $ 24/07/2014
 */

@Action(configurationClass = MonitorConfiguration.class)
public class MonitorAction extends BaseAction<FileSystemEvent> {

    private MonitorConfiguration configuration;
    
    private MigrationMonitorDAO migrationMonitorDAO;
    
    public MonitorAction(MonitorConfiguration configuration) {
        super(configuration);
        this.configuration = configuration;
    }

    public void setMigrationMonitorDAO(MigrationMonitorDAO migrationMonitorDAO) {
        this.migrationMonitorDAO = migrationMonitorDAO;
    }
    
    @Override
    public Queue<FileSystemEvent> execute(Queue<FileSystemEvent> events) throws ActionException {

        // return object
        final Queue<FileSystemEvent> outputEvents = new LinkedList<FileSystemEvent>();

        try {
            // looking for file
            if (events.size() == 0)
                throw new IllegalArgumentException(
                        "MonitorAction::execute(): Wrong number of elements for this action: "
                                + events.size());

            listenerForwarder.setTask("config");
            listenerForwarder.started();

            if (configuration == null) {
                final String message = "MonitorAction::execute(): DataFlowConfig is null.";
                if (LOGGER.isErrorEnabled())
                    LOGGER.error(message);
                throw new IllegalStateException(message);
            }

            // retrieve all the ELEMENTS table from the DB
            List<MigrationMonitor> migrationList = migrationMonitorDAO.findTablesToMigrate();
            DS2DSTokenResolver tknRes = null;

            // init some usefull counters
            int failsCounter = 0;
            int iterCounter = 0;
            int totElem = migrationList.size();

            // Process all MigrationMonitors retrieved
            for (MigrationMonitor mm : migrationList) {
                iterCounter++;
                try {
                    tknRes = new DS2DSTokenResolver(mm);
                } catch (IOException e) {
                    failsCounter++;
                    LOGGER.error(e.getMessage(), e);
                    LOGGER.error("error while processing MigrationMonitor " + mm.toString()
                            + " let's skip it! (the error happens while resolving tokens...)");
                    LOGGER.error("fail number: " + failsCounter + " MigrationMonitor processed "
                            + iterCounter + "/" + totElem);
                    continue;
                }

                String filename = mm.getTabella() + mm.getIdStrato() + ".xml";
                Writer writer = null;
                try {
                    writer = new FileWriter(getTempDir() + "/" + filename);
                    writer.append(tknRes.getOutputFileContent());
                } catch (IOException e) {
                    LOGGER.error("error while processing MigrationMonitor "
                            + mm.toString()
                            + " let's skip it! (the error happens while writing on the output file)");
                    LOGGER.error("fail number: " + failsCounter + " MigrationMonitor processed "
                            + iterCounter + "/" + totElem);
                    continue;
                } finally {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                }

                mm.setStatoMigrazione(MigrationStatus.INPROGRESS.toString().toUpperCase());
                migrationMonitorDAO.merge(mm);
            }

        } catch (Exception t) {
            final String message = "MonitorAction::execute(): " + t.getLocalizedMessage();
            if (LOGGER.isErrorEnabled())
                LOGGER.error(message, t);
            final ActionException exc = new ActionException(this, message, t);
            listenerForwarder.failed(exc);
            throw exc;
        }

        return outputEvents;
    }
    
    @CheckConfiguration
    public boolean checkConfiguration() {
        LOGGER.info("Calculating if this action could be Created...");
        return true;
    }
}
