package net.coffeetariat.cafe;

//import net.coffeetariat.cafe.jaxb.niem.DateType;
//import net.coffeetariat.cafe.jaxb.niem.PersonNameTextType;
//import net.coffeetariat.cafe.jaxb.niem.PersonNameType;
//import net.coffeetariat.cafe.jaxb.niem.PersonType;
//import org.springframework.boot.SpringApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import gov.niem.release.niem.niem_core._5.PersonNameType;
import gov.niem.release.niem.niem_core._5.PersonType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.xml.crypto.dsig.XMLObject;
import java.util.List;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper(new YAMLFactory());
        XmlMapper xmlOm = new XmlMapper();

        gov.niem.release.niem.niem_core._5.PersonType personType = new PersonType();
        List<PersonNameType> names = personType.getPersonName();
        var nm1 = new PersonNameType();
        nm1.setPersonNameCommentText("Rawr a name goes here!");
        names.add(nm1);

        System.out.println(om.writeValueAsString(personType));
        System.out.println(xmlOm.writeValueAsString(personType));
    }
}
