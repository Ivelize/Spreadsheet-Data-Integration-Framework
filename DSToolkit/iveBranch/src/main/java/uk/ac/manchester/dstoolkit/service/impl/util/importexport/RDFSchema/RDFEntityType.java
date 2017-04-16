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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="RDFEntityTypeName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}SimpleAttrOfRDFEntityType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}MultiAttrOfRDFEntityType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}CompositeAttrOfRDFEntityType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}RDFBinaryRelationshipType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}RDFWeakEntityType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="rdftype" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "rdfEntityTypeName",
    "simpleAttrOfRDFEntityType",
    "multiAttrOfRDFEntityType",
    "compositeAttrOfRDFEntityType",
    "rdfBinaryRelationshipType",
    "rdfWeakEntityType"
})
@XmlRootElement(name = "RDFEntityType")
public class RDFEntityType {

    @XmlElement(name = "RDFEntityTypeName", required = true)
    protected String rdfEntityTypeName;
    @XmlElement(name = "SimpleAttrOfRDFEntityType")
    protected List<SimpleAttrOfRDFEntityType> simpleAttrOfRDFEntityType;
    @XmlElement(name = "MultiAttrOfRDFEntityType")
    protected List<MultiAttrOfRDFEntityType> multiAttrOfRDFEntityType;
    @XmlElement(name = "CompositeAttrOfRDFEntityType")
    protected List<CompositeAttrOfRDFEntityType> compositeAttrOfRDFEntityType;
    @XmlElement(name = "RDFBinaryRelationshipType")
    protected List<RDFBinaryRelationshipType> rdfBinaryRelationshipType;
    @XmlElement(name = "RDFWeakEntityType")
    protected List<RDFWeakEntityType> rdfWeakEntityType;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String rdftype;

    /**
     * Gets the value of the rdfEntityTypeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRDFEntityTypeName() {
        return rdfEntityTypeName;
    }

    /**
     * Sets the value of the rdfEntityTypeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRDFEntityTypeName(String value) {
        this.rdfEntityTypeName = value;
    }

    /**
     * Gets the value of the simpleAttrOfRDFEntityType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleAttrOfRDFEntityType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleAttrOfRDFEntityType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleAttrOfRDFEntityType }
     * 
     * 
     */
    public List<SimpleAttrOfRDFEntityType> getSimpleAttrOfRDFEntityType() {
        if (simpleAttrOfRDFEntityType == null) {
            simpleAttrOfRDFEntityType = new ArrayList<SimpleAttrOfRDFEntityType>();
        }
        return this.simpleAttrOfRDFEntityType;
    }

    /**
     * Gets the value of the multiAttrOfRDFEntityType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the multiAttrOfRDFEntityType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMultiAttrOfRDFEntityType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MultiAttrOfRDFEntityType }
     * 
     * 
     */
    public List<MultiAttrOfRDFEntityType> getMultiAttrOfRDFEntityType() {
        if (multiAttrOfRDFEntityType == null) {
            multiAttrOfRDFEntityType = new ArrayList<MultiAttrOfRDFEntityType>();
        }
        return this.multiAttrOfRDFEntityType;
    }

    /**
     * Gets the value of the compositeAttrOfRDFEntityType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compositeAttrOfRDFEntityType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompositeAttrOfRDFEntityType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CompositeAttrOfRDFEntityType }
     * 
     * 
     */
    public List<CompositeAttrOfRDFEntityType> getCompositeAttrOfRDFEntityType() {
        if (compositeAttrOfRDFEntityType == null) {
            compositeAttrOfRDFEntityType = new ArrayList<CompositeAttrOfRDFEntityType>();
        }
        return this.compositeAttrOfRDFEntityType;
    }

    /**
     * Gets the value of the rdfBinaryRelationshipType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rdfBinaryRelationshipType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRDFBinaryRelationshipType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RDFBinaryRelationshipType }
     * 
     * 
     */
    public List<RDFBinaryRelationshipType> getRDFBinaryRelationshipType() {
        if (rdfBinaryRelationshipType == null) {
            rdfBinaryRelationshipType = new ArrayList<RDFBinaryRelationshipType>();
        }
        return this.rdfBinaryRelationshipType;
    }

    /**
     * Gets the value of the rdfWeakEntityType property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rdfWeakEntityType property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRDFWeakEntityType().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RDFWeakEntityType }
     * 
     * 
     */
    public List<RDFWeakEntityType> getRDFWeakEntityType() {
        if (rdfWeakEntityType == null) {
            rdfWeakEntityType = new ArrayList<RDFWeakEntityType>();
        }
        return this.rdfWeakEntityType;
    }

    /**
     * Gets the value of the rdftype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRdftype() {
        return rdftype;
    }

    /**
     * Sets the value of the rdftype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRdftype(String value) {
        this.rdftype = value;
    }

}
