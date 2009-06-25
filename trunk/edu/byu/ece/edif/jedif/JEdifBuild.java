package edu.byu.ece.edif.jedif;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import com.martiansoftware.jsap.JSAPResult;

import edu.byu.ece.edif.core.EdifCell;
import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifDesign;
import edu.byu.ece.edif.core.EdifEnvironment;
import edu.byu.ece.edif.core.EdifNameConflictException;
import edu.byu.ece.edif.core.EdifRuntimeException;
import edu.byu.ece.edif.core.EdifUtils;
import edu.byu.ece.edif.core.InvalidEdifNameException;
import edu.byu.ece.edif.core.Property;
import edu.byu.ece.edif.tools.LogFile;
import edu.byu.ece.edif.tools.flatten.FlattenedEdifCell;
import edu.byu.ece.edif.tools.flatten.PreservedHierarchyByNames;
import edu.byu.ece.edif.tools.replicate.nmr.EdifReplicationPropertyReader;
import edu.byu.ece.edif.tools.replicate.nmr.NMRUtilities;
import edu.byu.ece.edif.tools.sterilize.fmap.FmapRemover;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchArchitecture;
import edu.byu.ece.edif.tools.sterilize.halflatch.HalfLatchCopyReplace;
import edu.byu.ece.edif.tools.sterilize.halflatch.XilinxHalfLatchArchitecture;
import edu.byu.ece.edif.tools.sterilize.lutreplace.LUTReplacer;
import edu.byu.ece.edif.tools.sterilize.lutreplace.RLOCRemove;
import edu.byu.ece.edif.util.graph.EdifCellInstanceGraph;
import edu.byu.ece.edif.util.iob.IOBAnalyzer;
import edu.byu.ece.edif.util.iob.XilinxVirtexIOBAnalyzer;
import edu.byu.ece.edif.util.jsap.EdifCommandParser;
import edu.byu.ece.edif.util.jsap.commandgroups.ConfigFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.IOBCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifOutputCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.JEdifSterilizeCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.LogFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.MergeParserCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.OutputFileCommandGroup;
import edu.byu.ece.edif.util.jsap.commandgroups.TechnologyCommandGroup;

public class JEdifBuild extends EDIFMain {

    public static PrintStream out;
    public static PrintStream err;
    
