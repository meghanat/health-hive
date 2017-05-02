package cdaGeneration;
 
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.*;

 
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="ClinicalDocument",namespace="urn:something")

public class ClinicalDocument  {
    private Id id;
    private String title;
    private EffectiveTime effectiveTime ;


    public ClinicalDocument(){
        this.id=null;
        this.title=null;
        this.effectiveTime=null;
    }
    
     
    public String getTitle() {
        return title;
    }
 
    public void setTitle(String title) {
        this.title = title;
    }

    public EffectiveTime getEffectiveTime() {
        return effectiveTime;
    }
 
    public void setEffectiveTime(String effectiveTime) {
        this.effectiveTime = new EffectiveTime(effectiveTime);
    }
    
    
    public Id getId() {
        return id;
    }
 
    public void setId(String extension, String root) {
        this.id = new Id(extension,root);
    } 
 
    @Override
    public String toString() {
        return "ClinicalDocument [title=" + title + ", effectiveTime=" + effectiveTime + ", id="
                + id + "]";
    }   
}