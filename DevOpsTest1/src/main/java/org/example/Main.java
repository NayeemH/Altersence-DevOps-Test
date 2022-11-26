package org.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Log log = null;
		try {
			log = new Log("log.txt");
			File myObj = new File("input.txt");
			OutputStream os = os = new FileOutputStream(new File("output.txt"));
		   
		     Scanner myReader = new Scanner(myObj);
		     while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
				double xyz = Double.parseDouble(data);
				int value = ((int) xyz);
				boolean c = isPrime(value);
		        System.out.println(value+ " "+c);
		        if(c) {
		        	os.write((data+ " is a prime number\n").getBytes());
		        } else {
		        	os.write((data+ " is not a prime number\n").getBytes());
		        }
		        
		     }
//		     myWriter.close();
		     myReader.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.logger.severe("exception:"+e.toString());
			
		}

	}
	
	static  boolean isPrime(int num){
        if(num<=1)
        {
            return false;
        }
       for(int i=2;i<=num/2;i++)
       {
           if((num%i)==0)
               return  false;
       }
       return true;
    }

}
