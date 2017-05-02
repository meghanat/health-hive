// @XmlSchema(namespace = "urn:example.com:foo",
//     xmlns = { 
//         @XmlNs(namespaceURI = "urn:example.com:foo", prefix = "")
//     },
//     elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)

@XmlSchema(elementFormDefault = XmlNsForm.QUALIFIED)
package cdaGeneration;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
