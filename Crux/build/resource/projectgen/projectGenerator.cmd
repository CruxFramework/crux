@java -cp "./;./lib/build/crux-dev-deps.jar;./lib/build/crux-dev.jar;" org.cruxframework.crux.tools.projectgen.DependenciesChecker %*
IF %ERRORLEVEL% EQU 0 @java -cp "./;./lib/build/crux-dev-deps.jar;./lib/build/crux-dev.jar;./lib/gadget/build/crux-gadgets.jar;./lib/build/gwt-dev.jar;./lib/build/gwt-user.jar;" org.cruxframework.crux.tools.projectgen.CruxProjectGenerator %*

