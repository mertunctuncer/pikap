package me.koply.pikap.api.cli.command;

import com.github.tomaslanger.chalk.Chalk;
import me.koply.pikap.Constants;
import me.koply.pikap.api.cli.Console;
import me.koply.pikap.sound.SoundManager;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public static final Map<String, CommandClassData> COMMAND_CLASSES = new HashMap<>();
    private final CommandInitializer initializer = new CommandInitializer(COMMAND_CLASSES);

    public CommandHandler(String...packages) {
        initializer.registerPackage(packages);
    }

    public void registerInstance(CLICommand command) {
        initializer.registerInstance(command);
    }

    public static boolean isRunning = true;

    /**
     * Blocks the caller thread for handle commands from cli.
     */
    public void startNewHandler() {
        while (isRunning) {
            System.out.print(Chalk.on("> ").yellow());
            String entry = Console.SC.nextLine().trim();
            String[] args = entry.split(" ");

            if (entry.equalsIgnoreCase("exit") || entry.equalsIgnoreCase("quit")) {
                SoundManager.shutdown();
                // TODO close the KeyboardListener
                break;
            }

            if (!COMMAND_CLASSES.containsKey(args[0])) {
                Console.println(Constants.PREFIX + "There is no command for this entry.");
                continue;
            }

            CommandClassData ccd = COMMAND_CLASSES.get(args[0]);
            CommandEvent event = new CommandEvent(args, entry);
            callCommandMethod(event, ccd);
        }
    }

    private void callCommandMethod(CommandEvent event, CommandClassData ccd) {
        try {
            CommandClassData.MethodAndAnnotation maa = ccd.methods.get(event.getArgs()[0]);
            Object result = maa.method.invoke(ccd.instance, event);
            if (maa.returnType == ReturnType.BOOLEAN && (boolean) result) {
                Console.prefixln(maa.annotation.usageMsg());
                if (maa.annotation.sendWithDesc()) Console.println(maa.annotation.desc());
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


}