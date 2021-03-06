//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.04 at 04:59:33 PM BRT 
//


package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel;

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
 *         &lt;element name="bnodeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}RDFProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}RDFPredicate" maxOccurs="unbounded" minOccurs="0"/>
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
    "bnodeType",
    "rdfProperty",
    "rdfPredicate"
})
@XmlRootElement(name = "RDFBlankNode")
public class RDFBlankNode {

    @XmlElement(required = true)
    protected String bnodeType;
    @XmlElement(name = "RDFProperty")
    protected List<RDFProperty> rdfProperty;
    @XmlElement(name = "RDFPredicate")
    protected List<RDFPredicate> rdfPredicate;

    /**
     * Gets the value of the bnodeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBnodeType() {
        return bnodeType;
    }

    /**
     * Sets the value of the bnodeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBnodeType(String value) {
        this.bnodeType = value;
    }

    /**
     * Gets the value of the rdfProperty property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rdfProperty property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRDFProperty().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RDFProperty }
     * 
     * 
     */
    public List<RDFProperty> getRDFProperty() {
        if (rdfProperty == null) {
            rdfProperty = new ArrayList<RDFProperty>();
        }
        return this.rdfProperty;
    }

    /**
     * Gets the value of the rdfPredicate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rdfPredicate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRDFPredicate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RDFPredicate }
     * 
     * 
     */
    public List<RDFPredicate> getRDFPredicate() {
        if (rdfPredicate == null) {
            rdfPredicate = new ArrayList<RDFPredicate>();
        }
        return this.rdfPredicate;
    }

}
