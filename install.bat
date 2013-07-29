@echo install the library, source and javadoc packages into Maven repository.
mvn clean javadoc:jar source:jar install -DskipTests=true