    public static void main(String[] args) {
        out = System.out;
        err = System.err;
        
        EXECUTABLE_NAME = "JEdifBuild";
        TOOL_SUMMARY_STRING = "Creates merged netlists in a .jedif file format from multiple .edf files";
        
        printProgramExecutableString(out);
        
        EdifCommandParser parser = new EdifCommandParser();
        parser.addCommands(new MergeParserCommandGroup());
        parser.addCommands(new JEdifOutputCommandGroup());
        parser.addCommands(new TechnologyCommandGroup());
        parser.addCommands(new JEdifSterilizeCommandGroup());
        parser.addCommands(new IOBCommandGroup());
        parser.addCommands(new LogFileCommandGroup("build.log"));
        parser.addCommands(new ConfigFileCommandGroup(EXECUTABLE_NAME));

        JSAPResult result = parser.parse(args, err);
        if (!result.success())
            System.exit(1);

        LogFileCommandGroup.CreateLog(result);
        out = LogFile.out();
        err = LogFile.err();

        printProgramExecutableString(LogFile.log());

        // Parse EDIF file and generate a EdifEnvironment object
        EdifEnvironment env = MergeParserCommandGroup.getEdifEnvironment(result);
        try {
            TechnologyCommandGroup.getPartFromEDIF(result, env);
        } catch (IllegalArgumentException ex) {
            //don't care right now, but other tools might later
        }
        
        // Removing FMAPs modifies the existing EdifEnvironment
        if (JEdifSterilizeCommandGroup.getRemoveFMaps(result)) {
            out.print("Removing FMAPs... ");
            int fmapCount = FmapRemover.removeFmaps(env);
            out.println("Done.");
            LogFile.log().println("Removed " + fmapCount + " fmaps");
        }
        
        // Removing RLOCs modifies the existing EdifEnvironment
        if (JEdifSterilizeCommandGroup.getRemoveRLOCs(result)) {
            out.print("Removing RLOCs... ");
            RLOCRemove rlocRemover = new RLOCRemove(env);
            out.println("Done.");
        }
        
        // Replacing SRLs and lutrams creates a new EdifEnvironment
        if (JEdifSterilizeCommandGroup.getReplaceLuts(result)) {
            out.print("Replacing SRLs and lutrams... ");
            env = LUTReplacer.replaceLUTs(env, LogFile.log(), out);
            
            out.println("Done.");
        }
        
        // Flatten if necessary
        PreservedHierarchyByNames hierarchy = null;
        if (MergeParserCommandGroup.performFlatten(result) || JEdifSterilizeCommandGroup.getRemoveHL(result)) {
            env = flatten(result, env);
            // preserve hierarchy via name references
            hierarchy = new PreservedHierarchyByNames((FlattenedEdifCell) env.getTopCell());
        }
        
        // remove half latches
        if (JEdifSterilizeCommandGroup.getRemoveHL(result)) {
            env = removeHalfLatches(env, true, false, result, out);
        }
        
        // Serialize output
        // If the circuit got flattened, the preserved hierarchy also goes in
        // the serialized output (after the EdifEnvironment)
        Collection objects = new ArrayList(2);
        objects.add(env);
        if (hierarchy != null)
            objects.add(hierarchy);
        if (!result.userSpecified(OutputFileCommandGroup.OUTPUT_OPTION)) {
            String name = MergeParserCommandGroup.getInputFileName(result);
            name = name.substring(0, name.lastIndexOf('.'));     
            JEdifOutputCommandGroup.serializeObjects(out, name + ".jedif", objects);
        } else {
            JEdifOutputCommandGroup.serializeObjects(out, result, objects);
        }
        out.println();
        
    }
    
    private static EdifEnvironment flatten(JSAPResult result, EdifEnvironment top) {
        FlattenedEdifCell flatCell = null;
        EdifCell oldCell = top.getTopCell();
        try {
            if (oldCell instanceof FlattenedEdifCell) {
                flatCell = (FlattenedEdifCell) oldCell;
            } else {
                
                // Make a Set of cells not to flatten
                Set<EdifCell> noFlattenCells = new LinkedHashSet<EdifCell>();
                for (EdifCell cell : top.getLibraryManager().getCells()) {
                    if (EdifReplicationPropertyReader.isDoNotFlattenCell(cell) || EdifReplicationPropertyReader.isPremitigatedCell(cell))
                        noFlattenCells.add(cell);
                }
                
                if (noFlattenCells.size() > 0)
                    out.println("Not flattening the following cell type(s): " + noFlattenCells);
                
                out.print("Flattening... ");
                flatCell = new FlattenedEdifCell(oldCell, "_flat", noFlattenCells);
            }
        } catch (EdifNameConflictException e) {
            e.toRuntime();
        } catch (InvalidEdifNameException e) {
            e.toRuntime();
        }

        // Set flat cell at top
        EdifCellInstance flatInstance = null;
        EdifDesign newDesign = null;
        try {
            flatInstance = new EdifCellInstance(flatCell.getName(), null, flatCell);
            newDesign = new EdifDesign(flatCell.getEdifNameable());
        } catch (InvalidEdifNameException e1) {
            e1.toRuntime();
        }
        newDesign.setTopCellInstance(flatInstance);
        // copy design properties
        EdifDesign oldDesign = top.getTopDesign();
        if (oldDesign.getPropertyList() != null) {
            for (Object o : oldDesign.getPropertyList().values()) {
                Property p = (Property) o;
                newDesign.addProperty((Property) p.clone());
            }
        }
        top.setTopDesign(newDesign);

        // Delete old cell(s)
        if (!JEdifOutputCommandGroup.getNoDelete(result)) {
            flatCell.getLibrary().deleteCell(oldCell, true);
            flatCell.getLibrary().getLibraryManager().pruneNonReferencedCells();
        }

        out.println("Done");

        out.println("\tFlattened circuit contains " + EdifUtils.countRecursivePrimitives(flatCell) + " primitives, "
                + EdifUtils.countRecursiveNets(flatCell) + " nets, and " + EdifUtils.countPortRefs(flatCell, true)
                + " net connections");
        return top;
    }
    
