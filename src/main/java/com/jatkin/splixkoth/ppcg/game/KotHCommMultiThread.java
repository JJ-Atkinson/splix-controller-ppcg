package com.jatkin.splixkoth.ppcg.game;

import com.nmerrill.kothcomm.communication.Arguments;
import com.nmerrill.kothcomm.communication.Downloader;
import com.nmerrill.kothcomm.communication.LanguageLoader;
import com.nmerrill.kothcomm.communication.languages.Language;
import com.nmerrill.kothcomm.communication.languages.local.LocalJavaLoader;
import com.nmerrill.kothcomm.game.TournamentRunner;
import com.nmerrill.kothcomm.game.games.AbstractGame;
import com.nmerrill.kothcomm.game.players.AbstractPlayer;
import com.nmerrill.kothcomm.game.players.Submission;
import com.nmerrill.kothcomm.game.scoring.Aggregator;
import com.nmerrill.kothcomm.game.scoring.ItemAggregator;
import com.nmerrill.kothcomm.game.scoring.Scoreboard;
import com.nmerrill.kothcomm.game.tournaments.Sampling;
import com.nmerrill.kothcomm.game.tournaments.Tournament;
import com.nmerrill.kothcomm.ui.text.TextUI;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;


/** Almost entirely based on com.nmerrill.kothcomm.game.KotHComm.
 *  The only major change is to add multi-thread support.
 * @param <T>
 * @param <U>
 */
public class KotHCommMultiThread<T extends AbstractPlayer<T>, U extends AbstractGame<T>> {
    private final Supplier<U> gameSupplier;
    private final MutableList<Language<T>> languages;
    private final LocalJavaLoader<T> localLoader;
    private Aggregator<Scoreboard<Submission<T>>> aggregator;
    private BiFunction<MutableList<Submission<T>>, Random, Tournament<Submission<T>>> tournamentSupplier;
    private Arguments arguments;
    private TextUI printer;
    private int gameSize;
    private int threadCount;
    private boolean shouldDownload;

    public KotHCommMultiThread(Supplier<U> gameSupplier){
        this.gameSupplier = gameSupplier;
        this.languages = Lists.mutable.empty();
        localLoader = new LocalJavaLoader<>();
        this.languages.add(localLoader);
        this.tournamentSupplier = Sampling::new;
        this.gameSize = 2;
        this.arguments = new Arguments();
        this.shouldDownload = true;
        this.printer = new TextUI();
        this.aggregator = new ItemAggregator<>();
        this.threadCount = 4;
    }

    public void addLanguage(Language<T> language){
        this.languages.add(language);
    }

    public void addSubmission(String name, Supplier<T> playerConstructor){
        this.localLoader.register(name, playerConstructor);
    }

    public void setGameSize(int gameSize) {
        this.gameSize = gameSize;
    }

    public void setPrinter(TextUI printer) {
        this.printer = printer;
    }

    public void setAggregator(Aggregator<Scoreboard<Submission<T>>> aggregator) {
        this.aggregator = aggregator;
    }

    public void shouldDownload(boolean shouldDownload) {
        this.shouldDownload = shouldDownload;
    }

    public void setArgumentParser(Arguments arguments) {
        this.arguments = arguments;
    }

    public void setTournament(BiFunction<MutableList<Submission<T>>, Random, Tournament<Submission<T>>> tournamentSupplier) {
        this.tournamentSupplier = tournamentSupplier;
    }

    public void setTournament(Function<MutableList<Submission<T>>, Tournament<Submission<T>>> tournamentSupplier) {
        this.tournamentSupplier = (i, j) -> tournamentSupplier.apply(i);
    }

    public void setTournament(Supplier<Tournament<Submission<T>>> tournamentSupplier){
        this.tournamentSupplier = (i, j) -> tournamentSupplier.get();
    }
    
    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public void run(String[] args){
        Arguments.parse(args);
        LanguageLoader<T> loader = new LanguageLoader<>(arguments);
        languages.forEach(loader::addLoader);
        if (shouldDownload && arguments.validQuestionID()) {
            new Downloader(loader, arguments.questionID).downloadQuestions();
        }
        MutableList<Submission<T>> players = loader.load();
        Random random = arguments.getRandom();
        Tournament<Submission<T>> tournament = tournamentSupplier.apply(players, random);

        TournamentRunner<T, U> runner = runThreaded(random, tournament);
        printer.printProgress(arguments.iterations, arguments.iterations);
        Scoreboard<Submission<T>> scoreboard = runner.scoreboard();
        printer.printScoreboard(scoreboard);
    }

    private TournamentRunner<T, U> runThreaded(Random random, Tournament<Submission<T>> tournament) {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger numberOfGamesLeft = new AtomicInteger(arguments.iterations);
        ReentrantLock tournamentLock = new ReentrantLock();
        ReentrantLock printLock = new ReentrantLock();
        Semaphore finishedLock = new Semaphore(1);
        TournamentRunner<T, U> runner = new TournamentRunner<>(tournament, aggregator, gameSize, gameSupplier, random);
        
        printer.out.println("Running "+arguments.iterations+" games");
        
        Runnable gameRunnerTask = () -> {
            while (numberOfGamesLeft.get() > 0) {
                System.err.println("Retrieving lock on runner: " + Thread.currentThread().getName());
                long now = System.currentTimeMillis();
                tournamentLock.lock();
                System.err.println("Got lock on runner in " + (System.currentTimeMillis() - now) + "ms --- " + Thread.currentThread().getName());
                U game;
                try {
                    game = runner.createGame();}
                finally {tournamentLock.unlock();}
                System.err.println("Got game: " + Thread.currentThread().getName());
                game.run();
                printLock.lock();
                try {printer.printProgress(arguments.iterations - numberOfGamesLeft.get(), arguments.iterations);}
                finally {printLock.unlock();}
                numberOfGamesLeft.decrementAndGet();
            }
            finishedLock.release();
        };

        for (int i = 0; i < threadCount; i++) {
            executor.submit(gameRunnerTask);
        }

        try {
            finishedLock.acquire();// halt until workers are done
            finishedLock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // kill all tasks, since we are done and some threads may be hanging around processing stray games
        executor.shutdownNow();
        return runner;
    }

}
