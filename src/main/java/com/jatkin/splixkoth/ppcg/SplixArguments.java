package com.jatkin.splixkoth.ppcg;

import com.beust.jcommander.Parameter;
import com.nmerrill.kothcomm.communication.Arguments;

/**
 * Created by Jarrett on 06/15/17.
 */
public class SplixArguments extends Arguments {
    @Parameter(
        names = {"-m", "--multi-thread"},
        description = "Allow multi-threaded running (same as `--thread-count 1`)",
        arity = 1
    )
    public boolean multiThread = true;
    
    @Parameter(
        names = {"-c", "--thread-count"},
        description = "Number of threads used to process the game"
    )
    public int threadCount = 4;
    
    {
        iterations = 500;// more reasonable default
    }
}
