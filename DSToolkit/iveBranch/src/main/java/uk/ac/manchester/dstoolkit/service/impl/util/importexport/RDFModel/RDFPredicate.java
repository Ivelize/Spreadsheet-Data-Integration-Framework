//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.04 at 04:59:33 PM BRT 
//


package uk.ac.manchester.dstoolkit.service.impl.util.importexport.RDFModel;

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
 *         &lt;element name="predicateName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="predicateNS" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="referencedObject" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "predicateName",
    "predicateNS",
    "referencedObject"
})
@XmlRootElement(name = "RDFPredicate")
public class RDFPredicate {

    @XmlElement(required = true)
    protected String predicateName;
    @XmlElement(required = true)
    protected String predicateNS;
    @XmlElement(required = true)
    protected String referencedObject;

    /**
     * Gets the value of the predicateName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPredicateName() {
        return predicateName;
    }

    /**
     * Sets the value of the predicateName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPredicateName(String value) {
        this.predicateName = value;
    }

    /**
     * Gets the value of the predicateNS property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPredicateNS() {
        return predicateNS;
    }

    /**
     * Sets the value of the predicateNS property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPredicateNS(String value) {
        this.predicateNS = value;
    }

    /**
     * Gets the value of the referencedObject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencedObject() {
        return referencedObject;
    }

    /**
     * Sets the value of the referencedObject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencedObject(String value) {
        this.referencedObject = value;
    }

}
