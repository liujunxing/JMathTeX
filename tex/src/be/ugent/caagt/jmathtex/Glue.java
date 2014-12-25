/* Glue.java
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

import be.ugent.caagt.jmathtex.box.Box;
import be.ugent.caagt.jmathtex.box.GlueBox;

/**
 * 表示一个 glue(粘连), 含其3个部分. (也)包含 glue 规则.
 * Represents glue by its 3 components. Contains the "glue rules".
 */
public class Glue {
    
    // the glue components
    private final float space; 		// 正常间距
    private final float stretch;	// 可伸展能力
    private final float shrink;		// 可收缩能力
    
    private final String name;		// 名字? 粘连有名字吗?
    
    // contains the different glue types; 不同的粘连类型.
    // 在配置文件中所有 GlueType 的数组, 当前共4个: default, thin, med, thick.
    private static Glue[] glueTypes;
    
    // 粘连的表格, 表示粘连的规则(如同在 TeX 中), 在配置文件 GlueSettings.xml 中.
    // 其三个索引分别为 left_type,right_type,display_style, 值为 glueTypes[] 表的索引.
    // the glue table representing the "glue rules" (as in TeX)
    private static final int[][][] glueTable;
    
    static {
    	// 从配置 GlueSettings.xml 文件中加载 glue 的配置(规则)
        GlueSettingsParser parser = new GlueSettingsParser();
        glueTypes = parser.getGlueTypes();
        glueTable = parser.createGlueTable();
    }
    
    public Glue(float space, float stretch, float shrink, String name) {
        this.space = space;
        this.stretch = stretch;
        this.shrink = shrink;
        this.name = name;
    }
    
    /**
     * Name of this glue object.
     */
    public String getName () {
       return this.name; 
    }
    
    /**
     * 创建一个盒子. 根据 glue 规则, 在指定的左类型的 atom 和右类型的 atom 之间应插入
     *   什么样的 glue, 盒子中放置该 glue.
     * Creates a box representing the glue type according to the "glue rules" based
     * on the atom types between which the glue must be inserted.
     *
     * @param lType left atom type (左元件类型)
     * @param rType right atom type (右元件类型)
     * @param env the TeXEnvironment (环境, 从中可得到 display style, 以查找 GlueTable.)
     * @return a box containing representing the glue
     */
    public static Box get(int lType, int rType, TeXEnvironment env) {
        // types > INNER are considered of type ORD for glue calculations
        int l = (lType > 7 ? TeXConstants.TYPE_ORDINARY : lType);
        int r = (rType > 7 ? TeXConstants.TYPE_ORDINARY : rType);
        
        // search right glue-type in "glue-table"
        int glueType = glueTable[l][r][env.getStyle() / 2];        
        return glueTypes[glueType].createBox(env);
    }
    
    // 内部调用. 创建 GlueBox()
    private Box createBox(TeXEnvironment env) {
        TeXFont tf = env.getTeXFont();
        // use "quad" from a font marked as an "mu font"
        float quad = tf.getQuad(env.getStyle(), tf.getMuFontId());
        return new GlueBox((space / 18.0f) * quad, (stretch / 18.0f) * quad,
                (shrink / 18.0f) * quad);
    }
}
