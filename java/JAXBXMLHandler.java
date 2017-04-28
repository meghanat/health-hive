import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


public class JAXBXMLHandler {
 
    // Export
    public static void marshal(ClinicalDocument doc, File selectedFile)
            throws IOException, JAXBException {
        JAXBContext context;
        BufferedWriter writer = null;
        writer = new BufferedWriter(new FileWriter(selectedFile));
        context = JAXBContext.newInstance(ClinicalDocument.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(doc, writer);
        writer.close();
    }
 
    // Import
    public static ClinicalDocument unmarshal(File importFile) throws JAXBException {
        ClinicalDocument doc = new ClinicalDocument();
 
        JAXBContext context = JAXBContext.newInstance(ClinicalDocument.class);
        Unmarshaller um = context.createUnmarshaller();
        doc = (ClinicalDocument) um.unmarshal(importFile);
 
        return doc;
    }
}