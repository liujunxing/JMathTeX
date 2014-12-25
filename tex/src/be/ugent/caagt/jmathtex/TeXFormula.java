/* TeXFormula.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

import be.ugent.caagt.jmathtex.atom.AccentedAtom;
import be.ugent.caagt.jmathtex.atom.Atom;
import be.ugent.caagt.jmathtex.atom.BigOperatorAtom;
import be.ugent.caagt.jmathtex.atom.CharAtom;
import be.ugent.caagt.jmathtex.atom.ColorAtom;
import be.ugent.caagt.jmathtex.atom.FencedAtom;
import be.ugent.caagt.jmathtex.atom.FractionAtom;
import be.ugent.caagt.jmathtex.atom.NthRoot;
import be.ugent.caagt.jmathtex.atom.OverUnderDelimiter;
import be.ugent.caagt.jmathtex.atom.OverlinedAtom;
import be.ugent.caagt.jmathtex.atom.PhantomAtom;
import be.ugent.caagt.jmathtex.atom.RowAtom;
import be.ugent.caagt.jmathtex.atom.ScriptsAtom;
import be.ugent.caagt.jmathtex.atom.SpaceAtom;
import be.ugent.caagt.jmathtex.atom.SymbolAtom;
import be.ugent.caagt.jmathtex.atom.TypedAtom;
import be.ugent.caagt.jmathtex.atom.UnderOverAtom;
import be.ugent.caagt.jmathtex.atom.UnderlinedAtom;
import be.ugent.caagt.jmathtex.atom.VCenteredAtom;
import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.StrutBox;
import be.ugent.caagt.jmathtex.ex.DelimiterMappingNotFoundException;
import be.ugent.caagt.jmathtex.ex.EmptyFormulaException;
import be.ugent.caagt.jmathtex.ex.FormulaNotFoundException;
import be.ugent.caagt.jmathtex.ex.InvalidAtomTypeException;
import be.ugent.caagt.jmathtex.ex.InvalidDelimiterException;
import be.ugent.caagt.jmathtex.ex.InvalidDelimiterTypeException;
import be.ugent.caagt.jmathtex.ex.InvalidSymbolTypeException;
import be.ugent.caagt.jmathtex.ex.InvalidTeXFormulaException;
import be.ugent.caagt.jmathtex.ex.InvalidUnitException;
import be.ugent.caagt.jmathtex.ex.ParseException;
import be.ugent.caagt.jmathtex.ex.SymbolNotFoundException;

/*
 * 这个类有太多的 addXXX 方法了, 是否可以减少一些呢??
 */

/**
 * Represents a logical mathematical formula that will be displayed (by creating a
 * {@link TeXIcon} from it and painting it) using algorithms that are based on the
 * TeX algorithms.
 * <p>
 * These formula's can be built using the built-in primitive TeX parser
 * (methods with String arguments) or using other TeXFormula objects. Most methods
 * have (an) equivalent(s) where one or more TeXFormula arguments are replaced with
 * String arguments. These are just shorter notations, because all they do is parse
 * the string(s) to TeXFormula's and call an equivalent method with (a) TeXFormula argument(s).
 * Most methods also come in 2 variants. One kind will use this TeXFormula to build
 * another mathematical construction and then change this object to represent the newly
 * build construction. The other kind will only use other
 * TeXFormula's (or parse strings), build a mathematical construction with them and
 * insert this newly build construction at the end of this TeXFormula.
 * Because all the provided methods return a pointer to this (modified) TeXFormula
 * (except for the createTeXIcon method that returns a TeXIcon pointer),
 * method chaining is also possible.
 * <p>
 * <b> Important: All the provided methods modify this TeXFormula object, but all the
 * TeXFormula arguments of these methods will remain unchanged and independent of
 * this TeXFormula object!</b>
 */
public class TeXFormula {
	public static boolean debug_stop = false;
    
    // TeX commands and text styles (for parsing)
    private static Set<String> commands = new HashSet<String>();
    
    /**
     * 配置的 TextStyle 的名字, 当前为 "mathrm", "mathit", "mathcal".
     * 疑问: 很多地方都配置有东西, 似乎有重叠, 有相互引用, 难道不能简化一下? 在一个地方配? 不重复? 配置相互关系简单?
     */
    private static Set<String> textStyles;
    
    // table for putting delimiters over and under formula's,
    // indexed by constants from "TeXConstants"
    private static final String[][] delimiterNames = {
        { "lbrace", "rbrace" },
        { "lsqbrack", "rsqbrack" },
        { "lbrack", "rbrack" },
        { "downarrow", "downarrow" },
        { "uparrow", "uparrow" },
        { "updownarrow", "updownarrow" },
        { "Downarrow", "Downarrow" },
        { "Uparrow", "Uparrow" },
        { "Updownarrow", "Updownarrow" },
        { "vert", "vert" },
        { "Vert", "Vert" }
    };
    
    /** the escape character; 表示转义字符 `\' */
    private static final char ESCAPE = '\\';
    
    // grouping characters (for parsing)
    /** 开始一个组 */
    private static final char L_GROUP = '{';
    private static final char R_GROUP = '}';
    private static final char L_BRACK = '[';
    private static final char R_BRACK = ']';
    
    // used as second index in "delimiterNames" table (over or under)
    private static final int OVER_DEL = 0;
    private static final int UNDER_DEL = 1;
    
    // for comparing floats with 0
    public static final float PREC = 0.0000001f;
    
    /** predefined TeXFormula's; 预定义的公式, 加载自 `TeXFormulaSettings.xml' 文件中. */
    private static Map<String, TeXFormula> predefinedTeXFormulas = new HashMap<String, TeXFormula>();
    
    // script characters (for parsing)
    private static final char SUB_SCRIPT = '_';
    private static final char SUPER_SCRIPT = '^';
    private static final char PRIME = '\'';
    
    // 和别处的 symbol-to-character 映射相反, 这里通过 character-映射到-symbolname 
    
    /**
     * 从字符代码(char, 如 '+')到其符号名字(如 "plus")的映射表.
     * 配置在 TeXFormulaSettings.xml 中, <CharacterToSymbolMappings> 节点下. 
     * 如 !=faculty, *=ast, +=plus, ;=semicolon, ?=question, [=lsqbrack 等.
     * 
     *  character-to-symbol mappings
     */
    public static String[] symbolMappings;
    
    /**
     * 从字符代码(char, 如 '[')到其符号名(如 'lsqbrack') 的映射表.
     * <CharacterToDelimiterMappings> 在配置文件 TeXFormulaSettings.xml 中.
     * 
     * character-to-delimiter mappings
     */
    private static String[] delimiterMappings;
    
    // 静态初始化.
    static {
        // character-to-symbol and character-to-delimiter mappings
        TeXFormulaSettingsParser parser = new TeXFormulaSettingsParser();
        
        // 解析子节点 <CharacterToSymbolMappings>
        symbolMappings = parser.parseSymbolMappings();
        
        // 解析子节点 <CharacterToDelimiterMappings>
        delimiterMappings = parser.parseDelimiterMappings();
        
        // textstyle commands; 解析子节点 <TextStyles>
        textStyles = parser.parseTextStyles();
        
        // commands; 添加两个特殊处理的数学命令? (不含 \above 等??)
        commands.add("frac");
        commands.add("sqrt");
        
        // predefined TeXFormula's; 预定义的数学公式.
        new PredefinedTeXFormulaParser().parse(predefinedTeXFormulas);
    }
    
    /** the string to be parsed (要被解析/正在被解析/已被解析的)字符串 */
    private String parseString;
    
    /** current position in the parse string */
    private int pos = 0;
    
    /** the root atom of the "atom tree" that represents the formula
     * 表示此公式的 atom 树的树根.  
     */
    public Atom root = null;
    
    /** the current text style */
    private String textStyle = null;
    
    /**
     * 创建一个空的 TeX 公式.
     * Creates an empty TeXFormula.
     *
     */
    public TeXFormula() {
        // do nothing
    }
    
    /**
     * Creates a new TeXFormula from a list of TeXFormula objects.
     * <p>
     * If the list is empty (or null), then an empty TeXFormula will be created.
     * Otherwise, the newly created TeXFormula is the same as if
     * all the TeXFormula's in the list were added one after another (starting with the
     * first one) to an empty TeXFormula using the {@link #add(TeXFormula)} method.
     * <p>
     * <b> The new TeXFormula is independent of all the TeXFormula's from the list!</b>
     *
     * @param l a list of TeXFormula objects
     */
    public TeXFormula(List<TeXFormula> l) {
        if (l != null) {
            if (l.size() == 1)
                addImpl(l.get(0));
            else {
                try {
                    root = new RowAtom(l);
                } catch (EmptyFormulaException e) {
                    root = null;
                }
            }
        }
    }
    
    /**
     * 解析所给的字符串 s 来构造出一个 TeXFormula 对象(使用主(?原语) TeX 解析器)
     * Creates a new TeXFormula by parsing the given string (using a primitive TeX parser).
     *
     * @param s the string to be parsed
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula(String s) throws ParseException {
        this(s, null);
    }
    
   /**
    * 用于当找到 text style 命令在字符串中时使用.
    * 这些 text style 命令应该是 \displaystyle, \textstyle, \scriptstyle, \scriptscriptstyle (见书 17.5)
    * 
    * Creates a TeXFormula by parsing the given string in the given text style.
    * Used when a text style command was found in the parse string.
    */
    private TeXFormula(String s, String textStyle) throws ParseException {
        this.textStyle = textStyle;
        if (s != null && s.length() != 0)
            parse(s);
    }
    
    /**
     * Creates a new TeXFormula that is a copy of the given TeXFormula.
     * <p>
     * <b>Both TeXFormula's are independent of one another!</b>
     *
     * @param f the formula to be copied
     */
    public TeXFormula(TeXFormula f) {
        if (f != null)
            addImpl(f);
    }
    
   /*
    * Inserts an atom at the end of the current formula
    */
    private TeXFormula add(Atom el) {
        if (el != null) {
            if (root == null)
                root = el;
            else {
                if (!(root instanceof RowAtom))
                    root = new RowAtom(root);
                ((RowAtom) root).add(el);
            }
        }
        return this;
    }
    
    /**
     * Parses the given string and inserts the resulting formula
     * at the end of the current TeXFormula.
     *
     * @param s the string to be parsed and inserted
     * @throws ParseException if the string could not be parsed correctly
     * @return the modified TeXFormula
     */
    public TeXFormula add(String s) throws ParseException {
        if (s != null && s.length() != 0) {
            // reset parsing variables
            textStyle = null;
            pos = 0;
            // parse and add the string
            parse(s);
        }
        return this;
    }
    
    /**
     * Inserts the given TeXFormula at the end of the current TeXFormula.
     *
     * @param f the TeXFormula to be inserted
     * @return the modified TeXFormula
     */
    public TeXFormula add(TeXFormula f) {
        addImpl (f);
        return this;
    }
    
