package edu.byu.ece.edif.util.graph;

import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import edu.byu.ece.edif.core.EdifCellInstance;
import edu.byu.ece.edif.core.EdifNet;
import edu.byu.ece.edif.core.EdifPort;
import edu.byu.ece.edif.core.EdifSingleBitPort;
import edu.byu.ece.edif.core.NamedPropertyObject;
import edu.byu.ece.graph.AbstractGraph;
import edu.byu.ece.graph.AbstractGraphToDotty;
import edu.byu.ece.graph.Edge;

public class CustomGraphToDotty {
    public CustomGraphToDotty() {
        super();

    }

    public static void addColor(StringBuffer sb, Color color) {
        sb.append(" color=\"" + color + "\"");
    }

    public void addEdgeLabel(StringBuffer sb, Edge edge) {
        sb.append("[label=\"" + convertSpecialsToEscapes(edge2Dotty(edge)));
        sb.append("\" " + edgeProperties(edge) + "];\n");
    }

    public static void addEdgeName(StringBuffer sb, String src, String sink) {
        sb.append("\t\"" + src + "\" -> \"" + sink + "\" ");
    }

    public static void addFillColor(StringBuffer sb, Color color) {
        sb.append(" fillcolor=\"" + color + "\"");
    }

    public void addNodeLabel(StringBuffer sb, Object node) {
        sb.append("[label=\"" + node2Dotty(node));
        sb.append("\" " + nodeProperties(node) + "];\n");
    }

    public void addNodeLabel(StringBuffer sb, Object node, String color, String shape) {
        sb.append("[label=\"" + node2Dotty(node));
        sb.append("\" " + nodeProperties(node, color, shape) + " fontcolor=\"white\" style=\"filled\"" + "];\n");
    }

    public static void addNodeName(StringBuffer sb, String nodeName) {
        sb.append("\t\"" + nodeName + "\" ");
    }

    public static void addDefaultNodeStyle(StringBuffer sb, Color color, Shape shape) {
        sb.append("node [color=" + color + ", shape=" + shape + "];\n");
    }

    public static void addRank(StringBuffer sb, Collection<String> list) {
        if (!list.isEmpty()) {
            sb.append("{ rank=same; ");
            for (String name : list)
                sb.append("\"" + name + "\"; ");
            sb.append("}\n");
        }
    }

    public static void addStyle(StringBuffer sb, Style style) {
        sb.append(" style=\"" + style + "\"");
    }

    public String createColoredDottyBody(AbstractGraph graph, Collection<Collection> nodeCollections) {
        StringBuffer sb = new StringBuffer();
        sb.append(header());
        int nodecnt = 0;
        _nodeMap = new LinkedHashMap<Object, String>();
        int colorCount = 0;
        String[] colors = { "blue", "red", "green", "yellow", "black", "brown", "purple", "cyan" };
        String color = "black", shape = "circle";

        Collection blackNodes = graph.getNodes();

        // Color the nodes in each collection, one color per collection
        for (Collection nodeCollection : nodeCollections) {

            // Color all nodes in this collection with the same color
            for (Object node : nodeCollection) {
                // Remove from blackNodes Collection
                blackNodes.remove(node);

                String nodeName = "Node" + nodecnt++;
                addNodeName(sb, nodeName);
                if (node instanceof EdifCellInstance)
                    addNodeLabel(sb, ((EdifCellInstance) node).getName() + "\n("
                            + ((EdifCellInstance) node).getCellType().getName() + ")", colors[colorCount], shape);
                else
                    addNodeLabel(sb, node.toString(), colors[colorCount], shape);
                _nodeMap.put(node, nodeName);
            }

            // Move on to next color (wrap around)
            colorCount = (colorCount + 1) % colors.length;
        }

        // Do other nodes
        color = "black";
        for (Object node : blackNodes) {
            String nodeName = "Node" + nodecnt++;
            addNodeName(sb, nodeName);
            if (node instanceof EdifCellInstance)
                addNodeLabel(sb, ((EdifCellInstance) node).getName() + "\n("
                        + ((EdifCellInstance) node).getCellType().getName() + ")", color, shape);
            else
                addNodeLabel(sb, node.toString(), color, shape);
            _nodeMap.put(node, nodeName);
        }

        sb.append(writeDottyEdges(graph));

        sb.append(footer());
        return sb.toString();
    }

