package fr.labri.gumtree.client.ui.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.labri.gumtree.actions.ActionGenerator;
import fr.labri.gumtree.actions.model.Action;
import fr.labri.gumtree.client.DiffClient;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.client.TreeGeneratorRegistry;
import fr.labri.gumtree.io.TreeGenerator;
import fr.labri.gumtree.io.TreeIoUtils;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

public class JSONDiff {

	JSONObject jObj;
	String src;
	String dest;
	String extension;
	File fSrc;
	File fDst;
	
	public JSONDiff(String srcIn, String destIn,String extIn) {
		src = srcIn;
		dest = destIn;
		extension = extIn;
	}

	public void start() {		
		this.fSrc = getTempFileFromString(src);
		this.fDst = getTempFileFromString(dest);
		Tree src = null;
		Tree dst = null;
		try {
			src = TreeGeneratorRegistry.getInstance().getTree(fSrc.getAbsolutePath());
			dst = TreeGeneratorRegistry.getInstance().getTree(fDst.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Matcher matcher = MatcherFactories.newMatcher(src, dst);
		matcher.match();
		ActionGenerator g = new ActionGenerator(src, dst, matcher.getMappings());
		g.generate();
		JSONArray JSONActions = getJSONActions(g.getActions());
		//this.script = g.getActions();
		System.out.println(JSONActions);
	}

    //THIS IS WHERE JSON CAN BE EXPANDED
	private JSONArray getJSONActions(List<Action> actions) {
		JSONArray jsA = new JSONArray();
		for (Action action : actions) { 
			System.out.println(action);
			JSONObject jObj = new JSONObject();
			jObj.put("Action", action.getName());
			Tree n = action.getNode();
			jObj.put("Node", n.getLabel());
			jObj.put("Position",n.getPos());
            jObj.put("TypeLabel",n.getTypeLabel());
            jObj.put("Id", n.getId());
            jObj.put("parentID", n.getParent().getId());
			jsA.add(jObj);
		}
		return jsA;
	}

	private File getTempFileFromString(String fileContents){
		File tmpFile = null;
		try {
			tmpFile = File.createTempFile(UUID.randomUUID().toString(), extension);
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
	        bw.write(fileContents);
	        bw.close();
	        //return fromFile(tmpFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tmpFile;
	}

}