    private void addImpl (TeXFormula f) {
        if (f.root != null) {
            // special copy-treatment for Mrow as a root!!
            if (f.root instanceof RowAtom)
                add(new RowAtom(f.root));
            else
                add(f.root);
        }
    }
    
    /**
     * Centers the current TeXformula vertically on the axis (defined by the parameter
     * "axisheight" in the resource "DefaultTeXFont.xml".
     *
     * @return the modified TeXFormula
     */
    public TeXFormula centerOnAxis() {
        root = new VCenteredAtom(root);
        return this;
    }
    
    /**
     * Parses the given string(s) into a TeXFormula, puts the given accent above it and
     * inserts the result at the end of the current TeXFormula.
     *
     * @param s the string to be parsed into a TeXFormula above which te given accent
     * 			will be placed
     * @param accentName the name of the accent symbol
     * @return the modified TeXFormula
     * @throws InvalidSymbolTypeException if the symbol is not defined as an accent
     * @throws SymbolNotFoundException if there's no symbol defined with the given name
     * @throws ParseException if the string(s) could not be parsed correctly
     */
    public TeXFormula addAcc(String s, String accentName)
    throws InvalidSymbolTypeException, SymbolNotFoundException,
            ParseException {
        return addAcc(new TeXFormula(s), accentName);
    }
    
    /**
     * Puts the given accent above the given TeXFormula and inserts the result
     * at the end of the current TeXFormula.
     *
     * @param base the TeXFormula above which the given accent will be placed
     * @param accentName the name of the accent symbol
     * @return the modified TeXFormula
     * @throws InvalidSymbolTypeException if the symbol is not defined as an accent
     * @throws SymbolNotFoundException if there's no symbol defined with the given name
     */
    public TeXFormula addAcc(TeXFormula base, String accentName)
    throws InvalidSymbolTypeException, SymbolNotFoundException {
        return add(new AccentedAtom((base == null ? null : base.root), accentName));
    }
    
    /**
     * Puts the given accent TeXFormula (that must represent a single accent symbol!) above
     * the given base TeXFormula and inserts the result at the end of the current TeXFormula.
     * <p>
     * <b>It's recommended to use one of the other more simple "addAcc"-mehods that require
     * the symbolname of the accent as a string! This method was added only because it was the
     * best way for parsing the MathML "mover" element into a TeXFormula.</b>
     *
     * @param base the TeXFormula above which the given accent will be placed
     * @param accent the TeXFormula that must represent a single accent symbol
     * @return the modified TeXFormula
     * @throws InvalidSymbolTypeException if the symbol that the given accent TeXFormula
     * 			represents, is not defined as an accent
     * @throws InvalidTeXFormulaException if the given accent TeXFormula does not represent
     * 			a single symbol
     */
    public TeXFormula addAcc(TeXFormula base, TeXFormula accent)
    throws InvalidSymbolTypeException, InvalidTeXFormulaException {
        return add(new AccentedAtom((base == null ? null : base.root), accent));
    }
    
    /**
     * Parses the given string into a TeXFormula, surrounds it with the given
     * delimiters and inserts the result at the end of the current TeXformula.
     *
     * @param s the string to be parsed into a TeXFormula that will be surrounded
     * 			by the given delimiters
     * @param l the left delimiter character
     * @param r the right delimiter character
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if one of the delimiter characters is mapped
     * 			to an unknown symbol
     * @throws InvalidDelimiterException if one of the delimiter characters is mapped
     * 			to a symbol that is not defined as a delimiter symbol
     * @throws ParseException if the string could not be parsed correctly
     * @throws DelimiterMappingNotFoundException if no character-to-symbol mapping is
     * 			found for one of the delimiter characters
     */
    public TeXFormula addEmbraced(String s, char l, char r)
    throws SymbolNotFoundException, InvalidDelimiterException,
            ParseException, DelimiterMappingNotFoundException {
        return addEmbraced(new TeXFormula(s), l, r);
    }
    
    /**
     * Parses the given string(s) into a TeXFormula, surrounds it with the given
     * delimiters (if not null) and inserts the result at the end of the current
     * TeXFormula.
     *
     * @param s the string to be parsed into a TeXFormula that will be
     * 			surrounded by the given delimiters
     * @param left the symbol name of the left delimiter (or null: no delimiter)
     * @param right the symbol name of the right delimiter (or null: no delimiter)
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if no symbol is defined for one of the
     * 			given names
     * @throws ParseException if the string(s) could not be parsed correctly
     * @throws InvalidDelimiterException if one of the symbols is not defined as a
     * 			delimiter symbol
     */
    public TeXFormula addEmbraced(String s, String left, String right)
    throws SymbolNotFoundException, ParseException,
            InvalidDelimiterException {
        return addEmbraced(new TeXFormula(s), left, right);
    }
    
    /**
     * Surrounds the given TeXFormula with the given delimiters and
     * inserts the result at the end of the current TeXformula.
     *
     * @param f the TeXFormula that will be surrounded by the given delimiters
     * @param l the left delimiter character
     * @param r the right delimiter character
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if one of the delimiter characters is mapped
     * 			to an unknown symbol
     * @throws InvalidDelimiterException if one of the delimiter characters is mapped
     * 			to a symbol that is not defined as a delimiter symbol
     * @throws DelimiterMappingNotFoundException if no character-to-symbol mapping is
     * 			found for one of the delimiter characters
     */
    public TeXFormula addEmbraced(TeXFormula f, char l, char r)
    throws SymbolNotFoundException, InvalidDelimiterException,
            DelimiterMappingNotFoundException {
        return addEmbraced(f, getCharacterToDelimiterMapping(l),
                getCharacterToDelimiterMapping(r));
    }
    
    /**
     * Surrounds the given TeXFormula with the given delimiters (if not null) and inserts the
     * result at the end of the current TeXFormula.
     *
     * @param f the TeXFormula that will be surrounded by the given delimiters
     * @param left the symbol name of the left delimiter (or null: no delimiter)
     * @param right the symbol name of the right delimiter (or null: no delimiter)
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if no symbol is defined for one of the
     * 			given names
     * @throws InvalidDelimiterException if one of the symbols is not defined as a
     * 			delimiter symbol
     */
    public TeXFormula addEmbraced(TeXFormula f, String left, String right)
    throws SymbolNotFoundException, InvalidDelimiterException {
        return add(new FencedAtom((f == null ? null : f.root),
                getDelimiterSymbol(left), getDelimiterSymbol(right)));
    }
    
    /**
     * Parses the given strings into TeXFormula's that will represent the numerator (num)
     * and the denominator (denom) of a fraction, draws a line between them
     * depending on "rule" and inserts the result at the end of the current
     * TeXFormula.
     *
     * @param num the string to be parsed into a TeXFormula that will represent the
     * 			numerator of the fraction
     * @param denom the string to be parsed into a TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     * @throws ParseException if one of the strings could not be parsed correctly
     */
    public TeXFormula addFraction(String num, String denom, boolean rule)
    throws ParseException {
        return addFraction(new TeXFormula(num), new TeXFormula(denom), rule);
    }
    
    /**
     * Parses the given strings into TeXFormula's that will represent the numerator (num)
     * and denominator (denom) of a fraction, draws a line between them
     * depending on "rule", aligns the numerator and denominator in comparison with
     * each other (indicated by numAlign and denomAlign) and inserts the result at
     * the end of the current TeXFormula.
     *
     * @param num the string to be parsed into a TeXFormula that will represent the
     * 			numerator of the fraction
     * @param denom the string to be parsed into a TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @param numAlign an alignment constant (from {@link TeXConstants}) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     * @throws ParseException if one of the strings could not be parsed correctly
     */
    public TeXFormula addFraction(String num, String denom, boolean rule,
            int numAlign, int denomAlign) throws ParseException {
        return addFraction(new TeXFormula(num), new TeXFormula(denom), rule,
                numAlign, denomAlign);
    }
    
    /**
     * Parses the given string into a TeXFormula that will represent the numerator of
     * a fraction, uses the given TeXFormula as the denominator of this fraction,
     * draws a line between them depending on "rule" and inserts the
     * result at the end of the current TeXFormula.
     *
     * @param num the string to be parsed into a TeXFormula that will represent the
     * 			numerator of the fraction
     * @param denom the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula addFraction(String num, TeXFormula denom, boolean rule)
    throws ParseException {
        return addFraction(new TeXFormula(num), denom, rule);
    }
    
    /**
     * Parses the given string into a TeXFormula that will represent the denominator of
     * a fraction, uses the given TeXFormula as the numerator of this fraction,
     * draws a line between them depending on "rule" and inserts the
     * result at the end of the current TeXFormula.
     *
     * @param num the TeXFormula that will represent the
     * 			numerator of the fraction
     * @param denom the string to be parsed into a TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula addFraction(TeXFormula num, String denom, boolean rule)
    throws ParseException {
        return addFraction(num, new TeXFormula(denom), rule);
    }
    
    /**
     * Uses the given TeXFormula's as the numerator (num) and denominator (denom) of
     * a fraction, draws a line between them depending on "rule"
     * and inserts the result at the end of the current TeXFormula.
     *
     * @param num the TeXFormula that will represent the
     * 			numerator of the fraction
     * @param denom the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     */
    public TeXFormula addFraction(TeXFormula num, TeXFormula denom, boolean rule) {
        return add(new FractionAtom((num == null ? null : num.root),
                (denom == null ? null : denom.root), rule));
    }
    
    /**
     * Uses the given TeXFormula's as the numerator (num) and denominator (denom) of
     * a fraction, draws a line between them depending on "rule",
     * aligns the numerator and denominator in comparison with
     * each other (indicated by numAlign and denomAlign) and inserts the result at
     * the end of the current TeXFormula.
     *
     * @param num the TeXFormula that will represent the
     * 			numerator of the fraction
     * @param denom the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @param numAlign an alignment constant (from {@link TeXConstants}) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     */
    public TeXFormula addFraction(TeXFormula num, TeXFormula denom,
            boolean rule, int numAlign, int denomAlign) {
        return add(new FractionAtom((num == null ? null : num.root),
                (denom == null ? null : denom.root), rule, numAlign, denomAlign));
    }
    
    /**
     * Parses the given strings into TeXFormula's, puts them under a root
     * sign (base) and in the upper left corner over this root sign (nthRoot)
     * and inserts the result at the end of the current TeXFormula.
     *
     * @param base the string to be parsed into a TeXFormula that will be put
     * 			under the root sign.
     * @param nthRoot the string to be parsed into a TeXFormula that will be put
     * 			in the upper left corner over the root sign
     * @return the modified TeXFormula
     * @throws ParseException if one of the strings could not be parsed correctly
     */
    public TeXFormula addNthRoot(String base, String nthRoot)
    throws ParseException {
        return addNthRoot(new TeXFormula(base), new TeXFormula(nthRoot));
    }
    