    public String createDottyBody(AbstractGraph graph, Map<EdifNet, Set<EdifCellInstance>> eciMap) {
        StringBuffer sb = new StringBuffer();
        sb.append(header());
        int nodecnt = 0;
        _nodeMap = new LinkedHashMap<Object, String>();
        Map<Set<EdifNet>, String> colorMap = new LinkedHashMap<Set<EdifNet>, String>();
        int colorCount = 0;
        String[] colors = { "blue", "red", "green", "yellow", "black", "brown", "purple", "cyan" };
        String color = "", shape = "";

        for (Object node : graph.getNodes()) {

            // get domain(s) of ECIs
            Set<EdifNet> clkSet = new LinkedHashSet<EdifNet>();
            for (EdifNet n : eciMap.keySet()) {
                if (eciMap.get(n).contains(node))
                    clkSet.add(n);
            }
            if (colorMap.get(clkSet) == null)
                colorMap.put(clkSet, colors[colorCount++]);
            color = colorMap.get(clkSet);
            if (((EdifCellInstance) node).getCellType().getName().toLowerCase().startsWith("fd"))
                shape = "box";
            else
                shape = "circle";

            String nodeName = "Node" + nodecnt++;
            addNodeName(sb, nodeName);
            addNodeLabel(sb, ((EdifCellInstance) node).getName() + "\n("
                    + ((EdifCellInstance) node).getCellType().getName() + ")", color, shape);
            _nodeMap.put(node, nodeName);
        }
        String keyNode = "";
        for (Set<EdifNet> s : colorMap.keySet()) {
            keyNode += colorMap.get(s) + " = " + s.toString() + "\n";
        }
        addNodeName(sb, "key");
        addNodeLabel(sb, keyNode, "black", "box");
        //_nodeMap.put(node, nodeName);
        sb.append(writeDottyEdges(graph));

        sb.append(footer());
        return sb.toString();
    }

    public String createDottyBody(AbstractGraph graph) {
        StringBuffer sb = new StringBuffer();
        sb.append(header());
        int nodecnt = 0;
        _nodeMap = new LinkedHashMap<Object, String>();

        for (Object node : graph.getNodes()) {
            String nodeName = "Node" + nodecnt++;
            addNodeName(sb, nodeName);
            String shape = "circle";
            String color = "black";
            if (node instanceof EdifCellInstance) {
                EdifCellInstance eci = (EdifCellInstance) node;
                String lcName = eci.getName().toLowerCase();
                
                
                if (lcName.startsWith("fd"))
                    shape = "box";
                else if (lcName.contains("voter"))
                    shape = "triangle";
                else if (lcName.contains("_comparator_"))
                	shape = "invtriangle";
                
                if (lcName.contains("_tmr_0") || lcName.contains("voter0") || lcName.contains("_tmr0") || lcName.contains("voter_0"))
                    color = "orangered";
                else if (lcName.contains("_tmr_1") || lcName.contains("voter1") || lcName.contains("_tmr1") || lcName.contains("voter_1"))
                    color = "yellow";
                else if (lcName.contains("_tmr_2") || lcName.contains("voter2") || lcName.contains("_tmr2") || lcName.contains("voter_2"))
                    color = "royalblue";
                
                else if (lcName.contains("_dwc0") || lcName.contains("_dwc_0"))
                	color = "green";
                else if (lcName.contains("_dwc1") || lcName.contains("_dwc_1"))
                	color = "purple";
                	
            }
            else if (node instanceof EdifSingleBitPort) {
                shape = "oval";
                EdifSingleBitPort esbp = (EdifSingleBitPort) node;
                EdifPort port = esbp.getParent();
                String lcName = port.getName().toLowerCase();
                if (lcName.contains("_tmr_0") || lcName.contains("_tmr0"))
                    color = "orangered";
                else if (lcName.contains("_tmr_1") || lcName.contains("_tmr1"))
                    color = "yellow";
                else if (lcName.contains("_tmr_2") || lcName.contains("_tmr2"))
                    color = "royalblue";
                
                else if (lcName.contains("_dwc0") || lcName.contains("_dwc_0"))
                	color = "green";
                else if (lcName.contains("_dwc1") || lcName.contains("_dwc_1"))
                	color = "purple";
            }
            addNodeLabel(sb, node, color, shape);
 
            _nodeMap.put(node, nodeName);
        }
        sb.append(writeDottyEdges(graph));

        sb.append(footer());
        return sb.toString();
    }

