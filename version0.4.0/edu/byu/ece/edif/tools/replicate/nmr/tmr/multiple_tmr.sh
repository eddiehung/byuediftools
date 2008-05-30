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
  -c, --cp, --classpath
                 Specify the classpath (Default=./BLTmr.jar)
  -f, --force    Overwrite existing files
  -h, --high     <high Utilization Exansion Factor>]
  -i, --inc      <increment step> (Default=20)
  -j, --java     path/to/java/bin/java (Default determined by \`which java')
  -l, --low      <low Utilization Expansion Factor> (Default=0)
  -q, --quiet    Supress all output; overrides -v
  -r, --ram      <amount of RAM to dedicate to the JVM> (e.g. 500M) 
                 (Default=90% of total ram, as determined by /proc/meminfo)
  -s, --scc      Specify the SCCSortType (1, 2, or 3) (default=3)
  -v, --verbose  Verbose output (unless -q or -s)
  --factor_type  <uef|duf|asuf> specify which factor type to use (default=asuf)
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
	-c | --cp | --classpath ) class_path=$2 ; shift ;;
	-f | --force ) force=1 ;;
	-h | --high ) high=$2 ; shift;;
	-i | --inc ) inc=$2 ; shift ;;
	-j | --java ) java_bin=$2 ; shift ;;
	-l | --low ) low=$2 ; shift ;;
	-q | --quiet ) quiet=1 ; verbose=0 ;;
	-r | --ram ) ram=$2 ; shift ;;
	-s | --scc ) scc_type=$2 ; shift ;;
	-v | --verbose ) verbose=${verbose:-1} ; quiet=${quiet:-0} ;;
	--factor_type ) factor_type=$2 ; shift ;;
	--help ) usage -v ; exit 0 ;;
	--version ) version ; exit 0 ;;
	* ) echo "Unknown parameter: $1"; usage ; exit 1 ;; # unknown parameter
    esac
    shift
done

# 0.b) Check for zero command-line arguments, print usage if necessary
if [ $# -eq 0 ] || [ -z $1 ]; then
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
class_path=${class_path:-$CLASSPATH}
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

# 3. Ensure file exists, isn't empty, and we have read permission
if [ ! -s "$infile" ] && [ $quiet = 0 ]; then
    echo "$this: Error: File $infile does not exist or is empty; aborting" ; exit 1
fi
if [ ! -r "$infile" ] && [ $quiet = 0 ]; then
    echo "$this: Error: You do not have read permission on file $infile; aborting" ; exit 1
fi

#
# 4. Actual work begins here. Iterate across the values specified and
# run the BLTmr tool for each iteration.
#
for (( i = low; i <= high; i += inc )); do
    design=$(basename $infile)
    design=${design%.edf}               # Remove a single .edf, if it exists
    outdir=${design}${suffix}${factor_type}_${i}_scc${scc_type}_add${add_type}
    outfile=${outdir}.edf
    logfile=${outdir}.BLTmr.log
    domain_report=${outdir}_domain_report.txt
    factor=$($div $i)          # Get fractional utilizationExpansionFactor

    # Check for existing file; if found, skip this iteration, unless
    # -f was specified
    if [ $force -eq 0 ] && [ -s $outdir/$outfile ] && [ $quiet = 0 ]; then
	echo "$this: Warning: File $outdir/$outfile already exists, skipping design. (Use \`$this -f ...' to force overwrite.)"
	continue
    fi

    # Give feedback if mkdir fails or if directory already exists.
    if [ -e "$outdir" ] && [ -d "$outdir" ] && [ $quiet = 0 ]; then
	echo "$this: Warning: directory $outdir already exists"
    else
	if [ $(mkdir $outdir) ] && [ $quiet = 0 ]; then
	    echo "$this: Error: Failed to created directory: $outdir. Perhaps you don't have write permission. Skipping."
	    continue
	fi
    fi

    # Prepare to execute
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
--no_tmr_p xp_in \
--tmr_i CLK_BUFG \
--no_tmr_i CLK_IBUFG \
--SCCSortType ${scc_type} \
--inputAdditionType ${add_type} \
--outputAdditionType ${add_type}"

    # Print command to stdout, if --verbose, before executing
    if [ "$verbose" = 1 ]; then
	echo "$this: Executing command: $command"
    fi
    $command

done
