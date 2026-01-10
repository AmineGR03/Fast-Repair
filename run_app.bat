@echo off
echo ========================================
echo Fast-Repair Application Launcher
echo ========================================
echo.

echo Setting classpath...
set CLASSPATH=target\classes

REM Add Hibernate and related JARs
set CLASSPATH=%CLASSPATH%;C:\Users\USER\.m2\repository\org\hibernate\hibernate-core\5.6.15.Final\hibernate-core-5.6.15.Final.jar
set CLASSPATH=%CLASSPATH%;C:\Users\USER\.m2\repository\javax\persistence\javax.persistence-api\2.2\javax.persistence-api-2.2.jar
set CLASSPATH=%CLASSPATH%;C:\Users\USER\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar
set CLASSPATH=%CLASSPATH%;C:\Users\USER\.m2\repository\org\slf4j\slf4j-simple\1.7.36\slf4j-simple-1.7.36.jar
set CLASSPATH=%CLASSPATH%;C:\Users\USER\.m2\repository\org\slf4j\slf4j-api\1.7.36\slf4j-api-1.7.36.jar
set CLASSPATH=%CLASSPATH%;C:\Users\USER\.m2\repository\org\hibernate\javax\persistence\hibernate-jpa-2.0-api\1.0.1.Final\hibernate-jpa-2.0-api-1.0.1.Final.jar

echo.
echo Starting Fast-Repair Application...
echo.

java -cp "%CLASSPATH%" presentation.MainWindow

echo.
pause
