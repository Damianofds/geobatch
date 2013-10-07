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

package it.geosolutions.geobatch.mock.slowsuccess;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.geobatch.annotations.Action;
import it.geosolutions.geobatch.annotations.CheckConfiguration;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.flow.event.action.BaseAction;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 
 * This is a mock action useful for testing
 * 
 * 
 * @author DamianoG
 *
 */
@Action(configurationClass = MockSlowConfiguration.class)
public class MockSlowAction extends BaseAction<FileSystemEvent> {

    private MockSlowConfiguration configuration;

    public MockSlowAction(MockSlowConfiguration configuration) throws IOException {
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

        try {
            // looking for file
            if (events.size() == 0)
                throw new IllegalArgumentException(
                        "MockAction::execute(): Wrong number of elements for this action: "
                                + events.size());

            listenerForwarder.setTask("config");
            listenerForwarder.started();

            // The return
            Queue<FileSystemEvent> ret = new LinkedList<FileSystemEvent>();
            
            while (events.size() > 0) {

                // run
                listenerForwarder.progressing(0, "Embedding overviews");

                // Going to Simulate a long processing step
                Thread.sleep(10000);
                
                final FileSystemEvent event = events.remove();
                ret.add(event);
                final File eventFile = event.getSource();

                listenerForwarder.setProgress(10);
                listenerForwarder.progressing(10, "config");

                LOGGER.debug("Mock configuration field value: " + configuration.getMockProperty());

            }
            listenerForwarder.completed();

            return ret;
            
        } catch (Exception t) {
            final String message = "Mock1::execute(): " + t.getLocalizedMessage();
            if (LOGGER.isErrorEnabled())
                LOGGER.error(message, t);
            final ActionException exc = new ActionException(this, message, t);
            listenerForwarder.failed(exc);
            throw exc;
        }
    }

}
