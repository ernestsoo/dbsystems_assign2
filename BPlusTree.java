


import java.io.*;
import java.util.*;


public class BPlusTree {
	int fanout;

	Nodes.InnerNode root;
	Nodes.LeafNode fLeafNode;

	ArrayList<Nodes.Node> layers;
	ArrayList<String> treeString;

	Nodes n;

	public BPlusTree() {
		// Harded coded fanout for the purpose of this assignment.
		this.fanout = 4;
		this.root = null;

		layers = new ArrayList<Nodes.Node>();
		treeString = new ArrayList<String>();
		n = new Nodes();
	}


	// recursively get first node of each layer.
	public void getNextNode(Nodes.Node nextNode){
		layers.add(nextNode);

		if(nextNode instanceof Nodes.InnerNode){
			Nodes.InnerNode inNextNode = (Nodes.InnerNode)nextNode;
			if(inNextNode.pointers[0] != null){
				getNextNode(inNextNode.pointers[0]);
			}
		} else{
			System.out.println("Ended:"+layers.size());

			setIndexes();
			printTree();
		}
	}

	// set page index for each node.
	public void setIndexes(){
		int index=0;
		boolean ended = false;
		long counter = 1;

		Nodes.Node currentNode = layers.get(index);

		while(ended==false){
			// Set order in index file.
			currentNode.setIndex(counter);

			Nodes.InnerNode iNode;
			Nodes.LeafNode lNode;

			if (currentNode instanceof Nodes.InnerNode){
				iNode = (Nodes.InnerNode)currentNode;

				if(iNode.rightNode != null){
					currentNode = iNode.rightNode;
				} else {
					// Go to next layer.
					index++;
					currentNode = layers.get(index);
				}
			} else {
				lNode = (Nodes.LeafNode)currentNode;
				
				if(lNode.rightNode != null){
					currentNode = lNode.rightNode;
				} else {
					ended = true;
					//System.out.println(currentNode.getIndex());
					//System.out.println("REACHED LEAF!");
					//System.out.println("EXPLORED:" + counter);
				}
			}
			counter++; 
		}		
	}

	// traverse entire tree to be printed.
	public void printTree(){
		int index=0;
		boolean ended = false;
		//long counter = 1;


		Nodes.Node currentNode = layers.get(index);

		while(ended==false){
			// Set order in index file.
			//currentNode.setIndex(counter);

			String keys = "";
			String pointers = "";

			Nodes.InnerNode iNode;
			Nodes.LeafNode lNode;
			
			if (currentNode instanceof Nodes.InnerNode){
				iNode = (Nodes.InnerNode)currentNode;

			
				//System.out.println(iNode.getIndex());

				for (int i=0; i<iNode.keys.length - 1; i++){
					if(iNode.keys[i]!= null){
						keys += String.valueOf(iNode.keys[i])+",";
					} else{
						keys += ".,";
					}
				}
				if(iNode.keys[iNode.keys.length - 1]!=null){
					keys += String.valueOf(iNode.keys[iNode.keys.length - 1]);
				} else {
					keys += ".";
				}
				

				for (int i=0; i<iNode.pointers.length - 1; i++){
					if(iNode.pointers[i]!= null){
						pointers += String.valueOf(iNode.pointers[i].getIndex())+",";
					} else {
						pointers += ".,";
					}
				}
				if(iNode.pointers[iNode.pointers.length - 1]!= null){
					pointers += String.valueOf(iNode.pointers[iNode.pointers.length - 1].getIndex());
				} else {
					pointers += ".";
				}
				

				if(iNode.rightNode != null){
					currentNode = iNode.rightNode;
				} else {
					// Go to next layer.
					index++;
					currentNode = layers.get(index);
				}
			} else {
				lNode = (Nodes.LeafNode)currentNode;


				for (int i=0; i<lNode.dPointers.length - 1; i++){
					if(lNode.dPointers[i]!=null){
						keys += String.valueOf(lNode.dPointers[i].key)+",";
						pointers += lNode.dPointers[i].value+",";
					} else {
						keys += ".,";
						pointers += ".,";
					}
				}
				if(lNode.dPointers[lNode.dPointers.length - 1]!=null){
					keys += String.valueOf(lNode.dPointers[lNode.dPointers.length - 1].key);
					pointers += lNode.dPointers[lNode.dPointers.length - 1].value;
				} else{
					keys += ".";
					pointers += ".";
				}
				
				if(lNode.rightNode != null){
					currentNode = lNode.rightNode;
				} else {
					ended = true;
					System.out.println(currentNode.getIndex());


					System.out.println("REACHED LEAF!");
					//System.out.println("EXPLORED:" + counter);
				}
			}
			treeString.add(keys+"-"+pointers);
			if (treeString.size() > 9999){
				try{
					TreeWriter sl = new TreeWriter(treeString);
				} catch (IOException e){
					System.out.println("Failed to load part of tree.");
					return;
				}
				treeString.clear();
			}

		}		

		/*
		try{
			load sl = new load(treeString);
		} catch (IOException e){
			System.out.println("Failed to load part of tree.");
			return;
		}	*/
	}

	

