/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.geosolutions.geobatch.migrationmonitor.dao;

import it.geosolutions.geobatch.migrationmonitor.model.MigrationMonitor;
import it.geosolutions.geobatch.migrationmonitor.utils.enums.MigrationStatus;

import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.search.ISearch;
import com.googlecode.genericdao.search.Search;

/**
 *
 * @author DamianoG
 */
@Transactional(value = "migration-monitorTransactionManager")
public class MigrationMonitorDAOImpl extends BaseDAO<MigrationMonitor, Long> implements MigrationMonitorDAO{

   @Override
    public List<MigrationMonitor> search(ISearch search) {
        return super.search(search);
    }

    @Override
    public void persist(MigrationMonitor... entities) {
        super.persist(entities);
    }

    @Override
    public MigrationMonitor merge(MigrationMonitor entity) {
        return super.merge(entity);
    }

    @Override
    public boolean remove(MigrationMonitor entity) {
        return super.remove(entity);
    }

    @Override
    public boolean removeById(Long id) {
        return super.removeById(id);
    }

    @Override
    public MigrationMonitor findByName(String variable) {
         Search searchCriteria = new Search(MigrationMonitor.class);
         searchCriteria.addFilterEqual("variable", variable);
         List<MigrationMonitor> entries = this.search(searchCriteria);
         if ( entries.size() > 0){
             return entries.get(0);
         }
         return null;
    }

    @Override
    public List<MigrationMonitor> findTablesToMigrate() {
        Search searchCriteria = new Search(MigrationMonitor.class);
        searchCriteria.addFilterEqual("attivo", true);
        searchCriteria.addFilterIn("statoMigrazione", MigrationStatus.NOTYET, MigrationStatus.INPROGRESS);
        List<MigrationMonitor> entries = this.search(searchCriteria);
        if (entries == null){
            return new ArrayList<MigrationMonitor>();
        }
        return entries;
    }

    @Override
    public MigrationMonitor findByTablename(String host, String db, String schema, String tableName) throws Exception {
        Search searchCriteria = new Search(MigrationMonitor.class);
        searchCriteria.addFilterEqual("serverIp", host);
        searchCriteria.addFilterEqual("database", db);
        searchCriteria.addFilterEqual("schemaNome", schema);
        searchCriteria.addFilterEqual("tabella", tableName);
        
        searchCriteria.addFilterEqual("attivo", true);
        searchCriteria.addFilterIn("statoMigrazione", MigrationStatus.INPROGRESS, MigrationStatus.NOTYET );
        
        List<MigrationMonitor> entries = this.search(searchCriteria);
        if (entries == null){
            return new MigrationMonitor();
        }
        if (entries.size() != 1){
            throw new Exception("More than one results for the search is not expected...");
        }
        return entries.get(0);
    }
    
}