    /**
     * Parses the given string into a TeXFormula, puts it under a root
     * sign, puts the given TeXFormula in the upper left corner over this root sign
     * and inserts the result at the end of the current TeXFormula.
     *
     * @param base the string to be parsed into a TeXFormula that will be put
     * 			under the root sign.
     * @param nthRoot the TeXFormula that will be put
     * 			in the upper left corner over the root sign
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula addNthRoot(String base, TeXFormula nthRoot)
    throws ParseException {
        return addNthRoot(new TeXFormula(base), nthRoot);
    }
    
    /**
     * Parses the given string into a TeXFormula, puts it in the upper
     * left corner over the root sign, puts the given TeXFormula under this root sign
     * and inserts the result at the end of the current TeXFormula.
     *
     * @param base the TeXFormula that will be put under the root sign.
     * @param nthRoot the string to be parsed into a TeXFormula that will be put
     * 			in the upper left corner over the root sign
     * @return the modified TeXFormula
     * @throws ParseException if the strings could not be parsed correctly
     */
    public TeXFormula addNthRoot(TeXFormula base, String nthRoot)
    throws ParseException {
        return addNthRoot(base, new TeXFormula(nthRoot));
    }
    
    /**
     * Puts the given TeXFormula's under a root sign (base) and in the upper left
     * corner over this root sign (nthRoot) and inserts the result at the end of the
     * current TeXFormula.
     *
     * @param base the TeXFormula that will be put under the root sign.
     * @param nthRoot the TeXFormula that will be put
     * 			in the upper left corner over the root sign
     * @return the modified TeXFormula
     */
    public TeXFormula addNthRoot(TeXFormula base, TeXFormula nthRoot) {
        return add(new NthRoot((base == null ? null : base.root),
                (nthRoot == null ? null : nthRoot.root)));
    }
    
    /**
     * Parses the given strings into TeXFormula's that will represent a "big operator"
     * (op), it's lower (low) and upper (up) bound, and inserts the result at the end
     * of the current TeXFormula. The positioning of the upper and lower bound
     * (as limits: over and under the "big operator", or as scripts: superscript and
     * subscript) will be determined automatically according to the TeX algorithms.
     * If low is null, the lower bound will be omitted. If up is null, the upper
     * bound will be omitted.
     *
     * @param op the string to be parsed into a TeXFormula that will represent the
     * 			"big operator"
     * @param low the string to be parsed into a TeXFormula that will represent
     * 			the lower bound of the "big operator" (or null: no lower bound)
     * @param up the string to be parsed into a TeXFormula that will represent
     * 			the upper bound of the "big operator" (or null: no upper bound)
     * @return the modified TeXFormula
     * @throws ParseException if one of the strings could not be parsed correctly
     */
    public TeXFormula addOp(String op, String low, String up)
    throws ParseException {
        return addOp(new TeXFormula(op), new TeXFormula(low), new TeXFormula(up));
    }
    
    /**
     * Parses the given strings into TeXFormula's that will represent a "big operator"
     * (op), it's lower (low) and upper (up) bound, and inserts the result at the end
     * of the current TeXFormula. The positioning of the upper and lower bound
     * (as limits: over and under the "big operator", or as scripts: superscript and
     * subscript) is determined by lim.
     *
     * @param op the string to be parsed into a TeXFormula that will represent the
     * 			"big operator"
     * @param low the string to be parsed into a TeXFormula that will represent
     * 			the lower bound of the "big operator" (or null: no lower bound)
     * @param up the string to be parsed into a TeXFormula that will represent
     * 			the upper bound of the "big operator" (or null: no upper bound)
     * @param lim whether the upper and lower bound should be displayed as limits
     * 			(<-> scripts)
     * @return the modified TeXFormula
     * @throws ParseException if one of the strings could not be parsed correctly
     */
    public TeXFormula addOp(String op, String low, String up, boolean lim)
    throws ParseException {
        return addOp(new TeXFormula(op), new TeXFormula(low), new TeXFormula(up),
                lim);
    }
    
    /**
     * Uses the given TeXFormula's as a "big operator"
     * (op), it's lower (low) and upper (up) bound, and inserts the result at the end
     * of the current TeXFormula. The positioning of the upper and lower bound
     * (as limits: over and under the "big operator", or as scripts: superscript and
     * subscript) will be determined automatically according to the TeX algorithms.
     * If low is null, the lower bound will be omitted. If up is null, the upper
     * bound will be omitted.
     *
     * @param op the TeXFormula that will represent the
     * 			"big operator"
     * @param low the TeXFormula that will represent
     * 			the lower bound of the "big operator" (or null: no lower bound)
     * @param up the TeXFormula that will represent
     * 			the upper bound of the "big operator" (or null: no upper bound)
     * @return the modified TeXFormula
     */
    public TeXFormula addOp(TeXFormula op, TeXFormula low, TeXFormula up) {
        return add(new BigOperatorAtom((op == null ? null : op.root),
                (low == null ? null : low.root), (up == null ? null : up.root)));
    }
    
    /**
     * Uses the given TeXFormula's as a "big operator"
     * (op), it's lower (low) and upper (up) bound, and inserts the result at the end
     * of the current TeXFormula. The positioning of the upper and lower bound
     * (as limits: over and under the "big operator", or as scripts: superscript and
     * subscript) is determined by lim.
     *
     * @param op the TeXFormula that will represent the
     * 			"big operator"
     * @param low the TeXFormula that will represent
     * 			the lower bound of the "big operator" (or null: no lower bound)
     * @param up the TeXFormula that will represent
     * 			the upper bound of the "big operator" (or null: no upper bound)
     * @param lim whether the upper and lower bound should be displayed as limits
     * 			(<-> scripts)
     * @return the modified TeXFormula
     */
    public TeXFormula addOp(TeXFormula op, TeXFormula low, TeXFormula up,
            boolean lim) {
        return add(new BigOperatorAtom((op == null ? null : op.root),
                (low == null ? null : low.root), (up == null ? null : up.root), lim));
    }
    
    /**
     * Parses the given string into a phantom TeXFormula and inserts the result at the
     * end of the current TeXFormula. A phantom TeXFormula will be rendered invisibly.
     * Although the inserted formula is invisible, it's
     * still treated as a normal visible formula when it comes to inserting glue.
     *
     * @param phantom the string to be parsed as a phantom TeXFormula
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula addPhantom(String phantom) throws ParseException {
        return addPhantom(new TeXFormula(phantom));
    }
    
    /**
     * Parses the given string into a phantom TeXFormula and inserts the result at the
     * end of the current TeXFormula. Only the dimensions set to true will be taken into
     * account for drawing the whitespace. Although the inserted formula is invisible, it's
     * still treated as a normal visible formula when it comes to inserting glue.
     *
     * @param phantom the string to be parsed as a phantom TeXFormula
     * @param width whether the width of the TeXFormula's box should be used (<-> width 0)
     * @param height whether the height of the TeXFormula's box should be used (<-> height 0)
     * @param depth whether the depth of the TeXFormula's box should be used (<-> depth 0)
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula addPhantom(String phantom, boolean width, boolean height,
            boolean depth) throws ParseException {
        return addPhantom(new TeXFormula(phantom), width, height, depth);
    }
    
    /**
     * Inserts the given TeXFormula as a phantom TeXFormula at the
     * end of the current TeXFormula. A phantom TeXFormula will be rendered invisibly.
     * Although the inserted formula is invisible, it's
     * still treated as a normal visible formula when it comes to inserting glue.
     *
     * @param phantom the TeXFormula to be inserted as a phantom TeXFormula
     * @return the modified TeXFormula
     */
    public TeXFormula addPhantom(TeXFormula phantom) {
        return add(new PhantomAtom((phantom == null ? null : phantom.root)));
    }
    
    /**
     * Inserts the given TeXFormula as a phantom TeXFormula at the
     * end of the current TeXFormula. Only the dimensions set to true will be taken into
     * account for drawing the whitespace. Although the inserted formula is invisible, it's
     * still treated as a normal visible formula when it comes to inserting glue.
     *
     * @param phantom the TeXFormula to be inserted as a phantom TeXFormula
     * @param width whether the width of the TeXFormula's box should be used (<-> width 0)
     * @param height whether the height of the TeXFormula's box should be used (<-> height 0)
     * @param depth whether the depth of the TeXFormula's box should be used (<-> depth 0)
     * @return the modified TeXFormula
     */
    public TeXFormula addPhantom(TeXFormula phantom, boolean width,
            boolean height, boolean depth) {
        return add(new PhantomAtom((phantom == null ? null : phantom.root),
                width, height, depth));
    }
    
    /**
     * Parses the given string into a TeXFormula that will be displayed under a root
     * sign and inserts the result at the end of the current TeXFormula.
     *
     * @param base the string to be parsed into a TeXFormula that will be displayed
     * 			under a root sign
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula addSqrt(String base) throws ParseException {
        return addSqrt(new TeXFormula(base));
    }
    
    /**
     * Displays the given TeXFormula under a root
     * sign and inserts the result at the end of the current TeXFormula.
     *
     * @param base the TeXFormula that will be displayed
     * 			under a root sign
     * @return the modified TeXFormula
     */
    public TeXFormula addSqrt(TeXFormula base) {
        return addNthRoot(base, (TeXFormula) null);
    }
    
    /**
     * Inserts a strut box (whitespace) with the given width, height and depth (in
     * the given unit) at the end of the current TeXFormula.
     *
     * @param unit a unit constant (from {@link TeXConstants})
     * @param width the width of the strut box
     * @param height the height of the strut box
     * @param depth the depth of the strut box
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not represent
     * 			a valid unit
     */
    public TeXFormula addStrut(int unit, float width, float height, float depth)
    throws InvalidUnitException {
        return add(new SpaceAtom(unit, width, height, depth));
    }
    
    /**
     * Inserts a strut box (whitespace) with the given width (in widthUnits), height
     * (in heightUnits) and depth (in depthUnits) at the end of the current TeXFormula.
     *
     * @param widthUnit a unit constant used for the width (from {@link TeXConstants})
     * @param width the width of the strut box
     * @param heightUnit a unit constant used for the height (from TeXConstants)
     * @param height the height of the strut box
     * @param depthUnit a unit constant used for the depth (from TeXConstants)
     * @param depth the depth of the strut box
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not represent
     * 			a valid unit
     */
    public TeXFormula addStrut(int widthUnit, float width, int heightUnit,
            float height, int depthUnit, float depth) throws InvalidUnitException {
        return add(new SpaceAtom(widthUnit, width, heightUnit, height, depthUnit,
                depth));
    }
    
    /**
     * Inserts the symbol with the given name at the end of the current TeXFormula.
     *
     * @param name the name of the symbol
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if there's no symbol defined with the given name
     */
    public TeXFormula addSymbol(String name) throws SymbolNotFoundException {
        return add(SymbolAtom.get(name));
    }
    
