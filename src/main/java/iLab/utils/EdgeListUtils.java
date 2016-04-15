package main.java.iLab.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.cxio.aspects.datamodels.EdgeAttributesElement;
import org.cxio.aspects.datamodels.EdgesElement;
import org.cxio.aspects.datamodels.NodeAttributesElement;
import org.cxio.aspects.datamodels.NodesElement;
import org.cxio.aspects.readers.EdgeAttributesFragmentReader;
import org.cxio.aspects.readers.EdgesFragmentReader;
import org.cxio.aspects.readers.NodeAttributesFragmentReader;
import org.cxio.aspects.readers.NodesFragmentReader;
import org.cxio.core.CxElementReader;
import org.cxio.core.CxWriter;
import org.cxio.core.interfaces.AspectElement;
import org.cxio.core.interfaces.AspectFragmentReader;
import org.cxio.metadata.MetaDataCollection;
import org.cxio.misc.AspectElementCounts;
import org.cxio.tools.BasicTable;
import org.cxio.tools.BasicTableParser;

import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import com.jmatio.types.MLSparse;

/**
 * This class is for simple conversions from text resources to edge lists / cx resources.
 * @author Mark Teixeira <mteixeira@ucsd.edu>
 */

public final class EdgeListUtils {
	
	private static final char   COLUMN_DELIMITER = '\t';
    private static final String WEIGHT           = "weight";
    private static final String NAME             = "name";
    
    /**
	 * This is a utility routine to generate network file resources.
	 * This routine is usually commented-out, and only run when new resources need to be generated.
	 */
	public static void convertEdgelistFiles(Boolean outputEdgelist) throws IOException {
		
		/**
		 * CSV - an existing import file of edges defined by positions relating to MAP
		 * OUT - using CSV and MAP construct a new entrez-based import file (also of type CSV)
		 * SRC - use CSV to create an associated positional based cx file (used by NBS)
		 * MAP - a list of entrez id's in positions used by CSV
		 * DST - the ultimate file created by a cx network and a position map.
		 * 
		 * NOTE:
		 * these routines are highly dependent on the inclusion of a mapping file, hence positional elements.
		 * this is NOT optimal, but compatible with the current NBS implementation.
		 */
		
		final String csv = "C:\\Users\\Master\\Desktop\\UCSD\\nbs\\inputs\\HN90_edgelist_trim_index.csv";
		final String out = "C:\\Users\\Master\\Desktop\\UCSD\\nbs\\inputs\\HN90_edgelist_trim_entrez.csv";
		final String src = "C:\\Users\\Master\\Desktop\\UCSD\\nbs\\outputs\\HN90_edgelist_trim_entrez.cx";
		final String map = "C:\\Users\\Master\\Desktop\\UCSD\\nbs\\inputs\\HN90_entrez_keylist.txt";
		final String dst = "C:\\Users\\Master\\Dropbox\\MATLAB\\HN90_edgelist_entrez.mat";
		
//		final String out = "C:\\Users\\Master\\Desktop\\UCSD\\nbs\\inputs\\HN90_edgelist_trim_geneId.csv";
//		final String src = "C:\\Users\\Master\\Desktop\\UCSD\\nbs\\outputs\\HN90_edgelist_trim_geneId.cx";
//		final String dst = "C:\\Users\\Master\\Dropbox\\MATLAB\\HN90_edgelist_trim_geneId.mat";

		if( outputEdgelist ) {
			EdgeListUtils.IndexToEntrezMat(csv, map, out);
			EdgeListUtils.EdgelistToCxFile(out, src);
			EdgeListUtils.EdgeListToMatlab(src, dst);
		}
	}

