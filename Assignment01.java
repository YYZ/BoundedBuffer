import java.util.*;
import java.io.*;
//Author - Sam Halligan
class BoundedBuffer
{
	int size;
	boolean timeElapsed = false;
	int time = 1;
	int nextIn = 0;
	int nextOut = 0;
	int occupied = 0;
	int ins = 0;
	int outs = 0;
	boolean dataAvailable = false;
	boolean roomAvailable = true;
	int [] buffer;
	public BoundedBuffer(int s)//Constructor for buffer
	{
		size = s;
		buffer = new int[size];
	}
	
	public synchronized void insertItem(int item) throws InterruptedException//Method to add an item to the buffer
	{
		while(roomAvailable == false)//Thread sleeps when there's no room in the buffer
		{
			wait();
		}
		buffer[nextIn] = item;
		nextIn = (nextIn + 1) % size;
		dataAvailable = true;
		ins++;
		occupied++;
		System.out.println("Item Added. " + occupied);
		if(occupied == size)
			roomAvailable = false;
		notifyAll();
		
	}
	
	public synchronized void removeItem() throws InterruptedException//Method to remove an item from the buffer
	{
		while(dataAvailable == false)
		{
			wait();
		}
		int tmp = buffer[nextOut];
		buffer[nextOut] = 0;
		nextOut = (nextOut + 1) % size;
		outs++;
		occupied--;
		System.out.println("Item removed. " + occupied);
		roomAvailable = true;
		if(occupied == 0)
			dataAvailable = false;
			
		notifyAll();
		
	}
	public synchronized void incTime() throws InterruptedException
	{
		time++;
		if(time == 60)
			timeElapsed = true;
	}
}

class Producer extends Thread
{
	private BoundedBuffer buffer;
	boolean completed;
	
	public Producer(BoundedBuffer b)
	{
		buffer = b;
	}
	public void run()
	{ 
		while(buffer.timeElapsed == false)
		try 
		{
			int i = ((int)((Math.random()*100) + 1));
			buffer.insertItem(i);
			Thread.sleep((int)((Math.random()*100) + 1));
		}
	 	catch (InterruptedException e) {}

	}
}
class Consumer extends Thread
{
	private BoundedBuffer buffer;
	boolean completed;
	
	public Consumer(BoundedBuffer b)
	{
		buffer = b;
	}
	public void run()
	{ 
		while(buffer.timeElapsed == false)
		try 
		{
			buffer.removeItem();
			Thread.sleep((int)(Math.random() * 100 + 1));
	  	}
	 	catch (InterruptedException e) {}
	}
}
class Watcher extends Thread
{
	private BoundedBuffer buffer;
	public Watcher(BoundedBuffer b)
	{
		buffer = b;
	}
	public void run()
	{
		while(buffer.time < 60)
		{
			try
			{
				Thread.sleep(1000); //Sleep for 1 second
				System.out.println("Delta = " + ((buffer.ins-buffer.outs) - buffer.occupied) + " Occupied = " + buffer.occupied + " Time elapsed = " + buffer.time);
				buffer.incTime();
				
			}
			catch (InterruptedException e){}
		}
	}
} 

class Assignment01
{
	public static void main(String [] args)
	{
		System.out.println("Enter the size of the buffer: ");
		Scanner in = new Scanner(System.in);
		int size = in.nextInt();
		in.close();
		if(size <= 0)
		{
			System.out.println("Size must be greater than 0.");
			System.exit(0);
		}
		
		BoundedBuffer buffer = new BoundedBuffer(size);
		Producer producer = new Producer(buffer);
		Consumer consumer = new Consumer(buffer);
		Watcher watcher = new Watcher(buffer);
		producer.start();
		consumer.start();
		watcher.start();
		
		try
		{
			producer.join();
			consumer.join();
			watcher.join();
		}
		catch(InterruptedException e) { }
	}
	
}
