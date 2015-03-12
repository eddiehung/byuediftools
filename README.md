BYU EDIF Tools is an API for creating, modifying, or analyzing EDIF netlists within the Java programming language. We are currently using this API to analyze EDIF netlists as a part of our FPGA reliability project. We intend to keep the API as general as possible to support other netlist analysis and manipulation activities.

The current release of our EDIF infrastructure includes the following tools:

Java parser to parse pre-generated EDIF netlists
JHDL generator for circuits represented in the EDIF data structure
View EDIF circuit structure
Simulate circuits (EDIF netlists based on Xilinx primitives)
Integrate custom JHDL GUI tools
EDIF merging routines for merging multiple-file EDIF circuits
Primitive library for Xilinx FPGAs
Automated TMR (Triple Modular Redundancy) application
The BYU EDIF Tools are now open source and released under the GNU GPL. We encourage outside use of these tools and encourage outside contributions.

Please visit the project page hosted at http://sourceforge.net/projects/byuediftools/ in order to download and contribute to the tools.

For more information on the BYU-LANL TMR Tool (BL-TMR), please see the BL-TMR User Guide available on the Sourceforge project download page (PDF document).

For a tutorial on building the EDIF tools from source and more information on developing with the tools, please see our Build Instructions page.

You may also be interested in the Javadoc-style EDIF Tools API in order to become familiar with the classes and methods available in the BYU EDIF Tools suite.
