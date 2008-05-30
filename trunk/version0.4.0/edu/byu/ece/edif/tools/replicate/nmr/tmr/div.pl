#!/usr/bin/perl

######################################################################
# $Id$
#
# Given a dividend n, an optional divisor d, outputs (to stdout) the
# quotient (n/d), rounded to p decimal places, where p is an optional
# parameter, 2 by default. If a divisor is not specified, a default of
# 100 is used.
#
# Original author: James Carroll <jcarroll@byu.net>
######################################################################

use strict;
use warnings;

my $version = q"$Revision$";
my $date = q"$Date$";

# Print usage information
sub usage(){
    print "Usage: div.pl DIVIDEND [DIVISOR [PRECISION]]\n";
}

# Check for not usage information 
if (@ARGV == 0) {
    usage();
    exit 1;
}

my $n = $ARGV[0];   # dividend (numerator)
my $d = 100;        # divisor  (denominator)
my $p = 2;          # precision

# Optional divisor and optional precision
if (@ARGV > 1) {
    $d = $ARGV[1];

    if (@ARGV > 2) {
	$p = $ARGV[2];
    }
}

# Workaround to prevent DIV_ZERO errors
if ($n == 0) {
    $d = 1;
}

exit printf ("%1.${p}f", $n/$d);
