package net.coffeetariat.cafe;

import net.coffeetariat.cafe.jaxb.niem.DateType;
import net.coffeetariat.cafe.jaxb.niem.PersonNameTextType;
import net.coffeetariat.cafe.jaxb.niem.PersonNameType;
import net.coffeetariat.cafe.jaxb.niem.PersonType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.dsig.XMLObject;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        net.coffeetariat.cafe.jaxb.niem.PersonType pt = new PersonType();

        var dt = new DateType();
        var dr = dt.getDateRepresentation();
//        dr.add
        pt.getPersonBirthDate().add(dt);

        var pnt = new PersonNameType();
        var pgn = pnt.getPersonGivenName();
        var pgnt = new PersonNameTextType();
        pgnt.setValue("Derrick");
        pgn.add(pgnt);
        var bleh = pt.getPersonName();
        bleh.add(pnt);

        java.lang.System.out.println(pt.toString());
//        net.coffeetariat.cafe
//        gov.niem.release.niem.niemCore.x50.EmployeeDocument ed = new EmployeeDocumentImpl(EmployeeDocument.type);
//        PersonType g = ed.addNewEmployee();
//        g.insertNewPersonBirthDate(0).insertNewDateRepresentation(0);
//        g.addNewPersonBirthDate().addNewDateRepresentation();
//
//
//        ContactEmailIDDocument doc = new ContactEmailIDDocumentImpl(ContactEmailIDDocument.type);
//        var s = new StringImpl(gov.niem.release.niem.proxy.niemXs.x50.String.type);
//        doc.setContactEmailID(s);
//
//        java.lang.System.out.println(doc.toString());

//        var edf = new EmployeeDocument.Factory.parse();
//        ed.addNewEmployee();
//        System.out.println(ed.toString());
//        PersonType granowski;
//        DateType birthDate;
//        birthDate.setDateRepresentationArray(dateXmlObject);
//        granowski.setPersonBirthDateArray(new DateType[] { birthDate });

//        SpringApplication.run(Application.class, args);
    }

}
