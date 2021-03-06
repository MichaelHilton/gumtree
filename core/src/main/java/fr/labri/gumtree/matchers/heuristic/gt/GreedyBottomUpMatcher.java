package fr.labri.gumtree.matchers.heuristic.gt;

import static fr.labri.gumtree.tree.TreeUtils.postOrder;
import static fr.labri.gumtree.tree.TreeUtils.removeMatched;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.labri.gumtree.matchers.Mapping;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactory;
import fr.labri.gumtree.matchers.optimal.rted.RtedMatcher;
import fr.labri.gumtree.tree.Tree;
import fr.labri.gumtree.tree.TreeUtils;

/**
 * Match the nodes using a bottom-up approach. It browse the nodes of the source and destination trees
 * using a post-order traversal, testing if the two selected trees might be mapped. The two trees are mapped 
 * if they are mappable and have a dice coefficient greater than SIM_THRESHOLD. Whenever two trees are mapped
 * a exact ZS algorithm is applied to look to possibly forgotten nodes.
 */
public class GreedyBottomUpMatcher extends Matcher {

	private static final double SIM_THRESHOLD = 0.50D;
	
	private static final int SIZE_THESHOLD = 200;

	private Map<Integer, Tree> srcIds = new HashMap<Integer, Tree>();
	
	private Map<Integer, Tree> dstIds = new HashMap<Integer, Tree>();
	
	public GreedyBottomUpMatcher(Tree src, Tree dst) {
		super(src, dst);
	}
	
	public void match() {
		List<Tree> srcs = postOrder(src);
		List<Tree> dsts = postOrder(dst);
		for (Tree t : srcs) srcIds.put(t.getId(), t);
		for (Tree t : dsts) dstIds.put(t.getId(), t);
		match(TreeUtils.removeMapped(srcs), TreeUtils.removeMapped(dsts));
		clean();
	}

	private void match(List<Tree> poSrc, List<Tree> poDst) {
		for (Tree src: poSrc)  {
			for (Tree dst: poDst) {
				if (src.isMatchable(dst) && !(src.isLeaf() || dst.isLeaf())) {
					double sim = jaccardSimilarity(src, dst);
					if (sim >= SIM_THRESHOLD || (src.isRoot() && dst.isRoot()) ) {
						if (!(src.areDescendantsMatched() || dst.areDescendantsMatched())) lastChanceMatch(src, dst);
						addMapping(src, dst);
						break;
					}
				}
			}
		}
	}

	//FIXME checks if it is better or not to remove the already found mappings.
	private void lastChanceMatch(Tree src, Tree dst) {
		Tree cSrc = removeMatched(src.deepCopy());
		Tree cDst = removeMatched(dst.deepCopy());
		if (cSrc.getSize() < SIZE_THESHOLD && cDst.getSize() < SIZE_THESHOLD) {
			Matcher m = new RtedMatcher(cSrc, cDst);
			for (Mapping candidate: m.getMappings()) {
				Tree left = srcIds.get(candidate.getFirst().getId());
				Tree right = dstIds.get(candidate.getSecond().getId());
				if (left.getId() == src.getId() || right.getId() == dst.getId()) {
					continue;
				} else if (left.isMatched() && right.isMatched()) {
					continue;
				} else if (!left.isMatchable(right)) {
					continue;
				} else if (left.getParent().getType() != right.getParent().getType()) {
					continue;
				} else addMapping(left, right);
			}
			
			for(Tree t : cSrc.getTrees()) srcIds.get(t.getId()).setMatched(true);
			for(Tree t : cDst.getTrees()) dstIds.get(t.getId()).setMatched(true);
		}

	}
	
	public static class GreedyBottumUpMatcherFactory implements MatcherFactory {

		@Override
		public Matcher newMatcher(Tree src, Tree dst) {
			return new GreedyBottomUpMatcher(src, dst);
		}
		
	}

}
