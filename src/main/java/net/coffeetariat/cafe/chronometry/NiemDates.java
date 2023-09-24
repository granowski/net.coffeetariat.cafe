package net.coffeetariat.cafe.chronometry;

import jakarta.xml.bind.JAXBElement;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import java.time.Instant;

import gov.niem.release.niem.proxy.niem_xs._5.DateTime;

public class NiemDates {
    public static gov.niem.release.niem.niem_core._5.DateType getNiemDateTimeFromInstant(Instant i, QName qn) throws DatatypeConfigurationException {
        var dt = new gov.niem.release.niem.niem_core._5.DateType();

        DateTime dt1 = new DateTime();

        String dateTimeString = i.toString();

        XMLGregorianCalendar dt2
                = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);

        dt1.setValue(dt2);

        var qn1 = QName.valueOf("");
        if (qn != null) {
            qn1 = qn;
        }

        var jDt = new JAXBElement<>(qn1, DateTime.class, dt1);

        jDt.setValue(dt1);
        dt.getDateRepresentation().add(jDt);

        return dt;
    }
}
