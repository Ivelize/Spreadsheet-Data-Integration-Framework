//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.04 at 04:59:33 PM BRT 
//


package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFSchema;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}RDFSchemaMetadata"/>
 *         &lt;element ref="{}RDFEntityType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rdfSchemaMetadata",
    "rdfEntityType"
})
@XmlRootElement(name = "RDFEntityTypes")
public class RDFEntityTypes {

    @XmlElement(name = "RDFSchemaMetadata", required = true)
    protected RDFSchemaMetadata rdfSchemaMetadata;
    @XmlElement(name = "RDFEntityType", required = true)
    protected List<RDFEntityType> rdfEntityType;

    /**
     * Gets the value of the rdfSchemaMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link RDFSchemaMetadata }
     *     
     */
    public RDFSchemaMetadata getRDFSchemaMetadata() {
        return rdfSchemaMetadata;
    }

    /**
     * Sets the value of the rdfSchemaMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link RDFSchemaMetadata }
     *     
     */
    public void setRDFSchemaMetadata(RDFSchemaMetadata value) {
        this.rdfSchemaMetadata = value;
    }

    /**
     * Gets the value of the rdfEntityType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rdfEntityType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRDFEntityType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RDFEntityType }
     * 
     * 
     */
    public List<RDFEntityType> getRDFEntityType() {
        if (rdfEntityType == null) {
            rdfEntityType = new ArrayList<RDFEntityType>();
        }
        return this.rdfEntityType;
    }

}
