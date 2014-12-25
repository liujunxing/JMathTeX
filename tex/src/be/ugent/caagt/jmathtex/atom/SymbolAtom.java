/* SymbolAtom.java
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

import java.util.BitSet;
import java.util.Map;

import be.ugent.caagt.jmathtex.Char;
import be.ugent.caagt.jmathtex.CharFont;
import be.ugent.caagt.jmathtex.SimpleXmlWriter;
import be.ugent.caagt.jmathtex.TeXConstants;
import be.ugent.caagt.jmathtex.TeXEnvironment;
import be.ugent.caagt.jmathtex.TeXFont;
import be.ugent.caagt.jmathtex.TeXSymbolParser;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.CharBox;
import be.ugent.caagt.jmathtex.ex.InvalidSymbolTypeException;
import be.ugent.caagt.jmathtex.ex.SymbolNotFoundException;

/**
 * 一个盒子,表示一个符号(一个非字母数字的字符);
 * symbol: 符号, 记号, 象征, 标志.
 * A box representing a symbol (a non-alphanumeric character).
 */
public class SymbolAtom extends CharSymbol {
    
    // whether it's is a delimiter symbol
    private final boolean delimiter;
    
    // symbol name; 此符号的名字, 如 "hat"
    private final String name;
    
    /** 
     * contains all defined symbols; 从名字=>符号的映射.
     * 加载自文件 TeXSymbols.xml
     */
    private static Map<String, SymbolAtom> symbols;
    
    // contains all the possible valid symbol types; 包含所有可能的(也仅包含)有效的符号类型.
    private static BitSet validSymbolTypes;
    
    // 静态初始化
    static {
        symbols = new TeXSymbolParser().readSymbols();
        
        // set valid symbol types (这样多麻烦, 直接用 x|y|z 不就可以了?? )
        validSymbolTypes =  new BitSet(16);
        validSymbolTypes.set(TeXConstants.TYPE_ORDINARY); 			// =0
        validSymbolTypes.set(TeXConstants.TYPE_BIG_OPERATOR); 		// =1
        validSymbolTypes.set(TeXConstants.TYPE_BINARY_OPERATOR);	// =2
        validSymbolTypes.set(TeXConstants.TYPE_RELATION);			// =3
        validSymbolTypes.set(TeXConstants.TYPE_OPENING);			// =4
        validSymbolTypes.set(TeXConstants.TYPE_CLOSING);			// =5
        validSymbolTypes.set(TeXConstants.TYPE_PUNCTUATION);		// =6
        validSymbolTypes.set(TeXConstants.TYPE_ACCENT);				// =10(重音)
    }
    
    public SymbolAtom(SymbolAtom s, int type) throws InvalidSymbolTypeException {
    	// 检查 type 是有效的类型, 只有八种类型是 SymbolAtom 使用的.
    	// 问题: 异常种类太多??
        if (!validSymbolTypes.get(type))
            throw new InvalidSymbolTypeException(
                    "The symbol type was not valid! "
                    + "Use one of the symbol type constants from the class 'TeXConstants'.");
        name = s.name;
        this.type = type;
        delimiter = s.delimiter;
    }
    
    /**
     * Constructs a new symbol. This used by "TeXSymbolParser" and the symbol
     * types are guaranteed to be valid.
     *
     * @param name symbol name
     * @param type symbol type constant
     * @param del whether the symbol is a delimiter
     */
    public SymbolAtom(String name, int type, boolean del) {
        this.name = name;
        this.type = type;
        delimiter = del;
    }
    
    /**
     * 在表 symbols 中查找指定的 name(如名字 "plus"), 返回对应的 SymbolAtom 对象(其表示该 symbol)
     * 该表示在类初始化时从配置 'TeXSymbols.xml' 文件中加载进来的.
     * 
     * Looks up the name in the table and returns the corresponding SymbolAtom representing
     * the symbol (if it's found).
     *
     * @param name the name of the symbol
     * @return a SymbolAtom representing the found symbol
     * @throws SymbolNotFoundException if no symbol with the given name was found
     */
    public static SymbolAtom get(String name) throws SymbolNotFoundException {
    	SymbolAtom obj = symbols.get(name);
        if (obj == null) // not found
            throw new SymbolNotFoundException(name);
        else
            return obj;
    }
    
    /**
     *
     * @return true if this symbol can act as a delimiter to embrace formulas
     */
    public boolean isDelimiter() {
        return delimiter;
    }
    
    public String getName() {
        return name;
    }
    
    public Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont(); // 得到字体(管理器,容器)
        int style = env.getStyle();    // 显示样式(由调用者给出, 或父 Atom 根据需要调整)
        Char c = tf.getChar1(name, style);  // 得到此实例符号的 Char 对象. 包含 字符代码,字体,尺寸信息.
        return new CharBox(c);
    }
    
    public CharFont getCharFont(TeXFont tf) {
        // style doesn't matter here
        return tf.getChar1(name, TeXConstants.STYLE_DISPLAY).getCharFont();
    }

    @Override
    public String toString() {
    	return "SymbolAtom{name=" + name + ", delim=" + this.delimiter + "}";
    }
 
    public void dump() {
    	System.out.println(toString());
    }

    /**
     * 转换为 XML:
     *   <SymbolAtom name="string" delimiter=bool">
     *     <Atom>...基类的... </Atom>
     *   </SymbolAtom>
     */
    public void toXml(SimpleXmlWriter sxw, Object hint) {
    	sxw.appendRaw("<SymbolAtom ")
    		.attribute("name", name).blank()
    		.attribute("delimiter", delimiter).appendRaw(">").ln();
    	
    	super.superToXml(sxw);
    	
    	sxw.endElement("SymbolAtom").ln();
    }
}
