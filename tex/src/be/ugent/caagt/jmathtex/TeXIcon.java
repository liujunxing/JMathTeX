/* TeXIcon.java
 * =========================================================================
 * This file is part of the JMathTeX Library - http://jmathtex.sourceforge.net
 * 
 * Copyright (C) 2004-2007 Universiteit Gent
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 * 
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 * 
 */

package be.ugent.caagt.jmathtex;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;

import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.VerticalBox;

/**
 * An {@link javax.swing.Icon} implementation that will paint the TeXFormula
 * that created it.
 * <p>
 * This class cannot be instantiated directly. It can be constructed from a
 * TeXFormula using the {@link TeXFormula#createTeXIcon(int,float)} method.
 * 
 * @author Kurt Vermeulen
 */
public class TeXIcon implements Icon {

   private Box box;

   /** 字体点数, 如 10pt 则 size=10.0 */
   private final float size;

   /** 边框大小? */
   private Insets insets = new Insets(0, 0, 0, 0);

   /**
    * 创建一个新的 icon, 将以指定的(字体)点数绘制指定的公式盒子.
    * Creates a new icon that will paint the given formula box in the given point size.
    *  
    * @param b the formula box to be painted 
    * @param size the point size (应该是指字体点数大小)
    */
   public TeXIcon(Box b, float size) {
      box = b;
      this.size = size;
   }

   /**
    * Get the insets of the TeXIcon.
    * 
    * @return the insets 
    */
   public Insets getInsets() {
      return insets;
   }

   /**
    * Set the insets of the TeXIcon.
    * 
    * @param insets the insets
    */
   public void setInsets(Insets insets) {
      this.insets = insets;
   }

   /**
    * Change the width of the TeXIcon. The new width must be greater than the current
    * width, otherwise the icon will remain unchanged. The formula will be aligned to the
    * left ({@linkplain TeXConstants#ALIGN_LEFT}), to the right 
    * ({@linkplain TeXConstants#ALIGN_RIGHT}) or will be centered 
    * in the middle ({@linkplain TeXConstants#ALIGN_CENTER}).
    * 
    * @param width the new width of the TeXIcon
    * @param alignment a horizontal alignment constant: LEFT, RIGHT or CENTER
    */
   public void setIconWidth(int width, int alignment) {
      float diff = width - getIconWidth();
      if (diff > 0)
         box = new HorizontalBox(box, box.getWidth() + diff, alignment);
   }

   /**
    * Change the height of the TeXIcon. The new height must be greater than the current
    * height, otherwise the icon will remain unchanged. The formula will be aligned on top 
    * (TeXConstants.TOP), at the bottom (TeXConstants.BOTTOM) or will be centered 
    * in the middle (TeXConstants.CENTER).
    * 
    * @param height the new height of the TeXIcon
    * @param alignment a vertical alignment constant: TOP, BOTTOM or CENTER
    */
   public void setIconHeight(int height, int alignment) {
      float diff = height - getIconHeight();
      if (diff > 0)
         box = new VerticalBox(box, diff, alignment);
   }

   /**
    * Get the total height of the TeXIcon. This also includes the insets.
    */
   public int getIconHeight() {
      return (int) ((box.getHeight() + box.getDepth()) * size + 0.99
            + insets.top + insets.bottom);
   }

   /**
    * Get the total width of the TeXIcon. This also includes the insets.
    */
   public int getIconWidth() {
      return (int) (box.getWidth() * size + 0.99 + insets.left + insets.right);
   }
   
   public float getBaseLine() {
       return (float)( (box.getHeight() * size + 0.99 + insets.top) /
               ((box.getHeight() + box.getDepth()) * size + 0.99 + insets.top + insets.bottom));
   }

   public void paintIcon(Component c, Graphics g, int x, int y) {
	   paintIcon(c.getForeground(), g, x, y);
   }
   
   /**
    * 绘制此 icon. 问题: 我们能否绘制到 image 上面, 并将 image 保存起来? 
    * 
    * Paint the {@link TeXFormula} that created this icon.    
    */
   public void paintIcon(Color foreground, Graphics g, int x, int y) {
      Graphics2D g2 = (Graphics2D) g;
      // copy graphics settings
      RenderingHints oldHints = g2.getRenderingHints();
      AffineTransform oldAt = g2.getTransform();
      Color oldColor = g2.getColor();

      // new settings
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      g2.scale(size, size); // the point size 
      g2.setColor(foreground); // foreground will be used as default painting color 

      // draw formula box
      box.draw(g2, (x + insets.left) / size, (y + insets.top) / size
            + box.getHeight());

      // restore graphics settings
      g2.setRenderingHints(oldHints);
      g2.setTransform(oldAt);
      g2.setColor(oldColor);
   }
}
