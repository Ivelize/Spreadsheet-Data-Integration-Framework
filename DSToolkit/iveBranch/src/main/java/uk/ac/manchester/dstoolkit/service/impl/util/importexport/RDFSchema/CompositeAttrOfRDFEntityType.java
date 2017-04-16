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
 *         &lt;element name="CompositeAttrName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element ref="{}SimpleAttrOfRDFEntityType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "compositeAttrName",
    "simpleAttrOfRDFEntityType"
})
@XmlRootElement(name = "CompositeAttrOfRDFEntityType")
public class CompositeAttrOfRDFEntityType {

    @XmlElement(name = "CompositeAttrName", required = true)
    protected String compositeAttrName;
    @XmlElement(name = "SimpleAttrOfRDFEntityType")
    protected List<SimpleAttrOfRDFEntityType> simpleAttrOfRDFEntityType;
    @XmlAttribute
    protected String namespace;

    /**
     * Gets the value of the compositeAttrName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompositeAttrName() {
        return compositeAttrName;
    }

    /**
     * Sets the value of the compositeAttrName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompositeAttrName(String value) {
        this.compositeAttrName = value;
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
     * Gets the value of the namespace property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Sets the value of the namespace property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

}