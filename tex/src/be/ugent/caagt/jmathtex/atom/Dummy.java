/* Dummy.java
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

import be.ugent.caagt.jmathtex.CharFont;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.box.Box;

/**
 * 被 RowAtom 使用. (根据 TeX 算法) textSymbol-属性和元件类型能改变.
 *   或者这个元件可以被 lig 替代(如果是一个 CharAtom). 但是 atom 不能改变(model 不改变, 
 *   否则两次可以创建出不同的盒子!!)...
 *   这个 dummy 元件确保对 atom 的改变在 createBox 之后会被重置(改回去).
 * 
 * dummy -- 哑元,伪元...(伪，哑；空；虚；假)
 * 
 * Used by RowAtom. The "textSymbol"-property and the type of an atom can change 
 * (according to the TeX-algorithms used). Or this atom can be replaced by a ligature, 
 * (if it was a CharAtom). But atoms cannot be changed, otherwise 
 * different boxes could be made from the same TeXFormula, and that is not desired!
 * This "dummy atom" makes sure that changes to an atom (during the createBox-method of
 * a RowAtom) will be reset.
 */
public class Dummy {

   private Atom el; // 原 atom (被 dummy 包装起来的)

   private boolean textSymbol = false; // ?? 是否为 textSymbol

   private int type = -1; // 类型, -1 表示类型未被修改.

   /**
    * 使用指定的 atom 创建一个 Dummy 的新实例.
    * Creates a new Dummy for the given atom.
    * 
    * @param a an atom
    */
   public Dummy(Atom a) {
      el = a;
   }

   /**
    * (临时)改变 atom 的类型(在 RowAtom.changeToOrd() 函数中有对该函数的调用)
    * Changes the type of the atom
    * 
    * @param t the new type
    */
   public void setType(int t) {
      type = t;
   }

   /**
    * 
    * @return the changed type, or the old left type if it hasn't been changed
    */
   public int getLeftType() {
      return (type >= 0 ? type : el.getLeftType());
   }

   /**
    * 
    * @return the changed type, or the old right type if it hasn't been changed
    */
   public int getRightType() {
      return (type >= 0 ? type : el.getRightType());
   }

   /**
    * atom 是否 CharSymbol?
    * @return
    */
   public boolean isCharSymbol() {
      return el instanceof CharSymbol;
   }

   /*
    * This method will only be called if isCharSymbol returns true.
    */
   public CharFont getCharFont(TeXFont tf) {
      return ((CharSymbol) el).getCharFont(tf);
   }

   /**
    * 将这个 atom 改变为指定的 `lig atom'. (连排处理)
    * Changes this atom into the given "ligature atom".
    * 
    * @param a the ligature atom
    */
   public void changeAtom(FixedCharAtom a) {
      textSymbol = false;
      type = -1;
      el = a;
   }

   /*
    * 创建盒子.
    */
   public Box createBox(TeXEnvironment rs) {
      if (textSymbol)
         ((CharSymbol) el).markAsTextSymbol();  // 标记为 textSymbol??
      Box b = el.createBox(rs); // 然后创建盒子, 创建之后再恢复标记.
      if (textSymbol)
         ((CharSymbol) el).removeMark(); // atom remains unchanged!
      return b;
   }

   public void markAsTextSymbol() {
      textSymbol = true;
   }

   public boolean isKern() {
      return el instanceof SpaceAtom;
   }

   // only for Row-elements
   public void setPreviousAtom(Dummy prev) {
      if (el instanceof Row)
         ((Row) el).setPreviousAtom(prev);
   }
}
