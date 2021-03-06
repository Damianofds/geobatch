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

package it.geosolutions.geobatch.octave;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * 
 * @author Carlo Cancellieri - carlo.cancellieri@geo-solutions.it
 *
 */
@XStreamAlias("OctaveFile")
public class SerializableOctaveFile extends SerializableOctaveString{
    
    //< contains the value of this variable
    @XStreamAsAttribute
    @XStreamAlias("output")
    private boolean _output=false;
    
    //< contains the value of this variable
    @XStreamAsAttribute
    @XStreamAlias("input")
    private boolean _input=true;
    
    public SerializableOctaveFile(String name,String val){
        super(name,val);
    }
    
    public SerializableOctaveFile(String name,String val, boolean in, boolean out){
        super(name,val);
        _output=out;
        _input=in;
    }
    
    /**
     * is it an input file?
     * @return
     */
    public boolean isInput(){
        return _input;
    }
    
    public void setInput(){
        _input=true;
    }
    
    /**
     * is it an output file?
     * @return
     */
    public boolean isOutput(){
        return _output;
    }
    
    public void setOutput(){
        _output=true;
    }
    
    @Override
    public Object clone(){
        SerializableOctaveFile sof=
            new SerializableOctaveFile(
                    new String(this.getName()),
                    new String(this.getValue()),
                    isInput(),
                    isOutput());
        return sof;
    }
}
