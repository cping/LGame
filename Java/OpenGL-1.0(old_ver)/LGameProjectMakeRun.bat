@ECHO OFF

for %%X in (java.exe) do (set IS_JAVA_IN_PATH=%%~$PATH:X)

IF defined IS_JAVA_IN_PATH (
	start "LGameMake" java -cp LGameProjectMake.jar org.gui.Main
) ELSE (
	IF EXIST "%PROGRAMFILES%\Java\jre7" (
		start "NXT NRS" "%PROGRAMFILES%\Java\jre7\bin\java.exe" -cp LGameProjectMake.jar org.gui.Main
	) ELSE IF EXIST "%PROGRAMFILES%\Java\jre8" (
		start "NXT NRS" "%PROGRAMFILES%\Java\jre8\bin\java.exe" -cp LGameProjectMake.jar org.gui.Main
	) ELSE (
		IF EXIST "%PROGRAMFILES(X86)%\Java\jre7" (
			start "NXT NRS" "%PROGRAMFILES(X86)%\Java\jre7\bin\java.exe" -cp LGameProjectMake.jar org.gui.Main
		) ELSE (
			ECHO Java software not found on your system. Please go to http://java.com/en/ to download a copy of Java.
			PAUSE
		)
	)
)
