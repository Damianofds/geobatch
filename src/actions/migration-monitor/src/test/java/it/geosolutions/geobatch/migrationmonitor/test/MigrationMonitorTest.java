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

package it.geosolutions.geobatch.migrationmonitor.test;

import it.geosolutions.filesystemmonitor.monitor.FileSystemEvent;
import it.geosolutions.geobatch.flow.event.action.ActionException;
import it.geosolutions.geobatch.migrationmonitor.dao.MigrationMonitorDAO;
import it.geosolutions.geobatch.migrationmonitor.model.MigrationMonitor;
import it.geosolutions.geobatch.migrationmonitor.monitor.MonitorAction;
import it.geosolutions.geobatch.migrationmonitor.monitor.MonitorConfiguration;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.googlecode.genericdao.search.Search;


/**
 * @author DamianoG
 *
 */
//@Ignore(value="This is just a quick and dirty test to check database connections, DAO configuration and implementation")
public class MigrationMonitorTest {

    
    private static final Logger LOGGER = Logger.getLogger(MigrationMonitorTest.class);
    
    private MigrationMonitorDAO migrationMonitorDAO; 
    
    public void setMigrationMonitorDAO(MigrationMonitorDAO migrationMonitorDAO){
        this.migrationMonitorDAO = migrationMonitorDAO;
    }
    
    @Test
    public void basicRawTest(){
        
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        MigrationMonitorDAO dao = (MigrationMonitorDAO)context.getBean("migrationMonitorDAO");
        int count = dao.count(new Search(MigrationMonitor.class));
        MigrationMonitor monitor = dao.find(1l);
        monitor.getServerName();
        List<MigrationMonitor> listToMigrate = dao.findAll();
        for(MigrationMonitor mm : listToMigrate){
            System.out.println(mm.getTabella());
        }
       
    }
    
    @Test
    public void geobatchActionRunner(){
        
        MonitorConfiguration mc = new MonitorConfiguration("Monitor","Monitor","Monitor");
        MonitorAction ca = new MonitorAction(mc);
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        MigrationMonitorDAO dao = (MigrationMonitorDAO)context.getBean("migrationMonitorDAO");
        try {
            ca.setMigrationMonitorDAO(dao);
            ca.execute(new LinkedList<FileSystemEvent>());
        } catch (ActionException e) {
            LOGGER.error(e.getMessage(), e);
        }
       
    }
}
