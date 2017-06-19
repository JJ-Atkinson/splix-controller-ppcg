package com.jatkin.splixkoth.ppcg.loaders;

import clojure.lang.RT;
import com.nmerrill.kothcomm.communication.languages.Language;
import com.nmerrill.kothcomm.game.players.AbstractPlayer;
import com.nmerrill.kothcomm.game.players.Submission;
import org.eclipse.collections.api.list.MutableList;

import java.io.File;
import java.util.function.Supplier;

/**
 * Created by Jarrett on 06/17/17.
 */
public final class ClojureLoader<T extends AbstractPlayer<T>> implements Language<T> {

    private Supplier<T> clojurePlayerSupplier;

    /**
     * @param clojurePlayer 
     */
    public ClojureLoader(Supplier<T> clojurePlayerSupplier) {
        this.clojurePlayerSupplier = clojurePlayerSupplier;
    }

    @Override
    public boolean fileBased() {
        return true;
    }

    @Override
    public String directoryName() {
        return "clojure";
    }

    @Override
    public String name() {
        return "Clojure";
    }

    @Override
    public MutableList<Submission<T>> loadPlayers(MutableList<File> files) {
        return files.collect(f -> {
            // this may be interpreting the code, which is slower. Need to research that.
            String safePath = f.toString().replace('\\', '/');
            RT.var("clojure.core", "load-file").invoke(safePath);
            String name = f.getName().replace(".clj", "").replace("_", "-");

            return new Submission<>(name, clojurePlayerSupplier);
        });
    }
}
