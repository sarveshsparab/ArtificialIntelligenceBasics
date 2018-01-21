import java.util.*;
public class TSP {
	private static Scanner input;
	public static void main(String[] args) {
		int cityCount=0,bound=50,kVal=1000,netIter=1000,mutPer=20;
		ArrayList<City> graph = new ArrayList<City>();
		ArrayList<Integer> oneTour;
		ArrayList<Tour> kTours = new ArrayList<Tour>();
		input = new Scanner(System.in);
		System.out.print("Enter the number of cities : ");
		cityCount = input.nextInt();
		if((bound*(bound-1)/2)<cityCount)
			System.out.println("Please INCREASE the BOUND to accomodate for required cities!");
		else{
			while(graph.size()!=cityCount){
				City newCity = new City(new Random().nextInt(bound)-bound/2, new Random().nextInt(bound)-bound/2);
				if(!graph.contains(newCity))
					graph.add(newCity);
			}
			printGraph(graph);
			printCityMatrix(graph);
			while(kTours.size()!=kVal){
				oneTour = new ArrayList<Integer>();
				while(oneTour.size()!=cityCount){
					int newVisit=new Random().nextInt(cityCount);
					if(!oneTour.contains(newVisit))
						oneTour.add(newVisit);
				}
				Tour oneTourObj = new Tour(graph,oneTour,kVal);
				if(!kTours.contains(oneTourObj))
					kTours.add(oneTourObj);
			}
			Tour netMin = kTours.get(0);
			for(int iter=1;iter<=netIter;iter++){
				int p1,p2,mutVal = 0;
				double netFit=0.0;
				for(int i=0;i<kVal;i++)
					netFit+=kTours.get(i).fitVal;
				for(int i=0;i<kVal;i++)
					kTours.get(i).setPerVal(netFit);
				Collections.sort(kTours);
				if(kTours.get(0).tourCost<netMin.tourCost)
					netMin  = kTours.get(0);
				for(int i=0;i<kVal/2;i++){
					p1=getRoulette(kTours,kVal);
					p2=getRoulette(kTours,kVal);
					crossover(p1,p2,graph,kTours,kVal,netFit);
				}
				for(int i=0;i<kVal;i++)
					kTours.remove(0);
				for(int i=0;i<kVal;i++){
					mutVal = new Random().nextInt(100);
					if(mutVal<mutPer)
						mutate(kTours,i,cityCount);
				}
			}
			Collections.sort(kTours);
			System.out.println("-----------------------------------------------------");
			System.out.println("Tour cost : "+kTours.get(0).tourCost);
			kTours.get(0).printPath();
			System.out.println("-----------------------------------------------------");
			System.out.println("Tour cost : "+netMin.tourCost);
			netMin.printPath();
		}
	}
	public static int getRoulette(ArrayList<Tour> kTours,int kVal){
		double currSum=0.0,rand = new Random().nextFloat();
		boolean found=false;
		int i;
		for(i=0;!found && i<kVal;i++){
			currSum+=kTours.get(i).getPerVal();
			if(rand<currSum)
				found=true;
		}
		return (i-1);
	}
	public static void crossover(int p1,int p2,ArrayList<City> cList,ArrayList<Tour> kTours,int kVal,double netFit){
		ArrayList<Integer> child1 = new ArrayList<Integer>();
		ArrayList<Integer> child2 = new ArrayList<Integer>();
		int tourSize = kTours.get(p1).tourPath.size();
		int crossPoint = new Random().nextInt(tourSize);
		child1 = createChild(kTours.get(p1).tourPath,kTours.get(p2).tourPath,crossPoint,tourSize);
		child2 = createChild(kTours.get(p2).tourPath,kTours.get(p1).tourPath,crossPoint,tourSize);
		Tour ct1 = new Tour(cList, child1, kVal);
		Tour ct2 = new Tour(cList, child2, kVal);
		kTours.add(ct1);
		kTours.add(ct2);
	}
	public static ArrayList<Integer> createChild(ArrayList<Integer> p1,ArrayList<Integer> p2,int crossPoint,int tourSize){
		ArrayList<Integer> child = new ArrayList<Integer>();
		ArrayList<Integer> par1 = new ArrayList<Integer>();
		ArrayList<Integer> par2 = new ArrayList<Integer>();
		for(int i=0;i<tourSize;i++){
			par1.add(p1.get(i));
			par2.add(p2.get(i));
		}
		for(int i=0;i<=crossPoint;i++){
			child.add(par1.get(i));
			Collections.swap(par2, i, par2.indexOf(child.get(i)));
		}
		for(int i=crossPoint+1;i<tourSize;i++)
			child.add(par2.get(i));
		return child;
	}
	public static void mutate(ArrayList<Tour> kTours,int tIndex, int cityCount){
		int pos1 = new Random().nextInt(cityCount);
		int pos2 = new Random().nextInt(cityCount);
		Collections.swap(kTours.get(tIndex).tourPath, pos1, pos2);
	}
	public static void printGraph(ArrayList<City> graph){
		System.out.format("%10s%20s%20s\n%s\n", "City No", "X - Coordinate", "Y - Coordinate","-----------------------------------------------------");
		for(int i=0;i<graph.size();i++)
			System.out.format("%6d%20d%20d\n", (i+1), graph.get(i).x, graph.get(i).y);
		System.out.println("-----------------------------------------------------");
	}
	public static void printCityMatrix(ArrayList<City> graph){
		int cityCount = graph.size();
		System.out.format("%10s","");
		for(int i =0;i<cityCount;i++)
			System.out.format("%10d",i+1);
		System.out.println("");
		for(int i =0;i<cityCount;i++){
			System.out.format("%10d",i+1);
			for(int j=0;j<cityCount;j++)
				System.out.format("%10.2f",graph.get(i).getDist(graph.get(j)));
			System.out.println("");
		}
	}
}
class Tour implements Comparable<Tour>{
	public double tourCost;
	private int tourSize;
	public double fitVal;
	private double perVal;
	private int kVal;
	public ArrayList<Integer> tourPath;
	private ArrayList<City> cityGraph;
	Tour(ArrayList<City> cList,ArrayList<Integer> oneTour,int kVal){
		tourPath = new ArrayList<Integer>();
		tourPath = oneTour;
		cityGraph = new ArrayList<City>();
		cityGraph = cList;
		tourSize = cityGraph.size();
		this.kVal = kVal;
		updateOccured();
		perVal=-1;
	}
	public double getCost(){
		double cost=0;
		for(int i=0;i<tourSize;i++)
			cost += cityGraph.get(tourPath.get(i%tourSize)).getDist(cityGraph.get((tourPath.get((i+1)%tourSize))));
		return cost;
	}
	private void updateOccured(){
		tourCost = getCost();
		fitVal = tourSize*kVal/tourCost;
	}
	public double getPerVal() {
		return perVal;
	}
	public void setPerVal(double netFit) {
		this.perVal = this.fitVal/netFit;
	}
	public void print(){
		System.out.println("Tour cost : "+tourCost+" Fitness : "+fitVal+" Percent : "+perVal+" Tour Path : ");
		System.out.println("-------------------------------------");
		for(int i=0;i<tourSize;i++)
			System.out.println("X : "+cityGraph.get(tourPath.get(i)).x+" Y : "+cityGraph.get(tourPath.get(i)).y);
		System.out.println("-------------------------------------");
	}
	public void printPath(){
		for(int i=0;i<=tourSize;i++)
			System.out.print(tourPath.get(i%tourSize)+" -> ");
		System.out.println("");
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tourPath == null) ? 0 : tourPath.hashCode());
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tour other = (Tour) obj;
		if (tourPath == null) {
			if (other.tourPath != null)
				return false;
		} else if (!tourPath.equals(other.tourPath))
			return false;
		return true;
	}
	public int compareTo(Tour t) {
		return new Double(perVal).compareTo(t.perVal);
	}
}
class City{
	public int x,y;
	City(int x,int y){
		this.x=x;
		this.y=y;
	}
	double getDist(City c){
		return getDist(c.x,c.y);
	}
	double getDist(int x,int y){
		return Math.sqrt((this.x-x)*(this.x-x)+(this.y-y)*(this.y-y));
	}
	void print(){
		System.out.println("X : "+x+" Y : "+y);
	}
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
}
