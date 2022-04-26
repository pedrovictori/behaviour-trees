import behaviour_trees.core.Tree;
import behaviour_trees.core.TreePlanter;

import java.util.Map;

public class Test {
	public static void main(String[] args) {
		TreePlanter treePlanter = new TreePlanter(Test.class.getResourceAsStream("testTree.xml"),
				Map.of("Guard1", Guard1.class,
						"Guard2", Guard2.class,
						"Leaf1", Leaf1.class,
						"Leaf2", Leaf2.class,
						"Leaf3", Leaf3.class));
		Tree tree = treePlanter.plantTree(3);
	}
}