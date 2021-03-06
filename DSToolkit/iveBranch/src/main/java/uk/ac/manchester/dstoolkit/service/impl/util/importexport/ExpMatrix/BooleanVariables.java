//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.04 at 04:59:33 PM BRT 
//


package uk.ac.manchester.dstoolkit.service.impl.util.importexport.ExpMatrix;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for booleanVariables.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="booleanVariables">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="CSURI"/>
 *     &lt;enumeration value="CSN"/>
 *     &lt;enumeration value="CSE"/>
 *     &lt;enumeration value="CCS"/>
 *     &lt;enumeration value="CSP"/>
 *     &lt;enumeration value="CSR"/>
 *     &lt;enumeration value="CBSR"/>
 *     &lt;enumeration value="CHL"/>
 *     &lt;enumeration value="CEC"/>
 *     &lt;enumeration value="CSA"/>
 *     &lt;enumeration value="CEM"/>
 *     &lt;enumeration value="PSA"/>
 *     &lt;enumeration value="PSURI"/>
 *     &lt;enumeration value="PSN"/>
 *     &lt;enumeration value="PSE"/>
 *     &lt;enumeration value="PCS"/>
 *     &lt;enumeration value="PSP"/>
 *     &lt;enumeration value="PSR"/>
 *     &lt;enumeration value="DCSR"/>
 *     &lt;enumeration value="DCCPR"/>
 *     &lt;enumeration value="PHL"/>
 *     &lt;enumeration value="PSD"/>
 *     &lt;enumeration value="PSRA"/>
 *     &lt;enumeration value="PEP"/>
 *     &lt;enumeration value="idk"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "booleanVariables")
@XmlEnum
public enum BooleanVariables {

    CSURI("CSURI"),
    CSN("CSN"),
    CSE("CSE"),
    CCS("CCS"),
    CSP("CSP"),
    CSR("CSR"),
    CBSR("CBSR"),
    CHL("CHL"),
    CEC("CEC"),
    CSA("CSA"),
    CEM("CEM"),
    PSA("PSA"),
    PSURI("PSURI"),
    PSN("PSN"),
    PSE("PSE"),
    PCS("PCS"),
    PSP("PSP"),
    PSR("PSR"),
    DCSR("DCSR"),
    DCCPR("DCCPR"),
    PHL("PHL"),
    PSD("PSD"),
    PSRA("PSRA"),
    PEP("PEP"),
    @XmlEnumValue("idk")
    IDK("idk");
    private final String value;

    BooleanVariables(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BooleanVariables fromValue(String v) {
        for (BooleanVariables c: BooleanVariables.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
