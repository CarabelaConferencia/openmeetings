set RED5DIR=%~dp0

set CLASSPATH=%RED5DIR%\*;%RED5DIR%\conf;%RED5DIR%\lib\*;%RED5DIR%\webapps\openmeetings\WEB-INF\lib\*;%RED5DIR%\webapps\openmeetings\WEB-INF;%RED5DIR%\webapps\openmeetings\WEB-INF\classes

java -cp "%CLASSPATH%" -Dred5.home=%RED5DIR% org.openmeetings.app.Admin %*

