/*
 *  Copyright (C) 2013 GeoSolutions S.A.S.
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
package it.geosolutions.geobatch.services.rest;

import static org.junit.Assume.assumeTrue;
import it.geosolutions.geobatch.services.rest.model.RESTConsumerList;
import it.geosolutions.geobatch.services.rest.model.RESTConsumerShort;
import it.geosolutions.geobatch.services.rest.model.RESTConsumerStatus;
import it.geosolutions.geobatch.services.rest.model.RESTConsumerStatus.Status;
import it.geosolutions.geobatch.services.rest.model.RESTFlow;
import it.geosolutions.geobatch.services.rest.model.RESTFlowList;
import it.geosolutions.geobatch.services.rest.model.RESTFlowShort;

import java.net.ConnectException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.geotools.test.OnlineTestSupport;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author ETj (etj at geo-solutions.it)
 * @author DamianoG (damiano.giampaoli at geo-solutions.it)
 */
public class GeoBatchClientTest extends OnlineTestSupport{
    private final static Logger LOGGER = Logger.getLogger(GeoBatchClientTest.class);

    public GeoBatchClientTest() {
    }

    @Override
    protected String getFixtureId() {
        return "geobatch/rest/restconnectionparams";
    }
    
    @Override
    protected Properties createExampleFixture() {
        Properties prop = new Properties();
        prop.put("geobatchRestURL", "http://<host>:<port>/<gb_instance_name>/rest");
        prop.put("user", "theUsername");
        prop.put("password", "thePassword");
        return prop;
    }

    @Override
    public void connect() throws Exception {
        GeoBatchRESTClient client = createClient();
        assumeTrue(pingGeoBatch(client));

    }

    protected GeoBatchRESTClient createClient() {
        GeoBatchRESTClient client = new GeoBatchRESTClient();
        client.setGeobatchRestUrl(super.getFixture().getProperty("geobatchRestURL"));
        client.setUsername(super.getFixture().getProperty("user"));
        client.setPassword(super.getFixture().getProperty("password"));

        return client;
    }

    protected boolean pingGeoBatch(GeoBatchRESTClient client) {
        try {
            client.getFlowService().getFlowList();
            return true;
        } catch (Exception ex) {
            LOGGER.debug("Error connecting to GeoBatch", ex);
            //... and now for an awful example of heuristic.....
            Throwable t = ex;
            while(t!=null) {
                if(t instanceof ConnectException) {
                    LOGGER.warn("Testing GeoBatch is offline");
                    return false;
                }
                t = t.getCause();
            }
            throw new RuntimeException("Unexpected exception: " + ex.getMessage(), ex);
        }
    }

    @Test
    public void testFlows() throws InterruptedException {
        GeoBatchRESTClient client = createClient();
        RESTFlowService flowService = client.getFlowService();
        RESTFlowList rfl = flowService.getFlowList();
        LOGGER.info("There are " + rfl.getList().size() + " flows");
        
        Iterator<RESTFlowShort> iter = rfl.iterator();
        while(iter.hasNext()){
            
            RESTFlowShort currentFlow = iter.next();
            
            LOGGER.info("working on flow: " + currentFlow.getName());
            if("mock_flow".equals(currentFlow.getId())){
                testSuccessFlow(flowService, currentFlow);
            }else if("mockSlow_flow".equals(currentFlow.getId())){
                testSlowFlow(flowService, currentFlow);
            }else if("mockError_flow".equals(currentFlow.getId())){
                //testErrorFlow(flowService, currentFlow);
            }
            
        }
    }
    