    public static String convertSpecialsToEscapes(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
            case '\n':
                sb.append("\\n");
                break;
            case '\t':
                sb.append("\\t");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\"':
                sb.append("\\\"");
                break;
            case '\'':
                sb.append("\\\'");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public String edge2Dotty(Edge edge) {
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    public String edgeProperties(Edge edge) {
        String retval = "";
        return retval;
    }

    public String footer() {
        StringBuffer sb = new StringBuffer();

        sb.append("}\n");

        return sb.toString();
    }

    public static void graphToDotty(AbstractGraph graph, String filename) {
        String data = new AbstractGraphToDotty().createDottyBody(graph);
        printFile(filename, data);
    }

    public String header() {
        StringBuffer sb = new StringBuffer();
        sb.append("digraph EDIF {\n");
        sb.append("\tsize=\"8,11\"\n");
        sb.append("\t// Vertices\n");
        return sb.toString();
    }

    public String node2Dotty(Object node) {
        StringBuffer sb = new StringBuffer();

        if (node instanceof EdifCellInstance) {
            EdifCellInstance eci = (EdifCellInstance) node;
            
            sb.append(convertSpecialsToEscapes(eci.getType().toString()));
        } else if (node instanceof NamedPropertyObject) {
            sb.append("[" + convertSpecialsToEscapes(((NamedPropertyObject) node).getOldName().trim()) + "]");
        } else if (node instanceof Collection) {
            Collection set = (Collection) node;
            sb.append(convertSpecialsToEscapes(set.toString().trim()));
        } else if (node instanceof Integer) {
            sb.append(((Integer) node).intValue());
        } else if (node instanceof EdifSingleBitPort) {
            EdifSingleBitPort esbp = (EdifSingleBitPort) node;
            sb.append(convertSpecialsToEscapes(esbp.getParent().getOldName()));
            sb.append(convertSpecialsToEscapes("["));
            sb.append(convertSpecialsToEscapes(Integer.toString(esbp.bitPosition())));
            sb.append(convertSpecialsToEscapes("]"));
        } else {
        
        
            sb.append(convertSpecialsToEscapes(node.toString()));
        }

        return sb.toString();
    }

    public String nodeProperties(Object node, String color, String shape) {
        StringBuffer sb = new StringBuffer();
        sb.append("color=\"" + color + "\" shape=\"" + shape + "\"");
        return sb.toString();
    }

    public String nodeProperties(Object node) {
        StringBuffer sb = new StringBuffer();
        addColor(sb, Color.black);
        return sb.toString();
    }

    public static void printFile(String filename, String data) {
        try {
            FileWriter fw = new FileWriter(filename);
            fw.write(data);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public String writeDottyEdges(AbstractGraph graph) {
        StringBuffer sb = new StringBuffer();

        sb.append("\t// Edges\n");
        for (Edge edge : graph.getEdges()) {
            Object src = edge.getSource();
            Object sink = edge.getSink();

            addEdgeName(sb, _nodeMap.get(src), _nodeMap.get(sink));
            addEdgeLabel(sb, edge);
        }

        return sb.toString();
    }

    public enum Color {
        /*
         * whites
         */
        anitquewhite1, anitquewhite2, anitquewhite3, anitquewhite4, azure1, azure2, azure3, azure4, bisque1, bisque2, bisque3, bisque4, cornsilk1, cornsilk2, cornsilk3, cornsilk4, honeydew1, honeydew2, honeydew3, honeydew4, ivory1, ivory2, ivory3, ivory4, lavenderblush1, lavenderblush2, lavenderblush3, lavenderblush4, lemonchiffon1, lemonchiffon2, lemonchiffon3, lemonchiffon4, mistyrose1, mistyrose2, mistyrose3, mistyrose4, navajowhite1, navajowhite2, navajowhite3, navajowhite4, peachpuff1, peachpuff2, peachpuff3, peachpuff4, seashell1, seashell2, seashell3, seashell4, snow1, snow2, snow3, snow4, thistle1, thistle2, thistle3, thistle4, wheat1, wheat2, wheat3, wheat4, blanchedalmond, floralwhite, lavender, linen, mintcream, moccasin, oldlace, papayawhip, white, whitesmoke,

        /*
         * greys - not included: gray[0-100]
         */
        darkslategray1, darkslategray2, darkslategray3, darkslategray4, slategray1, slategray2, slategray3, slategray4, dimgray, gray, lightgray, lightslategray,

        /*
         * blacks
         */
        black,

        /*
         * reds
         */
        coral1, coral2, coral3, coral4, deeppink1, deeppink2, deeppink3, deeppink4, firebrick1, firebrick2, firebrick3, firebrick4, hotpink1, hotpink2, hotpink3, hotpink4, inianred1, inianred2, inianred3, inianred4, lightpink1, lightpink2, lightpink3, lightpink4, lightsalmon1, lightsalmon2, lightsalmon3, lightsalmon4, maroon1, maroon2, maroon3, maroon4, orangered1, orangered2, orangered3, orangered4, palevioletred1, palevioletred2, palevioletred3, palevioletred4, pink1, pink2, pink3, pink4, red1, red2, red3, red4, salmon1, salmon2, salmon3, salmon4, tomato1, tomato2, tomato3, tomato4, violetred1, violetred2, violetred3, violetred4, crimson, darksalmon, mediumvioletred,

        /*
         * browns
         */
        brown1, brown2, brown3, brown4, burlywood1, burlywood2, burlywood3, burlywood4, chocolate1, chocolate2, chocolate3, chocolate4, khaki1, khaki2, khaki3, khaki4, rosybrown1, rosybrown2, rosybrown3, rosybrown4, sienna1, sienna2, sienna3, sienna4, tan1, tan2, tan3, tan4, beige, darkkhaki, peru, saddlebrown, sandybrown,

        /*
         * oranges
         */
        darkorange1, darkorange2, darkorange3, darkorange4, orange1, orange2, orange3, orange4,
        /* orangered1, orangered2, orangered3, orangered4, */

        /*
         * yellows
         */
        darkgoldenrod1, darkgoldenrod2, darkgoldenrod3, darkgoldenrod4, gold1, gold2, gold3, gold4, goldenrod1, goldenrod2, goldenrod3, goldenrod4, lightgoldenrod1, lightgoldenrod2, lightgoldenrod3, lightgoldenrod4, lightyellow1, lightyellow2, lightyellow3, lightyellow4, yellow1, yellow2, yellow3, yellow4, greenyellow, lightgoldenrodyellow, palegoldenrod, yellowgreen,

        /*
         * greens
         */
        chartruse1, chartruse2, chartruse3, chartruse4, darkolivegreen1, darkolivegreen2, darkolivegreen3, darkolivegreen4, darkseagreen1, darkseagreen2, darkseagreen3, darkseagreen4, green1, green2, green3, green4, olivedrab1, olivedrab2, olivedrab3, olivedrab4, palegreen1, palegreen2, palegreen3, palegreen4, seagreen1, seagreen2, seagreen3, seagreen4, springgreen1, springgreen2, springgreen3, springgreen4, darkgreen, forestgreen, /* greenyellow, */lawngreen, lightseagreen, limegreen, mediumseagreen, mediumspringgreen, /*
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 * mintcream,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 * yellowgreen,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 */

        /*
         * cyans
         */
        aquamarine1, aquamarine2, aquamarine3, aquamarine4, cyan1, cyan2, cyan3, cyan4, lightcyan1, lightcyan2, lightcyan3, lightcyan4, paleturquoise1, paleturquoise2, paleturquoise3, paleturquoise4, turquoise1, turquoise2, turquoise3, turquoise4, darkturquoise, mediumaquamarine, mediumturquoise,

        /*
         * blues
         */
        blue1, blue2, blue3, blue4, cadetblue1, cadetblue2, cadetblue3, cadetblue4, deepskyblue1, deepskyblue2, deepskyblue3, deepskyblue4, dodgerblue1, dodgerblue2, dodgerblue3, dodgerblue4, lightblue1, lightblue2, lightblue3, lightblue4, lightskyblue1, lightskyblue2, lightskyblue3, lightskyblue4, lightslateblue1, lightslateblue2, lightslateblue3, lightslateblue4, royalblue1, royalblue2, royalblue3, royalblue4, skyblue1, skyblue2, skyblue3, skyblue4, slateblue1, slateblue2, slateblue3, slateblue4, steelblue1, steelblue2, steelblue3, steelblue4, aliceblue, blueviolet, cornflowerblue, darkslateblue, indigo, mediumblue, mediumslateblue, midnightblue, navy, navyblue, powerblue,

        /*
         * magentas
         */
        darkorchid1, darkorchid2, darkorchid3, darkorchid4, magenta1, magenta2, magenta3, magenta4, mediumorchid1, mediumorchid2, mediumorchid3, mediumorchid4, mediumpurple1, mediumpurple2, mediumpurple3, mediumpurple4, orchid1, orchid2, orchid3, orchid4,
        /* palevioletred1, palevioletred2, palevioletred3, palevioletred4, */
        plum1, plum2, plum3, plum4, purple1, purple2, purple3, purple4,
        /* violetred1, violetred2, violetred3, violetred4, */
        /* blueviolet, */darkviolet, /* mediumvioletred, */violet
    }

    public enum ArrowHead {
        normal, dot, odot, inv, invdot, invodot, none
    }

    /*
     * the word "double" or "triple" can be prepended to most of these shapes.
     * records are not included
     */
    public enum Shape {
        box, polygon, ellipse, circle, point, egg, triangle, plaintext, diamond, trapezium, parallelogram, house, hexagon, octagon, invtriangle, invtrapezium, invhouse, Mdiamond, Msquare, Mcircle,
    }

    /*
     * incomplete
     */
    public enum Style {
        bold, dotted, filled
    }

    protected HashMap<Object, String> _nodeMap;
}