    private static EdifEnvironment removeHalfLatches(EdifEnvironment env, boolean reportTiming, boolean debug, JSAPResult result, PrintStream out) {

        /**
         * 8. Analyze IOBs of the flattened EdifCell TODO: Add IOBAnalyzer
         * objects for other architectures/technologies
         */

        // Pack IOBs or not?
        boolean packInputRegs = false, packOutputRegs = false;
        packInputRegs = IOBCommandGroup.packInputRegisters(result);
        packOutputRegs = IOBCommandGroup.packOutputRegisters(result);

        EdifCell topCell = env.getTopCell();
        FlattenedEdifCell flatCell = null;
        if (topCell instanceof FlattenedEdifCell) {
            flatCell = (FlattenedEdifCell) topCell;
        }
        else {
            throw new EdifRuntimeException("Error: attempting to remove half-latches from an unflattened design");
        }
        
        //Graph is used for IOBAnalyzer, which is used for half_latch removal.
        EdifCellInstanceGraph eciConnectivityGraph = new EdifCellInstanceGraph(flatCell, true, false);
        
        IOBAnalyzer iobAnalyzer = new XilinxVirtexIOBAnalyzer(flatCell, eciConnectivityGraph, packInputRegs, packOutputRegs);
        long startTime;
        startTime = System.currentTimeMillis();
        if (reportTiming)
            startTime = LogFileCommandGroup.reportTime(startTime, "IOB Analysis", out);

        out.println("Removing half-latches... ");
        // TODO: Ensure we are using Xilinx (the only supported architecture)
        HalfLatchArchitecture hlArchitecture = null;
        if (packInputRegs || packOutputRegs) {
            // Send the (possibly large) list to the log file only
            LogFile.log().println(
                    "\tThe following flip-flops were treated as IOB " + "registers during half-latch removal: "
                            + iobAnalyzer.getAllIOBRegisters());
            hlArchitecture = new XilinxHalfLatchArchitecture(iobAnalyzer);
        } else {
            hlArchitecture = new XilinxHalfLatchArchitecture();
        }

        String hlPortName = JEdifSterilizeCommandGroup.getHLPortName(result);
        boolean hlUsePort = false;
        if (hlPortName != null)
            hlUsePort = true;
        int constantPolarity = JEdifSterilizeCommandGroup.getHLConstant(result);
        
        HalfLatchCopyReplace hlcr = null;
        try {
            hlcr = new HalfLatchCopyReplace(env, hlArchitecture, iobAnalyzer, constantPolarity, hlUsePort, hlPortName);
        } catch (EdifNameConflictException e) {
            // this isn't supposed to happen
            e.toRuntime();
        }
        
        // Force replication of internal half-latch constant or port ibuf
        if (!JEdifSterilizeCommandGroup.hlNoTagConstant(result)) {
            EdifCellInstance safeConstantInstance;
            if (hlUsePort)
                safeConstantInstance = hlcr.getSafeConstantPortBufferInstance();
            else
                safeConstantInstance = hlcr.getSafeConstantGeneratorCell();
            safeConstantInstance.addProperty(new Property(EdifReplicationPropertyReader.HALF_LATCH_CONSTANT, true));
        }

        return hlcr.getNewEnvironment();
    }
    
}
