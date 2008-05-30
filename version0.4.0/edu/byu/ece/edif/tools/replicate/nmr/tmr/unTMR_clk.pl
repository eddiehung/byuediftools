#!/usr/bin/perl

######################################################################
# $Id$
#
# Given an XDL file with a triplicated clock line, untriplicate the
# clock by removing the second and third instances and changing nets
# as needed. See unTMR_clk.sh.
#
# Original Author: James Carroll
#
# TODO: 
#  * Ensure that the file has the clk triplicated
#  * Bring out the design-specific variables as command-line options
#
######################################################################

use warnings;
use strict;

my $version = q"$Revision$ ";
my $date = q"$Date$ ";

my $suffix = "_TMR";
my $inst_suffix = "_instanceTMR_";

# Design-specific variables
my $port = "XP_PCLK";
my $bufg = "bufg";
my $ibufg = "ibufg";
#my $bufg = "CLK_BUFG";
#my $ibufg = "CLK_IBUFG";
my $clk_buf = "clk_buf";
my $clk = "clk";

my $variables = "$port|$bufg|$ibufg|$clk_buf|$clk";

my $file;            # contents of the xdl file

#
# 1. Read the file in
#
while (<>) {
    $file .= $_;
}

#
# 2. Modify contents
#

# 2.a) Move sinks of clk_TMR1 and clk_TMR2 to clk_TMR0
my @net;             # List of the three nets clk_TMR[0,1,2]
my @clk_outpin;      # outpin from clk

# Get the contents of each clk net (clk_TMR0, clk_TMR1, and clk_TMR2)
for (0..2) {
    my $i = $_;

    # Find the keyword "net" at the beginning of a command followed by
    # the name of the clk with the appropriate TMR suffix. Save the
    # outpin and the rest of the net.
    if ($file =~ m/;[ \n]*net[^;]*?$clk$suffix${i}[^;]*?(outpin[^;]*?,)(.*?);/s) {
	$clk_outpin[$i] = $1;
	$net[$i] = $2;
    } else {
	die "Unable to find net $clk$suffix$i. Exiting...";
    }
}
$_ = $file;

# Find clk_TMR0 and replace the entire net declaration with the
# concatenation of all three nets: clk_TMR0, clk_TMR1, and clk_TMR2
# (with only one outpin).
s/(;[ \n]*)net[^;]*?$clk${suffix}0.*?;/$1net "$clk" ,\n  $clk_outpin[0]\n$net[0]\n$net[1]\n$net[2]\n  ;\n/s or warn;

# 2.b) Rename all offending nets and inst's in domain 0
s/($variables)($suffix|$inst_suffix)0/$1/gs or warn;

# 2.c) Delete all offending nets and inst's in domains 1 and 2.  Also
# remove inpins bufg_instanceTMR1 and bufg_instanceTMR2 from clk_bufg
s/(inpin|inst|net)[ ]*?"($variables)($suffix|$inst_suffix)[12].*?;//gs or warn; #"

#
# 3. Write results to stdout
#
print;

