package net.coffeetariat.cafe.seeds;

import gov.niem.release.niem.niem_core._5.PersonType;
import net.coffeetariat.cafe.chronometry.NiemDates;

import javax.xml.datatype.DatatypeConfigurationException;
import java.time.Instant;
import java.util.UUID;

public class Dancers {
    public static PersonType createAdminDancerNiemPersonType(String fn, String ln, String birthDate, String country) throws DatatypeConfigurationException {

        var personType = new gov.niem.release.niem.niem_core._5.PersonType();
        personType.setId(UUID.randomUUID().toString());

        Instant yourInstant = java.time.Instant.parse(birthDate);
        gov.niem.release.niem.niem_core._5.DateType dt = NiemDates.getNiemDateTimeFromInstant(yourInstant, null);
        personType.getPersonBirthDate().add(0, dt);

        var pct = net.coffeetariat.cafe.utils.NiemClassTypes.constructPersonCitizenshipType("USA");
        personType.getPersonCitizenship().add(pct);

        return personType;

    }
}