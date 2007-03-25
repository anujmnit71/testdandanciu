package ro.utcluj.dandanciu.utils.collections;

public class LastRecentlyUsed {
	int[] data;
	public LastRecentlyUsed(int size){
		data = new int[size];
		for(int i = 0; i < size; i++){
			data[i] = i;
		}
	}
	
	public void use(int which){
		for(int i = 0; i < data.length; i++){
			if(i == which) data[i] = 0;
			else data[i] = data[i] + 1;
		}
	}
	
	public int getLastRecentlyUsed(){
		int ind = -1;
		int max = -1;
		for(int i = 0; i < data.length; i++){
			if(max < data[i]) {
				max= data[i];
				ind = i;
			}
		}
		return ind;
	}
}