	// print index to file after building.
	public void printToFile() {
		Nodes.InnerNode currentNode = this.root;
		layers.add(this.root);

		if (currentNode.pointers[0] != null){
			
			layers.add(currentNode.pointers[0]);
			getNextNode(currentNode.pointers[0]);
		} else {
			System.out.print("Tree is empty!");
		}
	}

	// search for leaf node recursively.
	private Nodes.LeafNode search(long key) {
		Long[] keys = this.root.keys;


		int i;
		for (i = 0; i < this.root.degree - 1; i++) {
			//System.out.println(key)
			//System.out.println(keys[i])
			if (key < keys[i]) { 
				break; 
			}
		}

		Nodes.Node nextNode = this.root.pointers[i];


		if (nextNode instanceof Nodes.LeafNode) {
			//System.out.println("is leaf!")
			return (Nodes.LeafNode)nextNode;
		} else {
			return search((Nodes.InnerNode)nextNode, key);
		}
	}

	// search for leaf node recursively.
	private Nodes.LeafNode search(Nodes.InnerNode node, long key) {

		Long[] keys = node.keys;
		int i;

		for (i = 0; i < node.degree - 1; i++) {
			//System.out.println(key);
			//System.out.println(keys[i]);
			if (key < keys[i]) { 
				break; 
			}
		}

		Nodes.Node nextNode = node.pointers[i];

		if (nextNode instanceof Nodes.LeafNode) {
			//System.out.println("is leaf!");
			return (Nodes.LeafNode)nextNode;
		} else {
			return search((Nodes.InnerNode)node.pointers[i], key);
		}
	}


	// get index of pointer in leaf node.
	private int getPointerIndex(Nodes.Node[] pointers, Nodes.LeafNode node) {
		int i;
		for (i = 0; i < pointers.length; i++) {
			if (pointers[i] == node) { break; }
		}
		return i;
	}
	
	// get index in which node will be split.
	private int getMiddle() {
		return (int)Math.ceil((this.fanout + 1) / 2.0) - 1;
	}


	// split data pointers into half.
	private DataPointer[] splitdPointers(Nodes.LeafNode lNode, int split) {

		DataPointer[] dPointers = lNode.dPointers;

		DataPointer[] halfPointers = new DataPointer[this.fanout];

		for (int i = split; i < dPointers.length; i++) {
			halfPointers[i - split] = dPointers[i];
			lNode.delete(i);
		}

		return halfPointers;
	}

	// split keys into half when splitting nodes.
	private Long[] splitKeys(Long[] keys, int split) {

		Long[] halfKeys = new Long[this.fanout];

		keys[split] = null;


		for (int i = split + 1; i < keys.length; i++) {

			halfKeys[i - split - 1] = keys[i];
			keys[i] = null;

		}

		return halfKeys;
	}