    /**
     * Inserts the symbol with the given name at the end of the current TeXFormula
     * as a symbol of the given symbol type. This type can be (and is meant to be)
     * different from the symbol's defined type.
     *
     * @param name the name of the symbol
     * @param type a symbol type constant (from {@link TeXConstants})
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if there's no symbol defined with the given name
     * @throws InvalidSymbolTypeException if the given integer value does not represent
     * 			a valid symbol type
     */
    public TeXFormula addSymbol(String name, int type)
    throws SymbolNotFoundException, InvalidSymbolTypeException {
        return add(new SymbolAtom(SymbolAtom.get(name), type));
    }
    
   /**
    * 查看所给原子后面是否跟着有上下标, 如果有则附加到原子.
    * 根据是否有上下标, 创建的原子有所不同:
    *   没有上标, 同时也没有下标: 返回原来的原子(不附加东西)
    *   有上标, 或有下标, 或同时有上标下标: ScriptsAtom 或 BigOperatorAtom
    * 
    * 我们可以先研究普通 ScriptsAtom, 次之复杂一点的 BigOperatorAtom.
    * 
    * <p>Look for scripts at the current position in the parse string
    * and attach them to the given atom (if found)
    */
    private Atom attachScripts(Atom atom) throws ParseException {
        skipWhiteSpace(); // 忽略/跳过可能的空白.
        Atom f = atom;
        
        if (pos < parseString.length()) { // 未结束.
            // attach script(s) if present
            char ch = parseString.charAt(pos);
            
            // ' 相当于 上标\prime, 因此先替换这个, 然后...
            // ' = ^{\prime... so first replace this, then attach this script
            if (ch == PRIME) {
                replaceAccents();
                ch = parseString.charAt(pos);
            }
            
            // 看是否有上下标, 并附加到原子.
            // look for scripts and attach them
            if (ch == SUPER_SCRIPT || ch == SUB_SCRIPT) { // 是上标字符, 或下标字符.
                pos++;
                if (ch == SUPER_SCRIPT) { // superscript -- 是上标
                    TeXFormula sup = getScript(); // 读取上标
                    TeXFormula sub = new TeXFormula(); // 构造一个空下标
                    
                    skipWhiteSpace();
                    if (pos < parseString.length()
                    		&& parseString.charAt(pos) == SUB_SCRIPT) { // both; 即有上标又有下标.
                        pos++;
                        sub = getScript(); // 则读取下标.
                    }
                    
                    // 如果是巨算符, 则构造 BigOp 原子; 否则构造 Script 原子. 
                    // ? 这两种的详细情况呢? 为什么不用一种呢?
                    if(f.getRightType() == TeXConstants.TYPE_BIG_OPERATOR)
                        f = new BigOperatorAtom(f, sub.root, sup.root);
                    else
                        f = new ScriptsAtom(f, sub.root, sup.root);
                } 
                else { // subscript -- 下标.
                    TeXFormula sub = getScript(); // 下标.
                    TeXFormula sup = new TeXFormula(); // 空上标(可能没有上标)
                    
                    skipWhiteSpace();
                    if (pos < parseString.length()
                    		&& parseString.charAt(pos) == SUPER_SCRIPT) { // both
                        pos++;
                        sup = getScript(); // 先有下标, 后有上标.
                    }
                    
                    if(f.getRightType() == TeXConstants.TYPE_BIG_OPERATOR)
                        f = new BigOperatorAtom(f, sub.root, sup.root);
                    else
                        f = new ScriptsAtom(f, sub.root, sup.root);
                }
            }
        }
        return f;
    }
    
   /**
    * 将一个字符(从要解析的字符串中取得的)转换(创建)为一个 atom, 其可能是 CharAtom 或 SymbolAtom
    * 
    * Converts a character (from the parse string) to an atom (CharAtom or Symbol)
    */
    private Atom convertCharacter(char c) throws ParseException {
        pos++;
        if (isSymbol(c)) { // 如果是符号(非数字,非字母)
            String symbolName = symbolMappings[c]; // 得到该字符对应的名字. (也可能没有??, 例如 ` 字符没有?)
            if (symbolName == null) // 没有则提示未知字符? 不如原样显示?
                throw new ParseException("Unknown character : '"
                        + Character.toString(c) + "'");
            else
            	// 该符号字符有名字. 如 +="plus"
                try {
                	// 从 SymbolAtom 中得到该名字对应的 SymbolAtom. (其初始化时创建自配置文件)
                    return SymbolAtom.get(symbolName);
                } catch (SymbolNotFoundException e) {
                    throw new ParseException("The character '"
                            + Character.toString(c)
                            + "' was mapped to an unknown symbol with the name '"
                            + (String) symbolName + "'!", e);
                }
        } else
            // alphanumeric character; 创建为普通 CharAtom(包含字母,数字两种小分类)
            return new CharAtom(c, textStyle);
    }
    
   /*
    * 将这个公式转换为盒子(box), 使用指定的开始样式环境. (这个函数应该是递归调用的)
    * Convert this TeXFormula into a box, starting form the given style
    */
    public Box createBox(TeXEnvironment style) {
        if (root == null)
            return new StrutBox(0, 0, 0, 0);
        else
            return root.createBox(style);
    }
    
    /**
     * Creates a TeXIcon from this TeXFormula using the default TeXFont in the given
     * point size and starting from the given TeX style. If the given integer value
     * does not represent a valid TeX style, the default style
     * TeXConstants.STYLE_DISPLAY will be used.
     *
     * @param style a TeX style constant (from {@link TeXConstants}) to start from
     * @param size the default TeXFont's point size
     * @return the created TeXIcon
     */
    public TeXIcon createTeXIcon(int style, float size) {
    	// 为方便测试, 独立为多个语句:
    	DefaultTeXFont tf = new DefaultTeXFont(size); // 创建字体信息
    	TeXEnvironment env = new TeXEnvironment(style, tf); // 信息/布局环境
    	Box box = createBox(env); // 根据模型(TeXFormula) 创建为盒子(Box)... 我们也许能将盒子变成 XML/HTML??
    	TeXIcon icon = new TeXIcon(box, size); // 展现盒子到 Graphics.
    	return icon;
    }
    
    /**
     * Surrounds this TeXFormula with the given delimiters.
     *
     * @param left the left delimiter character
     * @param right the right delimiter character
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if one of the delimiter characters is mapped
     * 			to an unknown symbol
     * @throws InvalidDelimiterException if one of the delimiter characters is mapped
     * 			to a symbol that is not defined as a delimiter symbol
     * @throws DelimiterMappingNotFoundException if no character-to-symbol mapping is
     * 			found for one of the delimiter characters
     */
    public TeXFormula embrace(char left, char right)
    throws SymbolNotFoundException, InvalidDelimiterException,
            DelimiterMappingNotFoundException {
        return embrace(getCharacterToDelimiterMapping(left),
                getCharacterToDelimiterMapping(right));
    }
    
