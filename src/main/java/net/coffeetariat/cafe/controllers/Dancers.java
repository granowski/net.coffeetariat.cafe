package net.coffeetariat.cafe.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.niem.release.niem.niem_core._5.PersonType;
import gov.niem.release.niem.proxy.niem_xs._5.Date;
import jakarta.xml.bind.JAXBElement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.UUID;

@RestController
public class Dancers {
    @GetMapping("dancers")
    public EntityResponse<String> getDancers() throws DatatypeConfigurationException, JsonProcessingException {
        var al = new ArrayList<gov.niem.release.niem.niem_core._5.PersonType>();

        var granowski = new PersonType();
        granowski.setId(UUID.randomUUID().toString());
        var dt = new gov.niem.release.niem.niem_core._5.DateType();
        dt.getDateRepresentation();
        gov.niem.release.niem.proxy.niem_xs._5.Date d = new Date();

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new java.util.Date());
        XMLGregorianCalendar xCal = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);

        d.setValue(xCal);

        d.getValue().setMonth(12);
        d.getValue().setDay(31);
        d.getValue().setYear(1987);

        JAXBElement<gov.niem.release.niem.proxy.niem_xs._5.Date> jDt =
                new JAXBElement<>(QName.valueOf("qwer"), Date.class, d);

        dt.getDateRepresentation().add(jDt);
        granowski.getPersonBirthDate().add(0, dt);
        al.add(granowski);

        ObjectMapper om = new ObjectMapper();
        var s = om.writeValueAsString(al);
        return EntityResponse.fromObject(s).build();
    }
}