	// split inner node when overfull.
	private void splitInnerNode(Nodes.InnerNode iNode) {

		Nodes.InnerNode parentNode = iNode.parent;

		int split = getMiddle();
		Long newParentKey = iNode.keys[split];

		Long[] halfKeys = splitKeys(iNode.keys, split);

		Nodes.Node[] halfPointers = new Nodes.Node[this.fanout + 1];
		Nodes.Node[] pointers = iNode.pointers;

		for (int i = split + 1; i < pointers.length; i++) {
			halfPointers[i - split - 1] = pointers[i];

			iNode.removePointer(i);
		}



		iNode.resetDegree();

		//System.out.println(iNode.degree);

		Nodes.InnerNode sibling = n.new InnerNode(this.fanout, halfKeys, halfPointers);
		
		for (int i=0; i<halfPointers.length; i++){
			if (halfPointers[i] != null){
				halfPointers[i].parent = sibling;
			}
		}

		sibling.rightNode = iNode.rightNode;

		if (sibling.rightNode != null) {
			sibling.rightNode.leftNode = sibling;
		}

		iNode.rightNode = sibling;
		sibling.leftNode = iNode;

		if (parentNode != null) {
			parentNode.keys[parentNode.degree - 1] = newParentKey;

			Arrays.sort(parentNode.keys, 0, parentNode.degree);

			int pointerIndex = parentNode.getPointerIndex(iNode) + 1;
			parentNode.insertPointer(sibling, pointerIndex);
			sibling.parent = parentNode;

		} else {
			Long[] keys = new Long[this.fanout];
			keys[0] = newParentKey;

			Nodes.InnerNode tempRootNode = n.new InnerNode(this.fanout, keys);

			tempRootNode.appendPointer(iNode);
			tempRootNode.appendPointer(sibling);

			this.root = tempRootNode;
			iNode.parent = tempRootNode;
			sibling.parent = tempRootNode;
		}
	}

	// tuple wise insertion.
	public void insertTuple(long key, String value){
		if (fLeafNode == null) {

			Nodes.LeafNode lNode = n.new LeafNode(this.fanout, new DataPointer(key, value));
			this.fLeafNode = lNode;
		} else {
			Nodes.LeafNode lNode;

			if (this.root != null){
				lNode = search(key);
			} else {
				lNode = this.fLeafNode;
			}
												
			if (!lNode.insert(new DataPointer(key, value))) {

				lNode.dPointers[lNode.numDataPointers] = new DataPointer(key, value);
				lNode.numDataPointers++;

				lNode.sortDataPointersWithNull();

				DataPointer[] halfPointers = splitdPointers(lNode, getMiddle());

				if (lNode.parent == null) {

					Long[] parentKeys = new Long[this.fanout];
					parentKeys[0] = halfPointers[0].key;

					Nodes.InnerNode parent = n.new InnerNode(this.fanout, parentKeys);
					lNode.parent = parent;

					parent.appendPointer(lNode);
				} else {
					Long newParentKey = halfPointers[0].key;

					lNode.parent.keys[lNode.parent.degree - 1] = newParentKey;

					Arrays.sort(lNode.parent.keys, 0, lNode.parent.degree);
				}

				Nodes.LeafNode newLeafNode = n.new LeafNode(this.fanout, halfPointers, lNode.parent);

				int pointerIndex = lNode.parent.getPointerIndex(lNode) + 1;

				lNode.parent.insertPointer(newLeafNode, pointerIndex);

				newLeafNode.rightNode = lNode.rightNode;

				if (newLeafNode.rightNode != null) {
					newLeafNode.rightNode.leftNode = newLeafNode;
				}

				lNode.rightNode = newLeafNode;
				newLeafNode.leftNode = lNode;

				if (this.root == null) {

					this.root = lNode.parent;

				} else {
					Nodes.InnerNode iNode = lNode.parent;
					while (iNode != null) {
						if (iNode.isOverfull()) {
							splitInnerNode(iNode);
						} else {
							break;
						}
						iNode = iNode.parent;
					}
				}
			}
		}
	}

	// linear seach for resulting node.
	public int linearKeySearch(DataPointer[] dPointers, Long key){
		for (int i=0;i < dPointers.length; i++){
			if(dPointers[i] != null){
				if(dPointers[i].key==key){
					return i;
				}
			}
		}
		return -1;
	}
}