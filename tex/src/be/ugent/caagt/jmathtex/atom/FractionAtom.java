/* FractionAtom.java
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

package be.ugent.caagt.jmathtex.atom;

import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.HorizontalBox;
import be.ugent.caagt.jmathtex.box.HorizontalRule;
import be.ugent.caagt.jmathtex.box.StrutBox;
import be.ugent.caagt.jmathtex.box.VerticalBox;
import be.ugent.caagt.jmathtex.ex.InvalidUnitException;

/**
 * 表示分数的元件.
 * An atom representing a fraction.
 */
public class FractionAtom extends Atom {
    
	// 是否缺省的(分数线)粗细不应该在分数线上使用.
    // whether the default thickness should not be used for the fraction line
    private boolean noDefault = false;
    
    // unit used for the thickness of the fraction line
    private int unit;
    
    // 分子/分母的对齐设置. (缺省都是居中, 水平或垂直的)
    // alignment settings for the numerator and denominator
    private int numAlign = TeXConstants.ALIGN_CENTER,
            denomAlign = TeXConstants.ALIGN_CENTER;
    
    // the atoms representing the numerator(分子) and denominator(分母)
    private Atom numerator, denominator;
    
    // thickness of the fraction line
    private float thickness;
    
    // thickness of the fraction line relative to the default thickness
    private float defFactor;
    
    // whether the "defFactor" value should be used
    private boolean defFactorSet = false;
    
    /**
     * Uses the default thickness for the fraction line
     *
     * @param num the numerator
     * @param den the denominator
     */
    public FractionAtom(Atom num, Atom den) {
        this(num, den, true);
    }
    
    /**
     * Uses the default thickness for the fraction line
     *
     * @param num the numerator
     * @param den the denominator
     * @param rule whether the fraction line should be drawn
     */
    public FractionAtom(Atom num, Atom den, boolean rule) {
        this(num, den, !rule, TeXConstants.UNIT_PIXEL, 0f);
    }
    
    /**
     * Depending on noDef, the given thickness and unit will be used (<-> the default
     * thickness).
     *
     * @param num the numerator
     * @param den the denominator
     * @param noDef whether the default thickness should not be used for the fraction line
     * @param unit a unit constant for the line thickness
     * @param t the thickness of the fraction line (in the given unit)
     * @throws InvalidUnitException if the given integer is not a valid unit constant
     */
    public FractionAtom(Atom num, Atom den, boolean noDef, int unit, float t) throws InvalidUnitException {
        // check unit
        SpaceAtom.checkUnit(unit);
        
        // unit ok
        numerator = num;
        denominator = den;
        noDefault = noDef;
        thickness = t;
        this.unit = unit;
        type = TeXConstants.TYPE_INNER;
    }
    
    /**
     * Uses the default thickness for the fraction line.
     *
     * @param num the numerator
     * @param den the denominator
     * @param rule whether the fraction line should be drawn
     * @param numAlign alignment of the numerator
     * @param denomAlign alignment of the denominator
     */
    public FractionAtom(Atom num, Atom den, boolean rule, int numAlign,
            int denomAlign) {
        this(num, den, rule);
        this.numAlign = checkAlignment(numAlign);
        this.denomAlign = checkAlignment(denomAlign);
    }
    
    /**
     * The thickness of the fraction line will be "defFactor" times the default thickness.
     *
     * @param num the numerator
     * @param den the denominator
     * @param defFactor the thickness of the fraction line relative to the default thickness
     * @param numAlign alignment of the numerator
     * @param denomAlign alignment of the denominator
     */
    public FractionAtom(Atom num, Atom den, float defFactor, int numAlign,
            int denomAlign) {
        this(num, den, true, numAlign, denomAlign);
        this.defFactor = defFactor;
        defFactorSet = true;
    }
    
    /**
     * The thickness of the fraction line is determined by the given value "t" in the
     * given unit.
     *
     * @param num the numerator
     * @param den the denominator
     * @param unit a unit constant for the line thickness
     * @param t the thickness of the fraction line (in the given unit)
     * @param numAlign alignment of the numerator
     * @param denomAlign alignment of the denominator
     */
    public FractionAtom(Atom num, Atom den, int unit, float t, int numAlign,
            int denomAlign) {
        this(num, den, unit, t);
        this.numAlign = checkAlignment(numAlign);
        this.denomAlign = checkAlignment(denomAlign);
    }
    
    /**
     * The thickness of the fraction line is determined by the given value "t" in the
     * given unit.
     *
     * @param num the numerator
     * @param den the denominator
     * @param unit a unit constant for the line thickness
     * @param t the thickness of the fraction line (in the given unit)
     */
    public FractionAtom(Atom num, Atom den, int unit, float t) {
        this(num, den, true, unit, t);
    }
    
    // Checks if the alignment constant is valid.
    // If not, a default value will be used.
    private int checkAlignment(int align) {
        if (align == TeXConstants.ALIGN_LEFT ||
                align == TeXConstants.ALIGN_RIGHT)
            return align;
        else
            return TeXConstants.ALIGN_CENTER;
    }
    
