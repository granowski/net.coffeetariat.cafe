package net.coffeetariat.cafe.strings;

import jakarta.xml.bind.JAXBElement;

import javax.xml.namespace.QName;

public class NiemTexts {
    public static JAXBElement<String> getJaxbElementForString(String s, QName qn) {
        return new JAXBElement<String>(
                qn == null
                        ? QName.valueOf("")
                        : qn,
                String.class,
                new String(s));
    }
}
