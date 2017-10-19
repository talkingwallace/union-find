package assignment1;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

class UF{
	
	private int id[];
	private int sz[];
	private int count;
	private int total;
	
	public UF(int n){
		total = count = n;
		id = new int[n];
		for(int i=0;i<n;i++)id[i] = i;
		sz = new int[n];
		for(int i=0;i<n;i++)sz[i] = 1;
	}
	public int find(int a){//路径压缩
		while(a!=id[a]){
			id[a] = id[id[a]];
			a = id[a];
		}
		return a;
	}
	public void union(int p,int q){
		int i = find(p);
		int j = find(q);
		if(i==j)return ;
		if (sz[i]<sz[j]){
			id[i]=j;sz[j]+=sz[i];
		}
		else{
			id[j]=i;sz[i]+=sz[j];
		}
		count--;
	}
	public boolean connected(int i,int j){
		return find(i)==find(j);
	}
	public void showall(int n){
		
		int endofline = n;
		for(int i=0;i<total;i++){
			if(endofline%n==0)System.out.println('\n');
			System.out.print(find(i)+" ");
			endofline++;
		}
	}
	
}

class Percolation{
	
	private int n;
	private int total;
	private int top,buttom;
	private UF uf;
	private boolean opened[];
	private Random ra =new Random();
	private int openednum = 0;
	private double proportion;
	
	Percolation(int N){
		
		n = N;
		total = N*N;
		top = total;
		buttom = top+1;
		uf = new UF(total+2);//还有一个头 一个底 
		opened = new boolean[total];

	}
	
	public static void clear(){
		
        Robot r;
		try {
			r = new Robot();
	        r.mousePress(InputEvent.BUTTON3_MASK);       // 按下鼠标右键
	        r.mouseRelease(InputEvent.BUTTON3_MASK);    // 释放鼠标右键
	        r.keyPress(KeyEvent.VK_CONTROL);             // 按下Ctrl键
	        r.keyPress(KeyEvent.VK_R);                    // 按下R键
	        r.keyRelease(KeyEvent.VK_R);                  // 释放R键
	        r.keyRelease(KeyEvent.VK_CONTROL);            // 释放Ctrl键
	        r.delay(100); 
		} catch (AWTException e) {
			e.printStackTrace();
		}
      

    }
	
	boolean hasleft(int k){
		if(k%n==0)return false;
		else return true;
	}
	
	boolean hasright(int k){
		if((k+1)%n==0)return false;
		else return true;
	}
	
	private void open(int i,int j){
		
		int k =i+j*n;
		if(!opened[k]){
			
			opened[k] = true;
			
			int up = k-n;
			int down = k+n;
			if(up>=0&&opened[up]){
				uf.union(k,up);
			}
			if(down<total&&opened[down]){
				uf.union(k,down);
			}
			if(hasleft(k)){
				if(opened[k-1]){
					uf.union(k,k-1);
				}
			}
			if(hasright(k)){
				if(opened[k+1]){
					uf.union(k,k+1);
				}
			}
			if(j==0){
				uf.union(k, top);
			}
			if(j==n-1){
				uf.union(k, buttom);
			}
			openednum++;
		}
	}
	
	private void showopened(){
		
		int endofline = n;
		for(int k=0;k<total;k++){
			if(endofline%n==0)System.out.println('\n');
			if(opened[k]){
				System.out.print(1+" ");
			}
			else{
				System.out.print("  ");
			}
			endofline++;
		}
	}
	
	private boolean canpercolate(){
		if(uf.connected(top,buttom)){
			return true;
		}
		return false;
	}
	
	public void process(){
		
		int count = 0;
		
		while(true){
			int i = random();
			int j = random();
			open(i,j);
			count++;
			if(canpercolate()){
				proportion = (double)openednum/(double)total;
				break;
			}
		}
	}
	
	public int random(){
		return ra.nextInt(n);
	}
	
	public int getopenednum(){
		return openednum;
	}
	
	public double getproportion(){
		return proportion;
	}
}

class PercolationStats{
	
	private double upper,lower;
	private double miu,sigma;
	private ArrayList<Double> result = new ArrayList<Double>();
	private int T;
	
	 private void caculate(){
		 
		 double sum = 0;
		
		 for(double key:result){
			 sum+=(double)key;
		 }
		 miu = sum/(double)(T);
		 
		 sum = 0;
		 for(double key:result){
			 sum+=Math.pow(key-miu, 2);
		 }
		 sigma = Math.sqrt(sum/(double)(T-1));
		 
		 upper = miu-(1.96*sigma)/Math.sqrt(T);
		 lower = miu+(1.96*sigma)/Math.sqrt(T);
		 
	 }
	 
	 private void showresult(){
		 
		 System.out.println("mean: "+miu+"\nstddev: "+sigma+"\n"
		 		+ "95% confidence interval:\n "+upper+","+lower);
	 }
	
	 public PercolationStats(int N, int T){
		 
		 long startTime=System.currentTimeMillis();
		 this.T = T;
		 System.out.println("N:"+N+" T:"+T);
		 while(T-->0){
			 Percolation test = new Percolation(N);
			 test.process();
			 result.add(test.getproportion());
		 }
		caculate();
		showresult();
		long endTime=System.currentTimeMillis(); //获取结束时间
		System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
	 }
	 
}
