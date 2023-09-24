package net.coffeetariat.cafe.utils;

public class NiemClassTypes {
    public static gov.niem.release.niem.niem_core._5.PersonCitizenshipType constructPersonCitizenshipType(String s) {
        gov.niem.release.niem.niem_core._5.PersonCitizenshipType pct = new gov.niem.release.niem.niem_core._5.PersonCitizenshipType();
        gov.niem.release.niem.niem_core._5.CountryType ct = new gov.niem.release.niem.niem_core._5.CountryType();
        var jCR = net.coffeetariat.cafe.strings.NiemTexts.getJaxbElementForString(s, null);

        ct.getCountryRepresentation().add(jCR);
        pct.getPersonCitizenshipCountry().add(ct);
        return pct;
    }
}
