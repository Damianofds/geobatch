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

package it.geosolutions.geobatch.mock.error;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.geobatch.annotations.Action;
import it.geosolutions.geobatch.annotations.CheckConfiguration;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;

import java.io.IOException;
import java.util.Queue;

/**
 * 
 * This is a mock action useful for testing action error management.
 * 
 * 
 * @author DamianoG
 *
 */
@Action(configurationClass = MockErrorConfiguration.class)
public class MockErrorAction extends BaseAction<FileSystemEvent> {

    private MockErrorConfiguration configuration;

    public MockErrorAction(MockErrorConfiguration configuration) throws IOException {
        super(configuration);
        this.configuration = configuration;
    }

    @CheckConfiguration
    public boolean checkConfiguration() {
        LOGGER.info("Calculating if this action could be Created...");
        return true;
    }

    @Override
    public Queue<FileSystemEvent> execute(Queue<FileSystemEvent> events) throws ActionException {

        new ActionException(this, "Mock error, an Action Exception has been thrown");
        return events;
    }

}
