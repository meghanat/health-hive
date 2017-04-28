import javax.xml.bind.annotation.*;
 
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "id")

public class Id {
    @XmlAttribute
    private String extension;
    
    @XmlAttribute
    private String root ;

    public Id(){
        this.root=null;
        this.extension=null;
    }
    
    public Id(String extension, String root){

        this.extension=extension;
        this.root=root;
    }
     
    public String getExtension() {
        return this.extension;
    }
    
    
    public String getRoot() {
        return this.root;
    }
 
 
    @Override
    public String toString() {
        return "id [extension=" + extension + ", root=" + root + "]";
    }   
}