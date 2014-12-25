/* Atom.java
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
import be.ugent.caagt.jmathtex.box.Box;

/**
 * 所有逻辑数学构件(元件)的基类. 所有的子类都必须实现 createBox() 方法, 将自己转换为实作的盒子(box, 然后可以绘制).
 *   也需要定义它们的类型, 用于确定和临近的元件的粘连(glue)在一个 `行构造中'.
 *   
 * (TeXBook 第17章)
 * 最重要的项目称为 原子(atom), 有三个部分: 核,上标,下标.
 * 
 * 有十三种原子, 每种在公式中的表现都不同; 下表列出不同种类的完整列表:
 *   Ord       普通原子, 如 'x'(数学斜体的)
 *   Op			巨算符, 如 \sum.
 *   Bin		二元运算符, 如 +
 *   Rel		关系运算符, 如 =
 *   Open		开原子, 如 (
 *   Close		闭原子, 如 )
 *   Punct		标点, 如 ','(逗号)
 *   Inner		内部原子, 如 (二分之一的那种字符)
 *   Over		在其它原子上面加横线(东西)的
 *   Under		在其它原子下面加横线的
 *   Acc		加重音的, 如 x 上面加 ^ 形状的帽子.
 *   Rad		根号原子, 如 '根号2'
 *   Vcent		\vcenter 生成的垂直居中 vbox 
 * 
 * 原子的核(nucleus), 上标(superscript), 下标(subscript) 称之为它的字段(域).
 * 每个字段有四种可能的值:
 *   1. 空的(empty), 例如有的原子没有上标或下标, 或都没有, 甚至没有核.
 *   2. 数学符号(由字体族--fontid和位置--ch给出)
 *   3. 盒子.
 *   4. 数学列.
 * 
 * 
 * An abstract superclass for all logical mathematical constructions that can be
 * a part of a TeXFormula. All subclasses must implement the abstract 
 * {@link #createBox(TeXEnvironment)} method that transforms this logical unit into
 * a concrete box (that can be painted). They also must define their type, used for
 * determining what glue to use between adjacent atoms in a "row construction". That can
 * be one single type by asigning one of the type constants to the {@link #type} field.
 * But they can also be defined as having two types: a "left type" and a "right type".
 * This can be done by implementing the methods {@link #getLeftType()} and
 * {@link #getRightType()}.
 * The left type will then be used for determining the glue between this atom and the
 * previous one (in a row, if any) and the right type for the glue between this atom and 
 * the following one (in a row, if any).
 * 
 * @author Kurt Vermeulen
 */
public abstract class Atom {

   /**
    * 元件的类型, 缺省值: 一般元件.
    * The type of the atom (default value: ordinary atom)
    */
   public int type = TeXConstants.TYPE_ORDINARY;

   /**
    * 转换此 atom 到一个 box, 使用由 'parent' 设置的属性, 类似于 TeX 样式, 
    *   最后使用的字体, 颜色设置,...
    * Convert this atom into a {@link Box}, using properties set by "parent"
    * atoms, like the TeX style, the last used font, color settings, ...
    * 
    * @param env the current environment settings
    * @return the resulting box.
    */
   public abstract Box createBox(TeXEnvironment env);

   /**
    * Get the type of the leftermost child atom. Most atoms have no child atoms,
    * so the "left type" and the "right type" are the same: the atom's type. This
    * also is the default implementation.
    * But Some atoms are composed of child atoms put one after another in a 
    * horizontal row. These atoms must override this method.
    * 
    * @return the type of the leftermost child atom
    */
   public int getLeftType() {
      return type;
   }

   /**
    * Get the type of the rightermost child atom. Most atoms have no child atoms,
    * so the "left type" and the "right type" are the same: the atom's type. This
    * also is the default implementation.
    * But Some atoms are composed of child atoms put one after another in a 
    * horizontal row. These atoms must override this method.
    * 
    * @return the type of the rightermost child atom
    */
   public int getRightType() {
      return type;
   }
   
   /**
    * 用于调试, 以较详细的方式输出 atom 的内容.
    */
   public abstract void dump();
   
   /**
    * 以 XML 的格式输出此 atom, 如果有子 atom, 则也将子 atom 输出.
    */
   public abstract void toXml(SimpleXmlWriter sxw, Object hint);
   
   protected void superToXml(SimpleXmlWriter sxw) {
	   sxw.appendRaw("<Atom ").attribute("type", type).appendRaw("/>").ln();
   }
}
