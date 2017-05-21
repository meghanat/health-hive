//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.05.21 at 10:30:31 PM IST 
//


package cda;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the cda package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: cda
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ClinicalDocument }
     * 
     */
    public ClinicalDocument createClinicalDocument() {
        return new ClinicalDocument();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component }
     * 
     */
    public ClinicalDocument.Component createClinicalDocumentComponent() {
        return new ClinicalDocument.Component();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section }
     * 
     */
    public ClinicalDocument.Component.Section createClinicalDocumentComponentSection() {
        return new ClinicalDocument.Component.Section();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section.Entry }
     * 
     */
    public ClinicalDocument.Component.Section.Entry createClinicalDocumentComponentSectionEntry() {
        return new ClinicalDocument.Component.Section.Entry();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section.Entry.Observation }
     * 
     */
    public ClinicalDocument.Component.Section.Entry.Observation createClinicalDocumentComponentSectionEntryObservation() {
        return new ClinicalDocument.Component.Section.Entry.Observation();
    }

    /**
     * Create an instance of {@link ClinicalDocument.RecordTarget }
     * 
     */
    public ClinicalDocument.RecordTarget createClinicalDocumentRecordTarget() {
        return new ClinicalDocument.RecordTarget();
    }

    /**
     * Create an instance of {@link ClinicalDocument.RecordTarget.PatientRole }
     * 
     */
    public ClinicalDocument.RecordTarget.PatientRole createClinicalDocumentRecordTargetPatientRole() {
        return new ClinicalDocument.RecordTarget.PatientRole();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Custodian }
     * 
     */
    public ClinicalDocument.Custodian createClinicalDocumentCustodian() {
        return new ClinicalDocument.Custodian();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Custodian.AssignedCustodian }
     * 
     */
    public ClinicalDocument.Custodian.AssignedCustodian createClinicalDocumentCustodianAssignedCustodian() {
        return new ClinicalDocument.Custodian.AssignedCustodian();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization }
     * 
     */
    public ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization createClinicalDocumentCustodianAssignedCustodianRepresentedCustodianOrganization() {
        return new ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Id }
     * 
     */
    public ClinicalDocument.Id createClinicalDocumentId() {
        return new ClinicalDocument.Id();
    }

    /**
     * Create an instance of {@link ClinicalDocument.EffectiveTime }
     * 
     */
    public ClinicalDocument.EffectiveTime createClinicalDocumentEffectiveTime() {
        return new ClinicalDocument.EffectiveTime();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section.Code }
     * 
     */
    public ClinicalDocument.Component.Section.Code createClinicalDocumentComponentSectionCode() {
        return new ClinicalDocument.Component.Section.Code();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section.Entry.Observation.Code }
     * 
     */
    public ClinicalDocument.Component.Section.Entry.Observation.Code createClinicalDocumentComponentSectionEntryObservationCode() {
        return new ClinicalDocument.Component.Section.Entry.Observation.Code();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section.Entry.Observation.EffectiveTime }
     * 
     */
    public ClinicalDocument.Component.Section.Entry.Observation.EffectiveTime createClinicalDocumentComponentSectionEntryObservationEffectiveTime() {
        return new ClinicalDocument.Component.Section.Entry.Observation.EffectiveTime();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Component.Section.Entry.Observation.Value }
     * 
     */
    public ClinicalDocument.Component.Section.Entry.Observation.Value createClinicalDocumentComponentSectionEntryObservationValue() {
        return new ClinicalDocument.Component.Section.Entry.Observation.Value();
    }

    /**
     * Create an instance of {@link ClinicalDocument.RecordTarget.PatientRole.Id }
     * 
     */
    public ClinicalDocument.RecordTarget.PatientRole.Id createClinicalDocumentRecordTargetPatientRoleId() {
        return new ClinicalDocument.RecordTarget.PatientRole.Id();
    }

    /**
     * Create an instance of {@link ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization.Id }
     * 
     */
    public ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization.Id createClinicalDocumentCustodianAssignedCustodianRepresentedCustodianOrganizationId() {
        return new ClinicalDocument.Custodian.AssignedCustodian.RepresentedCustodianOrganization.Id();
    }

}