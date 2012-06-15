/**************************************************************
This file is part of Kinect Sensor Architecture Development Project.

   Kinect Sensor Architecture Development Project is free software:
	you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Kinect Sensor Architecture Development Project is distributed in
	the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Kinect Sensor Architecture Development Project.  If
	not, see <http://www.gnu.org/licenses/>.
**************************************************************/
/**************************************************************
The work was done in joint collaboration with Cisco Systems Inc.
Copyright © 2012, Cisco Systems, Inc. and UCLA
*************************************************************/
/**************************************************************
* Contains code from OpenNI Copyright (C) 2011 PrimeSense Ltd.  
************************************************************/

package UnifyingAPI.Samples.RGBUserTracker;
import org.OpenNI.*;
import UnifyingAPI.*;
import java.util.HashMap;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;

public class RGBUserTracker extends Component
{
	Sensor kinect;
	SensorSnapshot snapshot;
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   private byte[] imgbytes;
   HashMap<Integer, HashMap<Integer, float[]>> joints;
   private boolean drawPixels = true;
   private boolean drawSkeleton = true;
   
   
   private BufferedImage bimg;
   int width, height;
   public RGBUserTracker()
   {
       width = 320;
       height = 240;
       imgbytes = new byte[width*height*3];
       UniDevice device = new UniOpenNIDevice();
       kinect = new Sensor(device);
       joints = new HashMap<Integer, HashMap<Integer, float[]>>();
   }
   
   void updateRGB()
   {
       snapshot = kinect.getSensorSnapshot();
		
		Channel RGBChannel = snapshot.getChannel("RGB");

		for (int i = 0; i < RGBChannel.getNumberOfTuples(); ++i)
		{
		    byte red;
		    byte green;
		    byte blue;
			try {
				red = RGBChannel.getTuple(i).getElementByte(0);
				green = RGBChannel.getTuple(i).getElementByte(1);
				blue = RGBChannel.getTuple(i).getElementByte(2);
				
				imgbytes[3*i] = red;
				imgbytes[3*i+1] = green;
				imgbytes[3*i+2] = blue;
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// Update Joints
		updateJoints();
   }
   
   void updateJoints()
   {
   	Channel user1Channel = snapshot.getChannel("User1");
   	if (user1Channel != null)
   	{
   		HashMap<Integer, float[]> user1Skeleton = new HashMap<Integer, float[]>();	
	    	for (int jointIndex = 0; jointIndex < 15; ++jointIndex)
	    	{
	    		float[] coordsAndConf = new float[4]; // coordinates (x, y, z) and confidence
	    		try {
					coordsAndConf[0] = user1Channel.getTuple(jointIndex).getElementFloat(0);
		    		coordsAndConf[1] = user1Channel.getTuple(jointIndex).getElementFloat(1);
		    		coordsAndConf[2] = user1Channel.getTuple(jointIndex).getElementFloat(2);
		    		coordsAndConf[3] = user1Channel.getTuple(jointIndex).getElementFloat(3);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		user1Skeleton.put(jointIndex, coordsAndConf);
	    	}
	    	joints.put(1, user1Skeleton);
   	}
   }

   Color colors[] = {Color.RED, Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.PINK, Color.YELLOW, Color.WHITE};

   void drawLine(Graphics g, HashMap<Integer, float[]> dict, int i, int j)
   {
		float[] pos1 = dict.get(i);
		float[] pos2 = dict.get(j);

		if (pos1 != null && pos2 != null) 
		{
			if (pos1[3] == 0 || pos2[3] == 0)
				return;
	
			g.drawLine((int)pos1[0], (int)pos1[1], (int)pos2[0], (int)pos2[1]);
		}
   }
   
   public void drawSkeleton(Graphics g, int user) throws StatusException
   {
   	HashMap<Integer, float[]> dict = joints.get(new Integer(user));

   	if (dict != null)
   	{
	    	drawLine(g, dict, 0, 1);
	
	    	drawLine(g, dict, 2, 8);
	    	drawLine(g, dict, 5, 8);
	
	    	drawLine(g, dict, 1, 2);
	    	drawLine(g, dict, 2, 3);
	    	drawLine(g, dict, 3, 4);
	
	    	drawLine(g, dict, 2, 5);
	    	drawLine(g, dict, 5, 6);
	    	drawLine(g, dict, 6, 7);
	
	    	drawLine(g, dict, 9, 8);
	    	drawLine(g, dict, 12, 8);
	    	drawLine(g, dict, 9, 12);
	
	    	drawLine(g, dict, 9, 10);
	    	drawLine(g, dict, 10, 11);
	
	    	drawLine(g, dict, 12, 13);
	    	drawLine(g, dict, 13, 14);
   	}

   }
   
   public void paint(Graphics g)
   {
   	if (drawPixels)
   	{
           DataBufferByte dataBuffer = new DataBufferByte(imgbytes, width*height*3);

           WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, height, width * 3, 3, new int[]{0, 1, 2}, null); 

           ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8}, false, false, ComponentColorModel.OPAQUE, DataBuffer.TYPE_BYTE);

           bimg = new BufferedImage(colorModel, raster, false, null);

   		g.drawImage(bimg, 0, 0, null);
   	}
       try
		{
			int[] users = {1};
			for (int i = 0; i < users.length; ++i)
			{
		    	Color c = colors[users[i]%colors.length];
		    	c = new Color(255-c.getRed(), 255-c.getGreen(), 255-c.getBlue());

		    	g.setColor(c);
		    	if (drawSkeleton)
				{
					drawSkeleton(g, users[i]);
				}
			}
		} catch (StatusException e)
		{
			e.printStackTrace();
		}
   }
   
   public Dimension getPreferredSize() {
       return new Dimension(width, height);
   }
   
}


