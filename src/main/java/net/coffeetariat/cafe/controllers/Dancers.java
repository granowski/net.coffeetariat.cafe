package net.coffeetariat.cafe.controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import gov.niem.release.niem.niem_core._5.*;
import gov.niem.release.niem.niem_core._5.CountryType;
import gov.niem.release.niem.niem_core._5.DateType;
import gov.niem.release.niem.niem_core._5.PersonCitizenshipType;
import gov.niem.release.niem.niem_core._5.PersonType;
import jakarta.xml.bind.JAXBElement;
import net.coffeetariat.cafe.chronometry.NiemDates;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.EntityResponse;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@RestController
public class Dancers {
    private gov.niem.release.niem.niem_core._5.ObjectFactory niemOF = new gov.niem.release.niem.niem_core._5.ObjectFactory();

    @GetMapping("dancers")
    public EntityResponse<String> getDancers() throws DatatypeConfigurationException, IOException {
        var al = new ArrayList<gov.niem.release.niem.niem_core._5.PersonType>();

        XmlMapper xm = new XmlMapper();

        Writer w0 = new StringWriter();
        xm.writeValue(w0, al);

        System.out.println(w0);
        var resp = EntityResponse.fromObject(w0.toString()).contentType(MediaType.APPLICATION_XML).build();
        return resp;
    }
}
