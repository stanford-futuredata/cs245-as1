package memstore;

import picocli.CommandLine;

import java.io.File;

public class GraderArgs {
    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    @CommandLine.Option(names = { "-o", "--ofile" }, paramLabel = "OUTFILE", description = "json output path")
    File outFile = null;

    @CommandLine.Option(names = {"-m", "--mode"}, paramLabel = "MODE", description = "all,timings,correct,custom")
    String mode = "all";

    @CommandLine.Option(names = {"-i", "--iter"}, paramLabel = "NITER", description = "number of timing iterations")
    int numIter = 3;

    @CommandLine.Option(names = {"-ci", "--citer"}, paramLabel = "CITER", description = "number of iterations for custom workload")
    int numCustomIter = 2;
}
