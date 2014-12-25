/* HorizontalBox.java
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

package be.ugent.caagt.jmathtex.box;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ListIterator;

import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXFont;

/**
 * A box composed of a horizontal row of child boxes.
 */
public class HorizontalBox extends Box {
    
    private float curPos = 0; // NOPMD
    
    /**
     * 根据指定的 w(宽度), alignment(对齐方式) 为 box 创建必要的支柱(Strut)来使得 box 在
     *   合适的位置. 这里似乎有一个假设, 就是 b.width < w.
     * @param b
     * @param w
     * @param alignment
     */
    public HorizontalBox(Box b, float w, int alignment) {
        float rest = w - b.getWidth();
        if (alignment == TeXConstants.ALIGN_CENTER) { // 居中. 但是... 有可能 rest < 0 ???
            StrutBox s = new StrutBox(rest / 2, 0, 0, 0);
            add(s);
            add(b); // 水平盒子: StrutBox + b + StrutBox, 从而使得 b 居中.
            add(s);
        } else if (alignment == TeXConstants.ALIGN_LEFT) { // 居左
            add(b);
            add(new StrutBox(rest, 0, 0, 0)); // 水平盒子: b + Strut
        } else if (alignment == TeXConstants.ALIGN_RIGHT) { // 居右.
            add(new StrutBox(rest, 0, 0, 0)); // 水平盒子: Strut + b
            add(b);
        }
    }
    
    public HorizontalBox(Box b) {
        add(b);
    }
    
    public HorizontalBox() {
        // basic horizontal box
    }
    
    public HorizontalBox(Color fg, Color bg) {
        super(fg, bg);
    }
    
    public void draw(Graphics2D g2, float x, float y) {
        startDraw(g2, x, y);
        float xPos = x;
        for (Box box: children) {
            box.draw(g2, xPos, y + box.shift);
            xPos += box.getWidth();
        }
        endDraw(g2);
    }
    
    // 添加一个盒子到末尾.
    public final void add(Box b) {
        recalculate(b);  // 这里每次添加都要重新计算, 比较怪.
        super.add(b);
    }
    
    private void recalculate(Box b) {
        curPos += b.getWidth();
        width = Math.max(width, curPos);
        height = Math.max((children.size() == 0 ? Float.NEGATIVE_INFINITY
                : height), b.height - b.shift);
        depth = Math.max(
                (children.size() == 0 ? Float.NEGATIVE_INFINITY : depth), b.depth
                + b.shift);
    }
    
    @SuppressWarnings("unchecked")
	public int getLastFontId() {
        // iterate from the last child box to the first untill a font id is found
        // that's not equal to NO_FONT
        int fontId = TeXFont.NO_FONT;
        for (ListIterator it = children.listIterator(children.size()); fontId == TeXFont.NO_FONT
                && it.hasPrevious();)
            fontId = ((Box) it.previous()).getLastFontId();
        
        return fontId;
    }

    public String toString() {
    	return "HorizontalBox{e[]=" + super.children + "}";
    }

    public void dump() {
 	   System.out.println(toString());
    }

    /**
     * XML:
     *   <HorizontalBox curPos=xxx>
     *     <Box> ... 基类的... </Box>
     *   </HorizontalBox>
     */
    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.appendRaw("<HorizontalBox")
    	   .blank().attribute("curPos", curPos);
    	super.addAttr(sxw);
    	sxw.appendRaw(">").ln();
    	
    	super.addChildren(sxw);
    	
    	sxw.endElement("HorizontalBox").ln();
    }
}
