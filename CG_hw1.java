import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CG_hw1
{

    int INSIDE = 0; // 0000
    int LEFT = 1;   // 0001
    int RIGHT = 2;  // 0010
    int BOTTOM = 4; // 0100
    int TOP = 8;    // 1000
    
	int world_x1 = 0, world_y1 = 0, world_x2 = 499, world_y2 = 499;
	float scaling_factor = 1.0f;
	int width = (world_x2 - world_x1) + 1;
	int height = (world_y2 - world_y1) + 1;
	int rotation = 0, translation_x = 0, translation_y = 0;
	int pixels [][];
	String input = "hw1.ps";
    List<List<Integer>> lines = new ArrayList<List<Integer>>();
    List<List<Float>> transformed_lines = new ArrayList<List<Float>>();
    List<List<Float>> clipped_lines = new ArrayList<List<Float>>();


    public int updateBits(float x, float y)
    {
    	int code = INSIDE;
    	
    	//Setting bits
		if (x < world_x1)
			code += LEFT;
					
		if(x > world_x2)
            code += RIGHT;
		
		if(y > world_y2)
			code += TOP;
		
		if(y < world_y1)
			code += BOTTOM;
			
		return code;
    	
    }
    public void clipping()
    {
    	for (int i=0; i<transformed_lines.size(); i++)
    	{
    		float x1 = transformed_lines.get(i).get(0);
    		float y1 = transformed_lines.get(i).get(1);
    		float x2 = transformed_lines.get(i).get(2);
    		float y2 = transformed_lines.get(i).get(3);
    		
            int code1 = updateBits(x1, y1); 		
    		int code2 = updateBits(x2, y2);
    		
            boolean accept = false;
           
            
    		while(true)
    		{
    			//Line is visible
    			if((code1 | code2) == 0)
    			{
    				accept = true;
    				break;
    			}
    			
    			//Line is invisible
    			else if((code1 & code2) != 0)
    				break;
    			
    			//Line clipping
    			else
    			{
    				float x = 0.0f,y = 0.0f;
    				
    				int codeout;
    				
    				if(code1 >= 1)
    					codeout = code1;
    				else 
    					codeout = code2;
    				
    				//Line intersects top of window
    				if((codeout & TOP) >= 1)
    				{
    					x = x1 + (x2 - x1) * (world_y2 - y1) / (y2 - y1);
    					y = world_y2;
    				}
    				//Line intersects bottom of window
    				else if((codeout & BOTTOM) >= 1)
    				{
    					x = x1 + (x2 - x1) * (world_y1 - y1) / (y2 - y1);
    					y = world_y1;
    				}
    				//Line intersects right of window
    				else if((codeout & RIGHT) >= 1)
    				{
    					y = y1 + (y2 - y1) * (world_x2 - x1) / (x2 - x1);
    					x = world_x2;
    				}
    				//Line intersects left of window
    				else if((codeout & LEFT) >= 1)
    				{
    					y = y1 + (y2 - y1) * (world_x1 - x1) / (x2 - x1);
    					x = world_x1;
    				}
    				
    				if(codeout == code1)
    				{
    					x1 = x;
    					y1 = y;
    					code1 = updateBits(x1, y1);
    				}
    				else
    				{
    					x2 = x;
    					y2 = y;
    					code2 = updateBits(x2, y2);
    				}
    			}
    		}
    	if(accept)
    	{

    		List<Float> row = new ArrayList<Float>();
            row.add(x1);
            row.add(y1);
            row.add(x2);
            row.add(y2);
            
            clipped_lines.add(row);
    	}
    		    	
    	}
    }
    
    public void drawing()
    {
    	for (int i=0; i<height; i++)
    	{
    		for (int j=0; j<width; j++)
    		 {
    			pixels[i][j] = 0;
    		 }	
    	}
    	for (int i=0; i<clipped_lines.size(); i++)
    	{
    		float x1 = clipped_lines.get(i).get(0);
    		float y1 = clipped_lines.get(i).get(1);
    		float x2 = clipped_lines.get(i).get(2);
    		float y2 = clipped_lines.get(i).get(3);
    		
    		
    		//DDA
    		
    		float dx,dy,steps;
    		float xc,yc;
    		float x,y;
    		
    		dx = x2 - x1;
    		dy = y2 - y1;
    		
    		if(Math.abs(dx) > Math.abs(dy))
    			steps = Math.abs(dx);
    		
    		else
    			steps = Math.abs(dy);
    		
    		if(x1 == x2 && dy<0)
                 steps = Math.abs(dy);
    		
    		xc = dx/steps;
    		
    		yc = dy/steps;
    		
    		if(x1 == x2 && dy<0)
    			yc = Math.abs(dy)/steps;
        			
    			
    		x = (int)x1;
    		
    		y = (int)y1;
    		
    		
    		pixels[Math.round(y-world_y1)][Math.round(x-world_x1)] = 1;
    		
    		for (int j=0; j<steps; j++)
    		{
    			x = x + xc;
    			y = y + yc;

    			
    			if(!(x < world_x1 || y < world_y1 || x >= world_x2 || y >= world_y2))
    			pixels[Math.round(y-world_y1)][Math.round(x-world_x1)] = 1;
    		}
    	}
    }
    
    public void output() throws FileNotFoundException, UnsupportedEncodingException
    {
    	//PrintWriter writer = new PrintWriter("out.xpm" , "UTF-8");
    	System.out.println("/*XPM*/");
    	System.out.println("static char *sco100[] = { ");
    	System.out.println("/* width height num_colors chars_per_pixel */ ");
    	System.out.println("\""+ width + " " + height + " " + "2" + " " + "1" + "\"" + ",");
    	System.out.println("/*colors*/");
    	System.out.println("\""+ "0" + " " + "c" + " " + "#" + "ffffff" + "\"" + "," );
    	System.out.println("\""+ "1" + " " + "c" + " " + "#" + "000000" + "\"" + "," );
        System.out.println("/*pixels*/");
        for (int i=0; i<height; i++)
        {
        	System.out.print("\"");
        	for(int j=0; j<width; j++)
        	{
        		System.out.print(pixels[height-i-1][j]);
        	}
        	if(i == height - 1)
        		System.out.print("\"");
        	else
        	System.out.print("\"" + ",");
        	
        	System.out.println();
        }
        
        System.out.println("};");
        //writer.flush();
        //writer.close();
        //System.out.println("out.xpm");
    }
    public void transformation()
    {
		//Scaling

        List<List<Float>> scaled_lines = new ArrayList<List<Float>>();

    	for (int i=0; i<lines.size(); i++)
    	{
    		List<Float> row = new ArrayList<Float>();

    		for(int j=0; j<4; j++)
    		{
    			
        		float temp = lines.get(i).get(j);
        		temp = temp * scaling_factor;
                row.add(temp);
    		}
    		scaled_lines.add(row);
    	}
    	
    	//Rotation
        List<List<Float>> rotated_lines = new ArrayList<List<Float>>();
        
		for(int i=0; i<scaled_lines.size(); i++)
        {
			List<Float> row1 = new ArrayList<Float>();

        	for(int j=0; j<4; j+=2)
        	{
        		float x = scaled_lines.get(i).get(j);
        		float y = scaled_lines.get(i).get(j+1);
        		double x_prime = x * Math.cos(Math.toRadians(rotation)) - y * Math.sin(Math.toRadians(rotation));
        		double y_prime = x * Math.sin(Math.toRadians(rotation)) + y * Math.cos(Math.toRadians(rotation));

        		row1.add((float)x_prime);
        		row1.add((float)y_prime);
        	}
        	rotated_lines.add(row1);
        }
        
        //Translation
        for(int i=0; i<rotated_lines.size(); i++)
        {
    		List<Float> row2 = new ArrayList<Float>();

        	for(int j=0; j<4; j+=2)
        	{
        		float x = rotated_lines.get(i).get(j);
        		float y = rotated_lines.get(i).get(j+1);
        		x = x + translation_x;
        		y = y + translation_y;
        		row2.add(x);
        		row2.add(y);
        	}
        	transformed_lines.add(row2);
        }
    }
	public void read_file(String input) throws FileNotFoundException
	{
	    File file = new File(input);
        Scanner sc = new Scanner(file);
	    while(sc.hasNextLine())
	    {

	    	if(sc.nextLine().equals("%%%BEGIN"))
	    	{	

	    	while(sc.hasNextLine())
	    	{
	    		String line = sc.nextLine();

	    		if(line.equals("%%%END"))
	    		{
	    			break;
	    		}
	    		
	    		String parse[] = line.split(" ");
	    		int x1 = Integer.parseInt(parse[0]);
	    		int y1 = Integer.parseInt(parse[1]);
	    		int x2 = Integer.parseInt(parse[2]);
	    		int y2 = Integer.parseInt(parse[3]);
                
	    		List<Integer> row = new ArrayList<Integer>();
	    		row.add(x1); 
	    		row.add(y1); 
	    		row.add(x2); 
	    		row.add(y2); 
	    		
	    		lines.add(row);	
	    	}
	    	}
	    }
	    sc.close();   
	}
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException 
	{
        CG_hw1 obj = new CG_hw1();
        
        for (int i=0; i<args.length; i+=2)
        {
        	if(args[i].equals("-f"))
        	{
        		
        		 obj.input = args[i+1];
        	}
        	
        	if(args[i].equals("-a"))
        	{
        		obj.world_x1 = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-b"))
        	{
        		obj.world_y1 = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-c"))
        	{
        		obj.world_x2 = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-d"))
        	{
        		obj.world_y2 = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-r"))
        	{
        		obj.rotation = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-m"))
        	{
        		obj.translation_x = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-n"))
        	{
        		obj.translation_y = Integer.parseInt(args[i+1]);
        	}
        	if(args[i].equals("-s"))
        	{
        		obj.scaling_factor = Float.parseFloat(args[i+1]);
        	}
        }
        
        obj.read_file(obj.input);
        obj.width = (obj.world_x2 - obj.world_x1) + 1;
    	obj.height = (obj.world_y2 - obj.world_y1) + 1;
    	obj.pixels = new int[obj.height][obj.width];
        obj.transformation();
        obj.clipping();
        obj.drawing();
        obj.output();

       
	}

}
