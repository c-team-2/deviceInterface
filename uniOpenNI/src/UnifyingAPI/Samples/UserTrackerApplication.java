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

package UnifyingAPI.Samples;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


public class UserTrackerApplication {

    /**
	 * 
	 */
	public UserTracker viewer;
	private boolean shouldRun = true;
	private JFrame frame;

    public UserTrackerApplication (JFrame frame)
    {
    	this.frame = frame;
    	frame.addKeyListener(new KeyListener()
		{
			@Override
			public void keyTyped(KeyEvent arg0) {}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					shouldRun = false;
				}
			}
		});
    }

    public static void main(String s[])
    {
        JFrame f = new JFrame("OpenNI User Tracker");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        UserTrackerApplication app = new UserTrackerApplication(f);
        
        app.viewer = new UserTracker();
        f.add("Center", app.viewer);
        f.pack();
        f.setVisible(true);
        app.run();
    }

    void run()
    {
        while(shouldRun) {
            viewer.updateDepth();
            viewer.repaint();
        }
        frame.dispose();
    }
    
}
