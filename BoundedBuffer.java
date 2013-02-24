import java.util.*;
import java.io.*;

class BoundedBuffer
{
	int size;
	public BoundedBuffer(int s)//Constructor for buffer
	{
		size = s;
	}
	
	int nextIn = 0;
	int nextOut = 0;
	int occupied = 0;
	int ins = 0;
	int outs = 0;
	boolean dataAvailable = false;
	boolean roomAvailable = true;
	
	int [] buffer = new int[size];//Array for buffer
	
	public synchronized void insertItem(int item) throws InterruptedException//Method to add an item to the buffer
	{
		while(roomAvailable == false)//Thread sleeps when there's no room in the buffer
		{
			wait();
		}
		buffer[nextIn] = item;
		System.out.println("Item Added = " + item);
		nextIn = (nextIn + 1) % size;
		dataAvailable = true;
		ins++;
		occupied++;
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
		System.out.println("Item removed = " + tmp);
		nextOut = (nextOut + 1) % size;
		outs++;
		occupied--;
		roomAvailable = true;
		if(occupied == 0)
			dataAvailable = false;
			
		notifyAll();
		
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
		int i = (int)(Math.random() * 100);
		try 
		{
			buffer.insertItem(i);
			Thread.sleep((int)(Math.random() * 100));
	  	}
	 	catch (InterruptedException e) 
		{ 
			completed = false;
		}

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
		try 
		{
			buffer.removeItem();
			Thread.sleep((int)(Math.random() * 100));
	  	}
	 	catch (InterruptedException e) 
		{ 
			completed = false;
		}
	}
}