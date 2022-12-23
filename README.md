# The Coffeetariat Cafe Service APIs

This Web API hosts many features for various functionality in the form of REST endpoints.

## Regenerating the NIEM Core classes

First install the [XmlBeans](https://xmlbeans.apache.org/) library from Apache.
Then you can run the following command and it will generate a folder with all the java sources and a jar for linking:
```shell
scomp ./doc/NIEM-Releases-niem-5.1/xsd/niem-core.xsd -src ./src/main/java/ -out jars/niem-xmltypes.jar
```