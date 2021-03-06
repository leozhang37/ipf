//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.9-03/31/2009 04:14 PM(snajper)-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.05.19 at 10:15:07 AM CEST 
//


package org.openehealth.ipf.commons.ihe.xds.core.stub.ebrs21.rim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * Association is the mapping of the same named interface in ebRIM.
 * It extends RegistryObject.
 * 
 * An Association specifies references to two previously submitted
 * registry entrys.
 * 
 * The sourceObject is id of the sourceObject in association
 * The targetObject is id of the targetObject in association
 * 			
 * 
 * <p>Java class for AssociationType1 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AssociationType1">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.1}RegistryObjectType">
 *       &lt;attribute name="associationType" use="required" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.1}LongName" />
 *       &lt;attribute name="sourceObject" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="targetObject" use="required" type="{http://www.w3.org/2001/XMLSchema}IDREF" />
 *       &lt;attribute name="isConfirmedBySourceOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="isConfirmedByTargetOwner" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AssociationType1")
public class AssociationType1
    extends RegistryObjectType
{

    @XmlAttribute(required = true)
    protected String associationType;
    @XmlAttribute(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object sourceObject;
    @XmlAttribute(required = true)
    @XmlIDREF
    @XmlSchemaType(name = "IDREF")
    protected Object targetObject;
    @XmlAttribute
    protected Boolean isConfirmedBySourceOwner;
    @XmlAttribute
    protected Boolean isConfirmedByTargetOwner;

    /**
     * Gets the value of the associationType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssociationType() {
        return associationType;
    }

    /**
     * Sets the value of the associationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssociationType(String value) {
        this.associationType = value;
    }

    /**
     * Gets the value of the sourceObject property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getSourceObject() {
        return sourceObject;
    }

    /**
     * Sets the value of the sourceObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setSourceObject(Object value) {
        this.sourceObject = value;
    }

    /**
     * Gets the value of the targetObject property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getTargetObject() {
        return targetObject;
    }

    /**
     * Sets the value of the targetObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setTargetObject(Object value) {
        this.targetObject = value;
    }

    /**
     * Gets the value of the isConfirmedBySourceOwner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsConfirmedBySourceOwner() {
        return isConfirmedBySourceOwner;
    }

    /**
     * Sets the value of the isConfirmedBySourceOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsConfirmedBySourceOwner(Boolean value) {
        this.isConfirmedBySourceOwner = value;
    }

    /**
     * Gets the value of the isConfirmedByTargetOwner property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsConfirmedByTargetOwner() {
        return isConfirmedByTargetOwner;
    }

    /**
     * Sets the value of the isConfirmedByTargetOwner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsConfirmedByTargetOwner(Boolean value) {
        this.isConfirmedByTargetOwner = value;
    }

}
