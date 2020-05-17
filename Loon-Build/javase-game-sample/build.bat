@ECHO OFF
SET mypath=%~dp0
IF EXIST java (
	start "Loon Build" java -Xms1024m -Xms2048m -cp "%mypath%loon-build.jar" loon.build.Main javase build.txt
) ELSE (
	IF EXIST "%PROGRAMFILES%\Java\jre" (
		start "Loon Build" "%PROGRAMFILES%\Java\jre\bin\java.exe" -Xms1024m -Xms2048m -cp "%mypath%loon-build.jar" loon.build.Main javase build.txt
	) ELSE (
		IF EXIST "%PROGRAMFILES(X86)%\Java\jre" (
			start "Loon Build" "%PROGRAMFILES(X86)%\Java\jre\bin\java.exe" -Xms1024m -Xms2048m -cp "%mypath%loon-build.jar" loon.build.Main javase build.txt
		) ELSE (
			java -Xms1024m -Xms2048m -cp "%mypath%loon-build.jar" loon.build.Main javase build.txt
		)
	)
)

