package fr.labri.gumtree.actions.model;

import fr.labri.gumtree.tree.Tree;

public class Delete extends Action {

	public Delete(Tree node) {
		super(node);
	}

	@Override
	public String getName() {
		return "DEL";
	}

	@Override
	public String toString() {
		return getName() + " " + node.toString();
	}

}
