package cdaGeneration;

import javax.xml.bind.annotation.*;
 
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "effectiveTime")

public class EffectiveTime {
    @XmlAttribute
    private String value;
    
    public EffectiveTime(){
        this.value=null;
    }
    public EffectiveTime(String value){

        this.value=value;
    }
     
    public String getValue() {
        return this.value;
    }
    
    
    @Override
    public String toString() {
        return "effectiveTime [value=" + value+"]";
    }   
}