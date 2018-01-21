import java.util.*;
public class aStar {
	static String path=null;
	public static void main(String[] args) {
		ArrayList<Integer> initialTileList = new ArrayList<Integer>();
		ArrayList<Integer> goalTileList = new ArrayList<Integer>();
		int blankIndex;
		for(int i=1;i<10;i++){
				goalTileList.add(i%9);
				initialTileList.add(i-1); //*** Randomizer
		}
		/*int[][] initialTile = {{6,3,2},{4,5,1},{0,7,8}};
		int[][] initialTile = {{1,2,0},{4,5,3},{7,8,6}};
		for(int i=0;i<9;i++)
			initialTileList.add(initialTile[i/3][i%3]);*/
		while(true){ //*** Randomizer
			Collections.shuffle(initialTileList); //*** Randomizer
			blankIndex = initialTileList.indexOf(0);
			initialTileList.remove(blankIndex);
			if(checkSolvable(initialTileList)){
				initialTileList.add(blankIndex, 0);
				stateNode initTileObj = new stateNode(initialTileList,0,"S","");
				initTileObj.printTile();
				aStarAlgo(initTileObj,goalTileList);
				System.out.println("Path : "+path);
				System.out.println("Depth of goal state : "+path.length());
				break; //*** Randomizer
			}else
				initialTileList.add(blankIndex, 0);
		}//*** Randomizer
	}
	public static void aStarAlgo(stateNode tileObj,ArrayList<Integer> goalTileList){
		ArrayList<stateNode> fringe = new ArrayList<stateNode>();
		ArrayList<stateNode> expanded = new ArrayList<stateNode>();
		stateNode curState;
		boolean found=false;
		fringe.add(tileObj);
		while(!fringe.isEmpty() && !found){
			Collections.sort(fringe);
			curState = fringe.get(0);
			fringe.remove(0);
			if(curState.tileList.equals(goalTileList)){
				found=true;
				path=curState.path;
			}else{
				expanded.add(curState);
				populateFringe(fringe,expanded,curState);
			}
		}
	}
	public static void populateFringe(ArrayList<stateNode> fringe,ArrayList<stateNode> expanded, stateNode tileObj){
		int blankIndex = tileObj.blankIndex;
		stateNode newState;
		if((blankIndex+1)%3>0){
			newState = newState(tileObj, blankIndex, blankIndex+1, "R");
			if(!inList(fringe,newState) && !inList(expanded,newState))
				fringe.add(newState);
		}
		if(blankIndex%3>0){
			newState = newState(tileObj, blankIndex, blankIndex-1, "L");
			if(!inList(fringe,newState) && !inList(expanded,newState))
				fringe.add(newState);
		}
		if(blankIndex>2){
			newState = newState(tileObj, blankIndex, blankIndex-3, "U");
			if(!inList(fringe,newState) && !inList(expanded,newState))
				fringe.add(newState);
		}
		if(blankIndex<6){
			newState = newState(tileObj, blankIndex, blankIndex+3, "D");
			if(!inList(fringe,newState) && !inList(expanded,newState))
				fringe.add(newState);
		}
	}
	public static boolean inList(ArrayList<stateNode> list, stateNode tileObj){
		for(int i=0;i<list.size();i++){
			if(list.get(i).tileList.equals(tileObj.tileList))
				return true;
		}
		return false;
	}
	public static stateNode newState(stateNode oldTile,int a,int b,String dir){
		ArrayList<Integer> tileConfig = new ArrayList<Integer>();
		for(int i=0;i<9;i++)
			tileConfig.add(i, oldTile.tileList.get(i));
		int aVal = tileConfig.get(a);
		int bVal = tileConfig.get(b);
		tileConfig.remove(a);
		tileConfig.add(a, bVal);
		tileConfig.remove(b);
		tileConfig.add(b, aVal);
		stateNode newTile = new stateNode(tileConfig,oldTile.depth+1,dir,oldTile.path+dir);
		return newTile;
	}
	public static boolean checkSolvable(ArrayList<Integer> tiles){
        int inversions = 0;
        for(int i=0;i<tiles.size();i++){
            for(int j=i+1;j<tiles.size();j++){
                if(tiles.get(j)>tiles.get(i))
                    inversions++;
            }
        }
        return inversions%2==1?false:true;
    }
}
class stateNode implements Comparable<stateNode>{
	ArrayList<Integer> tileList;
	String dir,path;
	int depth,blankIndex;
	int mDist;
	stateNode(ArrayList<Integer> tileConfig,int depth, String dir, String path){
		tileList = new ArrayList<Integer>();
		this.depth=depth;
		this.dir=dir;
		this.path=path;
		blankIndex = tileConfig.indexOf(0);
		tileList = tileConfig;
		mDist = getManhattan(tileConfig);
	}
	public void printTile(){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++)
				System.out.print(tileList.get(3*i+j) + " ");
			System.out.println("");
		}
	}
	public int getManhattan(ArrayList<Integer> tileConfig){
		int dist=0,val;
		for(int i=0;i<9;i++){
			val=tileConfig.get(i);
			if(val!=0)
				dist+=Math.abs((i/3)-(val-1)/3)+Math.abs((i%3)-(val-1)%3);
		}
		return dist;
	}
	public int compareTo(stateNode stateNew) {
		return this.depth+this.mDist-stateNew.depth-stateNew.mDist;
	}
}
