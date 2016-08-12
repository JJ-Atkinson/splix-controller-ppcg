package KoTHComm.loader;

import KoTHComm.game.AbstractPlayer;
import KoTHComm.game.PlayerType;
import KoTHComm.messaging.PipeCommunicator;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileReader {
    public final static String JAVA_SUBDIRECTORY = "java";
    public final static String ALTERNATE_SUBDIRECTORY = "other";

    private final File javaDirectory, alternateDirectory;
    private final Compiler compiler;
    public FileReader(File submissionsDirectory){
        this.javaDirectory = new File(submissionsDirectory, JAVA_SUBDIRECTORY);
        this.alternateDirectory = new File(submissionsDirectory, ALTERNATE_SUBDIRECTORY);
        this.compiler = new Compiler();
    }

    public <T extends AbstractPlayer<T>> List<PlayerType<T>> registerAllSubmissions(Class<T> playerType, Function<PipeCommunicator, T> pipeBot){
        List<PlayerType<T>> submissions = new ArrayList<>();
        submissions.addAll(registerJavaFiles(playerType));
        submissions.addAll(registerOtherLanguages(pipeBot));
        return submissions;
    }

    public <T extends AbstractPlayer<T>> List<PlayerType<T>> registerJavaFiles(Class<T> playerType){
        ArrayList<PlayerType<T>> players = new ArrayList<>();
        File[] files = javaDirectory.listFiles();
        if (files == null){
            throw new RuntimeException("No folder at:"+javaDirectory.toString());
        }
        for (File file: files){
            Class<?> clazz = compiler.compile(file);
            players.add(classToPlayerType(clazz.asSubclass(playerType)));
        }
        return players;
    }

    public <T extends AbstractPlayer<T>> List<PlayerType<T>> registerOtherLanguages(Function<PipeCommunicator, T> pipeBot){
        ArrayList<PlayerType<T>> players = new ArrayList<>();
        File[] folders = alternateDirectory.listFiles();
        if (folders == null){
            throw new RuntimeException("No folder at:"+alternateDirectory.toString());
        }
        for (File folder: folders){
            players.add(new PlayerType<>(folder.getName(), () -> pipeBot.apply(new PipeCommunicator(folder))));
        }
        return players;
    }

    private <T extends AbstractPlayer<T>> PlayerType<T> classToPlayerType(Class<? extends T> clazz){
        try {
            Constructor<? extends T> constructor = clazz.getConstructor();
            return new PlayerType<>(clazz.getSimpleName(), () -> safeCallConstructor(constructor));
        } catch(NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }

    private <T extends AbstractPlayer<T>> T safeCallConstructor(Constructor<? extends T> constructor){
        try {
            return constructor.newInstance();
        } catch (IllegalAccessException|InvocationTargetException|InstantiationException e) {
            throw new RuntimeException(e);
        }
    }


}
