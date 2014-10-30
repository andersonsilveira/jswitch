package br.com.org.jswitch.cfg.win;
/**
 * 
 * Command constant used by @link {@link WindowsSystem}
 * @author Anderson
 *
 */
final class Command {

    static final String INSTALL_JSWITCH_EXE = "install-jswitch.exe";
    static final String FIND_JAVA_PATH = "cd %programFiles% \n" + "dir javac.exe /B /S";
    static final String RESTORE_JAVA_HOME = "setx JAVA_HOME \"{1}\" /m";
    static final String CREATE_FILE_CONF = "cd /d %ProgramFiles%\n" + "cd JSwitch\n" + "if exist config.cfg (\n"
    + "type nul> text.txt)\n" + "echo {0}>>config.cfg\n";
    static final String BOOTSTP = "reg add HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v \"JSwitchSysTray\" /d "
    + Command.QUOTE + Command.SLASHDOT + Command.QUOTE + "%ProgramFiles%\\JSwitch\\sysTray.exe" + Command.SLASHDOT + Command.QUOTE + Command.QUOTE + " /f\n";
    static final String REGISTRY = "cd /d %ProgramFiles%\n" + "if not exist JSwitch (\n" + "mkdir JSwitch)\n"
        + "cd JSwitch\n" + "if not exist {0}.bat (\n" + "echo "
        + RESTORE_JAVA_HOME
        + ">>{0}.bat\n"
        + "echo echo off>>{0}.bat\n"
        + "echo echo Setting JAVA_HOME>>{0}.bat\n"
        + "echo set JAVA_HOME={1}>>{0}.bat\n"
        + "echo echo setting PATH>>{0}.bat\n"
        + "echo set PATH=%%JAVA_HOME%%\\bin;%%PATH%%>>{0}.bat\n"
        + "echo echo Display java version>>{0}.bat\n"
        + "echo java -version>>{0}.bat\n"
        + "echo set currentDir=%%CD%%>>{0}.bat\n"
        + "echo cd %%programFiles%%\\JSwitch\\ ^&^& type nul^>.selected>>{0}.bat\n"
        + "echo cd %%programFiles%%\\JSwitch\\ ^&^& echo selectedJDK^={0}^>^>.selected^&^& cd %%currentDir%%>>{0}.bat)\n"
        + "reg add HKEY_CLASSES_ROOT\\Directory\\Background\\shell\\{0}\\command  /d \"cmd /k "+ Command.SLASHDOT + Command.QUOTE + "%ProgramFiles%\\JSwitch\\{0}" + Command.SLASHDOT + Command.QUOTE + Command.QUOTE + " /f\n"
        + "reg add HKEY_CLASSES_ROOT\\Folder\\shell\\{0}\\command /d \"cmd /k "+ Command.SLASHDOT + Command.QUOTE + Command.SLASHDOT+ Command.QUOTE + "%ProgramFiles%\\JSwitch\\{0}"+ Command.SLASHDOT + Command.QUOTE + " "+ Command.SLASHDOT+ Command.QUOTE + "%%1" + Command.SLASHDOT + Command.QUOTE + Command.SLASHDOT + Command.QUOTE + Command.QUOTE + " /f\n";
    static final String SLASHDOT = "\\";
    static final String QUOTE = "\"";

}
