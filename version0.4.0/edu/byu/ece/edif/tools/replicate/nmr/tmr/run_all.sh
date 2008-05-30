#!/bin/bash

###############################################################################
# $Id$
#
# Executes BLTmr.jar multiple times, modifying the amount of partial
# TMR each time. Requires bash, perl, sed, dc, div.pl and BLTmr 0.2.0
# (or greater)
#
# For usage, try `multiple_tmr.sh --help'.
#
# Original author: James Carroll <jcarroll@byu.net>
#
date="$Date$"
version="$Revision$"
###############################################################################

# Name of executable (without path)
this=$(basename $0)
# Get relative path to div.pl
div=${0/${this}/div.pl}
extract_data=${0/${this}/extract_data.sh}
unTMR_clk=${0/${this}/unTMR_clk.sh}

#
# Print usage information. For detailed info, call "usage -v"
#
usage() {
    # verbose usage information
    if [ $# != 0 ] && [ $1 = -v ]; then
	echo "Usage: $this [OPTIONS] EDIF_FILE 

Takes an EDIF file and triplicates the design with different levels of partial
triplication.  Each iteration of the design will be stored in a separate
subdirectory of the current directory.


OPTIONS

  -a, --add      Sepcify the additionType (1, 2, or 3) (default=3)
  -c, --cp       Specify the classpath (Default=./BLTmr.jar)
  -f, --force    Overwrite existing files
  -h, --high     <high Utilization Exansion Factor>]
  -i, --inc      <increment step> (Default=20)
  -j, --java     path/to/java/bin/java (Default determined by \`which java')
  -l, --low      <low Utilization Expansion Factor> (Default=0)
  -q, --quiet    Supress all output; overrides -v
  -r, --ram      <amount of RAM to dedicate to the JVM> (e.g. 500M) 
                 (Default=90% of total ram, as determined by /proc/meminfo)
  -s, --scc      Specify the SCCSortType (1, 2, or 3) (default=3)
  --seu          Specify the path and file of the seusim_elf binary 
                 (default=~/seusim_elf_
  -v, --verbose  Verbose output (unless -q or --quiet)
  --factor_type  <uef|duf|asuf> specify which factor type to use (default=asuf)
  --x0           Controller bit file (default=x0.bit)
  --x2           Golden bit file (default=DESIGN_xp2.bit)
                 uef:  utilizationExpansionFactor
                 duf:  desiredUtilizationFactor
                 asuf: availableSpaceUtilizationFactor

  --help         Display this help and exit.
  --version      Output version information and exit.


EXAMPLES

  Simple usage.  By default, $this will run BLTmr 99 times, from ASUF
  (Available Space Utilization Factor) 0.01 to ASUF 1.00:

$this design.edf

  Specify location of BLTmr.jar file:

$this -c /fpga2/jars/BLTmr.jar design.edf

  Specify location of java JRE:

$this -j /usr/local/java/bin/java design.edf

  Specify lower and upper bounds. Run BLTmr 11 times, from ASUF 0.25 to 0.35,
  inclusive:

$this -l 25 -h 35 design.edf

  Specify the utilization factor type. Run BLTmr from UEF (Utilization
  Expansion Factor) 100 to 200:

$this --factor_type uef -l 100 -h 200 design.edf

  Specify the maximum amount of RAM to be used by the JVM:

$this -r 1000M design.edf

Report bugs to <jcarroll@byu.net>" 

    # concise usage information
    else
	echo "Usage: $this [OPTIONS] EDIF_FILE
Try \`$this --help' for more information."
    fi
}

#
# Print version and copyright information.
#
version () {
    echo "$this, $version, $date
Copyright (C) 2006 Brigham Young University"
}

# Check for zero command-line arguments, print usage.
if [ $# -eq 0 ]; then
    usage
    exit 1
fi

# 0.a) process command-line options
while [ -n "$(echo $1 | grep '-')" ]; do
    case $1 in
	-a | --add ) add_type=$2 ; shift ;;
	-c | --cp ) class_path=$2 ; shift ;;
	-f | --force ) force=1 ;;
	-h | --high ) high=$2 ; shift;;
	-i | --inc ) inc=$2 ; shift ;;
	-j | --java ) java_bin=$2 ; shift ;;
	-l | --low ) low=$2 ; shift ;;
	-q | --quiet ) quiet=1 ; verbose=0 ;;
	-r | --ram ) ram=$2 ; shift ;;
	-s | --scc ) scc_type=$2 ; shift ;;
	--seu ) seusim_elf=$2 ; shift ;;
	-v | --verbose ) verbose=${verbose:-1} ; quiet=${quiet:-0} ;;
	--x0 ) x0=$2 ; shift ;;
	--x2 ) x2=$2 ; shift ;;
	--factor_type ) factor_type=$2 ; shift ;;
	--help ) usage -v ; exit 0 ;;
	--version ) version ; exit 0 ;;
	* ) echo "Unknown parameter: $1"; usage ; exit 1 ;; # unknown parameter
    esac
    shift
done

# 0.b) Check for zero command-line arguments, print usage if necessary
if [ $# -eq 0 ] || [ ! -s $1 ]; then
    echo "$this: Error: no EDIF_FILE specified"
    usage
    exit 1
fi

# 1. Input file name
infile=$1

#
# 2. Default values
#

# 2.a) Simple variables
design=$(basename $infile .edf) # Remove a single .edf, if it exists
class_path=${class_path:-BLTmr.jar}
if [ ! -e $class_path ]; then
    echo "$this: Warning: BLTmr jar file $class_path not found."
fi 
factor_type=${factor_type:-asuf} # Factor type
low=${low:-1}                    # Starting factor value
high=${high:-100}                # Ending factor value
inc=${inc:-1}                    # Step size
force=${force:-0}                # Overwrite existing files?
java_bin=${java:-$(which java)}  # Path to java executable (must be JRE 5.0)
quiet=${quiet:-0}                # Silence all output to stdout
verbose=${verbose:-0}            # Opposite of quiet
add_type=${add_type:-3}          # inputAdditionType and outputAdditionType
scc_type=${scc_type:-3}          # SCCSortType
suffix=_tmr_                     # Appended to design name


# 2.b) Calculate 90% of the total ram from /proc/meminfo, and set that
# to the maximum RAM usage for the JVM. Requires 'sed' and 'dc'.
ram=$(dc -e "$(sed -n -e 's#MemTotal:[ ]*\([0-9]*\) kB#\1#p' < /proc/meminfo) 1024 / 9 * 10 / p")M
# If the above failed, just use 512 MB
if [ $ram = "M" ]; then 
    ram=${ram:-512M}             # RAM used by JVM
fi

# 2.c) Check for valid "factor_type" parameter
case $factor_type in
    duf ) factor_type_verbose=desiredUtilizationFactor ;;
    uef ) factor_type_verbose=utilizationExpansionFactor ;;
    asuf ) factor_type_verbose=availableSpaceUtilizationFactor ;;
    * ) echo "$this: Warning: invalid factor type: $factor_type . Using 'asuf'"
	factor_type=asuf
	factor_type_verbose=availableSpaceUtilizationFactor ;;
esac

# 2.d) Simulator variables
seusim_elf=${seusim_elf:-~/seusim_elf} # location of SEU simulator executable
x0=${x0:-x0.bit}                 # Controller bit file
x2=${x2:-${design}_xp2.bit}      # Golden bit file

# 3. Ensure file exists, isn't empty, and we have read permission
if [ ! -s "$infile" ] && [ $quiet = 0 ]; then
    echo "$this: Error: File $infile does not exist or is empty; aborting" ; exit 1
fi
if [ ! -r "$infile" ] && [ $quiet = 0 ]; then
    echo "$this: Error: You do not have read permission on file $infile; aborting" ; exit 1
fi

#
# Actual work begins here. Iterate across the values specified and
# perform all the work
#
for (( i = low; i <= high; i += inc )); do
    # 4. Run the BLTmr tool for each iteration.
    outdir=${design}${suffix}${factor_type}_${i}_scc${scc_type}_add${add_type}
    directory=$outdir
    outfile=${outdir}.edf
    logfile=${outdir}.BLTmr.log
    domain_report=${outdir}_domain_report.txt
    factor=$($div $i)   # Get fractional utilization factor
    skip_edif=0

    # 4.a) Check for existing file; if found, don't create the EDIF
    # file, unless -f was specified
    if [ $force -eq 0 ] && [ -s $outdir/$outfile ] && [ $quiet = 0 ]; then
	echo "$this: Warning: File $outdir/$outfile already exists, skipping design. (Use \`create_edif -f ...' to force overwrite.)"
	skip_edif=1
    fi

    # 4.b) Give feedback if mkdir fails or if directory already exists.
    if [ -e "$outdir" ] && [ -d "$outdir" ] && [ $quiet = 0 ]; then
	echo "$this: Warning: directory $outdir already exists"
    else
	if [ $(mkdir $outdir) ] && [ $quiet = 0 ]; then
	    echo "$this: Error: Failed to created directory: $outdir. Perhaps you don't have write permission. Skipping."
	    continue
	fi
    fi

    # 4.c) Prepare to execute
    command="$java_bin \
-Xms$((${ram%M}/2))M \
-Xmx$ram \
-cp ${class_path} \
byucc.edif.tools.tmr.FlattenTMR $infile \
-o $outdir/$outfile \
--log $outdir/$logfile \
--$factor_type_verbose $factor \
--domainReport $outdir/$domain_report \
--tmr_inports \
--no_tmr_p XP_RST \
--no_tmr_p XP_IN \
--tmr_i clk_bufg \
--SCCSortType ${scc_type} \
--inputAdditionType ${add_type} \
--outputAdditionType ${add_type}"
#--tmr_i RST_BUFG \

    # 4.d) If --verbose, print command to stdout before executing
    if [ "$verbose" = 1 ]; then
	echo "$this: Executing command: $command ..."
    fi
    # 4.e) Execute!
    if [ $skip_edif = 0 ]; then
	$command
    fi

    #
    # 5. Create symbolic links and copy files to the instance folder
    #

    # 5.a) Populate the ucf file.
    ln -sf ../${design}_tmr_xp1.ucf $directory/${directory}_xp1.ucf

    # 5.b) Populate the Makefile.
    ln -sf ../Makefile $directory/Makefile

    # 5.c) Populate the do (ModelSim macro) file.
    ln -sf ../${design}.do $directory/${directory}.do

    # 5.d) Populate the .mpf (ModelSim project) file.
    mpf_file=${design}.mpf
    if [ -s $mpf_file ]; then
	cp -f $mpf_file $directory/${directory}.mpf
	sed -i -e 's/${design}/${directory}/g' $directory/${directory}.mpf
    fi

    #
    # 6. Synthesize through .par.ncd and then convert to xdl
    #
    make -C $directory xdl

    #
    # 7. Un-triplicate the clock
    #
    $unTMR_clk $directory/${directory}_xp1.xdl

    #
    # 8. Finish synthesizing to a bitstream
    #
    make -C $directory xp1

    #
    # 9. Simulate in SEU simulator
    #

    # 9.a) If we don't have access to a simulator, stop here.
    has_seusim=0
    # 
    if [[ $HOST == "rat" ]] || [[ $HOST == "mouse" ]]; then
	has_seusim=1
    fi
    if [ $has_seusim = 0 ]; then
	echo "It appears you don't have access to a SEU simulator. Aborting."
	exit 1
    fi
    simulate_log=${simulate_log:-simulate.log}
    
    # 9.b) Ensure unique output file names so we don't clobber previous results
    i=1
    sens=$directory/seu_${directory}_sens
    pers=$directory/seu_${directory}_pers
    while [ -s ${sens}_${i}.txt ] || [ -s ${pers}_${i}.txt ]; do
	((i++))
    done
    sens_output_file=${sens}_${i}.dat
    pers_output_file=${pers}_${i}.dat

    # 9.c) Set options for simulator
    # Set options for both sensitivity and persistence test
    x1=$directory/${directory}_xp1-fixed.bit       # Unit under test
    common_opts="-x0 $x0 -x1 $x1 -x2 $x2"

    # Set options for sensitivity test
    seed=$i
    sensitivity_opts="-o ${sens_output_file} -fixedrand $seed"

    # Set options for persistence test
    reset_style=0
    x=0
    y=1000
    z=10
    persistance_opts="-p $reset_style $x $y $z $pers_output_file -fixedrand $seed"


    # 9.d) Actually perform the simulation
    echo "$this: Starting persistance for BLTmr instance $directory with seed $seed..." >> $simulate_log
    $seusim_elf ${common_opts} ${persistance_opts}
    sed -n -e '/^%%%%/,/^%%%%/p' < ${pers_output_file} > ${pers_output_file%.dat}.txt

    echo "$this: Starting sensitivity for BLTmr instance $directory with seed $seed..." >> $simulate_log
    $seusim_elf ${common_opts} ${sensitivity_opts}
    sed -n -e '/^%%%%/,/^%%%%/p' < ${sens_output_file} > ${sens_output_file%.dat}.txt

    # 9.e) Compress the output files, forcing overwrite
    gzip -f $directory/*.dat 

done

# 10. Extract data from report files
$extract_data > ${design}.csv

# 11. Create plots
echo "set output \"${design}_usage_vs_uef.ps\"" $(cat seu.gp) \
"plot \"${design}.csv\" \
     using ($2):($18) axes x2y2 title \"Weighted Usage\" with linespoints, \
     '' using ($2):($8) axes x1y1 title \"FF %\" with lp, \
     '' using ($2):($11) axes x1y1 title \"LUT %\" with lp, \
     '' using ($2):($14) axes x1y1 title \"Slice %\" with lp" | gnuplot

# 11.a) Convert .ps files to .pdf
for file in $(ls *.ps); do 
    ps2pdf $file; 
done

# Done!
