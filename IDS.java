import java.util.*;
public class IDS {
	static String path=null;
	public static void main(String[] args) {
		ArrayList<Integer> initialTileList = new ArrayList<Integer>();  
		ArrayList<Integer> goalTileList = new ArrayList<Integer>(); 
		int cutoff=1,blankIndex;
		boolean flag=false;
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
				state initTileObj = new state(initialTileList,0,"S","");
				System.out.print("Loading.");
				while(!flag){
					System.out.print(".");
					flag=dfs(cutoff,initTileObj,goalTileList);
					cutoff++;
				}
				System.out.println("");
				System.out.println("Path : "+path);
				System.out.println("Depth of goal state : "+(cutoff-1));
				break; //*** Randomizer
			}else
				initialTileList.add(blankIndex, 0);
		}//*** Randomizer
	}	
	public static boolean dfs(int cutoff, state tileObj,ArrayList<Integer> goalTileList){
		ArrayList<state> fringe = new ArrayList<state>();
		ArrayList<state> expanded = new ArrayList<state>();
		state curState;
		boolean found=false;
		fringe.add(tileObj);
		int lastEntry = (fringe.size()-1);
		while(!fringe.isEmpty() && !found && lastEntry>=0){
			curState = fringe.get(lastEntry);
			if(curState.depth<cutoff){
				fringe.remove(lastEntry);
				if(curState.tileList.equals(goalTileList)){
					found=true;
					path=curState.path;
				}else{
					expanded.add(curState);
					populateFringe(fringe,expanded,curState);
				}
				lastEntry = (fringe.size()-1);
			}else
				lastEntry--;
		}
		return found;
	}
	public static void populateFringe(ArrayList<state> fringe,ArrayList<state> expanded, state tileObj){
		int blankIndex = tileObj.blankIndex;
		state newState;
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
	public static boolean inList(ArrayList<state> list, state tileObj){
		for(int i=0;i<list.size();i++){
			if(list.get(i).tileList.equals(tileObj.tileList))
				return true;
		}
		return false;
	}
	public static state newState(state oldTile,int a,int b,String dir){
		ArrayList<Integer> tileConfig = new ArrayList<Integer>();
		for(int i=0;i<9;i++)
			tileConfig.add(i, oldTile.tileList.get(i));
		int aVal = tileConfig.get(a);
		int bVal = tileConfig.get(b);
		tileConfig.remove(a);
		tileConfig.add(a, bVal);
		tileConfig.remove(b);
		tileConfig.add(b, aVal);
		state newTile = new state(tileConfig,oldTile.depth+1,dir,oldTile.path+dir);
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
class state{
	ArrayList<Integer> tileList;
	String dir,path;
	int depth,blankIndex;
	state(ArrayList<Integer> tileConfig,int depth, String dir, String path){
		tileList = new ArrayList<Integer>();
		this.depth=depth;
		this.dir=dir;
		this.path=path;
		blankIndex = tileConfig.indexOf(0);
		tileList = tileConfig;
	}
	public void printTile(){
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++)
				System.out.print(tileList.get(3*i+j) + " ");
			System.out.println("");
		}
	}
}