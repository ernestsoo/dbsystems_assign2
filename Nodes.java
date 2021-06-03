
import java.io.*;
import java.util.*;

public class Nodes{

	public class Node {
		InnerNode parent;
		long index;
		public void setIndex(long index){
			this.index=index;
		}
		public long getIndex(){
			return this.index;
		}
	}
	
	public class InnerNode extends Node {
		int degree;
		int maxDegree;
	
		InnerNode leftNode;
		InnerNode rightNode;
	
		Long[] keys;
		Node[] pointers;
	
		public InnerNode(int fanout, Long[] keys) {
			this.maxDegree = fanout;
			this.degree = 0;
			this.keys = keys;
			this.pointers = new Node[this.maxDegree+1];
		}
	
		public InnerNode(int fanout, Long[] keys, Node[] pointers) {
			this.maxDegree = fanout;
			this.keys = keys;
			this.pointers = pointers;
	
			resetDegree();
		}
	
		public void appendPointer(Node pointer) {
			this.pointers[degree] = pointer;
			this.degree++;
		}
		public int getPointerIndex(Node pointer) {
			for (int i = 0; i < pointers.length; i++) {
				if (pointers[i] == pointer) { 
					return i; 
				}
			}
			return -1;
		}
		public void insertPointer(Node pointer, int index) {
			for (int i = degree - 1; i >= index ;i--) {
				pointers[i + 1] = pointers[i];
			}

			this.pointers[index] = pointer;
			this.degree++;
		}
		public void removePointer(int index) {
			this.pointers[index] = null;
			this.degree--;
		}
		public void resetDegree(){
			for (int i = 0; i <  pointers.length; i++) {
				if (pointers[i] == null) { 
					this.degree = i;
					return;
				}
			}
		}
		public boolean isOverfull() {
			return this.degree == maxDegree + 1;
		}
	}
	
	public class LeafNode extends Node {
		int maxDataPointers;
		int numDataPointers;
	
		LeafNode leftNode;
		LeafNode rightNode;
	
		DataPointer[] dPointers;
	
		public LeafNode(int fanout, DataPointer pointer) {
			this.maxDataPointers = fanout - 1;
			this.dPointers = new DataPointer[fanout];
			this.numDataPointers = 0;
	
			this.insert(pointer);
		}
		public LeafNode(int fanout, DataPointer[] pointers, InnerNode parent) {
			this.parent = parent;
			this.maxDataPointers = fanout - 1;
			this.dPointers = pointers;
	
			resetNumDataPointers();
		}
	
		
		public boolean insert(DataPointer dp) {
			if (this.isFull()) {
				return false;
			} else {
				this.dPointers[numDataPointers] = dp;
				numDataPointers++;
				sortDataPointers();
				return true;
			}
		}
		public void delete(int index) {
			this.dPointers[index] = null;
			numDataPointers--;
		}
		public void sortDataPointers(){
			Arrays.sort(this.dPointers, 0, numDataPointers);
		}
		public void sortDataPointersWithNull() {
			Arrays.sort(this.dPointers, new Comparator<DataPointer>() {
				@Override
				public int compare(DataPointer dPointer1, DataPointer dPointer2) {
					if (dPointer1 == null && dPointer2 == null) { 
						return 0; 
					}
					if (dPointer1 == null) { 
						return 1; 
					}
					if (dPointer2 == null) { 
						return -1; 
					}
					return dPointer1.compareTo(dPointer2);
				}
			});
		}
		public void resetNumDataPointers(){
			for (int i = 0; i <  dPointers.length; i++) {
				if (dPointers[i] == null) { 
					this.numDataPointers = i;
					return; 
				}
			}
		}
		public boolean isFull() { 
			return numDataPointers == maxDataPointers; 
		}
	
		
	}

}

