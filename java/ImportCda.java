
import cda.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import java.io.File;
import java.util.*;

public class ImportCda {

  /**
   * @param args
   * @throws JAXBException 
   */
  public static void main(String[] args) throws JAXBException {
    
    File file=new File("sampleCDA.xml");
    ClinicalDocument doc = JAXBXMLHandler.unmarshal(file);
        
    List <ClinicalDocument.Component> componentList = new ArrayList<ClinicalDocument.Component>();
    componentList=doc.getComponent();
    for(ClinicalDocument.Component component:componentList){

      if(component==null){
        // System.out.println("Is null");
        continue;
      }

        ClinicalDocument.Component.Section section=component.getSection();
        if(section!=null){
          String tableName=section.getTitle();
          System.out.println(tableName);
          String effectiveTime="";
          String columnNames="(";
          String columnValues="("; 
          List<ClinicalDocument.Component.Section.Entry> entryList=section.getEntry();
          if(entryList!=null){

            for(ClinicalDocument.Component.Section.Entry entry: entryList){

              if(entry==null){
                continue;
              }
              else{
                 ClinicalDocument.Component.Section.Entry.Observation.Code code=entry.getObservation().getCode();

                 ClinicalDocument.Component.Section.Entry.Observation.Value value=entry.getObservation().getValue();

                String columnName=code.getOriginalText();
                if(columnName==null){

                  columnName=code.getDisplayName();

                }
                String columnValue=value.getOriginalText();
                if(columnValue==null){

                  columnValue=value.getDisplayName();

                }
                if(columnValue.trim().length()==0||columnValue==null){
                    columnValue="null";
                  }
                else{
                  columnValue="'"+columnValue+"'";
                }

                // System.out.println("Column:"+columnName+" Value: "+columnValue);
                columnNames+=columnName+",";
                columnValues+=columnValue+",";
              }

            }
            columnNames = columnNames.substring(0, columnNames.length()-1);
            columnValues = columnValues.substring(0, columnValues.length()-1);
            columnNames+=")";
            columnValues+=")";
            System.out.println("\nINSERT INTO "+tableName+" "+columnNames+" VALUES "+columnValues);

          }


        }
        

    }

    




    // ExpenseT expenseObj = unmarshalledObject.getValue();
    // UserT user = expenseObj.getUser();
    // ItemListT items = expenseObj.getItems();

    // //Obtaining all the required data from the JAXB Root class instance.
    // System.out.println("Printing the Expense for: "+user.getUserName());
    // for ( ItemT item : items.getItem()){
    //   System.out.println("Name: "+item.getItemName());
    //   System.out.println("Value: "+item.getAmount());
    //   System.out.println("Date of Purchase: "+item.getPurchasedOn());
    // }   
  }

}