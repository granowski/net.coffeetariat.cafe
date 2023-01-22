# The Coffeetariat Cafe Service APIs

This Web API hosts many features for various functionality in the form of REST endpoints.

## Regenerating the NIEM Core classes

The NIEM core classes should generate automatically through the maven build. The jaxb2-maven-plugin is configured to
read the NIEM core and pulls in all the imported schemas and etc and generates everything.
Surprisingly, this was all able to be done without binding files.