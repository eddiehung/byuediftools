#!/bin/bash

######################################################################
# $Id$
#
# Pulls the relavant information from the various log files and prints
# them in CSV format to stdout. Due to commas used in the fields, the
# default separator is not a comma, but rather a verticl bar: `|'.
#
# For usage, try `multiple_tmr.sh --help'.
#
# Original author: James Carroll <jcarroll@byu.net>
#
# NOTE: I'm not sure if the clk units will always be ns in the timing
# report; here, they are assumed to be ns.
#
date="$Date$"
version="$Revision$"
######################################################################

# Name of executable (without path)
this=$(basename $0)
# Include guessDesign() function
source ${0/${this}/guessDesign.sh}
# Point to div.pl
div_pl=${0/${this}/div.pl}

#
# Print usage information.
#
usage()
{
    echo "Usage: $this [OPTIONS] [DESIGN]

Used after create_edif, populate.sh, and map.sh. Pulls key data from
the various log files and passes them as a CSV to stdout.

OPTIONS:
  -s, --separator  Specify separator symbol(s) to place between fields
                   (Default='|')
  --headers        Print overall headers in addition to detailed
                   headers

  -h, --help       Display this help and exit
  -v, --version    Display version information and exit

EXAMPLES

The following examples assume a source edif file \"design\".edf which
has already be triplicated with multiple_tmr.sh.

  Simple usage. Send output to a file.

$this design > data.csv

  Specify commas as separator character:

$this -s , > data.csv

  Specify semicolon as separator character (escaped to prevent
  processing by bash):

$this -s \; > data.csv

Report bugs to <jcarroll@byu.net>" 
}

#
# Print version and copyright information.
#
version () {
    echo "$this, $version, $date
Copyright (C) 2006 Brigham Young University"
}

# Process command-line options.
while [ -n "$(echo $1 | grep '-')" ]; do
    case $1 in
	-h | --help ) usage ; exit 0 ;;
	-s | --separator ) sep=$2 ; shift ;;
	-v | --version ) version ; exit 0 ;;
	--headers ) headers=1 ;;
	* ) echo "$this: Error: Unknown parameter: $1" ; usage ; exit 1 ;;
    esac
    shift
done

