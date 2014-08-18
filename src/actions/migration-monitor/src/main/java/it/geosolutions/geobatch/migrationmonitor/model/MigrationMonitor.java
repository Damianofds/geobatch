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
package it.geosolutions.geobatch.migrationmonitor.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 * 
 * This Class models the strati_rif DataBase table
 * 
 * 
 * @author DamianoG
 *
 */
@Entity(name="strati_rif")
public class MigrationMonitor {
    
    @Id
    @Column(name="id_strato")
    private Long idStrato;
    
    @Column(name="id_repertorio")
    private Integer idRepertorio;
    
    @Column(name="server_name")
    private String serverName;
    
    @Column(name="server_ip")
    private String serverIp;
    
    @Column(name="db")
    private String database;
    
    @Column(name="schema_nome")
    private String schemaNome;
    
    @Column(name="tabella")
    private String tabella;
    
    @Column(name="id_attivazione")
    private Integer idAttivazione;
    
    @Column(name="epsg")
    private Integer epsg;
    
    @Lob
    @Column(name="note")
    private String note;
    
    @Column(name="stato_migrazione")
    private String statoMigrazione;
    
    @Column(name="attivo")
    private Boolean attivo;

    
    public MigrationMonitor() {}

    /**
     * @return the idStrato
     */
    public Long getIdStrato() {
        return idStrato;
    }

    /**
     * @param idStrato the idStrato to set
     */
    public void setIdStrato(Long idStrato) {
        this.idStrato = idStrato;
    }

    /**
     * @return the idRepertorio
     */
    public Integer getIdRepertorio() {
        return idRepertorio;
    }

    /**
     * @param idRepertorio the idRepertorio to set
     */
    public void setIdRepertorio(Integer idRepertorio) {
        this.idRepertorio = idRepertorio;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    /**
     * @return the serverIp
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * @param serverIp the serverIp to set
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the schemaNome
     */
    public String getSchemaNome() {
        return schemaNome;
    }

    /**
     * @param schemaNome the schemaNome to set
     */
    public void setSchemaNome(String schemaNome) {
        this.schemaNome = schemaNome;
    }

    /**
     * @return the tabella
     */
    public String getTabella() {
        return tabella;
    }

    /**
     * @param tabella the tabella to set
     */
    public void setTabella(String tabella) {
        this.tabella = tabella;
    }

    /**
     * @return the idAttivazione
     */
    public Integer getIdAttivazione() {
        return idAttivazione;
    }

    /**
     * @param idAttivazione the idAttivazione to set
     */
    public void setIdAttivazione(Integer idAttivazione) {
        this.idAttivazione = idAttivazione;
    }

    /**
     * @return the epsg
     */
    public Integer getEpsg() {
        return epsg;
    }

    /**
     * @param epsg the epsg to set
     */
    public void setEpsg(Integer epsg) {
        this.epsg = epsg;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the statoMigrazione
     */
    public String getStatoMigrazione() {
        return statoMigrazione;
    }

    /**
     * @param statoMigrazione the statoMigrazione to set
     */
    public void setStatoMigrazione(String statoMigrazione) {
        this.statoMigrazione = statoMigrazione;
    }

    /**
     * @return the attivo
     */
    public Boolean getAttivo() {
        return attivo;
    }

    /**
     * @param attivo the attivo to set
     */
    public void setAttivo(Boolean attivo) {
        this.attivo = attivo;
    }

    
}
