package KoTHComm.game.tournaments;

import KoTHComm.game.*;
import KoTHComm.game.tournaments.types.SamplingProvider;
import KoTHComm.game.tournaments.types.ScoreboardRanker;

import java.util.stream.Stream;

public class TournamentRunner<T extends AbstractPlayer<T>> {
    private final ProviderSupplier<T> providerSupplier;
    private final RankerSupplier<T> rankerSupplier;
    private final TournamentSupplier<T> bothSupplier;
    public TournamentRunner(ProviderSupplier<T> providerSupplier, RankerSupplier<T> rankerSupplier){
        this.providerSupplier = providerSupplier;
        this.rankerSupplier = rankerSupplier;
        bothSupplier = null;
    }

    public TournamentRunner(TournamentSupplier<T> bothSupplier){
        this.bothSupplier = bothSupplier;
        this.providerSupplier = null;
        this.rankerSupplier = null;
    }

    public TournamentRunner(GameManager<T> gameManager){
        this(new SamplingProvider<>(gameManager), new ScoreboardRanker<>());
    }



    @SuppressWarnings("ConstantConditions")
    public Scoreboard<PlayerType<T>> run(int numGames){
        GameProvider<T> provider;
        GameRanker<T> ranker;
        if (bothSupplier == null){
            provider = providerSupplier.getTournament();
            ranker = rankerSupplier.getRanker();
        } else {
            Tournament<T> tournament = bothSupplier.get();
            provider = tournament;
            ranker = tournament;
        }

        Stream.generate(provider).limit(numGames).forEach(game -> {
            game.run();
            ranker.scoreGame(game);
        });

        return ranker.getRankings();
    }

}
