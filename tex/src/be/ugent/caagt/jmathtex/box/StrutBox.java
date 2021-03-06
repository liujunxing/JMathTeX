/* StrutBox.java
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

import java.awt.Graphics2D;

import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXFont;

/**
 * A box representing whitespace.
 */
public class StrutBox extends Box {
    
    public StrutBox(float w, float h, float d, float s) {
        width = w;
        height = h;
        depth = d;
        shift = s;
    }
    
    public void draw(Graphics2D g2, float x, float y) {
        // no visual effect
    }
    
    public int getLastFontId() {
        return TeXFont.NO_FONT;
    }

    public String toString() {
    	return "StrutBox{width=" + super.width + ", height=" + super.height + ", depth=" + super.depth + "}";
    }

    public void dump() {
 	   System.out.println(toString());
    }

    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.appendRaw("<StrutBox ");
    	super.addAttr(sxw);
    	sxw.appendRaw(">").ln();
    	
    	super.addChildren(sxw);
    	
    	sxw.endElement("StrutBox").ln();
    }
}

