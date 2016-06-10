package net.pyraetos.util;

public class Vector<T>{

	private T[] elements;
	private int size;
	
	@SafeVarargs
	public Vector(T... elements){
		this.elements = elements;
		size = elements.length;
	}
	
	public T getX(){
		return elements[0];
	}
	
	public T getY(){
		return elements[1];
	}

	public T getZ(){
		return elements[2];
	}
	
	public T get(int index){
		return elements[index];
	}
	
	public void set(int index, T value){
		elements[index] = value;
	}
	
	public int space(){
		return elements.length;
	}
	
	public int size(){
		return size;
	}
	
	@SuppressWarnings("unchecked")
	public void add(T value){
		if(elements[elements.length - 1] != null){
			Object[] temp = new Object[elements.length * 2];
			for(int i = 0; i < elements.length; i++){
				temp[i] = elements[i];
			}
			temp[elements.length] = value;
			elements = (T[])temp;
		}else{
			elements[size] = value;
		}
		size++;
	}
	
	@SuppressWarnings("unchecked")
	public void erase(int index){
		if(size <= index || index > size)
			return;
		if(size == 1){
			elements = (T[])new Object();
			size = 0;
			return;
		}
		Object[] temp = new Object[elements.length - 1];
		for(int i = 0, n = 0; i < elements.length; i++){
			if(i == index)
				continue;
			temp[n] = elements[i];
			n++;
		}
		elements = (T[])temp;
		size--;
	}
	
	public void eraseLast(){
		erase(size - 1);
	}
	
	@Override
	public String toString(){
		String string = "[";
		int last = elements.length - 1;
		for(int i = 0; i < elements.length - 1; i++){
			if(elements[i + 1] == null){
				last = i;
				break;
			}
			string += elements[i] + ", ";
		}
		return string + elements[last].toString() + "]";
	}
}