	/**
	 * IndexToEntrezMat - 
	 * The original HN90 matrix is comprised of edges of index positions.
	 * These index positions point to actual entrez id's in another array.
	 * Substitute the index positions with the actual values they point to.
	 * 
	 * @throws IOException
	 */
	public static void IndexToEntrezMat(final String src, final String map, final String dst) throws IOException {
		
		final BasicTable<String> t = BasicTableParser.parse(new File(src), COLUMN_DELIMITER);
        final BasicTable<String> u = BasicTableParser.parse(new File(map), COLUMN_DELIMITER);
        final BufferedWriter 	 w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(dst)), "utf-8"));
       
        //	substitute entrez id's from 'u' for index positions in 't' (t currently holds index positions into u)
        
        for (int r = 0; r < t.getNumberOfRows(); ++r) {
        	
        	//	substitute...
        	
        	t.setValue(0, r, 
        			u.getValue(0, Integer.parseInt(
        					t.getValue(0, r))-1));
        	t.setValue(1, r,
        			u.getValue(0, Integer.parseInt(
        					t.getValue(1, r))-1));
           
        	//	write line to new output file...
        	
            String line = 	t.getValue(0, r) + COLUMN_DELIMITER +
            				t.getValue(1, r) + COLUMN_DELIMITER +
            				t.getValue(2, r);
            
            line += System.getProperty("line.separator");
            w.write(line);
        }
        w.close();
	 }
	
	 /**
	 * EdgelistToCxFile - 
	 * Generate a CX file from a tab-separated input file of edges.
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public static void EdgelistToCxFile(final String src, final String dst) throws IOException {

//        final File srcFile = new File(src);
//        final File dstFile = new File(dst);
//        
//        final BasicTable<String> t = BasicTableParser.parse(srcFile, COLUMN_DELIMITER);
//        final SortedMap<String, Integer> node_to_id = new TreeMap<String, Integer>();
//        final SortedMap<Integer, String> id_to_node = new TreeMap<Integer, String>();
//
//        int node_counter = 0;
//        for (int r = 0; r < t.getNumberOfRows(); ++r) {
//            final String node_0 = t.getValue(0, r);
//            final String node_1 = t.getValue(1, r);
//
//            if (!node_to_id.containsKey(node_0)) {
//                node_to_id.put(node_0, node_counter);
//                id_to_node.put(node_counter, node_0);
//                ++node_counter;
//            }
//            if (!node_to_id.containsKey(node_1)) {
//                node_to_id.put(node_1, node_counter);
//                id_to_node.put(node_counter, node_1);
//                ++node_counter;
//            }
//        }
//
//        final List<AspectElement> cx_nodes = new ArrayList<AspectElement>();
//        final List<AspectElement> cx_edges = new ArrayList<AspectElement>();
//        final List<AspectElement> cx_node_attributes = new ArrayList<AspectElement>();
//        final List<AspectElement> cx_edge_attributes = new ArrayList<AspectElement>();
//
//        for (final Entry<Integer, String> e : id_to_node.entrySet()) {
//            cx_nodes.add(new NodesElement(e.getKey()));
//            cx_node_attributes.add(new NodeAttributesElement(String.valueOf(e.getKey()), NAME, e.getValue()));
//        }
//
//        for (int r = 0; r < t.getNumberOfRows(); ++r) {
//            cx_edges.add(new EdgesElement(r, node_to_id.get(t.getValue(0, r)), node_to_id.get(t.getValue(1, r))));
//            cx_edge_attributes.add(new EdgeAttributesElement(String.valueOf(r), WEIGHT, t.getValue(2, r)));
//        }
//
//        final OutputStream out = new FileOutputStream(dstFile);
//        final CxWriter w = CxWriter.createInstanceWithAllAvailableWriters(out, true, true);
//
//        w.start();
//        w.writeAspectElements(cx_nodes);
//        w.writeAspectElements(cx_edges);
//        w.writeAspectElements(cx_node_attributes);
//        w.writeAspectElements(cx_edge_attributes);
//        w.end();
//
//        out.close();

    }
	
	/**
	 * EdgeListToMatlab - 
	 * Convert an edgeList (cx) file to compatible format for NBS (Matlab)
	 * @param inFileName
	 * @param outMatName
	 * @throws IOException
	 */
	public static void EdgeListToMatlab(final String src, final String dst) throws IOException {
		
		/**
		 * Create the parts of the Matlab output file...
		 */
		ArrayList<MLArray> list = cxToMatlab(src);
		new MatFileWriter(dst, list);
	}
	
	/**
	 * cxToMatlabSparseMatrix - 
	 * from a previously generated cx edge file, create a compatible Matlab sparse array of edges.
	 * @param src - a path to parsed cx file
	 * @return - MLSparse packed sparse double 
	 * @throws IOException
	 */
	private static ArrayList<MLArray> cxToMatlab(final String src) throws IOException {
		
        final CxElementReader reader = CxElementReader.createInstanceWithAllAvailableReaders(new File(src), true, true);
        final SortedMap<String, List<AspectElement>> map = CxElementReader.parseAsMap(reader);
		final List<AspectElement> edgeElements = map.get(EdgesElement.ASPECT_NAME);
		final List<AspectElement> edgeAttribute = map.get(EdgeAttributesElement.ASPECT_NAME);
		final List<AspectElement> nodeAttribute = map.get(NodesElement.ASPECT_NAME);
		
		if( edgeElements.size() != edgeAttribute.size() ) {
			throw new IllegalArgumentException("Illegal cx Map!");
		}	
	
		/**
		 * Create the parts of the Matlab output file...
		 * This is the sparse matrix of edges - 2 edge index values + an attribute (float).
		 */
		final int s1 = nodeAttribute.size();
		final int s2 = edgeElements.size();
		MLSparse mlSparse = new MLSparse("norm_adj_mat", new int[] {s1, s1}, MLArray.mtFLAG_GLOBAL, s2);

		for (int i = 0; i < edgeElements.size(); i++) {
	        final EdgesElement ee = (EdgesElement) edgeElements.get(i);
	        final int s = (int) ee.getSource();
	        final int t = (int) ee.getTarget();
	        final long id = ee.getId();
	        final EdgeAttributesElement ea = (EdgeAttributesElement) edgeAttribute.get((int) id);
	        if ( ea == null ) {
	        	throw new IOException("no edge attribute for " + id );
	        }
	        
	        // This was incorrect:
	        //final EdgeAttributesElement ea = (EdgeAttributesElement) edgeAttribute.get(i);
	        final double d = Double.parseDouble(ea.getValue());
	        mlSparse.setReal(d, s, t);
		}
		
		/**
		 * each edge index from above points to a node which contains a gene symbol or ID.
		 * This is the id.
		 */
		final double values[] = new double[nodeAttribute.size()];
		for (int i = 0; i < nodeAttribute.size(); i++) {
			final NodesElement na = (NodesElement) nodeAttribute.get(i);
			values[i] = Integer.parseInt(na.getNodeName());
		}
		
		/**
		 * Add the parts to the Matlab output file...
		 */
		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add(mlSparse);		
		list.add(new MLDouble("entrezKey", values, nodeAttribute.size()));
		
		return list;
	}
}