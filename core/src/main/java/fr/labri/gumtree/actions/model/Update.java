package fr.labri.gumtree.actions.model;

import fr.labri.gumtree.tree.Tree;

public class Update extends Action {
	
	private String value;
	
	public Update(Tree node, String value) {
		super(node);
		this.value = value;
	}

	@Override
	public String getName() {
		return "UPD";
	}

	@Override
	public String toString() {
		return getName() + " " + node.toString() + " from " + node.getLabel() + " to " + value;
	}

}