    public void testSuccessFlow(RESTFlowService flowService, RESTFlowShort currentFlow) throws InterruptedException{
        
        RESTFlow currentFlowProperties = flowService.getFlow(currentFlow.getId());
        LOGGER.info("----- Number of flow Actions: " + currentFlowProperties.getActionList().size());
        
        RESTConsumerList consumerlist = flowService.getFlowConsumers(currentFlow.getId());
        LOGGER.info("----- Number of Consumers: " + consumerlist.getConsumerList().size());
        
        byte[] content = new byte[10];
        content[0] = '4';
        flowService.run(currentFlow.getId(), false, content);
        LOGGER.info("----- Flow Started!");
        
        LOGGER.info("----- Sleeping for 3 seconds wainting flow termination...");
        LOGGER.info("----- 1...");
        Thread.sleep(1000);
        LOGGER.info("----- 2...");
        Thread.sleep(1000);
        LOGGER.info("----- 3...");
        Thread.sleep(1000);
        
        RESTConsumerList consumerList = flowService.getFlowConsumers(currentFlow.getId());
        LOGGER.info("----- ConsumerList size: " + consumerList.getConsumerList().size());
        
        RESTConsumerShort consumer = consumerList.iterator().next();
        RESTConsumerStatus consumerStatus = flowService.getConsumerStatus(consumer.getUuid());
        LOGGER.info("----- ConsumerStatus: " + consumerStatus.getStatus());
        
        String consumerLog = flowService.getConsumerLog(consumer.getUuid());
        LOGGER.info("----- ConsumerLog:" + consumerLog);            
        
        flowService.cleanupConsumer(consumer.getUuid());
        consumerlist = flowService.getFlowConsumers(currentFlow.getId());
        LOGGER.info("----- Number of Consumers: " + consumerlist.getConsumerList().size());
        
    }
    
    public void testSlowFlow(RESTFlowService flowService, RESTFlowShort currentFlow) throws InterruptedException{
        
        byte[] content = new byte[10];
        content[0] = '4';
        flowService.run(currentFlow.getId(), false, content);
        LOGGER.info("----- Flow Started!");
        
        RESTConsumerList consumerList = flowService.getFlowConsumers(currentFlow.getId());
        LOGGER.info("----- ConsumerList size: " + consumerList.getConsumerList().size());
        
        RESTConsumerShort consumer = consumerList.iterator().next();
        RESTConsumerStatus consumerStatus = flowService.getConsumerStatus(consumer.getUuid());
        LOGGER.info("----- ConsumerStatus: " + consumerStatus.getStatus());
        
        flowService.pauseConsumer(consumer.getUuid());
        LOGGER.info("----- Consumer has been paused, waiting 1 second befire restart it...");
        Thread.sleep(2000);
        
        LOGGER.info("----- Check if the consumer is still running...");
        consumerStatus = flowService.getConsumerStatus(consumer.getUuid());
        LOGGER.info("----- ConsumerStatus: " + consumerStatus.getStatus());
        
        flowService.resumeConsumer(consumer.getUuid());
        LOGGER.info("----- Consumer Resumed...");
        
        long start = System.currentTimeMillis();
        while(System.currentTimeMillis() - start < 10000){
            consumerStatus = flowService.getConsumerStatus(consumer.getUuid());
            if(Status.SUCCESS.equals(consumerStatus.getStatus())){
                return;
            }
        }
        Assert.fail();
    }
    
    public void testErrorFlow(RESTFlowService flowService, RESTFlowShort currentFlow) throws InterruptedException{
        
        byte[] content = new byte[10];
        content[0] = '4';
        flowService.run(currentFlow.getId(), false, content);
        LOGGER.info("----- Flow Started!");
        
        Thread.sleep(2000);
        
        RESTConsumerList consumerList = flowService.getFlowConsumers(currentFlow.getId());
        LOGGER.info("----- ConsumerList size: " + consumerList.getConsumerList().size());
        
        RESTConsumerShort consumer = consumerList.iterator().next();
        RESTConsumerStatus consumerStatus = flowService.getConsumerStatus(consumer.getUuid());
        
        if(!Status.FAIL.equals(consumerStatus.getStatus())){
            Assert.fail();
        }
    }

}