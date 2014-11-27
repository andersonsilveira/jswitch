@echo off
set SAVEDIR=%CD%
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set BASEDIR=%~dp0\..
set REPO=%BASEDIR%\dist
set CLASSPATH="%REPO%"\commons-io\commons-io\2.4\commons-io-2.4.jar;"%REPO%"\org\codehaus\plexus\plexus-utils\3.0.15\plexus-utils-3.0.15.jar;"%REPO%"\stax\stax-api\1.0.1\stax-api-1.0.1.jar;"%REPO%"\net\java\dev\stax-utils\stax-utils\20060502\stax-utils-20060502.jar;"%REPO%"\stax\stax\1.1.1-dev\stax-1.1.1-dev.jar;"%REPO%"\junit\junit\3.8.1\junit-3.8.1.jar;"%REPO%"\br\com\org\jswitch\0.0.1-SNAPSHOT\jswitch-0.0.1-SNAPSHOT.jar
start javaw -classpath %CLASSPATH% br.com.org.jswitch.Main