    public Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont();
        int style = env.getStyle();
        
        // set thickness to default if default value should be used
        float drt = tf.getDefaultRuleThickness(style);
        if (noDefault)
            // convert the thickness to pixels
            thickness = new SpaceAtom(unit, 0, thickness, 0).createBox(env).getHeight();
        else
            thickness = (defFactorSet ? defFactor * drt : drt);
        
        // create equal width boxes (in appropriate styles); 创建分子,分母的盒子.
        Box num = (numerator == null ? new StrutBox(0, 0, 0, 0) : numerator
                .createBox(env.numStyle()));
        Box denom = (denominator == null ? new StrutBox(0, 0, 0, 0) : denominator
                .createBox(env.denomStyle()));
        	// 根据分子,分母宽度, 使得它们等宽以指定对齐.
        if (num.getWidth() < denom.getWidth())
            num = new HorizontalBox(num, denom.getWidth(), numAlign);
        else
            denom = new HorizontalBox(denom, num.getWidth(), denomAlign);
        
        // calculate default shift amounts; 需要参见 TeXFont.getNum1,2,3, getDenom1,2 等含义.
        float shiftUp, shiftDown;
        if (style < TeXConstants.STYLE_TEXT) {
            shiftUp = tf.getNum1(style);
            shiftDown = tf.getDenom1(style);
        } else {
            shiftDown = tf.getDenom2(style);
            if (thickness > 0)
                shiftUp = tf.getNum2(style);
            else
                shiftUp = tf.getNum3(style);
        }
        
        // upper part of vertical box = numerator; 结果盒子应为 VerticalBox
        VerticalBox vBox = new VerticalBox();
        vBox.add(num);
        
        // calculate clearance clr, adjust shift amounts and create vertical box
        float clr, delta, axis = tf.getAxisHeight(style);
        
        if (thickness > 0) { // WITH fraction rule
            // clearance clr
            if (style < TeXConstants.STYLE_TEXT)
                clr = 3 * thickness;
            else
                clr = thickness;
            
            // adjust shift amounts
            delta = thickness / 2;
            float kern1 = shiftUp - num.getDepth() - (axis + delta), kern2 = axis
                    - delta - (denom.getHeight() - shiftDown);
            float delta1 = clr - kern1, delta2 = clr - kern2;
            if (delta1 > 0) {
                shiftUp += delta1;
                kern1 += delta1;
            }
            if (delta2 > 0) {
                shiftDown += delta2;
                kern2 += delta2;
            }
            
            // fill vertical box
            vBox.add(new StrutBox(0, kern1, 0, 0));
            vBox.add(new HorizontalRule(thickness, num.getWidth(), 0));
            vBox.add(new StrutBox(0, kern2, 0, 0));
        } else { // WITHOUT fraction rule
            // clearance clr
            if (style < TeXConstants.STYLE_TEXT)
                clr = 7 * drt;
            else
                clr = 3 * drt;
            
            // adjust shift amounts
            float kern = shiftUp - num.getDepth()
            - (denom.getHeight() - shiftDown);
            delta = (clr - kern) / 2;
            if (delta > 0) {
                shiftUp += delta;
                shiftDown += delta;
                kern += 2 * delta;
            }
            
            // fill vertical box
            vBox.add(new StrutBox(0, kern, 0, 0));
        }
        
        // finish vertical box; 添加分母, 计算/设置合适的高度, 深度. (基线)
        vBox.add(denom);
        vBox.setHeight(shiftUp + num.getHeight());
        vBox.setDepth(shiftDown + denom.getDepth());
        return vBox;
    }

    @Override
    public String toString() {
    	return "FractionAtom{num=" + this.numerator + ", denom=" + this.denominator + "}";
    }
    
    public void dump() {
    	System.out.println(toString());
    }

    /**
     * 生成 XML. 格式为:
     *   <FractionAtom noDefault=bool unit=int thickness=float defFactor=float defFactorSet=bool>
     *     <numerator>...分子...</numerator>
     *     <denominator>...分母...</denominator>
     *   </FractionAtom>
     *   
     */
    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.appendRaw("<FractionAtom ").attribute("noDefault", this.noDefault)
    		.blank().attribute("unit", unit)
    		.blank().attribute("thickness", thickness)
    		.blank().attribute("defFactor", defFactor)
    		.blank().attribute("defFactorSet", defFactorSet)
    		.appendRaw(">").ln();
    	
    	super.superToXml(sxw);
    	
    	if (numerator != null) {
    		sxw.beginElement("numerator").ln();
    		numerator.toXml(sxw, null);
    		sxw.endElement("numerator").ln();
    	}
    	if (denominator != null) {
    		sxw.beginElement("denominator").ln();
    		denominator.toXml(sxw, null);
    		sxw.endElement("denominator").ln();
    	}
    	
    	sxw.endElement("FractionAtom").ln();
    }
}
