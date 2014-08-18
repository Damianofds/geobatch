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
package it.geosolutions.geobatch.migrationmonitor.utils.enums;

/**
 * @author DamianoG
 *
 */
public class DS2DSConfigTokens {

    public static String TYPENAME = "${typeName_value}";
    public static String CRS = "${crs_value}";
    public static String DBTYPE = "${dbtype_value}";
    public static String SERVER = "${server_value}";
    public static String PORT = "${port_value}";
    public static String INSTANCE = "${instance_value}";
    public static String USER = "${user_value}";
    public static String PASSWORD = "${password_value}";
    public static String MAX_CONN = "${pool.maxConnections_value}";
    public static String MIN_CONN = "${pool.minConnections_value}";
    public static String ALLOW_NON_SPATIAL_TABLES = "${datastore.allowNonSpatialTables_value}";
    public static String TIMEOUT = "${pool.timeOut_value}";
}