# If no design given, check if there is only one *.edf file in the
# current directory. If so, assume it is the design we want.
if [ $# -eq 0 ]; then
    design=$(guessDesign)
elif [ -n $1 ]; then
    design=$1
else
    usage ; exit 1
fi

# Get list of directories
dirs=$(ls -d ${design}_[adu]*[0-9]*)

# Add the no TMR'd version, if it exists
if [ -e ${design}_no_tmr ] && [ -d ${design}_no_tmr ]; then
    dirs="${dirs} ${design}_no_tmr"
fi
if [ -e ${design} ] && [ -d ${design} ]; then
    dirs="${dirs} ${design}"
fi

sep=${sep:-'|'}       # Separator for CSV file
precision=2           # Number of decimal places to keep
headers=${headers:-0} # Should the overall headers be printed?

# Overall headers. Useful for viewing data in a spreadsheet program,
# but not needed for gnuplot processing.
if [ $headers = 1 ]; then
    echo "Design${sep}\
Factor${sep}\
Usage prediction (.BLTmr.log)${sep}${sep}${sep}\
Usage (.par.par)${sep}${sep}${sep}\
Usage (.mrp)${sep}${sep}${sep}\
${sep}${sep}${sep}\
${sep}${sep}${sep}\
${sep}\
Persistence avg. (*pers.txt)${sep}${sep}${sep}${sep}\
Sensitivity avg. (*sens.txt)${sep}${sep}${sep}${sep}\
Timing (.twr)${sep}${sep}${sep}${sep}\
Power (.pwr)"
fi

# Detailed headers
echo "Design${sep}\
Factor${sep}\
Used Blocks${sep}Total Blocks${sep}Blocks %${sep}\
Used SLICEs${sep}Total SLICEs${sep}SLICEs %${sep}\
Used Slice FFs${sep}Total Slice FFs${sep}FF %${sep}\
4 input LUTs${sep}Total LUTs${sep}LUT %${sep}\
Used Slices${sep}Total Slices${sep}Slices %${sep}\
Weighted Usage${sep}\
n${sep}Persistent config. bits${sep}Total config. bits${sep}Persistence %${sep}\
n${sep}Sensitive config. bits${sep}Total config. bits${sep}Sensitivity %${sep}\
Max clk freq. (MHz)${sep}\
Min clk period (ns)${sep}\
Setup time (ns)${sep}\
Hold time (ns)${sep}\
Power consumption (mW)${sep}"

for directory in $dirs; do
    # Change "./some_folder" to "some_folder"
    directory=${directory#*/} 

    log_file=$directory/${directory}.BLTmr.log
    par_par_file=$directory/${directory}_xp1-fixed.par.par
    mrp_file=$directory/${directory}_xp1.mrp
    twr_file=$directory/${directory}_xp1-fixed.par.twr
    pwr_file=$directory/${directory}_xp1-fixed.pwr

    # Reset each of the strings for each iteration of the loop
    log=""
    par_par=""
    mrp_ff=""
    mrp_luts=""
    mrp_slices=""
    mrp_usage=""
    pers=""
    sens=""
    pwr=""
    twr_clk=""
    twr_setup=""
    twr_clk2q=""

    # The real work of the script begins here.  Note that each of the
    # regex patterns to extract the data is slightly different due to
    # the different combinations of commas, periods, units (ns, ps,
    # etc.), etc. in the various log files

    # 1. Usage prediction from .BLTmr.log
    if [ -s $log_file ]; then
	log=$(sed -n -e "s/Logic Blocks (estimated): *\([0-9]*\) out of \([0-9]*\)[ ]*(\([0-9]*%\)).*$/\1${sep}\2${sep}\3${sep}/p" $log_file)
    fi
    # Default value if no numbers found: just separators
    log=${log:-${sep}${sep}${sep}}

    # 2. Number of used SLICEs and total number of SLICEs from fixed.par.par
    if [ -s $par_par_file ]; then
	par_par=$(sed -n -e "s/[ ]*Number of SLICEs[ ]*\([0-9]*\) out of \([0-9]*\)[ ]*\([0-9]*%\).*$/\1${sep}\2${sep}\3${sep}/p" < $par_par_file)
    fi
    par_par=${par_par:-${sep}${sep}${sep}}

    # 3. Usage from .mrp
    if [ -s $mrp_file ]; then
        # 3.a) Used slice FF's
	mrp_ff=$(sed -n -e "s/[ ]*Number of Slice Flip Flops:[ ]*\([0-9,]*\) out of \([0-9,]*\)[ ]*\([0-9]*%\).*$/\1${sep}\2${sep}\3${sep}/p" < $mrp_file)
        # 3.b) Used 4-input LUTs
	mrp_luts=$(sed -n -e "s/Total Number \(of \)*4 input LUTs:[ ]*\([0-9,]*\) out of \([0-9,]*\)[ ]*\([0-9]*%\).*$/\2${sep}\3${sep}\4${sep}/p" < $mrp_file)
	# 3.c) Occupied Slices, total slices
	mrp_slices=$(sed -n -e "s/[ ]*Number of occupied Slices:[ ]*\([0-9,]*\) out of \([0-9,]*\)[ ]*\([0-9]*%\).*$/\1${sep}\2${sep}\3${sep}/p" < $mrp_file)
    fi

    # 3.d) Calculate weighted usage. Currently, weighted usage is
    # simply the sum of the number of LUTs and FFs

    # Get just the number (the first of the three values in each string)
    ff=${mrp_ff%%${sep}*}
    luts=${mrp_luts%%${sep}*}
    # Remove any commas
    ff=${ff/,/}
    luts=${luts/,/}
    # Ensure non-zero and store the sum
    if [ -n $ff ] && [ -n $luts ]; then
	mrp_usage="$(( ff + luts ))${sep}"
    fi

    mrp_ff=${mrp_ff:-${sep}${sep}${sep}}
    mrp_luts=${mrp_luts:-${sep}${sep}${sep}}
    mrp_slices=${mrp_slices:-${sep}${sep}${sep}}
    mrp_usage=${mrp_usage:-${sep}}

    mrp=${mrp_ff}${mrp_luts}${mrp_slices}${mrp_usage}

    # 4. Persistent error from *pers.txt file(s) (arithmetic mean)
    avg_sensitive_bits="0"         # Temporary variable used for both overall sensitivity and persistent sensitivity
    total_sensitive_bits="0"       # Temporary variable used to calculate avg_sensitive_bits
    total_bits="0"                 # Another temporary variable used to calculate avg_sensitive_bits

    # 4.a) If there are multiple simulations of the same EDIF file,
    # calculate the average persistent error.
    i=0
    if [ -s $directory/seu_${directory}_pers_1.txt ]; then
	for pers_file in $(ls $directory/*pers*.txt); do
	    if [ -s $pers_file ]; then
		sensitive_bits=$(sed -n -e "s/% Persistence error rate: \([0-9]*\).*$/\1/p" < $pers_file)
		((total_sensitive_bits += sensitive_bits))
		total_bits=$(sed -n -e "s/% Persistence error rate: [0-9]* \/ \([0-9]*\).*$/\1/p" < $pers_file)
		((i++))
	    fi
	done
    # 4.b) If there aren't multiple simulations of the same EDIF file,
    # look for data from a single run.
    elif [ -s $directory/seu_${directory}_pers.txt ]; then
	pers_file=$directory/seu_${directory}_pers.txt
	sensitive_bits=$(sed -n -e "s/% Persistence error rate: \([0-9]*\).*$/\1/p" < $pers_file)
	((total_sensitive_bits += sensitive_bits))
	total_bits=$(sed -n -e "s/% Persistence error rate: [0-9]* \/ \([0-9]*\).*$/\1/p" < $pers_file)
	((i++))
    fi
    avg_sensitive_bits=$($div_pl $total_sensitive_bits $i $precision)
    pers=${i}${sep}${avg_sensitive_bits}${sep}${total_bits}${sep}${sep}

    # 5. Overall error from *sens.txt file(s) (arithmetic mean)
    avg_sensitive_bits="0"
    total_sensitive_bits="0"
    total_bits="0"

    # 5.a) As with the persistent error, check for multiple sets of
    # data for overal (sensitive) error.
    i=0
    if [ -s $directory/seu_${directory}_sens_1.txt ]; then
	for sens_file in $(ls $directory/*sens*.txt); do    
	    if [ -s $sens_file ]; then
		sensitive_bits=$(sed -n -e "s/% Error rate: \([0-9]*\).*$/\1/p" < $sens_file)
		((total_sensitive_bits += sensitive_bits))
		total_bits=$(sed -n -e "s/% Error rate: [0-9]* \/ \([0-9]*\).*$/\1/p" < $sens_file)
		((i++))
	    fi
	done
    # 5.b) Again, as with the persistent error, check for data from a
    # single simulation.
    elif [ -s $directory/seu_${directory}_sens.txt ]; then
	sens_file=$directory/seu_${directory}_sens.txt
	sensitive_bits=$(sed -n -e "s/% Error rate: \([0-9]*\).*$/\1/p" < $sens_file)
	((total_sensitive_bits += sensitive_bits))
	total_bits=$(sed -n -e "s/% Error rate: [0-9]* \/ \([0-9]*\).*$/\1/p" < $sens_file)
	((i++))
    fi
    avg_sensitive_bits=$($div_pl $total_sensitive_bits $i $precision)
    sens=${i}${sep}${avg_sensitive_bits}${sep}${total_bits}${sep}${sep}

    # 6. Timing analysis from .twr
    if [ -s $twr_file ]; then
        # 6.a) Maximum clock frequency, minimum clock period
	twr_clk=$(sed -n -e "s/[ ]*Minimum period:[ ]*\([0-9.]*\)\([npmu]s\).*(Maximum frequency:[ ]*\([0-9.]*\)MHz.*$/\3${sep}\1${sep}/p" < $twr_file)
        # 6.b) Minimum input before clock (t-setup)
	twr_setup=$(sed -n -e "s/[ ]*Minimum input required time before clock:[ ]*\([0-9.]*\)\([npmu]s\).*$/\1${sep}/p" < $twr_file)
        # 6.c) Maximum output delay after clock (t-clk-to-q)
	twr_clk2q=$(sed -n -e "s/[ ]*Maximum output delay after clock:[ ]*\([0-9.]*\)\([npmu]s\).*$/\1${sep}/p" < $twr_file)
    fi
    twr_clk=${twr_clk:-${sep}${sep}}
    twr_setup=${twr_setup:-${sep}}
    twr_clk2q=${twr_clk2q:-${sep}}

    twr=${twr_clk}${twr_setup}${twr_clk2q}

    # 7. Power analysis from .pwr
    if [ -s $pwr_file ]; then
	pwr=$(sed -n -e "s/Total estimated power consumption:[ ]*\([0-9]*\).*$/\1${sep}/p" < $pwr_file)
    fi
    pwr=${pwr:-${sep}}

    #
    # 8. Get the factor type (ASUF, DUF, or UEF) and value
    #
    # 8.a) Factor type
    factor_type=${directory#${design}_}
    factor_type=${factor_type%_[0-9]_*}

    # 8.b) Factor value
    factor_value=${directory#${design}_${factor_type}_}
    # Convert underscore to decimal
    factor_value=${factor_value/_/.}
    
    #
    # 9. After doing all the work, send the data to stdout
    #
    echo ${directory%_${factor_type}_*}${sep}${factor_value}${sep}${log}${par_par}${mrp}${pers}${sens}${twr}${pwr}${factor_type}

done

echo "Done with $this" > /dev/stderr
