package net.coffeetariat.cafe.controllers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.niem.release.niem.niem_core._5.PersonType;
import gov.niem.release.niem.proxy.niem_xs._5.Date;
import gov.niem.release.niem.proxy.niem_xs._5.DateTime;
import jakarta.xml.bind.JAXBElement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.UUID;

@RestController
public class Dancers {
    @GetMapping("dancers")
    public EntityResponse<String> getDancers() throws DatatypeConfigurationException, IOException {
        var al = new ArrayList<gov.niem.release.niem.niem_core._5.PersonType>();

        var granowski = new PersonType();
        granowski.setId(UUID.randomUUID().toString());
        var dt = new gov.niem.release.niem.niem_core._5.DateType();
        var dtrep = dt.getDateRepresentation();


        gov.niem.release.niem.proxy.niem_xs._5.DateTime dt1 = new DateTime();

        var yourInstant = java.time.Instant.parse("1987-12-31T01:00:00Z");
        String dateTimeString = yourInstant.toString();
        XMLGregorianCalendar date2
                = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);

        dt1.setValue(date2);

        var jDt = new JAXBElement<gov.niem.release.niem.proxy.niem_xs._5.DateTime>(QName.valueOf("qwer"), gov.niem.release.niem.proxy.niem_xs._5.DateTime.class, dt1);

        jDt.setValue(dt1);
        dtrep.add(jDt);
//        dt.getDateRepresentation();
//        gov.niem.release.niem.proxy.niem_xs._5.Date d = new Date();
//
//        GregorianCalendar cal = new GregorianCalendar();
//        cal.setTime(new java.util.Date());
//        XMLGregorianCalendar xCal = DatatypeFactory.newInstance()
//                .newXMLGregorianCalendar(cal);
//
//        d.setValue(xCal);
//
//        d.getValue().setMonth(12);
//        d.getValue().setDay(31);
//        d.getValue().setYear(1987);
//
//        JAXBElement<gov.niem.release.niem.proxy.niem_xs._5.Date> jDt =
//                new JAXBElement<>(QName.valueOf("qwer"), Date.class, d);
//
//        dt.getDateRepresentation().add(jDt);
        granowski.getPersonBirthDate().add(0, dt);
        al.add(granowski);
//
//        ObjectMapper om = new ObjectMapper();
//        var s = om.writeValueAsString(al);

        XmlMapper xm = new XmlMapper();

        Writer w0 = new StringWriter();
        xm.writeValue(w0, al);

        System.out.println(w0.toString());
//        return w0.toString();
        var resp = EntityResponse.fromObject(w0.toString()).contentType(MediaType.APPLICATION_XML).build();
        return resp;
    }
}
