//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.14 at 03:42:33 PM CET 
//


package com.silverpeas.admin.spaces;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.silverpeas.admin.spaces package. 
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

    private final static QName _SpaceTemplate_QNAME = new QName("http://silverpeas.org/xml/ns/space", "SpaceTemplate");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.silverpeas.admin.spaces
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SpaceComponentParameter }
     * 
     */
    public SpaceComponentParameter createSpaceComponentParameter() {
        return new SpaceComponentParameter();
    }

    /**
     * Create an instance of {@link SpaceComponent }
     * 
     */
    public SpaceComponent createSpaceComponent() {
        return new SpaceComponent();
    }

    /**
     * Create an instance of {@link SpaceTemplate }
     * 
     */
    public SpaceTemplate createSpaceTemplate() {
        return new SpaceTemplate();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SpaceTemplate }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://silverpeas.org/xml/ns/space", name = "SpaceTemplate")
    public JAXBElement<SpaceTemplate> createSpaceTemplate(SpaceTemplate value) {
        return new JAXBElement<SpaceTemplate>(_SpaceTemplate_QNAME, SpaceTemplate.class, null, value);
    }

}
