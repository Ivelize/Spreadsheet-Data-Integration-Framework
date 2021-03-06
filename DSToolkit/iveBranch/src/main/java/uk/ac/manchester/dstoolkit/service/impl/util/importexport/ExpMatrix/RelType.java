//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.04.17 at 02:31:13 PM BST 
//


package uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for relType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="relType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="subsumes"/>
 *     &lt;enumeration value="is-subsumed"/>
 *     &lt;enumeration value="equivalent"/>
 *     &lt;enumeration value="incompatible"/>
 *     &lt;enumeration value="idk"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "relType")
@XmlEnum
public enum RelType {

    @XmlEnumValue("subsumes")
    SUBSUMES("subsumes"),
    @XmlEnumValue("is-subsumed")
    IS_SUBSUMED("is-subsumed"),
    @XmlEnumValue("equivalent")
    EQUIVALENT("equivalent"),
    @XmlEnumValue("incompatible")
    INCOMPATIBLE("incompatible"),
    @XmlEnumValue("idk")
    IDK("idk");
    private final String value;

    RelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RelType fromValue(String v) {
        for (RelType c: RelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