    /**
     * Surrounds this TeXFormula with the given delimiters (if not null).
     *
     * @param left the symbol name of the left delimiter (or null: no delimiter)
     * @param right the symbol name of the right delimiter (or null: no delimiter)
     * @return the modified TeXFormula
     * @throws SymbolNotFoundException if no symbol is defined for one of the
     * 			given names
     * @throws InvalidDelimiterException if one of the symbols is not defined as a
     * 			delimiter symbol
     */
    public TeXFormula embrace(String left, String right)
    throws SymbolNotFoundException, InvalidDelimiterException {
        root = new FencedAtom(root, getDelimiterSymbol(left),
                getDelimiterSymbol(right));
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, parses the given string
     * into a TeXFormula that will represent the denominator of the fraction, draws a line
     * between them depending on "rule" and changes the current TeXFormula
     * into this resulting fraction.
     *
     * @param s the string to be parsed into a TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula fraction(String s, boolean rule) throws ParseException {
        return fraction(new TeXFormula(s), rule);
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, parses the given string
     * into a TeXFormula that will represent the denominator of the fraction, possibly
     * draws a line between them depending on "rule", aligns the numerator and
     * denominator in comparison with each other (indicated by numAlign and denomAlign)
     * and changes the current TeXFormula into this resulting fraction.
     *
     * @param s the string to be parsed into a TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @param numAlign an alignment constant (from {@link TeXConstants}) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula fraction(String s, boolean rule, int numAlign,
            int denomAlign) throws ParseException {
        return fraction(new TeXFormula(s), rule, numAlign, denomAlign);
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, the given TeXFormula
     * as the denominator of the fraction, draws a line between them depending on "rule"
     * and changes the current TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     */
    public TeXFormula fraction(TeXFormula f, boolean rule) {
        root = new FractionAtom(root, (f == null ? null : f.root), rule);
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, the given TeXFormula
     * as the denominator of the fraction, draws a line between them with the given
     * thickness (in the given unit)
     * and changes the current TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param unit a unit constant (from {@link TeXConstants})
     * @param thickness the thickness (in the given unit) of the line to be put between
     * 			the numerator and denominator
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not
     * 			represent a valid unit
     */
    public TeXFormula fraction(TeXFormula f, int unit, float thickness)
    throws InvalidUnitException {
        root = new FractionAtom(root, (f == null ? null : f.root), unit,
                thickness);
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, the given TeXFormula
     * as the denominator of the fraction, draws a line between them depending on "rule",
     * aligns the numerator and denominator in comparison with
     * each other (indicated by numAlign and denomAlign) and changes the current
     * TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param unit a unit constant (from {@link TeXConstants})
     * @param thickness the thickness (in the given unit) of the line to be put between
     * 			the numerator and denominator
     * @param numAlign an alignment constant (from TeXConstants) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given integer value does not
     * 			represent a valid unit
     */
    public TeXFormula fraction(TeXFormula f, int unit, float thickness,
            int numAlign, int denomAlign) throws InvalidUnitException {
        root = new FractionAtom(root, (f == null ? null : f.root), unit,
                thickness, numAlign, denomAlign);
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, the given TeXFormula
     * as the denominator of the fraction, draws a line between them with a thickness
     * of "defaultFactor" times the default rule thickness,
     * aligns the numerator and denominator in comparison with
     * each other (indicated by numAlign and denomAlign) and changes the current
     * TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param defaultFactor the thickness factor (to be multiplied by the default
     * 			rule thickness)
     * @param numAlign an alignment constant (from TeXConstants) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     */
    public TeXFormula fraction(TeXFormula f, float defaultFactor, int numAlign,
            int denomAlign) {
        root = new FractionAtom(root, (f == null ? null : f.root), defaultFactor,
                numAlign, denomAlign);
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the numerator of a fraction, the given TeXFormula
     * as the denominator of the fraction, draws a line between them depending on "rule",
     * aligns the numerator and denominator in comparison with
     * each other (indicated by numAlign and denomAlign) and changes the current
     * TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			denominator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @param numAlign an alignment constant (from {@link TeXConstants}) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     */
    public TeXFormula fraction(TeXFormula f, boolean rule, int numAlign,
            int denomAlign) {
        root = new FractionAtom(root, (f == null ? null : f.root), rule,
                numAlign, denomAlign);
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the denominator of a fraction, parses the given string
     * into a TeXFormula that will represent the numerator of the fraction, draws a line
     * between them depending on "rule" and changes the current TeXFormula
     * into this resulting fraction.
     *
     * @param s the string to be parsed into a TeXFormula that will represent the
     * 			numerator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula fractionInvert(String s, boolean rule)
    throws ParseException {
        return fractionInvert(new TeXFormula(s), rule);
    }
    
    /**
     * Uses the current TeXFormula as the denominator of a fraction, parses the given string
     * into a TeXFormula that will represent the numerator of the fraction, draws a line
     * between them depending on "rule", aligns the numerator and
     * denominator in comparison with each other (indicated by numAlign and denomAlign)
     * and changes the current TeXFormula into this resulting fraction.
     *
     * @param s the string to be parsed into a TeXFormula that will represent the
     * 			numerator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @param numAlign an alignment constant (from {@link TeXConstants}) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula fractionInvert(String s, boolean rule, int numAlign,
            int denomAlign) throws ParseException {
        return fractionInvert(new TeXFormula(s), rule, numAlign, denomAlign);
    }
    
    /**
     * Uses the current TeXFormula as the denominator of a fraction, the given TeXFormula
     * as the numerator of the fraction, draws a line between them depending on "rule"
     * and changes the current TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			numerator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @return the modified TeXFormula
     */
    public TeXFormula fractionInvert(TeXFormula f, boolean rule) {
        root = new FractionAtom((f == null ? null : f.root), root, rule);
        return this;
    }
    
    /**
     * Uses the current TeXFormula as the denominator of a fraction, the given TeXFormula
     * as the numerator of the fraction, draws a line between them depending on "rule",
     * aligns the numerator and denominator in comparison with
     * each other (indicated by numAlign and denomAlign) and changes the current
     * TeXFormula into this resulting fraction.
     *
     * @param f the TeXFormula that will represent the
     * 			numerator of the fraction
     * @param rule whether a line should be drawn between numerator and denominator
     * @param numAlign an alignment constant (from {@link TeXConstants}) indicating
     * 			how the numerator should be aligned in comparison with the (larger)
     * 			denominator
     * @param denomAlign an alignment constant (from TeXConstants) indicating
     * 			how the denominator should be aligned in comparison with the (larger)
     * 			numerator
     * @return the modified TeXFormula
     */
    public TeXFormula fractionInvert(TeXFormula f, boolean rule, int numAlign,
            int denomAlign) {
        root = new FractionAtom((f == null ? null : f.root), root, rule,
                numAlign, denomAlign);
        return this;
    }
    
   /**
    * 读取下一个组(使用指定的开符号和闭符号, 如 lbrace={, rbrace=} ).
    *   调试例子 "\mathrm {x+y}" 进入这里.
    * 
    * Get the next group (between the given opening and closing characters)
    * at the current position in the parse string, return it as a string and
    * adjust the current position (after the group).
    */
    private String getGroup(char open, char close) throws ParseException {
        int group = 0;
        if (pos < parseString.length()) {
            char ch = parseString.charAt(pos);
            if (ch == open) { // 遇到开符号: 开符号数量+1, 扫描里面的内容.
                pos++;
                StringBuffer buf = new StringBuffer();
                // 扫描内容, 直到遇到 `配对的' 闭符号, 或到了字符串末尾.
                while (pos < parseString.length() 
                		&& !(parseString.charAt(pos) == close && group == 0)) {
                    if (parseString.charAt(pos) == open)
                        group++;
                    else if (parseString.charAt(pos) == close)
                        group--;
                    buf.append(parseString.charAt(pos));
                    pos++;
                }
                if (pos == parseString.length())
                    // end of string reached, but not processed properly
                    throw new ParseException("Illegal end,  missing '" + close
                            + "'!");
                else { // end of group
                    pos++;
                    return buf.toString();
                }
            } else
                throw new ParseException("missing '" + open + "'!");
        }
        // end of string reached, but not processed properly
        throw new ParseException("Illegal end, missing '" + close + "'!");
    }
    
   /**
    * 解析原子后跟的上标/下标. 解析然后作为 TeXFormula 返回.
    * 
    * Get the next script at the current position in the parse string.
    * If a group opening character is found, this is the next group, otherwise
    * the next character. Parse it, return it as a TeXFormula and adjust
    * the current position.
    */
    private TeXFormula getScript() throws ParseException {
        skipWhiteSpace();
        char ch;
        if (pos < parseString.length()) {
            ch = parseString.charAt(pos);
            if (ch == L_GROUP) {
            	// 后跟 { 符号, 所以跟随一个组, 则读取组, 构造为 TeXFormula.
                return new TeXFormula(getGroup(L_GROUP, R_GROUP));
            } else {
            	// 单个字符做为上标/下标.
                pos++;
                return new TeXFormula(Character.toString(ch));
            }
        }
        // end of string reached, but not processed properly
        throw new ParseException("illegal end, missing script!");
    }
    
    /**
     * Changes this TeXFormula into a phantom TeXFormula. It will be rendered invisibly.
     * This means that a strut box (whitespace) will be displayed instead of the current
     * TeXFormula, with the same width, height and depth as the current TeXFormula's
     * box would have. Although this formula is now made invisible, it's
     * still treated as a normal visible formula when it comes to inserting glue.
     *
     * @return the modified TeXFormula
     */
    public TeXFormula makePhantom() {
        root = new PhantomAtom(root);
        return this;
    }
    
    /**
     * Changes this TeXFormula into a phantom TeXFormula. Only the dimensions set to true will be taken into
     * account for drawing the whitespace. Although this formula is now made invisible, it's
     * still treated as a normal visible formula when it comes to inserting glue.
     *
     * @param width whether the width of the TeXFormula's box should be used (<-> width 0)
     * @param height whether the height of the TeXFormula's box should be used (<-> height 0)
     * @param depth whether the depth of the TeXFormula's box should be used (<-> depth 0)
     * @return the modified TeXFormula
     */
    public TeXFormula makePhantom(boolean width, boolean height, boolean depth) {
        root = new PhantomAtom(root, width, height, depth);
        return this;
    }
    
    /**
     * Parses the given string into a TeXFormula, puts it in the upper
     * left corner over a root sign (nthRoot), puts the current TeXFormula under this
     * root sign and changes the current TeXFormula into this resulting root construction.
     *
     * @param nthRoot the string to be parsed into a TeXFormula that will be put
     * 			in the upper left corner over the root sign
     * @return the modified TeXFormula
     * @throws ParseException if the string could not be parsed correctly
     */
    public TeXFormula nthRoot(String nthRoot) throws ParseException {
        return nthRoot(new TeXFormula(nthRoot));
    }
    
    /**
     * Puts the given TeXFormula in the upper left corner over a root sign, puts the
     * current TeXFormula under this root sign and changes the current TeXFormula
     * into this resulting root construction.
     *
     * @param nthRoot the TeXFormula that will be put
     * 			in the upper left corner over the root sign
     * @return the modified TeXFormula
     */
    public TeXFormula nthRoot(TeXFormula nthRoot) {
        root = new NthRoot(root, (nthRoot == null ? null : nthRoot.root));
        return this;
    }
    
    /**
     * Puts a line over the current TeXFormula and changes the current TeXFormula into
     * the resulting construction.
     *
     * @return the modified TeXFormula
     */
    public TeXFormula overline() {
        root = new OverlinedAtom(root);
        return this;
    }
    
   /**
    * 解析指定的字符串(从位置 0 开始)
    * Starts parsing the given string (at position 0).
    */
    private void parse(String s) throws ParseException {
    	if (debug_stop) {
    		System.out.println("在这里加断点");
    	}
        parseString = s;
        if (parseString == null || parseString.length() == 0) return;

        // 遍历整个字符串, pos 指向当前正在扫描的字符.
        while (pos < parseString.length()) {
            char ch = parseString.charAt(pos); // 得到当前正在扫描的字符.
            // 1. ignore white space; 忽略空格(空白).
            if (isWhiteSpace(ch))
                pos++; 
            // 2. 遇到 `\' 转义字符, 处理转义序列.
            else if (ch == ESCAPE) 
                processEscape(); // 转义序列,命令,数学符号,textstyle,commands,预定义数学公式.
            // 3. 读入一个分组. {xxx}
            else if (ch == L_GROUP) { 
            	String g = getGroup(L_GROUP, R_GROUP);
            	TeXFormula txf = new TeXFormula(g);
            	Atom atom = attachScripts(txf.root); // 认真读取 <<TeX原本>> 中关于原子(atom)的说明.
                add(atom);
            }
            else if (ch == R_GROUP) { // 不匹配的 } (右花括号)
                throw new ParseException("Found a closing '" + R_GROUP
                        + "' without an opening '" + L_GROUP + "'!");
            }
            // 4. 上标,下标.
            else if (ch == SUPER_SCRIPT || ch == SUB_SCRIPT || ch == PRIME) { // ' = ^{\prime...
                if (pos == 0) // first character
                    throw new ParseException("Every script needs a base: \""
                            + SUPER_SCRIPT + "\", \"" + SUB_SCRIPT + "\" and \""
                            + PRIME + "\" can't be the first character!");
                else
                    throw new ParseException(
                            "Double scripts found! Try using more braces.");
            }
            // 5. 其它字符.
            else {
                add(attachScripts(convertCharacter(ch)));
            }
        }
    }
    
   /*
    * Processes the given TeX command (by parsing following command arguments
    * in the parse string).
    */
    private Atom processCommands(String command) throws ParseException {
        skipWhiteSpace();
        if ("frac".equals(command)) {
            TeXFormula num = new TeXFormula(getGroup(L_GROUP, R_GROUP));
            skipWhiteSpace();
            TeXFormula denom = new TeXFormula(getGroup(L_GROUP, R_GROUP));
            if (num.root == null || denom.root == null)
                throw new ParseException(
                        "Both numerator and denominator of a fraction can't be empty!");
            return new FractionAtom(num.root, denom.root, true);
        } else { // sqrt
            skipWhiteSpace();
            if (pos == parseString.length())
                // end of string reached, but not processed properly
                throw new ParseException("illegal end!");
            
            TeXFormula nRoot = new TeXFormula();
            if (parseString.charAt(pos) == L_BRACK) { // n-th root
                nRoot = new TeXFormula(getGroup(L_BRACK, R_BRACK));
                skipWhiteSpace();
            }
            return new NthRoot(new TeXFormula(getGroup(L_GROUP, R_GROUP)).root,
                    nRoot.root);
            
        }
    }
    
   /**
    * 尝试查找/发现一个 TeX 命令(如 \displaystyle)或一个 TeX 符号名(如 \frac), 
    * 在当前扫描位置(在发现了转义字符 `\' 之后).
    * 
    * Tries to find a TeX command or TeX symbol name at the current position
    * in the parse string (just after an escape character was found).
    */
    private void processEscape() throws ParseException {
        pos++; // 跳过 `\' 转义字符.
        StringBuffer buf = new StringBuffer(); // 用于保存 `\' 之后的字符串.
        // no longer symbol name or predefined TeXFormula name possible; 不会有更多命令或符号的字符了的标志(序列结束)
        boolean endOfEscape = false;
        // temporarily save symbol
        SymbolAtom symbolFound = null;
        int symbolPos = -1;
        // temporarily save predefined TeXFormula
        TeXFormula predefFound = null;
        int predefPos = -1;
        // what was the longest match: symbol or predefined TeXFormula?
        // 这里的算法也许不太好理解, 用最长匹配? 一般总是用最长的??
        boolean symbolLongest = true;
        
        while (pos < parseString.length()) { // 循环向后扫描.
            char ch = parseString.charAt(pos);
            boolean isEnd = (pos == parseString.length() - 1); // 这是字符串的最后一个字符
            
            // 这些字符不是命令或符号的允许使用的字符, 故而如果前面没有找到任何命令或符号, 则抛出相应的异常.
            // the following characters can't be part of a command or symbol, so
            // if there's no command or symbol found, then an exception is
            // thrown
            if (isWhiteSpace(ch) || ch == ESCAPE || ch == SUB_SCRIPT // 下标 `_'
                    || ch == SUPER_SCRIPT || isEnd) { // 上标 `^'
                endOfEscape = true; // escape 序列结束
                if (isEnd) { // 因为 isEnd 是最后判断的, 所以如果 isEnd 成立, 则前面都不成立. 这里的语义写法真的很不好.
                    buf.append(ch);
                    pos++;
                }
            } else {
                buf.append(ch);
                pos++;
            }
            // 现在 buf 里面是全部或部分 \ 后的名字序列.
            
            String command = buf.toString(); // 奇怪的是, 每扫描一个字符, 就查找一下??
            // 这里的查找方式比较怪, 正常思路是先找到所有字母(tex只使用字母), 然后再看是什么命令就行的.
            
            SymbolAtom s = null;
            TeXFormula predef = null;
            try { 
            	// 检查这个 `字符串' 是不是一个有效的符号名.
            	// check if 'command' is a valid symbolname
                s = SymbolAtom.get(command);
            } catch (SymbolNotFoundException e) { // symbol not found; 其实这里用抛出异常的方法是不推荐的实现方法. 返回 null 不是很好吗?
                // check if a predefined TeXFormula exists with that name
            	// 检查是不是一个预定义的 TeX公式.
                try {
                    predef = TeXFormula.get(command);
                } catch (FormulaNotFoundException e1) {
                    predef = null; // none found
                }
            } // 上面执行之后, 变量 s 或 predef 被设置, 或两者皆 null.
            
            if (s != null) { // symbol found! 是一个符号,如 \plus   // NOPMD
                if (endOfEscape) {
                    // no longer symbol name or predefined TeXFormula name possible
                    add(attachScripts(s));
                    return;
                } else { // could be part of another valid symbolname, like "in" and "infty"
                    symbolFound = s;
                    symbolPos = pos;
                    symbolLongest = true;
                }
            } else if (predef != null) { // predefined TeXFormula found!   // NOPMD
                if (endOfEscape) {
                    // no longer symbol name or predefined TeXFormula name possible
                    add(attachScripts(predef.root));
                    return;
                } else { // could be part of another valid symbolname, like "in" and "infty"
                    predefFound = predef;
                    predefPos = pos;
                    symbolLongest = false;
                }
            } else if ("nbsp".equals(command)) { // space found (for MathML-purposes!) 
            	// 语法 \nbsp, 表示空格?
                add(attachScripts(new SpaceAtom()));
                return;
            } else if (textStyles.contains(command)) { // textstyle found
            	// 如 \mathrm, \mathit, \mathcal , 配置在 xml 中.
                skipWhiteSpace();
                String g = getGroup(L_GROUP, R_GROUP); // 拆分以方便调试.
                TeXFormula txf = new TeXFormula(g, command);
                add(attachScripts(txf.root));
                return;
            } else if (commands.contains(command)) { // command found
            	// 当前的命令有 \frac, \sqrt. 也许暂时不支持更多的?
                add(attachScripts(processCommands(command)));
                return;
            } else if (endOfEscape) { // searching is over
                if (symbolLongest && symbolFound != null) {
                    // go back to that position and add that symbol
                    pos = symbolPos;
                    add(attachScripts(symbolFound));
                    return;
                } else if (!symbolLongest && predefFound != null) { // NOPMD
                    // go back to that position and add that predefined TeXFormula
                    pos = predefPos;
                    add(attachScripts(predefFound.root));
                    return;
                } else
                    // not a valid command or symbol or predefined TeXFormula found
                    throw new ParseException(
                            "Unknown symbol or command or predefined TeXFormula: '"
                            + command + "'");
            }
        }
        
        // escape-char found at the end of the string
        throw new ParseException("The escape-character '" + ESCAPE
                + "' can't be the last one!");
    }
    
    /**
     * Puts the given accent above the current TeXFormula and changes the current
     * TeXFormula into the resulting accent construction.
     *
     * @param accentName the name of the accent symbol
     * @return the modified TeXFormula
     * @throws InvalidSymbolTypeException if the symbol is not defined as an accent
     * @throws SymbolNotFoundException if there's no symbol defined with the given name
     */
    public TeXFormula putAccentOver(String accentName)
    throws InvalidSymbolTypeException, SymbolNotFoundException {
        root = new AccentedAtom(root, accentName);
        return this;
    }
    
    /**
     * Puts the delimiter symbol represented by the given delimiter type constant above the
     * current TeXFormula and changes the current TeXFormula into the resulting construction.
     *
     * @param delimiter a delimiter type constant (from {@link TeXConstants}) that represents
     * 			a delimiter symbol that will be put above the current TeXFormula
     * @return the modified TeXFormula
     * @throws InvalidDelimiterTypeException if the given integer value does not represent
     * 			a valid delimiter type
     * @throws SymbolNotFoundException if the definition of the symbol represented
     * 			by the delimiter constant was not found (due to user-made changes!)
     */
    public TeXFormula putDelimiterOver(int delimiter)
    throws InvalidDelimiterTypeException, SymbolNotFoundException {
        if (delimiter < 0 || delimiter >= delimiterNames.length)
            throw new InvalidDelimiterTypeException();
        
        String name = delimiterNames[delimiter][OVER_DEL];
        root = new OverUnderDelimiter(root, null, SymbolAtom.get(name),
                TeXConstants.UNIT_EX, 0, true);
        return this;
    }
    
    /**
     * Puts the delimiter symbol represented by the given delimiter type constant above the
     * current TeXFormula, parses the given string into a TeXFormula and
     * puts it above the delimiter symbol (seperated by an amount of vertical space defined
     * by the given float value and unit) in a smaller size (unless the current TeXFormula
     * will be displayed in the smallest possible size: the script_script style's size)
     * and finally changes the current TeXFormula into the resulting construction.
     *
     * @param delimiter a delimiter type constant (from {@link TeXConstants}) that represents
     * 			a delimiter symbol that will be put above the current TeXFormula
     * @param sup the string to be parsed into a TeXFormula that will be put above the
     * 			delimiter symbol, in a smaller size if possible
     * @param kernUnit a unit constant (from {@link TeXConstants})
     * @param kern amount of vertical space (in kernUnit) to be put between the delimiter
     * 			and the given TeXFormula sub
     * @return the modified TeXFormula
     * @throws InvalidDelimiterTypeException if the given integer value does not represent
     * 			a valid delimiter type
     * @throws InvalidUnitException if the given integer value (kernUnit) does not
     * 			represent a valid unit
     * @throws ParseException if the given string could not be parsed correctly
     * @throws SymbolNotFoundException if the definition of the symbol represented
     * 			by the delimiter constant was not found (due to user-made changes!)
     */
    public TeXFormula putDelimiterOver(int delimiter, String sup, int kernUnit,
            float kern) throws InvalidDelimiterTypeException,
            InvalidUnitException, ParseException, SymbolNotFoundException {
        return putDelimiterOver(delimiter, new TeXFormula(sup), kernUnit, kern);
    }
    
    /**
     * Puts the delimiter symbol represented by the given delimiter type constant above the
     * current TeXFormula, puts the given TeXFormula above the delimiter symbol (seperated
     * by an amount of vertical space defined by the given float value and unit) in a
     * smaller size (unless the current TeXFormula will be displayed in the smallest
     * possible size: the "script_script" style's size) and finally changes the current
     * TeXFormula into the resulting construction.
     *
     * @param delimiter a delimiter type constant (from {@link TeXConstants}) that represents
     * 			a delimiter symbol that will be put above the current TeXFormula
     * @param sup the TeXFormula that will be put above the
     * 			delimiter symbol, in a smaller size if possible
     * @param kernUnit a unit constant (from {@link TeXConstants})
     * @param kern amount of vertical space (in kernUnit) to be put between the delimiter
     * 			and the given TeXFormula sub
     * @return the modified TeXFormula
     * @throws InvalidDelimiterTypeException if the given integer value does not represent
     * 			a valid delimiter type
     * @throws InvalidUnitException if the given integer value (kernUnit) does not
     * 			represent a valid unit
     * @throws SymbolNotFoundException if the definition of the symbol represented
     * 			by the delimiter constant was not found (due to user-made changes!)
     */
    public TeXFormula putDelimiterOver(int delimiter, TeXFormula sup,
            int kernUnit, float kern) throws InvalidDelimiterTypeException,
            InvalidUnitException, SymbolNotFoundException {
        if (delimiter < 0 || delimiter >= delimiterNames.length)
            throw new InvalidDelimiterTypeException();
        
        String name = delimiterNames[delimiter][OVER_DEL];
        root = new OverUnderDelimiter(root, (sup == null ? null : sup.root),
                SymbolAtom.get(name), kernUnit, kern, true);
        return this;
    }
    
    /**
     * Puts the delimiter symbol represented by the given delimiter type constant under the
     * current TeXFormula and changes the current
     * TeXFormula into the resulting construction.
     *
     * @param delimiter a delimiter type constant (from {@link TeXConstants}) that represents
     * 			a delimiter symbol that will be put under the current TeXFormula
     * @return the modified TeXFormula
     * @throws InvalidDelimiterTypeException if the given integer value does not represent
     * 			a valid delimiter type
     * @throws SymbolNotFoundException if the definition of the symbol represented
     * 			by the delimiter constant was not found (due to user-made changes!)
     */
    public TeXFormula putDelimiterUnder(int delimiter)
    throws InvalidDelimiterTypeException, SymbolNotFoundException {
        if (delimiter < 0 || delimiter >= delimiterNames.length)
            throw new InvalidDelimiterTypeException();
        
        String name = delimiterNames[delimiter][UNDER_DEL];
        root = new OverUnderDelimiter(root, null, SymbolAtom.get(name),
                TeXConstants.UNIT_EX, 0, false);
        return this;
    }
    
    /**
     * Puts the delimiter symbol represented by the given delimiter type constant under the
     * current TeXFormula, parses the given string into a TeXFormula and puts
     * it under the delimiter symbol (seperated by an amount of vertical space defined
     * by the given float value and unit) in a smaller size (unless the current TeXFormula
     * will be displayed in the smallest possible size: the script_script style's size)
     * and finally changes the current TeXFormula into the resulting construction.
     *
     * @param delimiter a delimiter type constant (from {@link TeXConstants}) that represents
     * 			a delimiter symbol that will be put under the current TeXFormula
     * @param sub the string to be parsed into a TeXFormula that will be put under the
     * 			delimiter symbol, in a smaller size if possible
     * @param kernUnit a unit constant (from {@link TeXConstants})
     * @param kern amount of vertical space (in kernUnit) to be put between the delimiter
     * 			and the given TeXFormula sub
     * @return the modified TeXFormula
     * @throws InvalidDelimiterTypeException if the given integer value does not represent
     * 			a valid delimiter type
     * @throws InvalidUnitException if the given integer value (kernUnit) does not
     * 			represent a valid unit
     * @throws ParseException if the given string could not be parsed correctly
     * @throws SymbolNotFoundException if the definition of the symbol represented
     * 			by the delimiter constant was not found (due to user-made changes!)
     */
    public TeXFormula putDelimiterUnder(int delimiter, String sub, int kernUnit,
            float kern) throws InvalidDelimiterTypeException,
            InvalidUnitException, ParseException, SymbolNotFoundException {
        return putDelimiterUnder(delimiter, new TeXFormula(sub), kernUnit, kern);
    }
    
    /**
     * Puts the delimiter symbol represented by the given delimiter type constant under the
     * current TeXFormula, puts the given TeXFormula under the delimiter symbol (seperated
     * by an amount of vertical space defined by the given float value and unit) in a
     * smaller size (unless the current TeXFormula will be displayed in the smallest
     * possible size: the "script_script" style's size) and finally changes the current
     * TeXFormula into the resulting construction.
     *
     * @param delimiter a delimiter type constant (from {@link TeXConstants}) that represents
     * 			a delimiter symbol that will be put under the current TeXFormula
     * @param sub the TeXFormula that will be put under the
     * 			delimiter symbol, in a smaller size if possible
     * @param kernUnit a unit constant (from {@link TeXConstants})
     * @param kern amount of vertical space (in kernUnit) to be put between the delimiter
     * 			and the given TeXFormula sub
     * @return the modified TeXFormula
     * @throws InvalidDelimiterTypeException if the given integer value (delimiter)
     * 			does not represent a valid delimiter type
     * @throws InvalidUnitException if the given integer value (kernUnit) does not
     * 			represent a valid unit
     * @throws SymbolNotFoundException if the definition of the symbol represented
     * 			by the delimiter constant was not found (due to user-made changes!)
     */
    public TeXFormula putDelimiterUnder(int delimiter, TeXFormula sub,
            int kernUnit, float kern) throws InvalidDelimiterTypeException,
            InvalidUnitException, SymbolNotFoundException {
        if (delimiter < 0 || delimiter >= delimiterNames.length)
            throw new InvalidDelimiterTypeException();
        
        String name = delimiterNames[delimiter][UNDER_DEL];
        root = new OverUnderDelimiter(root, (sub == null ? null : sub.root),
                SymbolAtom.get(name), kernUnit, kern, false);
        return this;
    }
    
    /**
     * Puts the given TeXFormula
     * above the current TeXFormula, in a smaller size
     * depending on "overScriptSize" and seperated by a vertical space of size
     * "overSpace" in "overUnit" and changes the current TeXFormula into the
     * resulting construction.
     *
     * @param over the TeXFormula to be put over the current TeXFormula
     * @param overUnit a unit constant (from TeXConstants)
     * @param overSpace the size (in overUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (over) that will be put over it
     * @param overScriptSize whether the TeXFormula (over) that will be put over the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given unit integer values doesn't
     * 			represent a valid unit
     */
    public TeXFormula putOver(TeXFormula over, int overUnit, float overSpace,
            boolean overScriptSize) throws InvalidUnitException {
        root = new UnderOverAtom(root, (over == null ? null : over.root),
                overUnit, overSpace, overScriptSize, true);
        return this;
    }
    
    /**
     * Parses the given string into a TeXFormula that will be put
     * above the current
     * TeXFormula, in a smaller size
     * depending on "overScriptSize" and seperated by a vertical space of size
     * "overSpace" in "overUnit" and changes the current TeXFormula into the
     * resulting construction.
     *
     * @param over the string to be parsed into a TeXFormula that will be put over the
     * 			current TeXFormula
     * @param overUnit a unit constant (from TeXConstants)
     * @param overSpace the size (in overUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (over) that will be put over it
     * @param overScriptSize whether the TeXFormula (over) that will be put over the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @return the modified TeXFormula
     * @throws InvalidUnitException if one of the given unit integer values doesn't
     * 			represent a valid unit
     * @throws parseException if the given string could not be parsed correctly
     */
    public TeXFormula putOver(String over, int overUnit, float overSpace,
            boolean overScriptSize) throws InvalidUnitException {
        return putOver((over == null ? null : new TeXFormula(over)), overUnit,
                overSpace, overScriptSize);
    }
    
    /**
     * Parses the given string into a TeXFormula that will be put
     * under the current TeXFormula,
     * in a smaller size
     * depending on "underScriptSize" and seperated by a
     * vertical space of size "underSpace" in "underUnit" and changes the current
     * TeXFormula into the resulting construction.
     *
     * @param under the string to be parsed into a TeXFormula that will be put under
     * 			the current TeXFormula, or null to put nothing under it
     * @param underUnit a unit constant (from {@link TeXConstants})
     * @param underSpace the size (in underUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (under) that will be put under it
     * @param underScriptSize whether the TeXFormula (under) that will be put under the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @return the modified TeXFormula
     * @throws InvalidUnitException if one of the given unit integer values doesn't
     * 			represent a valid unit
     * @throws parseException if the given string could not be parsed correctly
     */
    public TeXFormula putUnder(String under, int underUnit, float underSpace,
            boolean underScriptSize) throws InvalidUnitException {
        return putUnder((under == null ? null : new TeXFormula(under)),
                underUnit, underSpace, underScriptSize);
    }
    
    /**
     * Puts the given TeXFormula under the current TeXFormula,
     * in a smaller size
     * depending on "underScriptSize" and seperated by a
     * vertical space of size "underSpace" in "underUnit" and changes the current
     * TeXFormula into the resulting construction.
     *
     * @param under the TeXFormula to be put under the current TeXFormula
     * @param underUnit a unit constant (from {@link TeXConstants})
     * @param underSpace the size (in underUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (under) that will be put under it
     * 			(if not null)
     * @param underScriptSize whether the TeXFormula (under) that will be put under the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @return the modified TeXFormula
     * @throws InvalidUnitException if the given unit integer values doesn't
     * 			represent a valid unit
     */
    public TeXFormula putUnder(TeXFormula under, int underUnit,
            float underSpace, boolean underScriptSize) throws InvalidUnitException {
        root = new UnderOverAtom(root, (under == null ? null : under.root),
                underUnit, underSpace, underScriptSize, false);
        return this;
    }
    
    /**
     * Parses the given string "under" into a TeXFormula that will be put
     * under the current TeXFormula,
     * in a smaller size
     * depending on "underScriptSize" and seperated by a
     * vertical space of size "underSpace" in "underUnit", parses the given string
     * "over" into a TeXFormula that will be put above the current
     * TeXFormula, in a smaller size
     * depending on "overScriptSize" and seperated by a vertical space of size
     * "overSpace" in "overUnit" and finally changes the current TeXFormula into the
     * resulting construction.
     *
     * @param under the string to be parsed into a TeXFormula that will be put under
     * 			the current TeXFormula, or null to put nothing under it
     * @param underUnit a unit constant (from {@link TeXConstants})
     * @param underSpace the size (in underUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (under) that will be put under it
     * @param underScriptSize whether the TeXFormula (under) that will be put under the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @param over the string to be parsed into a TeXFormula that will be put over the
     * 			current TeXFormula, or null to put nothing over it
     * @param overUnit a unit constant (from TeXConstants)
     * @param overSpace the size (in overUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (over) that will be put over it
     * @param overScriptSize whether the TeXFormula (over) that will be put over the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @return the modified TeXFormula
     * @throws InvalidUnitException if one of the given unit integer values doesn't
     * 			represent a valid unit
     * @throws parseException if one of the given strings could not be parsed correctly
     */
    public TeXFormula putUnderAndOver(String under, int underUnit,
            float underSpace, boolean underScriptSize, String over, int overUnit,
            float overSpace, boolean overScriptSize) throws InvalidUnitException,
            ParseException {
        return putUnderAndOver((under == null ? null : new TeXFormula(under)),
                underUnit, underSpace, underScriptSize, (over == null ? null
                : new TeXFormula(over)), overUnit, overSpace, overScriptSize);
    }
    
    /**
     * Puts the given TeXFormula "under" under the current TeXFormula,
     * in a smaller size
     * depending on "underScriptSize" and seperated by a
     * vertical space of size "underSpace" in "underUnit", puts the given TeXFormula
     * "over" above the current TeXFormula, in a smaller size
     * depending on "overScriptSize" and seperated by a vertical space of size
     * "overSpace" in "overUnit" and finally changes the current TeXFormula into the
     * resulting construction.
     *
     * @param under the TeXFormula to be put under the current TeXFormula
     * @param underUnit a unit constant (from {@link TeXConstants})
     * @param underSpace the size (in underUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (under) that will be put under it
     * @param underScriptSize whether the TeXFormula (under) that will be put under the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @param over the TeXFormula to be put over the current TeXFormula
     * @param overUnit a unit constant (from TeXConstants)
     * @param overSpace the size (in overUnit) of the vertical space between the
     * 			current TeXFormula and the TeXFormula (over) that will be put over it
     * @param overScriptSize whether the TeXFormula (over) that will be put over the
     * 			current TeXFormula should be displayed in a smaller size (if possible)
     * @return the modified TeXFormula
     * @throws InvalidUnitException if one of the given unit integer values doesn't
     * 			represent a valid unit
     */
    public TeXFormula putUnderAndOver(TeXFormula under, int underUnit,
            float underSpace, boolean underScriptSize, TeXFormula over,
            int overUnit, float overSpace, boolean overScriptSize)
            throws InvalidUnitException {
        root = new UnderOverAtom(root, (under == null ? null : under.root),
                underUnit, underSpace, underScriptSize, (over == null ? null
                : over.root), overUnit, overSpace, overScriptSize);
        return this;
    }
    
   /**
    * 将 ' 替换为 ^{\prime} (' 作为上标)
    * Replaces "'" with "^{\prime}", "''" with "^{\prime\prime}", etc. at the
    * current position in the parse string.
    */
    private void replaceAccents() {
        StringBuffer buf = new StringBuffer(SUPER_SCRIPT); // 字符 ^
        buf.append(L_GROUP); // 加上字符 {
        buf.append("\\prime"); // 加上 \prime
        int i = pos + 1;
        while (i < parseString.length()) {
            if (parseString.charAt(i) == PRIME) // 可能有多个 ', 则添加多个 \prime
                buf.append("\\prime");
            else if (!isWhiteSpace(parseString.charAt(i)))
                break;
            i++;
        }
        buf.append(R_GROUP); // 加上字符 } 现在字符串为 ^{\prime ... }
        
        // 构造新的要解析的字符串, 替代原有字符串.
        // construct the new parsing string
        parseString = parseString.substring(0, pos) + buf.toString()
        	+ parseString.substring(i);
    }
    
    /**
     * Changes the background color of the <i>current</i> TeXFormula into the given color.
     * By default, a TeXFormula has no background color, it's transparent.
     * The backgrounds of subformula's will be painted on top of the background of
     * the whole formula! Any changes that will be made to this TeXFormula after this
     * background color was set, will have the default background color (unless it will
     * also be changed into another color afterwards)!
     *
     * @param c the desired background color for the <i>current</i> TeXFormula
     * @return the modified TeXFormula
     */
    public TeXFormula setBackground(Color c) {
        if (c != null) {
            if (root instanceof ColorAtom)
                root = new ColorAtom(c, null, (ColorAtom) root);
            else
                root = new ColorAtom(root, c, null);
        }
        return this;
    }
    
    /**
     * Changes the (foreground) color of the <i>current</i> TeXFormula into the given color.
     * By default, the foreground color of a TeXFormula is the foreground color of the
     * component on which the TeXIcon (created from this TeXFormula) will be painted. The
     * color of subformula's overrides the color of the whole formula.
     * Any changes that will be made to this TeXFormula after this color was set, will be
     * painted in the default color (unless the color will also be changed afterwards into
     * another color)!
     *
     * @param c the desired foreground color for the <i>current</i> TeXFormula
     * @return the modified TeXFormula
     */
    public TeXFormula setColor(Color c) {
        if (c != null) {
            if (root instanceof ColorAtom)
                root = new ColorAtom(null, c, (ColorAtom) root);
            else
                root = new ColorAtom(root, null, c);
        }
        return this;
    }
    
    /**
     * Sets a fixed left and right type of the current TeXFormula. This has an influence
     * on the glue that will be inserted before and after this TeXFormula.
     *
     * @param leftType atom type constant (from {@link TeXConstants})
     * @param rightType atom type constant (from TeXConstants)
     * @return the modified TeXFormula
     * @throws InvalidAtomTypeException if the given integer value does not represent
     * 			a valid atom type
     */
    public TeXFormula setFixedTypes(int leftType, int rightType)
    throws InvalidAtomTypeException {
        root = new TypedAtom(leftType, rightType, root);
        return this;
    }
    
    /**
     * Parses the given strings into TeXFormula's and attaches them <i>together</i> to
     * the <i>current</i> TeXFormula as a subscript (sub) and a superscript (sup).
     * This is not the
     * same as attaching both scripts seperately one after another (in either order) using
     * the setSubscript(String) and the setSuperscript(String) methods!
     *
     * @param sub the string to be parsed into a TeXFormula that will be attached to
     * 			the current TeXFormula as a subscript
     * @param sup the string to be parsed into a TeXFormula that will be attached to
     * 			the current TeXFormula as a superscript
     * @return the modified TeXFormula
     * @throws ParseException if one of the given strings could not be parsed correctly
     */
    public TeXFormula setScripts(String sub, String sup) throws ParseException {
        return setScripts(new TeXFormula(sub), new TeXFormula(sup));
    }
    
    /**
     * Parses the given string into a TeXFormula's and attaches it to
     * the <i>current</i> TeXFormula as a subscript <i>together</i> with the given
     * TeXFormula
     * (as a superscript). This is not the same as attaching both scripts seperately
     * one after another (in either order) using the setSubscript(String)
     * and the setSuperscript(TeXFormula) methods!
     *
     * @param sub the string to be parsed into a TeXFormula that will be attached to
     * 			the current TeXFormula as a subscript
     * @param sup the TeXFormula that will be attached to
     * 			the current TeXFormula as a superscript
     * @return the modified TeXFormula
     * @throws ParseException if the given string could not be parsed correctly
     */
    public TeXFormula setScripts(String sub, TeXFormula sup)
    throws ParseException {
        return setScripts(new TeXFormula(sub), sup);
    }
    
    /**
     * Parses the given string into a TeXFormula's and attaches it to
     * the <i>current</i> TeXFormula as a superscript <i>together</i> with the given TeXFormula
     * (as a subscript). This is not the same as attaching both scripts seperately
     * one after another (in either order) using the setSubscript(TeXFormula)
     * and the setSuperscript(String) methods!
     *
     * @param sub the TeXFormula that will be attached to
     * 			the current TeXFormula as a subscript
     * @param sup the string to be parsed into a TeXFormula that will be attached to
     * 			the current TeXFormula as a superscript
     * @return the modified TeXFormula
     * @throws ParseException if the given string could not be parsed correctly
     */
    public TeXFormula setScripts(TeXFormula sub, String sup)
    throws ParseException {
        return setScripts(sub, new TeXFormula(sup));
    }
    
    /**
     * Attaches the given TeXFormula's <i>together</i> to the <i>current</i> TeXFormula as a subscript
     * (sub) and a superscript (sup). This is not the same as attaching both scripts seperately
     * one after another (in either order) using the setSubscript(TeXFormula)
     * and the setSuperscript(TeXFormula) methods!
     *
     * @param sub the TeXFormula that will be attached to
     * 			the current TeXFormula as a subscript
     * @param sup the TeXFormula that will be attached to
     * 			the current TeXFormula as a superscript
     * @return the modified TeXFormula
     */
    public TeXFormula setScripts(TeXFormula sub, TeXFormula sup) {
        root = new ScriptsAtom(root, (sub == null ? null : sub.root),
                (sup == null ? null : sup.root));
        return this;
    }
    
    /**
     * Parses the given string into a TeXFormula and attaches it to the <i>current</i>
     * TeXFormula as a subscript.
     *
     * @param sub the string to be parsed into a TeXFormula that will be attached
     * 			to the current TeXFormula as a subscript
     * @return the modified TeXFormula
     * @throws ParseException if the given string could not be parsed correctly
     */
    public TeXFormula setSubscript(String sub) throws ParseException {
        return setSubscript(new TeXFormula(sub));
    }
    
    /**
     * Attaches the given TeXFormula to the <i>current</i> TeXFormula as a subscript.
     *
     * @param sub the TeXFormula that will be attached
     * 			to the current TeXFormula as a subscript
     * @return the modified TeXFormula
     */
    public TeXFormula setSubscript(TeXFormula sub) {
        root = new ScriptsAtom(root, (sub == null ? null : sub.root), null);
        return this;
    }
    
    /**
     * Parses the given string into a TeXFormula and attaches it to the <i>current</i>
     * TeXFormula as a superscript.
     *
     * @param sup the string to be parsed into a TeXFormula that will be attached
     * 			to the current TeXFormula as a superscript
     * @return the modified TeXFormula
     * @throws ParseException if the given string could not be parsed correctly
     */
    public TeXFormula setSuperscript(String sup) throws ParseException {
        return setSuperscript(new TeXFormula(sup));
    }
    
    /**
     * Attaches the given TeXFormula to the <i>current</i> TeXFormula as a superscript.
     *
     * @param sup the TeXFormula that will be attached
     * 			to the current TeXFormula as a superscript
     * @return the modified TeXFormula
     */
    public TeXFormula setSuperscript(TeXFormula sup) {
        root = new ScriptsAtom(root, null, (sup == null ? null : sup.root));
        return this;
    }
    
   /*
    * change the current position (in the parse string) to the first following
    * non-whitespace character
    */
    private void skipWhiteSpace() {
        while (pos < parseString.length()
        && isWhiteSpace(parseString.charAt(pos)))
            pos++;
    }
    
    /**
     * Puts the current TeXFormula under a root sign and changes the current TeXFormula
     * into the resulting square root construction.
     *
     * @return the modified TeXFormula
     */
    public TeXFormula sqrt() {
        return nthRoot((TeXFormula) null);
    }
    
    /**
     * Puts a line under the current TeXFormula and changes the current TeXFormula into
     * the resulting construction.
     *
     * @return the modified TeXFormula
     */
    public TeXFormula underline() {
        root = new UnderlinedAtom(root);
        return this;
    }
    
    /**
     * 得到一个预定义的 TeX公式.
     * Get a predefined TeXFormula.
     *
     * @param name the name of the predefined TeXFormula
     * @return a copy of the predefined TeXFormula
     * @throws FormulaNotFoundException if no predefined TeXFormula is found with the
     * 			given name
     */
    public static TeXFormula get(String name) throws FormulaNotFoundException {
        Object formula = predefinedTeXFormulas.get(name);
        if (formula == null)
            throw new FormulaNotFoundException(name);
        else
            return new TeXFormula((TeXFormula) formula);
    }
    
   /*
    * Retrieves the delimiter mapping (a symbol name) of the given character
    * from a hash table.
    */
    private static String getCharacterToDelimiterMapping(char ch)
    throws DelimiterMappingNotFoundException {
        String str = delimiterMappings[ch];
        if (str == null)
            throw new DelimiterMappingNotFoundException(ch);
        else
            return str;
    }
    
   /*
    * Retrieves the delimiter symbol with the given name from a hash table
    * and checks if it's a valid delimiter.
    */
    private static SymbolAtom getDelimiterSymbol(String delName)
    throws SymbolNotFoundException, InvalidDelimiterException {
        SymbolAtom res = null;
        // null means no delimiter
        if (delName != null) {
            res = SymbolAtom.get(delName);
            // check if the symbol is a delimiter
            if (!res.isDelimiter())
                throw new InvalidDelimiterException(delName);
        }
        return res;
    }
    
   /**
    * 测试指定的字符 c 是否是一个符号(symbol)字符. 符号字符指不是 `数字,字母' 的字符.
    * Tests if the given character is a symbol character. A character is a
    * symbol character if it is not alphanumeric.
    */
    private static boolean isSymbol(char c) {
    	boolean alpha = (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    	if (alpha) return false; // 是字母, 创建 CharAtom(style=SMALL 小写字母, style=CAPITAL 大写字母)
    	boolean numeric = (c >= '0' && c <= '9');
    	if (numeric) return false; // 是数字, 创建 CharAtom(style=NUMBER)
    	
    	return true; // 现在是符号了, 对应创建为 SymbolAtom
    }
    
   /*
    * Tests if the given character is a whitespace character.
    */
    private static boolean isWhiteSpace(char ch) {
        return (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r');
    }

    @Override
    public String toString() {
    	return "TeXFormula{s=" + this.parseString + ", ts=" + this.textStyle + 
    		", root=" + this.root + "}";
    }
    
    public void dump() {
    	System.out.print("TeXFormula{str=" + this.parseString + "}\n");
    	root.dump();
    }
}
