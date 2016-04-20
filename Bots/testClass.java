package Bots;

import java.util.ArrayList;
import java.util.List;

public class testClass {

	testClass(){
		
	}
	
	public static void main(String[] args) {
		new testClass().start();
	}

	private void start() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("apples");
		list.add("Pears");
		list.add("cranberrys");		
		System.out.println(list.toString());
		list = mergeSort(list);
		System.out.println(list.toString());
	}
	
	private ArrayList<String> mergeSort(ArrayList<String> list) {
		if (list.size() > 1) {
			int left = list.size()/2;

			ArrayList<String> leftList = new ArrayList<String> (list.subList(0, left));
			ArrayList<String> rightList = new ArrayList<String> (list.subList(left, list.size()));

			leftList = mergeSort(leftList);
			rightList = mergeSort(rightList);

			return merge(leftList,rightList);
		}else{
			return list;
		}
	}

	private ArrayList<String> merge(ArrayList<String> leftList, List<String> rightList) {	// Removing the last element to hopefully make the code faster compared to removing the first element
		ArrayList<String> list = new ArrayList<String>();
		while(leftList.size()>0 && rightList.size()>0){
			if(leftList.get(leftList.size() - 1).compareTo(rightList.get(rightList.size() - 1)) < 0 ){
				list.add(leftList.remove(leftList.size() - 1));
			}else{
				list.add(rightList.remove(rightList.size() - 1));
			}
		}
		while(leftList.size() > 0){
			list.add(leftList.remove(leftList.size() - 1));
		}
		while(rightList.size() > 0){
			list.add(rightList.remove(rightList.size() - 1));
		}
		return list;
	}

}
