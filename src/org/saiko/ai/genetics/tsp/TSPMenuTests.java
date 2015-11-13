/*
 * $Source: f:/cvs/prgm/tsp/src/org/saiko/ai/genetics/tsp/TSPMenuTests.java,v $
 * $Id: TSPMenuTests.java,v 1.3 2005/08/23 23:18:04 dsaiko Exp $
 * $Date: 2005/08/23 23:18:04 $
 * $Revision: 1.3 $
 * $Author: dsaiko $
 *
 * Traveling Salesman Problem genetic algorithm.
 * This source is released under GNU public licence agreement.
 * dusan@saiko.cz
 * http://www.saiko.cz/ai/tsp/
 * 
 * Change log:
 * $Log: TSPMenuTests.java,v $
 * Revision 1.3  2005/08/23 23:18:04  dsaiko
 * Finished.
 *
 * Revision 1.2  2005/08/23 10:01:30  dsaiko
 * Gui and main program divided
 *
 * Revision 1.1  2005/08/12 23:52:17  dsaiko
 * Initial revision created
 *
 */

package org.saiko.ai.genetics.tsp;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import junit.framework.TestCase;

/**
 * @author Dusan Saiko (dusan@saiko.cz)
 * Last change $Date: 2005/08/23 23:18:04 $
 *
 * TSPMenu TestCase
 * @see org.saiko.ai.genetics.tsp.TSPMenu
 */
public class TSPMenuTests extends TestCase {

   /** String containing the CVS revision. **/
   public final static String CVS_REVISION = "$Revision: 1.3 $";
   

   /**
    * Test for menubar characteristics
    */
   public void testMenuBar() {
      JMenuBar menu=new TSPMenu(new TSP()).createMenuBar().parent.gui.getJMenuBar();
      
      //go through all menu items and check if all menu has at least one submenu
      for(Component m:menu.getComponents()) {
         assertTrue(m instanceof JMenu);
         assertTrue(((JMenu)m).getMenuComponentCount()>0);
      }
      
      //go through all menu items again
      List<Component> componentsToCheck=new ArrayList<Component>();
      componentsToCheck.addAll(Arrays.asList(menu.getComponents()));
      while(componentsToCheck.size()>0) {
         Component c=componentsToCheck.remove(0);
         if(!(c instanceof JMenuItem)) {
            continue;
         }
         
         JMenuItem m=(JMenuItem)c;

         //check that all menu item have text
         assertTrue(m.getText()!=null && m.getText().trim().length()>0);
         
         if(m instanceof JMenu) {
            assertTrue(((JMenu)m).getMenuComponentCount()>0);
            componentsToCheck.addAll(Arrays.asList(((JMenu)m).getMenuComponents()));
         } else {
            //check that all menu items have actionListener
            assertTrue(m.getText(),m.getActionListeners().length>0);
         }
      }      
   }
}