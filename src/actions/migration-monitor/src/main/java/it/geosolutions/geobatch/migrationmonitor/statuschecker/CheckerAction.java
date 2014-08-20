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
import it.geosolutions.geobatch.annotations.Action;
import it.geosolutions.geobatch.configuration.event.action.ActionConfiguration;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;
import it.geosolutions.geobatch.migrationmonitor.dao.MigrationMonitorDAO;


import java.util.Queue;

/**
 *
 * 
 * @author Damiano Giampaoli, GeoSolutions
 * 
 * @version $CheckerAction.java Revision: 0.1 $ 24/07/2014
 */

/**
 * 
 * The checker Action is a custom Action developed for ADBA that is responsible to periodically monitoring 
 * a database table searching for records that identify a "request for a migration"
 * 
 * * Each x seconds do
 * *** Load the data from the database table, filter the dataset with the filter "where attivo=TRUE AND stato_migrazione=NOTYET"
 * *** For each record represent a migration produce the related input file for the DS2DS action that is responsible for perform the migration
 * *** Mark the record as INPROGRESS   
 * 
 * 
 * @author DamianoG
 *
 */
@Action(configurationClass=CheckerConfiguration.class)
public class CheckerAction extends BaseAction<FileSystemEvent> {

    
    private MigrationMonitorDAO migrationMonitorDAO; 
    
    public void setMigrationMonitorDAO(MigrationMonitorDAO migrationMonitorDAO){
        this.migrationMonitorDAO = migrationMonitorDAO;
    }
    
    public CheckerAction(ActionConfiguration actionConfiguration) {
        super(actionConfiguration);
    }

    @Override
    public Queue<FileSystemEvent> execute(Queue<FileSystemEvent> arg0) throws ActionException {

        return null;
    }

    @Override
    public boolean checkConfiguration() {

        return true;
    }
}
